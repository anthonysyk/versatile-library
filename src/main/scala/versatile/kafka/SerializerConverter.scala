package versatile.kafka

import com.lightbend.kafka.scala.streams.DefaultSerdes
import org.apache.kafka.common.serialization.{ByteArraySerializer, IntegerSerializer, Serializer, StringSerializer}

trait SerializerConverter[T] {
  type Ser
  val serializer: Serializer[T]
  val value: String
}

object SerializerConverter {

  implicit def converterString_! : SerializerConverter[String] = new SerializerConverter[String] {
    override type Ser = StringSerializer
    override val serializer: Serializer[String] = DefaultSerdes.stringSerde.serializer()
    override val value: String = classOf[StringSerializer].getName
  }

  implicit def converterInteger_! : SerializerConverter[Int] = new SerializerConverter[Int] {
    override type Ser = IntegerSerializer
    override val serializer: Serializer[Int] = DefaultSerdes.integerSerde.serializer()
    override val value: String = classOf[IntegerSerializer].getName
  }

  implicit def converterByteArray_! : SerializerConverter[Array[Byte]] = new SerializerConverter[Array[Byte]] {
    override type Ser = ByteArraySerializer
    override val serializer: Serializer[Array[Byte]] = DefaultSerdes.byteArraySerde.serializer()
    override val value: String = classOf[ByteArraySerializer].getName
  }

  def getValueSerializer[V](implicit valueSerializerConverter: SerializerConverter[V]) = valueSerializerConverter

}