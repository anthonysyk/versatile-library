package versatile.base64

import scala.util.Try
import org.apache.commons.codec.binary.Base64


object Base64Helper {

  object Implicits {

    implicit class RichString(string: String) {
      def toBase64String: String = Base64.encodeBase64String(string.getBytes())

      def fromBase64String: Try[String] = Try(Base64.decodeBase64(string)).map(bytes => new String(bytes, "UTF-8"))

    }

  }

}
