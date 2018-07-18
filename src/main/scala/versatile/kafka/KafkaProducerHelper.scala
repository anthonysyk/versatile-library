package versatile.kafka

import java.util.Properties

import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.StringSerializer

trait KafkaProducerHelper[K, V] {

  val topic: String
  def keySerializer: String
  def valueSerializer: String

  val logsTopic: String = s"$topic.logs"
  val bootstrapServer: String = "localhost:9092"
  val clientId: String = Seq("Producer", topic).mkString("_")

  def createProps(keySer: String, valueSer: String): Properties = {
    val props = new java.util.Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
    props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySer)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSer)
    props
  }

  val producer = new KafkaProducer[K, V](createProps(keySerializer, valueSerializer))
  val logsProducer = new KafkaProducer[K, String](createProps(keySerializer, classOf[StringSerializer].getName))

  private def createLog(record: ProducerRecord[K, V]) = {
    KafkaUtils.createCallbackProducer(
      onError = { exception =>
        val errorMessage = s"[Failure] Event with key ${record.key} and value ${record.value()}"
        val logRecord = KafkaLog(None, record.topic(), errorMessage, Some(exception.toString)).toRecord(logsTopic, record.key())
        logsProducer.send(logRecord)
      },
      onSuccess = { metadata =>
        val successMessage = s"[Success] Event with key: ${record.key()} was sent successfully at offset: ${metadata.offset()}"
        val logRecord = KafkaLog(Some(metadata.offset()), record.topic(), successMessage, None).toRecord(logsTopic, record.key())
        logsProducer.send(logRecord)
      }
    )
  }

  def sendEventWithLogs(record: ProducerRecord[K, V]): Unit = producer.send(record, createLog(record))

}