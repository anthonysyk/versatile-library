package versatile.web

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse, StatusCodes}
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContextExecutor, Future}

trait WebClient {

  implicit val system: ActorSystem = ActorSystem()
  implicit val materializer: ActorMaterializer = ActorMaterializer()
  // needed for the future flatMap/onComplete in the end
  implicit val executionContext: ExecutionContextExecutor = system.dispatcher

  val http = Http()

  private def parseHttpResponse(eventuallyHttpResponse: Future[HttpResponse]): Future[Either[String, String]] = {
    eventuallyHttpResponse.flatMap {
      case HttpResponse(StatusCodes.OK, _, entity, _) =>
        Unmarshal(entity).to[String].map(Right(_))
      case value@_ =>
        Unmarshal(value.entity).to[String].map { response =>
          Left(s"Unexpected status code ${value.status} - $response")
        }
    }
  }

  private def buildGetUrl(url: String, args: Map[String, String]): String = {
    val argsParsed: String = args.map { case (key, value) => s"$key=$value" }.mkString("&")
    Seq(url, argsParsed).mkString("?")
  }

  def doGet(url: String, args: Map[String, String] = Map.empty[String, String], headers: Map[String, String] = Map.empty): Future[Either[String, String]] = {

    val parsedUrl = buildGetUrl(url, args)
    parseHttpResponse(http.singleRequest(HttpRequest(uri = parsedUrl)))

  }

  def doPost = ???

}