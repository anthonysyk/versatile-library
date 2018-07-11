package versatile.kafka.serde

import io.circe.parser.parse
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import org.apache.kafka.common.serialization.Serdes

object SerdeHelper {

  import versatile.utils.CirceHelper._

  def createSerde[T >: Null](implicit encoder: Encoder[T], decoder: Decoder[T]) = {
    Serdes.serdeFrom[T](getSerializer[T], getDeserializer[T])
  }

  private def getSerializer[T >: Null](implicit encoder: Encoder[T])  = new Serializer[T] {
    override def serialize(data: T): Array[Byte] = data.asJson.noSpaces.getBytes()
  }

  private def getDeserializer[T >: Null](implicit decoder: Decoder[T]) = new Deserializer[T] {
    override def deserialize(data: Array[Byte]): Option[T] =
      parse(new String(data)).getRight.as[T].right.toOption
  }

}
