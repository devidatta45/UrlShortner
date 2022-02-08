package com.xite.utils

import akka.http.scaladsl.server.Directives
import com.xite.models.{CodeDoesNotExist, RedisError, ShortUrlDomainError, ShortUrlServiceError, UrlParsingError}
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.syntax._
import io.circe.generic.auto._
import akka.http.scaladsl.model._

object DomainErrorMapper extends Directives with FailFastCirceSupport {
  val domainErrorMapper: ErrorMapper[ShortUrlDomainError] = {
    case UrlParsingError(message, code) =>
      HttpResponse(StatusCodes.BadRequest, entity = HttpEntity(MediaTypes.`application/json`, GenericErrorResponseBody(code, message).asJson.toString()))
    case ShortUrlServiceError(message, code) =>
      HttpResponse(StatusCodes.InternalServerError, entity = HttpEntity(MediaTypes.`application/json`, GenericErrorResponseBody(code, message).asJson.toString()))
    case RedisError(message, code) =>
      HttpResponse(StatusCodes.InternalServerError, entity = HttpEntity(MediaTypes.`application/json`, GenericErrorResponseBody(code, message).asJson.toString()))
    case CodeDoesNotExist(message, code) =>
      HttpResponse(StatusCodes.NotFound, entity = HttpEntity(MediaTypes.`application/json`, GenericErrorResponseBody(code, message).asJson.toString()))
  }

  case class GenericErrorResponseBody(code: String, message: String, errorDetails: Option[String] = None)

}
