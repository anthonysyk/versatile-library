package versatile.kafka.producer

import java.util.Properties

import io.confluent.kafka.serializers.KafkaAvroSerializer
import org.apache.kafka.clients.producer.{Callback, ProducerConfig, RecordMetadata}

object KafkaUtils {

  /**
    * Creates a kafka producer callback
    *
    * @param onSuccess do something with the metadata
    * @param onError   is a function that do something with the exception (which is a printStackTrace by default)
    * @return a Kafka Producer Callback (which is a SAM)
    */
  def createCallbackProducer(onSuccess: RecordMetadata => Unit, onError: Exception => Unit): Callback = {
    (metadata: RecordMetadata, exception: Exception) =>
      if (exception != null) {
        onError(exception)
      } else {
        onSuccess(metadata)
      }
  }

  val avroSerializer: String = classOf[KafkaAvroSerializer].getName

  def createProperties(bootstrapServer: String, clientId: String, keySer: String, valueSer: String): Properties = {
    val props = new java.util.Properties()
    props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServer)
    props.put(ProducerConfig.CLIENT_ID_CONFIG, clientId)
    props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, keySer)
    props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, valueSer)
    props.put("schema.registry.url", "http://localhost:8081")
    props
  }


}
