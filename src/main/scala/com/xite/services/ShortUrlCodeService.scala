package com.xite.services

import com.xite.models.{ShortUrlDomainError, UrlParsingError}
import zio.ZIO
import com.xite.utils._
import cats.syntax.either._

trait ShortUrlCodeService {
  val shortUrlCodeService: ShortUrlCodeService.Service
}

object ShortUrlCodeService {
  trait Service {
    def createShortUrl(url: String): ZIO[Any, ShortUrlDomainError, String]
  }
}

object ShortUrlCodeServiceImpl extends ShortUrlCodeService {
  override val shortUrlCodeService: ShortUrlCodeService.Service = (url: String) => {
    val shortUrlEither = Either.catchNonFatal {
      val md5Bytes = md5(url)
      val byteArr = md5Bytes.slice(12, 16)

      val builder = new StringBuilder()
      byteArr.foreach { r =>
        val bytes = Array(r)
        isValidUTF8(bytes) match {
          case Some(charBuffer) => builder.append(charBuffer)
          case None => builder.append("\\x%02X ".format(r))
        }
      }
      val utf8Str = builder.toString().replaceAll(" ", "")
      base64(utf8Str).dropRight(2)
    }.leftMap { error =>
      UrlParsingError(error.getMessage)
    }

    ZIO.fromEither(shortUrlEither)
  }
}