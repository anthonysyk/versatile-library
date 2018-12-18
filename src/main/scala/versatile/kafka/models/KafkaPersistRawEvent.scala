package versatile.kafka.models

import java.util.UUID

import com.sksamuel.avro4s.RecordFormat
import io.circe.Encoder
import io.circe.generic.semiauto._
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.ProducerRecord
import org.joda.time.DateTime

case class KafkaPersistEventKey(
                             year: Int,
                             event_timestamp: Long,
                             event_id: String
                           )

object KafkaPersistEventKey {

  import io.circe.Decoder
  import io.circe.generic.semiauto.deriveDecoder

  implicit val encoder: Encoder[KafkaPersistEventKey] = deriveEncoder[KafkaPersistEventKey]
  implicit val decoder: Decoder[KafkaPersistEventKey] = deriveDecoder[KafkaPersistEventKey]

  val format: RecordFormat[KafkaPersistEventKey] = RecordFormat[KafkaPersistEventKey]

}

case class KafkaPersistRawEvent(
                          source: String,
                          event_timestamp: Long,
                          readable_date: String,
                          event_raw: String,
                          event_type: String,
                          offset: Option[Long],
                          year: Int,
                          event_id: String = UUID.randomUUID().toString
                        ) {
  val key = KafkaPersistEventKey(year, event_timestamp, event_id)
  val value: KafkaPersistRawEvent = this

  def toRecord[K](topic: String): ProducerRecord[GenericRecord, GenericRecord] =
    new ProducerRecord[GenericRecord, GenericRecord](topic, KafkaPersistEventKey.format.to(key), KafkaPersistRawEvent.format.to(value))
}

object KafkaPersistRawEvent {

  import io.circe._
  import io.circe.generic.semiauto._

  implicit val encoder: Encoder[KafkaPersistRawEvent] = deriveEncoder[KafkaPersistRawEvent]
  implicit val decoder: Decoder[KafkaPersistRawEvent] = deriveDecoder[KafkaPersistRawEvent]

  val format: RecordFormat[KafkaPersistRawEvent] = RecordFormat[KafkaPersistRawEvent]

  def create(
              source: String,
              eventType: String,
              offset: Option[Long],
              value: String
            ): KafkaPersistRawEvent = {
    val now = new DateTime()

    KafkaPersistRawEvent(
      source = source,
      event_timestamp = now.getMillis,
      readable_date = now.toString,
      event_raw = value,
      event_type = eventType,
      offset = offset,
      year = now.getYear
    )
  }

}


