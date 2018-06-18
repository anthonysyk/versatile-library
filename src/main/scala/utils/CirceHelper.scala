package utils


import generic.Pure
import io.circe.Json

import scala.reflect.ClassTag

object CirceHelper {

  implicit val pureString = new Pure[String] {
    override def empty: String = ""
  }

  implicit val pureJson = new Pure[io.circe.Json] {
    override def empty: Json = Json.Null
  }

  implicit def pureArr[T: ClassTag](implicit p: Pure[T]) = new Pure[Array[T]] {
    override def empty: Array[T] = Array.empty[T]
  }

  implicit class RichEitherCirce[L <: io.circe.Error, R](either: Either[L, R]) {

    def getRight(implicit pure: Pure[R]): R = either.right.toOption.getOrElse(pure.empty)
  }

}
