package XMLParser

import versatile.xml.XMLHelper
import org.scalatest.FunSuite

class XMLParserTest extends FunSuite {

  test("convert XML to JSON") {

    val json = XMLHelper.createJsonFromXml(Fruit.apple, "return")

    val parentJson = XMLHelper.getCoProductJson[Fruit](json)

    println(parentJson)

  }

}
