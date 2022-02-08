package com.xite.models

import com.xite.models.ShortUrlDomainError._

sealed trait ShortUrlDomainError extends Product with Serializable {
  val message: String
  val code: String
}

case class UrlParsingError(override val message: String, override val code: String = URL_PARSING_FAILED) extends ShortUrlDomainError
case class ShortUrlServiceError(override val message: String, override val code: String = SHORT_URL_SERVICE_ERROR) extends ShortUrlDomainError

case class RedisError(override val message: String, override val code: String = URL_PARSING_FAILED) extends ShortUrlDomainError
case class CodeDoesNotExist(override val message: String, override val code: String = CODE_DOES_NOT_EXIST) extends ShortUrlDomainError

object ShortUrlDomainError {
  val URL_PARSING_FAILED = "url_parsing_failed"
  val SHORT_URL_SERVICE_ERROR = "short_url_service_failed"
  val REDIS_ERROR = "redis_error"
  val CODE_DOES_NOT_EXIST = "code_does_not_exist"
}