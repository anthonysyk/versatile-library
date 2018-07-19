package versatile.kafka

import java.util.Properties

import org.apache.kafka.clients.producer._
import org.apache.kafka.common.serialization.{ByteArraySerializer, Serializer, StringSerializer}
import org.codehaus.jackson.map.ser.StdSerializers.IntegerSerializer
import versatile.kafka.serde.DefaultSerdes

/**
  *
  * @tparam K : can be any primitive type
  * @tparam V : should always be either String or Array[Byte]
  */
trait KafkaProducerHelper[K, V] {

  val topic: String

  def keySerializer: String
  def valueSerializer: String

  val logsTopic: String = s"$topic.logs"
  val bootstrapServer: String = "localhost:9092"
  val clientId: String = Seq("Producer", topic).mkString("_")

  def createProperties(keySer: String, valueSer: String): Properties = {
    val props = new java.util.Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
    props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySer)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSer)
    props
  }

  val producer = new KafkaProducer[K, V](createProperties(keySerializer, valueSerializer))
  val logsProducer = new KafkaProducer[K, String](createProperties(keySerializer, classOf[StringSerializer].getName))

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

object KafkaProducerHelper {

  def createProducer[K,V](topicName: String, bootstrapServerUri: String = "localhost:9092")(implicit keySerializerConverter: SerializerConverter[K], valueSerializerConverter: SerializerConverter[V] ) = {
    new KafkaProducerHelper[K,V] {
      override val topic: String = topicName
      override val bootstrapServer: String = bootstrapServerUri
      override def keySerializer: String = keySerializerConverter.value
      override def valueSerializer: String = valueSerializerConverter.value
    }
  }

}