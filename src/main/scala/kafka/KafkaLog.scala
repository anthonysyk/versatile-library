package kafka

import io.circe.syntax._
import org.apache.kafka.clients.producer.ProducerRecord

case class KafkaLog(
                    offset: Option[Long],
                    topic: String,
                    message: String,
                    exception: Option[String],
                    isSuccess: Boolean
                   ) {
  def toRecord[K](topic: String, key: K) = new ProducerRecord[K, String](topic, key, this.asJson.noSpaces)
}

object KafkaLog {

  import io.circe._
  import io.circe.generic.semiauto._

  implicit val encoder: Encoder[KafkaLog] = deriveEncoder[KafkaLog]
  implicit val decoder: Decoder[KafkaLog] = deriveDecoder[KafkaLog]

  def apply(
             offset: Option[Long],
             topic: String,
             message: String,
             exception: Option[String]
           ): KafkaLog = new KafkaLog(offset, topic, message, exception, offset.isDefined && exception.isEmpty)
}
