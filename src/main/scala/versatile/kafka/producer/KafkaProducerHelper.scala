package versatile.kafka.producer

import java.util.Properties
import java.util.concurrent.Future

import io.circe.Json
import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer._
import versatile.kafka.models.{KafkaRawEvent, VFullRecord}

abstract class KafkaProducerHelper {

  val topic: String
  val source: String

  lazy val rawEventTopic: String = s"${topic}_raw".toLowerCase()
  lazy val bootstrapServer: String = "localhost:9092"
  lazy val clientId: String = Seq(source, topic).mkString("_")
  lazy val avroSerializer: String = classOf[KafkaAvroSerializer].getName

  def createProperties: Properties = {
    val props = new java.util.Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
    props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, avroSerializer)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, avroSerializer)
    props.put("schema.registry.url", "http://localhost:8081")
    props
  }


  val producer = new KafkaProducer[GenericRecord, GenericRecord](createProperties)
  val rawEventProducer = new KafkaProducer[GenericRecord, GenericRecord](createProperties)

  private def createRawEvent(fullRecord: VFullRecord): Callback = {
    KafkaUtils.createCallbackProducer(
      onError = { exception =>
        val errorMessage = s"[Failure] Event ${fullRecord.value.noSpaces} \n$exception"
        println(errorMessage)
        val rawRecord = KafkaRawEvent.create(source = fullRecord.source.getOrElse(source), offset = None, value = fullRecord.value.noSpaces)
        .toRecord(rawEventTopic)
        rawEventProducer.send(rawRecord)
      },
      onSuccess = { metadata =>
        val rawRecord = KafkaRawEvent.create(source = fullRecord.source.getOrElse(source), offset = Some(metadata.offset()), value = fullRecord.value.noSpaces)
            .toRecord(rawEventTopic)
        rawEventProducer.send(rawRecord)
      }
    )
  }

  def sendEventWithRaw(fullRecord: VFullRecord): Unit = producer.send(fullRecord.record, createRawEvent(fullRecord))

  def sendEvent(record: ProducerRecord[GenericRecord, GenericRecord]): Future[RecordMetadata] = producer.send(record)

  def sendEventRaw(maybeSource: Option[String], value: String): Future[RecordMetadata] = {
    val record = KafkaRawEvent.create(source = maybeSource.getOrElse(source), offset = None, value = value)
      .toRecord(rawEventTopic)
    rawEventProducer.send(record)
  }

}
