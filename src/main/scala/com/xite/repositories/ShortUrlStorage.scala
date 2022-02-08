package com.xite.repositories

import com.xite.models.ShortUrlDomainError
import zio.IO
import io.circe._

trait ShortUrlStorage {
  val shortUrlStorage: ShortUrlStorage.Service
}

object ShortUrlStorage {
  trait Service {
    def save[T](key: String, obj: T)(implicit encoder: Encoder[T]): IO[ShortUrlDomainError, Boolean]

    def get[T](key: String)(implicit decoder: Decoder[T]): IO[ShortUrlDomainError, Option[T]]

    def exists(key: String): IO[ShortUrlDomainError, Boolean]

    def increment(key: String): IO[ShortUrlDomainError, Long]
  }
}