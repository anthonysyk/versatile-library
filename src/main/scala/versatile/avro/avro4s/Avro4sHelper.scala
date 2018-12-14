package versatile.avro.avro4s

import org.apache.avro.Schema
import org.apache.avro.Schema.Parser
import versatile.avro.CreateSchema

object Avro4sHelper {

  import org.apache.avro.generic.GenericData

  val parser = new Schema.Parser()
  def getSchema[T](implicit m: Manifest[T]): Schema = new Parser().parse(CreateSchema.getSchema[T])
  val avroRecord = new GenericData.Record(getSchema)

}
