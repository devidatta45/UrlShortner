package com.xite.services

import com.xite.models.ShortUrlDomainError
import com.xite.repositories.ShortUrlStorage
import zio.ZIO

trait UrlStatisticsService {
  val urlStatisticsService: UrlStatisticsService.Service
}

object UrlStatisticsService {
  trait Service {
    def hit(key: String): ZIO[ShortUrlStorage, ShortUrlDomainError, Long]

    def getStatistics(key: String): ZIO[ShortUrlStorage, ShortUrlDomainError, Option[Long]]
  }
}

object UrlStatisticsServiceImpl extends UrlStatisticsService {
  override val urlStatisticsService: UrlStatisticsService.Service = new UrlStatisticsService.Service {
    override def hit(key: String): ZIO[ShortUrlStorage, ShortUrlDomainError, Long] = {
      ZIO.accessM[ShortUrlStorage](_.shortUrlStorage.increment(key))
    }

    override def getStatistics(key: String): ZIO[ShortUrlStorage, ShortUrlDomainError, Option[Long]] = {
      ZIO.accessM[ShortUrlStorage](_.shortUrlStorage.get[Long](key))
    }
  }
}