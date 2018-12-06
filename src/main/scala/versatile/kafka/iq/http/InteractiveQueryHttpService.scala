package versatile.kafka.iq.http
package http

/**
  * Copyright (C) 2018 Lightbend Inc. <https://www.lightbend.com>
  */

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.ExceptionHandler
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Flow
import org.apache.kafka.streams.state.HostInfo

import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport


/**
  * The interactive http query service. Offers APIs to start and stop the service.
  */
abstract class InteractiveQueryHttpService(hostInfo: HostInfo,
                                           actorSystem: ActorSystem,
                                           actorMaterializer: ActorMaterializer,
                                           ec: ExecutionContext)
  extends Directives with FailFastCirceSupport with LazyLogging {

  implicit val _actorSystem = actorSystem
  implicit val _actorMaterializer = actorMaterializer
  implicit val _ec = ec

  val myExceptionHandler = ExceptionHandler {
    case ex: Exception =>
      extractUri { uri =>
        logger.error(s"Request to $uri could not be handled normally", ex)
        complete(HttpResponse(InternalServerError, entity = "Request Failed!"))
      }
  }

  // define the routes
  val routes: Flow[HttpRequest, HttpResponse, Any]
  var bindingFuture: Future[Http.ServerBinding] = _


  // start the http server
  def start(): Unit = {
    bindingFuture = Http().bindAndHandle(routes, hostInfo.host, hostInfo.port)

    bindingFuture.onComplete {
      case Success(serverBinding) =>
        logger.info(s"Server bound to ${serverBinding.localAddress} ")

      case Failure(ex) =>
        logger.error(s"Failed to bind to ${hostInfo.host}:${hostInfo.port}!", ex)
        actorSystem.terminate()
    }
  }


  // stop the http server
  def stop(): Unit = {
    logger.info("Stopping the http server")
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => actorSystem.terminate())
  }
}
