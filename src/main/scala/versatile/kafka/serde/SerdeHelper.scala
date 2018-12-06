package versatile.kafka.serde

import java.util

import io.circe.parser.parse
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import org.apache.kafka.common.serialization.{Deserializer, Serdes, Serializer}

object SerdeHelper {

  import versatile.json.CirceHelper._

  def createSerde[T >: Null](implicit encoder: Encoder[T], decoder: Decoder[T]) = {
    Serdes.serdeFrom(getSerializer[T], getDeserializer[T])
    Serdes.serdeFrom[T](getSerializer[T], getDeserializer[T])
  }

  private def getSerializer[T >: Null](implicit encoder: Encoder[T])  = new Serializer[T] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    override def serialize(topic: String, data: T): Array[Byte] = data.asJson.noSpaces.getBytes()

    override def close(): Unit = ()
  }

  private def getDeserializer[T >: Null](implicit decoder: Decoder[T]) = new Deserializer[T] {
    override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

    override def deserialize(topic: String, data: Array[Byte]): T = parse(new String(data)).getRight.as[T].right.toOption.orNull

    override def close(): Unit = ()
  }

}
