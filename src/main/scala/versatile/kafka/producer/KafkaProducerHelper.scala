package versatile.kafka.producer

import java.util.Properties

import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.avro.generic.GenericRecord
import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringSerializer
import versatile.kafka.models.KafkaLog

abstract class KafkaProducerHelper {

  val topic: String
  val sender: String

  lazy val logsTopic: String = s"$topic.logs"
  lazy val bootstrapServer: String = "localhost:9092"
  lazy val clientId: String = Seq(sender, topic).mkString("_")
  lazy val avroSerializer: String = classOf[KafkaAvroSerializer].getName
  lazy val stringSerializer: String = classOf[StringSerializer].getName

  def createProperties: Properties = {
    val props = new java.util.Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
    props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, stringSerializer)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, avroSerializer)
    props.put("schema.registry.url", "http://localhost:8081")
    props
  }


  val producer = new KafkaProducer[String, GenericRecord](createProperties)
  val logsProducer = new KafkaProducer[String, String](createProperties)

  private def createLog(record: ProducerRecord[String, GenericRecord]) = {
    KafkaUtils.createCallbackProducer(
      onError = { exception =>
        val errorMessage = s"[Failure] Event with key ${record.key} and value ${record.value()}"
        println(errorMessage)
        val logRecord = KafkaLog(sender, None, record.topic(), errorMessage, Some(exception.toString)).toRecord(logsTopic, record.key())
        logsProducer.send(logRecord)
      },
      onSuccess = { metadata =>
        val successMessage = s"[Success] Event with key: ${record.key()} was sent successfully at offset: ${metadata.offset()}"
        val logRecord = KafkaLog(sender, Some(metadata.offset()), record.topic(), successMessage, None).toRecord(logsTopic, record.key())
        logsProducer.send(logRecord)
      }
    )
  }

  def sendEventWithLogs(record: ProducerRecord[String, GenericRecord]): Unit = producer.send(record, createLog(record))

}
