package kafka

import org.apache.kafka.clients.producer.{Callback, RecordMetadata}

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

}
