import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.scalatest.FunSuite
import versatile.kafka.KafkaProducerHelper

class LoggerTest extends FunSuite with KafkaProducerHelper[String, String] {

  val topic = "Test"
  def keySerializer = classOf[StringSerializer].getName
  def valueSerializer = classOf[StringSerializer].getName

  EmbeddedKafka.start()

  EmbeddedKafka.createCustomTopic(topic)
  EmbeddedKafka.createCustomTopic(logsTopic)

  val config: EmbeddedKafkaConfig = EmbeddedKafkaConfig.defaultConfig

  implicit val stringSerializer: StringSerializer = new StringSerializer
  implicit val stringDeserializer: StringDeserializer = new StringDeserializer

  override val producer: KafkaProducer[String, String] = EmbeddedKafka.aKafkaProducer[String](new StringSerializer, config)
  override val logsProducer: KafkaProducer[String, String] = EmbeddedKafka.aKafkaProducer[String](new StringSerializer, config)

  test("Test Logging DSL") {

    val record = new ProducerRecord[String, String](topic, "this is a message")

    sendEventWithLogs(record)

    val message = EmbeddedKafka.consumeFirstKeyedMessageFrom(topic)

    println(message)

    val log = EmbeddedKafka.consumeFirstKeyedMessageFrom(logsTopic)

    println(log)

    // TODO: make assertions more precise
    assert(message.productIterator.nonEmpty)
    assert(log.productIterator.nonEmpty)

  }

}
