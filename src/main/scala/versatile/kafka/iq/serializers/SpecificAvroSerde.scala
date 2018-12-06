package versatile.kafka.iq
package serializers

import java.util

import com.twitter.bijection.Injection
import com.twitter.bijection.avro.SpecificAvroCodecs
import org.apache.avro.Schema
import org.apache.kafka.common.serialization.{Deserializer, Serde, Serializer}

class SpecificAvroSerde[T <: org.apache.avro.specific.SpecificRecordBase](schema: Schema) extends Serde[T] {

  val recordInjection: Injection[T, Array[Byte]] = SpecificAvroCodecs.toBinary(schema)

  val avroSerde: SpecificAvroSerDeserializer[T] = new SpecificAvroSerDeserializer(recordInjection)

  override def serializer(): Serializer[T] = avroSerde.serializer()

  override def deserializer(): Deserializer[T] = avroSerde.deserializer()

  override def configure(configs: util.Map[String, _], isKey: Boolean): Unit = ()

  override def close(): Unit = ()
}

