package versatile.bigdata

import org.apache.spark.sql.DataFrame
import org.apache.spark.sql.types.{ArrayType, DataType, StructField, StructType}

object SparkUtils {

  // Permet de renommer les champs de manière recursive
  def deepRename(dataFrame: DataFrame, oldName: String, newName: String): DataFrame = {
    def renameInSchema(datatype: DataType): DataType = {
      datatype match {
        case StructType(structFields) => StructType(structFields.map({
          case sf @ StructField(`oldName`, dt, _, _) => sf.copy(name = newName, dataType = renameInSchema(dt))
          case sf => sf.copy(dataType = renameInSchema(sf.dataType))
        }))

        case at @ ArrayType(dt, _) => at.copy(renameInSchema(dt))
        case _ => datatype
      }
    }

    val ss = dataFrame.sparkSession
    val newSchema: DataType = renameInSchema(dataFrame.schema)

    ss.createDataFrame(dataFrame.rdd, newSchema.asInstanceOf[StructType])
  }


}
