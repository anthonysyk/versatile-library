package versatile.kafka.iq.serializers

import com.twitter.bijection.Injection
import org.apache.avro.specific.SpecificRecordBase
import java.util.{Map => JMap}

import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

import scala.util.Try

class SpecificAvroSerDeserializer[T <: SpecificRecordBase](injection: Injection[T, Array[Byte]]) extends Serde[T] {
  val inverted: Array[Byte] => Try[T] = injection.invert

  override def configure(configs: JMap[String, _], isKey: Boolean): Unit = ()


  def deserialize(s: String, bytes: Array[Byte]): T =  inverted(bytes).get

  override def serializer(): Serializer[T] = new Serializer[T] {
    override def configure(configs: JMap[String, _], isKey: Boolean): Unit = ()

    override def serialize(topic: String, data: T): Array[Byte] = injection(data)

    override def close(): Unit = ()
  }

  override def deserializer(): Deserializer[T] = new Deserializer[T] {
    override def configure(configs: JMap[String, _], isKey: Boolean): Unit = ()

    override def deserialize(topic: String, data: Array[Byte]): T = inverted(data).get

    override def close(): Unit = ()
  }

  override def close(): Unit = ()

}
