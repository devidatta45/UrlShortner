package com.xite

import akka.actor.ActorSystem
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.Materializer
import akka.util.Timeout
import com.xite.repositories.ShortUrlStorage
import com.xite.repositories.impl.RedisShortUrlStorage
import com.xite.routes.ShortUrlRoutes
import com.xite.services.{ShortUrlCodeService, ShortUrlCodeServiceImpl, UrlStatisticsService, UrlStatisticsServiceImpl}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.Duration
import com.xite.config.RedisConfig._

trait AppContext extends Directives {
  implicit def executionContext: ExecutionContext = system.dispatcher

  implicit def materializer: Materializer

  implicit def system: ActorSystem

  implicit def timeout: Timeout = Duration.fromNanos(100000)

  // Live environment for the application with all required dependency
  object LiveEnvironment extends ShortUrlCodeService with UrlStatisticsService with ShortUrlStorage {
    override val shortUrlStorage: ShortUrlStorage.Service = new RedisShortUrlStorage(redisHost, redisPort).shortUrlStorage
    override val shortUrlCodeService: ShortUrlCodeService.Service = ShortUrlCodeServiceImpl.shortUrlCodeService
    override val urlStatisticsService: UrlStatisticsService.Service = UrlStatisticsServiceImpl.urlStatisticsService
  }

  lazy val routes: Route = new ShortUrlRoutes(LiveEnvironment).routes
}
