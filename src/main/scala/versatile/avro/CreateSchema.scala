package versatile.avro

import com.sksamuel.avro4s.{AvroSchema, SchemaFor}
import io.circe.generic.auto._
import io.circe.syntax._
import versatile.filesystem.FileSystemHelper

import scala.io.Source

case class AvroSchemaImport(schema: String)

/**
  * Util application to create Avro schema files
  */
object CreateSchema extends FileSystemHelper {
  def importF(json: String): String = AvroSchemaImport(json).asJson.toString()

  def createSchemaFile[T: SchemaFor](implicit m: Manifest[T]): Unit = {
    val name = m.runtimeClass.getSimpleName
    writeToFile(AvroSchema[T].toString(false), s"$getResourcesPath/avro/${name}Import.json", importF)
    writeToFile(AvroSchema[T].toString(true), s"$getResourcesPath/avro/$name.json")
  }

  def writeToFile(content: String, filename: String, w: String => String = identity): Unit = {
    writeFile(filename, w(content))
  }

  def getSchema[T](implicit m: Manifest[T]): String = {
    val name = m.runtimeClass.getSimpleName
    Source.fromFile(s"$getResourcesPath/avro/$name").getLines().mkString
  }

}
