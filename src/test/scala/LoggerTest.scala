//import net.manub.embeddedkafka.{EmbeddedKafka, EmbeddedKafkaConfig}
//import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
//import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
//import org.scalatest.FunSuite
//import versatile.kafka.producer.KafkaProducerHelper
//
//class LoggerTest extends FunSuite {
//
//  EmbeddedKafka.start()
//
//  val config: EmbeddedKafkaConfig = EmbeddedKafkaConfig.defaultConfig
//
//  final val className: String = this.getClass.getSimpleName
//
//  val producer: KafkaProducerHelper[String, String] = new KafkaProducerHelper[String, String] {
//    override val topic: String = "Test"
//    override val sender: String = className
//    override val producer: KafkaProducer[String, String] = EmbeddedKafka.aKafkaProducer[String](new StringSerializer, config)
//    override val logsProducer: KafkaProducer[String, String] = EmbeddedKafka.aKafkaProducer[String](new StringSerializer, config)
//  }
//
//  EmbeddedKafka.createCustomTopic(producer.topic)
//  EmbeddedKafka.createCustomTopic(producer.logsTopic)
//
//  implicit val stringSerializer: StringSerializer = new StringSerializer
//  implicit val stringDeserializer: StringDeserializer = new StringDeserializer
//
//  test("Test Logging DSL") {
//
//    val record = new ProducerRecord[String, String](producer.topic,"1", "this is a message")
//
//    producer.sendEventWithLogs(record)
//
//    val message = EmbeddedKafka.consumeFirstKeyedMessageFrom(producer.topic)
//
//    println(message)
//
//    val log = EmbeddedKafka.consumeFirstKeyedMessageFrom(producer.logsTopic)
//
//    println(log)
//
//    // TODO: make assertions more precise
//    assert(message.productIterator.nonEmpty)
//    assert(log.productIterator.nonEmpty)
//
//    EmbeddedKafka.stop()
//
//  }
//
//}
//
