package versatile.future

import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.duration.Duration

object Timed {

  def timed[A](block: => Future[A])(implicit e: ExecutionContext): Future[(Duration, A)] = {
    val start = System.nanoTime()
    block.map { result =>
      val end = System.nanoTime()
      (Duration.fromNanos(end - start), result)
    }
  }

}
