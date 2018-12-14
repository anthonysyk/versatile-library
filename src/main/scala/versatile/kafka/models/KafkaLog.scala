package versatile.kafka.models

import java.time.ZonedDateTime

import io.circe.syntax._
import org.apache.kafka.clients.producer.ProducerRecord

case class KafkaLog(
                     sender: String,
                     zone_date_time: String,
                     offset: Option[Long],
                     topic: String,
                     message: String,
                     exception: Option[String],
                     isSuccess: Boolean
                   ) {
  val kafkaLog: KafkaLog = this

  def toRecord[K](topic: String, key: String): ProducerRecord[String, String] = new ProducerRecord[String, String](topic, key, kafkaLog.asJson.noSpaces)
}

object KafkaLog {

  import io.circe._
  import io.circe.generic.semiauto._

  implicit val encoder: Encoder[KafkaLog] = deriveEncoder[KafkaLog]
  implicit val decoder: Decoder[KafkaLog] = deriveDecoder[KafkaLog]

  def apply(
             sender: String,
             offset: Option[Long],
             topic: String,
             message: String,
             exception: Option[String]
           ): KafkaLog = new KafkaLog(sender, ZonedDateTime.now().toString, offset, topic, message, exception, offset.isDefined && exception.isEmpty)
}


