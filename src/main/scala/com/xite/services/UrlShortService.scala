package com.xite.services

import com.xite.models._
import com.xite.repositories.ShortUrlStorage
import com.xite.utils.KeyUtils._
import com.xite.utils._
import zio.ZIO

trait UrlShortService {

  def shortenUrl(request: URIRequest): ZIO[ShortUrlCodeService with UrlStatisticsService with ShortUrlStorage, ShortUrlDomainError, URIResponse]

  def getShortUrl(code: String): ZIO[ShortUrlCodeService with UrlStatisticsService with ShortUrlStorage, ShortUrlDomainError, Option[String]]

  def getStatistics(code: String): ZIO[ShortUrlCodeService with UrlStatisticsService with ShortUrlStorage, ShortUrlDomainError, Option[Statistics]]
}

object UrlShortService {
  val service: UrlShortService = new UrlShortService {
    override def shortenUrl(request: URIRequest): ZIO[ShortUrlCodeService with UrlStatisticsService with ShortUrlStorage, ShortUrlDomainError, URIResponse] = {
      for {
        codeOpt <- getByUrl(request.uri)
        result <- codeOpt match {
          case Some(code) => ZIO {
            URIResponse(code)
          }.mapError(err => ShortUrlServiceError(err.getMessage))
          case None => save(request.uri)
        }
      } yield result
    }

    override def getShortUrl(code: String): ZIO[ShortUrlCodeService with UrlStatisticsService with ShortUrlStorage, ShortUrlDomainError, Option[String]] = {
      for {
        ifExists <- ZIO.accessM[ShortUrlStorage](_.shortUrlStorage.exists(codeAsKey(code)))
        _ <- ZIO.fromEither(Either.cond(ifExists, (), CodeDoesNotExist("Short code does not exist")))
        result <- ZIO.accessM[ShortUrlStorage](_.shortUrlStorage.get[String](codeAsKey(code)))
        _ <- result match {
          case Some(_) => ZIO.accessM[UrlStatisticsService with ShortUrlStorage](_.urlStatisticsService.hit(codeAsStatsKey(code)))
          case None => ZIO.unit
        }
      } yield result
    }

    override def getStatistics(code: String): ZIO[ShortUrlCodeService with UrlStatisticsService with ShortUrlStorage, ShortUrlDomainError, Option[Statistics]] = {
      for {
        statsOpt <- ZIO.accessM[UrlStatisticsService with ShortUrlStorage](_.urlStatisticsService.getStatistics(codeAsStatsKey(code)))
        result = statsOpt match {
          case Some(callCount) => Some(Statistics(callCount))
          case None => None
        }
      } yield result
    }
  }

  private def getByUrl(url: String): ZIO[ShortUrlStorage, ShortUrlDomainError, Option[String]] = {
    ZIO.accessM[ShortUrlStorage](_.shortUrlStorage.get[String](urlAsKey(urlSafeEncode64(url))))
  }

  private def save(url: String): ZIO[ShortUrlStorage with ShortUrlCodeService, ShortUrlDomainError, URIResponse] =
    for {
      code <- ZIO.accessM[ShortUrlCodeService](_.shortUrlCodeService.createShortUrl(url))
      _ <- ZIO.accessM[ShortUrlStorage](_.shortUrlStorage.save[String](codeAsKey(code), url))
      _ <- ZIO.accessM[ShortUrlStorage](_.shortUrlStorage.save[String](urlAsKey(urlSafeEncode64(url)), code))
    } yield URIResponse(code)
}