package data

import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

trait XMLResponse {
  implicit val className: String
  val rawJson: Json
  val parentJson: Json = XMLHelper.getCoProductJson(rawJson)
  val JsonVersion: Json = XMLHelper.parseNestedJson(rawJson)
  val JsonStringVersion: String = JsonVersion.noSpaces
}

// circe doesn't like Seq[_] if the field is not on the json but works with Option[Seq[_]]
