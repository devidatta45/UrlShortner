package com.xite

import com.xite.repositories.ShortUrlStorage
import com.xite.services.UrlStatisticsServiceImpl
import com.xite.utils.Generators._
import com.xite.utils.InMemoryShortUrlStorage
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import org.scalatest.{OptionValues, Suite}
import zio.Runtime
import zio.internal.Platform

class UrlStatisticsServiceSpec extends AnyFlatSpec with Suite with should.Matchers with OptionValues {

  object TestEnvironment extends ShortUrlStorage {
    override val shortUrlStorage: ShortUrlStorage.Service = InMemoryShortUrlStorage.shortUrlStorage
  }

  val myRuntime: Runtime[ShortUrlStorage] = Runtime(TestEnvironment, Platform.default)

  behavior of "UrlStatisticsService"

  it should "increment count based on hits" in {
    val shortCode = shortCodeGen.sample.value
    val hitResult = UrlStatisticsServiceImpl.urlStatisticsService.hit(shortCode)
    myRuntime.unsafeRun(hitResult) shouldBe 1
    val againHitResult = UrlStatisticsServiceImpl.urlStatisticsService.hit(shortCode)
    myRuntime.unsafeRun(againHitResult) shouldBe 2
    val retrievedResult = UrlStatisticsServiceImpl.urlStatisticsService.getStatistics(shortCode)
    myRuntime.unsafeRun(retrievedResult).value shouldBe 2
  }

  it should "return None for non existing keys" in {
    val shortCode = shortCodeGen.sample.value
    val retrievedResult = UrlStatisticsServiceImpl.urlStatisticsService.getStatistics(shortCode)
    myRuntime.unsafeRun(retrievedResult) shouldBe None
  }
}
