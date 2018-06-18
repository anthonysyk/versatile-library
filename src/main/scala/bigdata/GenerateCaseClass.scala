package bigdata


import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types.StructType


import java.text.SimpleDateFormat
import java.util.Date

import org.apache.spark.sql.types._

// TODO: Ajouter la possibilité d'étendre un sealed trait
object SchemaUtils {

  private val mappingTypes: Map[DataType, String] = Map(
    StringType -> "String",
    BooleanType -> "Boolean",
    DoubleType -> "Double",
    FloatType -> "Float",
    IntegerType -> "Int",
    LongType -> "Long",
    ShortType -> "Short",
    TimestampType -> "Long"
  )

  private def handleOption(optional: Boolean, value: String) = if (optional) s"Option[$value]" else value

  private def handleFieldName(fieldName: String) = fieldName match {
    case "type" => "`type`"
    case _ => fieldName
  }

  private def parseStructField(sf: StructField): String = sf match {
    case StructField(name, fieldType: DecimalType, optional, _) =>
      val fieldTypeAsString = handleOption(optional, "BigDecimal")
      s"\t$name: $fieldTypeAsString"
    case StructField(name, StructType(fields), optional, _) => s"\t${handleFieldName(name)} : ${handleOption(optional, parseNameWithUnderscore(name).capitalize)}"
    case StructField(name, ArrayType(dt, nullable), optional, _) => s"\t${handleFieldName(name)} : Seq[${parseNameWithUnderscore(name).capitalize}]"
    case StructField(name, fieldType: DataType, optional, _) =>
      val fieldTypeAsString = handleOption(optional, mappingTypes.getOrElse(fieldType, s"not handled $fieldType"))
      s"\t${handleFieldName(name)}: $fieldTypeAsString"
  }

  def findStructTypeSchemas(name: String, structField: DataType): Map[String, DataType] = {
    structField match {
      case StructType(fields) => fields.foldLeft(Seq.empty[(String, DataType)]) { (acc, curr) =>
        curr match {
          case StructField(n, StructType(_), opt, _) =>
            (acc :+ curr.name -> curr.dataType) ++ findStructTypeSchemas(curr.name, curr.dataType)
          case StructField(n, ArrayType(dt, _), opt, _) =>
            (acc :+ curr.name -> dt) ++ findStructTypeSchemas(curr.name, dt)
          case _ =>
            acc
        }
      }
    }
  }.toMap

  def parseNameWithUnderscore(name: String): String =
    name.foldLeft(("", true)) { (accTuple, next) =>
      (accTuple, next) match {
        case ((acc, isNextUpper), _) if isNextUpper =>
          (acc :+ next.toUpper) -> false
        case ((acc, isNextUpper), _) if !isNextUpper =>
          if (next == '_') acc -> true else (acc :+ next) -> false
      }
    }._1

  def createCaseClass(fullName: String, schema: DataType, tabletrait: Option[String] = None): String = {

    val allFields: String = schema match {
      case StructType(fields) => fields.map(parseStructField).filter(_.nonEmpty).mkString(",\n")
    }

    s"case class ${parseNameWithUnderscore(fullName.split('.').last)} (\n$allFields\n)"
  }

  def generateCaseClassFile(schemas: (String, DataType)*): String = {
    val structs: Seq[(String, DataType)] = schemas.flatMap { case (name, dt) =>
      SchemaUtils.findStructTypeSchemas(name, dt) + (name -> dt)
    }

    val caseClasses = structs.map { case (name, dt) => SchemaUtils.createCaseClass(name, dt) }

    createCaseClassFile(caseClasses)
  }


  /**
    * //IDEA : Use Scalafix
    *
    * @param caseClasses
    * @return
    */

  private def createCaseClassFile(caseClasses: Iterable[String]*): String = {

    val date = new SimpleDateFormat("dd/MM/yyyy").format(new Date)
    val generatedComment: String = s"/**************** Generated on $date ****************/"

    Seq(generatedComment, caseClasses.flatten.mkString("\n\n")).mkString("\n\n")

  }

}

object GenerateCaseClass {

  val TABLE_ARTICLE = "dw_smco.twh_ods_amc_articles"

  implicit val ss: SparkSession = SparkSession.builder().master("local[4]").enableHiveSupport().getOrCreate()

  def main(args: Array[String]): Unit = {

    if (args.isEmpty) {
      println("please specify table names (format dwh.tablename)"); System.exit(0)
    }

    val fullTableNames = args

    fullTableNames.foreach(println)

    val schemas: Array[(String, StructType)] = fullTableNames.map(tablename => tablename -> ss.read.table(tablename).schema)

    val file = SchemaUtils.generateCaseClassFile(schemas: _*)

    println(file)

    println("Job Ended")

  }


}
