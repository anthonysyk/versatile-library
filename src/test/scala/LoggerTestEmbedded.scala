import net.manub.embeddedkafka.EmbeddedKafka
import org.apache.kafka.clients.producer.ProducerRecord
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.scalatest.FunSuite
import versatile.kafka.{EmbeddedKafkaHelper, EmbeddedKafkaProducerHelper}

class LoggerTestEmbedded extends FunSuite with EmbeddedKafkaHelper {

  final val className: String = this.getClass.getSimpleName

  val producer = new EmbeddedKafkaProducerHelper[String] {
    override val topic: String = "Test"
    override val sender: String = className
  }

  override val topics = producer.topic :: producer.logsTopic :: Nil

  startEmbeddedKafka()

  implicit val stringSerializer: StringSerializer = new StringSerializer
  implicit val stringDeserializer: StringDeserializer = new StringDeserializer

  test("Test Logging DSL") {

    val record1 = new ProducerRecord[String, String](producer.topic, "1", "this is a message")

    val record2 = new ProducerRecord[String, String](producer.topic, "2", "this is a message number 2")


    producer.sendEventWithLogs(record1)

    Thread.sleep(1000)

    producer.sendEventWithLogs(record2)

    val message1 = EmbeddedKafka.consumeFirstKeyedMessageFrom(producer.topic)
    val message2 = EmbeddedKafka.consumeFirstKeyedMessageFrom(producer.topic)

    println(message1)
    println(message2)

    val log1 = EmbeddedKafka.consumeFirstKeyedMessageFrom(producer.logsTopic)
    val log2 = EmbeddedKafka.consumeFirstKeyedMessageFrom(producer.logsTopic)

    println(log1)
    println(log2)

    // TODO: make assertions more precise
    assert(message1.productIterator.nonEmpty)
    assert(log1.productIterator.nonEmpty)

    EmbeddedKafka.stop()

  }
}
