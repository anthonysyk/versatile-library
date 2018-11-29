package XMLParser

import versatile.xml.XMLResponse
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

case class Fruit(name: String, weight: Int, color: String) extends XMLResponse {
  val className: String = getClass.getSimpleName
  lazy val rawJson: Json = this.asJson
}

object Fruit {
  val banana = """<return><name>Banana</name><weight>50</weight><color>yellow</color></return>"""
  val apple = """<return><name>Apple</name><weight>100</weight><color>red</color></return>"""
}

case class Can(name: String, weight: Int, size: String) extends XMLResponse {
  val className: String = getClass.getSimpleName
  lazy val rawJson: Json = this.asJson
}

object Can {
  val beans = """<return><name>Beans</name><weight>300</weight><size>XL</size></return>"""
  val carrots = """<return><name>Carrots</name><weight>500</weight><size>S</size></return>"""
}
