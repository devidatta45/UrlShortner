package com.xite.routes

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Directives
import com.xite.models.{ShortUrlDomainError, URIRequest}
import com.xite.repositories.ShortUrlStorage
import com.xite.services.{ShortUrlCodeService, UrlShortService, UrlStatisticsService}
import com.xite.utils._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import zio.internal.Platform

import scala.concurrent.ExecutionContext

class ShortUrlRoutes(env: ShortUrlCodeService with UrlStatisticsService with ShortUrlStorage)(
  implicit executionContext: ExecutionContext,
  system: ActorSystem,
) extends ZioToRoutes[ShortUrlCodeService with UrlStatisticsService with ShortUrlStorage] with Directives with FailFastCirceSupport {
  override def environment: ShortUrlCodeService with UrlStatisticsService with ShortUrlStorage = env

  override def platform: Platform = Platform.default

  private lazy val service = UrlShortService.service

  implicit val errorMapper: ErrorMapper[ShortUrlDomainError] = DomainErrorMapper.domainErrorMapper

  private lazy val cors = new CORSHandler {}

  val routes = cors.corsHandler {
    pathPrefix("api" / "shorten") {
      post {
        entity(as[URIRequest]) { uRIRequest =>
          for {
            response <- service.shortenUrl(uRIRequest)
          } yield complete(
            response
          )
        }
      }
    } ~ pathPrefix("api" / Segment) { shortCode =>
      get {
        for {
          response <- service.getShortUrl(shortCode)
        } yield complete(
          response
        )
      }
    } ~ pathPrefix("apis" / "stats" / Segment) { shortCode =>
      get {
        for {
          response <- service.getStatistics(shortCode)
        } yield complete(
          response
        )
      }
    }
  }
}
