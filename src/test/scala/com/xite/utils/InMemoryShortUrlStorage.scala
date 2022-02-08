package com.xite.utils

import com.xite.models.ShortUrlDomainError
import com.xite.repositories.ShortUrlStorage
import com.xite.utils.InMemoryShortUrlStorage.State
import io.circe.parser.decode
import io.circe.syntax._
import io.circe.{Decoder, Encoder}
import zio.Runtime.default
import zio.{IO, Ref}

trait InMemoryShortUrlStorage extends ShortUrlStorage {
  override val shortUrlStorage: ShortUrlStorage.Service = new ShortUrlStorage.Service {
    val ref: Ref[InMemoryShortUrlStorage.State] = default.unsafeRun(Ref.make(State(Map())))

    override def save[T](key: String, obj: T)(implicit encoder: Encoder[T]): IO[ShortUrlDomainError, Boolean] = {
      ref.modify(_.save(key, obj.asJson.noSpaces))
    }

    override def get[T](key: String)(implicit decoder: Decoder[T]): IO[ShortUrlDomainError, Option[T]] = {
      for {
        optValue <- ref.modify(_.get(key))
        result = optValue.flatMap(value => decode[T](value).toOption)
      } yield result

    }

    override def exists(key: String): IO[ShortUrlDomainError, Boolean] = {
      ref.modify(_.exists(key))
    }

    override def increment(key: String): IO[ShortUrlDomainError, Long] = {
      ref.modify(_.increment(key))
    }
  }
}

object InMemoryShortUrlStorage extends InMemoryShortUrlStorage {
  final case class State(storage: Map[String, String]) {
    def save(key: String, value: String): (Boolean, State) = {
      val newMap = storage + (key -> value)
      (true, copy(storage = newMap))
    }

    def get(key: String): (Option[String], State) = {
      val valueOpt = storage.get(key)
      (valueOpt, this)
    }

    def exists(key: String): (Boolean, State) = {
      val ifExists = storage.contains(key)
      (ifExists, this)
    }

    def increment(key: String): (Long, State) = {
      val valueOpt = storage.get(key)
      valueOpt.map { value =>
        val incrementedValue = value.toLong + 1
        val stringValue = incrementedValue.toString
        val newMap = storage + (key -> stringValue)
        (incrementedValue, copy(storage = newMap))
      }.getOrElse {
        val newMap = storage + (key -> 1L.toString)
        (1L, copy(storage = newMap))
      }
    }
  }
}