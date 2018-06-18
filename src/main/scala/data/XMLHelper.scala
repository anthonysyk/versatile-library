package data

import io.circe
import io.circe.generic.auto._
import io.circe.parser.{decode, parse}
import io.circe.{Json, JsonObject}

import scala.xml.NodeSeq

import scala.xml.NodeSeq

object XMLHelper {


  private def getXmlFromXmlResponseString(response: String, tagStart: String): NodeSeq = scala.xml.XML.loadString(response) \\ tagStart

  def createJsonFromXml(response: String, tagStart: String): Json = {

    val root = getXmlFromXmlResponseString(response, tagStart).flatMap(_.child)

    import spray.json._

    implicit object NodeSeqFormat extends JsonFormat[NodeSeq] {

      override def write(obj: NodeSeq): JsValue = writeNested(obj, None, false)

      //FIXME: IsArray is deducted, but if there is only one occurence, it will be count as jsobject not array
      def writeNested(nodeSeq: NodeSeq, maybeParent: Option[String], isArray: Boolean): JsValue = {
        val result = nodeSeq.map {
          case field if field.text.isEmpty =>
            JsObject(field.label -> JsNull)
          case field if field.label.contains("#PCDATA") =>
            JsObject(maybeParent.get -> JsString(field.text))
          case field if field.child.nonEmpty && field.child.head.label == "#PCDATA" =>
            JsObject(field.label -> JsString(field.child.head.text))
          case field if isArray =>
            val isChildArray = field.child.map(_.label).distinct.length != field.child.length || field.label.endsWith("s")
            writeNested(field.child, None, isChildArray)
          case field@_ =>
            val isChildArray = field.child.map(_.label).distinct.length != field.child.length || field.label.endsWith("s")
            val label = if (isChildArray) field.child.head.label else field.label
            JsObject(label -> writeNested(field.child, Some(field.label), isChildArray))
        }.toVector

        if (isArray) JsArray(result) else JsObject(result.flatMap(_.asJsObject.fields).toMap)

      }

      def read(value: JsValue) = ???
    }

    val json = root.toJson.toString()
    //TODO: Transformer le Map[String, Any] en Json

    val result = parse(json).right.toOption.getOrElse(Json.Null)

    result
  }

  object ParseJson {
    def unapply(arg: String): Option[Json] = {
      parse(arg).fold[Option[Json]](_ => None, Option(_))
    }
  }

  object JString {
    def unapply(arg: Json): Option[String] = arg.asString
  }

  object JObject {
    def unapply(arg: Json): Option[JsonObject] = arg.asObject
  }

  def parseNestedJson(json: Json): Json = {

    def parseJsonObject(jsonObject: JsonObject) = jsonObject.toMap.map(parseTuple)

    def parseTuple(tuple: (String, Json)): (String, Json) = {
      tuple._1 -> (tuple._2 match {
        case value@JString(string) => parse(string).right.getOrElse(value)
        case mapValue => mapValue
      })
    }

    Json.fromFields(json.asObject.map(parseJsonObject).getOrElse(Map.empty[String, Json]))

  }

  // This function add the className of the product to the JSON
  def getCoProductJson[T](json: Json)(implicit m: Manifest[T]): Json = {

    implicit val className = m.runtimeClass.getSimpleName

    Json.fromFields(className -> json :: Nil)
  }

}
