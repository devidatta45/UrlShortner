package com.xite

import com.xite.models.ShortUrlDomainError
import com.xite.services.ShortUrlCodeServiceImpl
import org.scalatest.Suite
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import zio.internal.Platform
import zio.{Runtime, ZIO}

class ShortUrlCodeServiceSpec extends AnyFlatSpec with Suite with should.Matchers {
  object TestEnvironment

  val myRuntime: Runtime[Any] = Runtime(TestEnvironment, Platform.default)

  behavior of "ShortUrlCodeService"

  it should "correctly parse the code" in {
    val key = "XX9ceEQxcQ"
    val url = "https://www.lala.com/"

    val asyncResult: ZIO[Any, ShortUrlDomainError, String] = ShortUrlCodeServiceImpl.shortUrlCodeService.createShortUrl(url)
    myRuntime.unsafeRun(asyncResult) shouldBe key
  }
}
