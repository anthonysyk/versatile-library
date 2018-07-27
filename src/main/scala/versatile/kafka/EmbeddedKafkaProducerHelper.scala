package versatile.kafka

import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.producer.KafkaProducer
import org.apache.kafka.common.serialization.StringSerializer

/**
  * The key in embedded kafka is ALWAYS a String
  * @tparam V : should always be either String or Array[Byte]
  */
abstract class EmbeddedKafkaProducerHelper[V](implicit valueConverter: SerializerConverter[V]) extends KafkaProducerHelper[String,V] {

  override val topic: String

  override def keySerializer = classOf[StringSerializer].getName

  val config: EmbeddedKafkaConfig = EmbeddedKafkaConfig.defaultConfig

  override val logsProducer: KafkaProducer[String, String] = EmbeddedKafka.aKafkaProducer[String](new StringSerializer, config)

  override val producer: KafkaProducer[String, V] = EmbeddedKafka.aKafkaProducer[V](valueConverter.serializer, config)
}

trait EmbeddedKafkaHelper {

  val topics: Seq[String]

  def startEmbeddedKafka() = {
    EmbeddedKafka.start()
    createTopics()
  }

  private def createTopics() = topics.foreach(topic => EmbeddedKafka.createCustomTopic(topic))

}