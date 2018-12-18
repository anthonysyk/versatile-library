package versatile.kafka.models

import io.circe.Json
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer.ProducerRecord

case class VFullRecord(
                        source: Option[String],
                        eventType: String,
                        record: ProducerRecord[GenericRecord, GenericRecord],
                        value: Json
                      )
