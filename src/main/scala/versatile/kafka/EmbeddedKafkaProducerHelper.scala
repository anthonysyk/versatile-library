package versatile.kafka

import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringSerializer

/**
  * The key in embedded kafka is ALWAYS a String
  * @tparam V : should always be either String or Array[Byte]
  */
trait EmbeddedKafkaProducerHelper[V] extends KafkaProducerHelper[String,V] {

  override val topic = "Test"

  override def keySerializer = classOf[StringSerializer].getName

  val config: EmbeddedKafkaConfig = EmbeddedKafkaConfig.defaultConfig

  override val logsProducer: KafkaProducer[String, String] = EmbeddedKafka.aKafkaProducer[String](new StringSerializer, config)

}

object EmbeddedKafkaProducerHelper {

  def createEmbeddedProducer[V](topicName: String)(implicit valueConverter: SerializerConverter[V]): EmbeddedKafkaProducerHelper[V] = {
    new EmbeddedKafkaProducerHelper[V] {
      override val topic: String = topicName
      override def valueSerializer: String = valueConverter.value
      override val producer: KafkaProducer[String, V] = EmbeddedKafka.aKafkaProducer[V](valueConverter.serializer, config)
    }
  }

}

trait EmbeddedKafkaHelper {

  val topics: Seq[String]

  def startEmbeddedKafka() = {
    EmbeddedKafka.start()
    createTopics()
  }

  private def createTopics() = topics.foreach(topic => EmbeddedKafka.createCustomTopic(topic))



}