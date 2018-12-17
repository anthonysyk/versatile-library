package versatile.kafka.models

import java.util.UUID

import com.sksamuel.avro4s.RecordFormat
import io.circe.Encoder
import io.circe.generic.semiauto._
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.joda.time.DateTime

case class KafkaRawEventKey(
                             year: Int,
                             event_timestamp: Long,
                             event_id: String
                           )

object KafkaRawEventKey {

  import io.circe.Decoder
  import io.circe.generic.semiauto.deriveDecoder

  implicit val encoder: Encoder[KafkaRawEventKey] = deriveEncoder[KafkaRawEventKey]
  implicit val decoder: Decoder[KafkaRawEventKey] = deriveDecoder[KafkaRawEventKey]

  val format: RecordFormat[KafkaRawEventKey] = RecordFormat[KafkaRawEventKey]

}

case class KafkaRawEvent(
                          source: String,
                          event_timestamp: Long,
                          readable_date: String,
                          event_raw: String,
                          offset: Option[Long],
                          year: Int,
                          event_id: String = UUID.randomUUID().toString
                        ) {
  val key = KafkaRawEventKey(year, event_timestamp, event_id)
  val value: KafkaRawEvent = this

  def toRecord[K](topic: String): ProducerRecord[GenericRecord, GenericRecord] =
    new ProducerRecord[GenericRecord, GenericRecord](topic, KafkaRawEventKey.format.to(key), KafkaRawEvent.format.to(value))
}

object KafkaRawEvent {

  import io.circe._
  import io.circe.generic.semiauto._

  implicit val encoder: Encoder[KafkaRawEvent] = deriveEncoder[KafkaRawEvent]
  implicit val decoder: Decoder[KafkaRawEvent] = deriveDecoder[KafkaRawEvent]

  val format: RecordFormat[KafkaRawEvent] = RecordFormat[KafkaRawEvent]

  def create(
              source: String,
              offset: Option[Long],
              value: String
            ): KafkaRawEvent = {
    val now = new DateTime()

    KafkaRawEvent(
      source = source,
      event_timestamp = now.getMillis,
      readable_date = now.toString,
      event_raw = value,
      offset = offset,
      year = now.getYear
    )
  }

}


