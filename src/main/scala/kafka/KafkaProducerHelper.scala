package kafka

import org.apache.kafka.clients.producer._

trait KafkaProducerHelper[K, V] {

  val topic: String

  val logsTopic: String = s"$topic.logs"

  val clientId: String = Seq("Producer", topic).mkString("_")
  val props = new java.util.Properties()
  props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092")
  props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId)
  props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.IntegerSerializer")
  props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.ByteArraySerializer")

  val producer = new KafkaProducer[K, V](props)
  val logsProducer = new KafkaProducer[K, String](props)

  private def createLog(record: ProducerRecord[K, V]) = {
    KafkaUtils.createCallbackProducer(
      onError = { exception =>
        val errorMessage = s"[Failure] Event with key ${record.key} and value ${record.value()}"
        println(errorMessage)
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
