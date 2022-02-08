package com.xite.repositories.impl

import akka.actor.ActorSystem
import com.xite.models.{RedisError, ShortUrlDomainError}
import com.xite.repositories.ShortUrlStorage
import io.circe._
import io.circe.parser._
import io.circe.syntax._
import redis.RedisClient
import zio.{IO, ZIO}

class RedisShortUrlStorage(host: String, port: Int)(implicit val actorSystem: ActorSystem) extends ShortUrlStorage {

  val redis = RedisClient(host = host, port = port)

  override val shortUrlStorage: ShortUrlStorage.Service = new ShortUrlStorage.Service {
    override def save[T](key: String, obj: T)(implicit encoder: Encoder[T]): IO[ShortUrlDomainError, Boolean] = {
      ZIO.fromFuture {
        implicit ec => redis.set(key, obj.asJson.noSpaces)
      }.mapError(error => RedisError(error.getMessage))
    }

    override def get[T](key: String)(implicit decoder: Decoder[T]): IO[ShortUrlDomainError, Option[T]] = {
      ZIO.fromFuture {
        implicit ec => redis.get(key).map(_.flatMap(v => decode[T](v.utf8String).toOption))
      }.mapError(error => RedisError(error.getMessage))
    }

    override def exists(key: String): IO[ShortUrlDomainError, Boolean] = {
      ZIO.fromFuture {
        implicit ec => redis.exists(key)
      }.mapError(error => RedisError(error.getMessage))
    }

    override def increment(key: String): IO[ShortUrlDomainError, Long] = {
      ZIO.fromFuture {
        implicit ec => redis.incr(key)
      }.mapError(error => RedisError(error.getMessage))
    }
  }
}
