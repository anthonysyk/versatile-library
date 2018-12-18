package versatile.kafka.producer

import java.util.Properties
import java.util.concurrent.Future

import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer._
import versatile.kafka.models.{KafkaPersistRawEvent, VFullRecord}

abstract class KafkaProducerHelper {

  val topic: String
  val persistEventTopic: String
  val source: String

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
  val persistEventProducer = new KafkaProducer[GenericRecord, GenericRecord](createProperties)
  val rawEventProducer = new KafkaProducer[GenericRecord, GenericRecord](createProperties)

  private def createRawEvent(fullRecord: VFullRecord): Callback = {
    KafkaUtils.createCallbackProducer(
      onError = { exception =>
        val errorMessage = s"[Failure] Event ${fullRecord.value.noSpaces} \n$exception"
        println(errorMessage)
        val rawRecord = KafkaPersistRawEvent.create(source = fullRecord.source.getOrElse(source), eventType = fullRecord.eventType, offset = None, value = fullRecord.value.noSpaces)
        .toRecord(persistEventTopic)
        persistEventProducer.send(rawRecord)
      },
      onSuccess = { metadata =>
        val rawRecord = KafkaPersistRawEvent.create(source = fullRecord.source.getOrElse(source), eventType = fullRecord.eventType, offset = Some(metadata.offset()), value = fullRecord.value.noSpaces)
            .toRecord(persistEventTopic)
        persistEventProducer.send(rawRecord)
      }
    )
  }

  def sendEventWithRaw(fullRecord: VFullRecord): Unit = producer.send(fullRecord.record, createRawEvent(fullRecord))

  def sendEvent(record: ProducerRecord[GenericRecord, GenericRecord]): Future[RecordMetadata] = producer.send(record)

  def sendEventRaw(rawTopic: String, eventType: String, maybeSource: Option[String], value: String): Future[RecordMetadata] = {
    val record = KafkaPersistRawEvent.create(source = maybeSource.getOrElse(source), eventType = eventType, offset = None, value = value)
      .toRecord(rawTopic)
    rawEventProducer.send(record)
  }

}
