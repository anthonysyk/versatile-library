import net.manub.embeddedkafka.EmbeddedKafka
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.scalatest.FunSuite
import versatile.kafka.{EmbeddedKafkaHelper, EmbeddedKafkaProducerHelper}

class LoggerTestEmbedded extends FunSuite with EmbeddedKafkaHelper {

  val topic = "Test"

  override val topics: Seq[String] = topic :: Nil

  startEmbeddedKafka()

  val producer = EmbeddedKafkaProducerHelper.createEmbeddedProducer[String](topic)

  implicit val stringSerializer: StringSerializer = new StringSerializer
  implicit val stringDeserializer: StringDeserializer = new StringDeserializer

  test("Test Logging DSL") {

    val record = new ProducerRecord[String, String](topic, "this is a message")

    producer.sendEventWithLogs(record)

    val message = EmbeddedKafka.consumeFirstKeyedMessageFrom(topic)

    println(message)

    val log = EmbeddedKafka.consumeFirstKeyedMessageFrom(producer.logsTopic)

    println(log)

    // TODO: make assertions more precise
    assert(message.productIterator.nonEmpty)
    assert(log.productIterator.nonEmpty)

  }
}
