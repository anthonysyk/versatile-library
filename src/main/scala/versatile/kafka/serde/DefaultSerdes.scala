package versatile.kafka.serde

import org.apache.kafka.common.serialization.{Deserializer, Serde, Serdes, Serializer}

/**
  * Implicit values for default serdes
  */
object DefaultSerdes {
  implicit val stringSerde: Serde[String] = Serdes.String()
  implicit val longSerde: Serde[Long] = Serdes.Long().asInstanceOf[Serde[Long]]
  implicit val byteArraySerde: Serde[Array[Byte]] = Serdes.ByteArray()
  implicit val bytesSerde: Serde[org.apache.kafka.common.utils.Bytes] = Serdes.Bytes()
  implicit val floatSerde: Serde[Float] = Serdes.Float().asInstanceOf[Serde[Float]]
  implicit val doubleSerde: Serde[Double] = Serdes.Double().asInstanceOf[Serde[Double]]
  implicit val integerSerde: Serde[Int] = Serdes.Integer().asInstanceOf[Serde[Int]]

  implicit val stringSerializer: Serializer[String] = stringSerde.serializer()
  implicit val integerSerializer: Serializer[Int] = integerSerde.serializer()
  implicit val byteArraySerializer: Serializer[Array[Byte]] = byteArraySerde.serializer()

  implicit val stringDeserializer: Deserializer[String] = stringSerde.deserializer()
  implicit val integerDeserializer: Deserializer[Int] = integerSerde.deserializer()
  implicit val byteArrayDeserializer: Deserializer[Array[Byte]] = byteArraySerde.deserializer()

}
