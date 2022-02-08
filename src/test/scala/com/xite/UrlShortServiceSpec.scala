package com.xite

import com.xite.models.{Statistics, URIRequest, URIResponse}
import com.xite.repositories.ShortUrlStorage
import com.xite.services.{ShortUrlCodeService, ShortUrlCodeServiceImpl, UrlShortService, UrlStatisticsService, UrlStatisticsServiceImpl}
import com.xite.utils.InMemoryShortUrlStorage
import org.scalatest.{OptionValues, Suite}
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should
import zio.Runtime
import zio.internal.Platform
import com.xite.utils.Generators._

class UrlShortServiceSpec extends AnyFlatSpec with Suite with should.Matchers with OptionValues {

  object TestEnvironment extends ShortUrlCodeService with UrlStatisticsService with ShortUrlStorage {
    override val shortUrlStorage: ShortUrlStorage.Service = InMemoryShortUrlStorage.shortUrlStorage
    override val shortUrlCodeService: ShortUrlCodeService.Service = ShortUrlCodeServiceImpl.shortUrlCodeService
    override val urlStatisticsService: UrlStatisticsService.Service = UrlStatisticsServiceImpl.urlStatisticsService
  }

  val myRuntime: Runtime[ShortUrlCodeService with UrlStatisticsService with ShortUrlStorage] = Runtime(TestEnvironment, Platform.default)

  behavior of "UrlShortService"

  it should "shorten url" in {
    val uriRequest = URIRequest("https://www.lala.com/")
    val shortenResult = UrlShortService.service.shortenUrl(uriRequest)
    myRuntime.unsafeRun(shortenResult) shouldBe URIResponse("XX9ceEQxcQ")
  }
  it should "get the short url" in {
    val uriRequest = URIRequest(urlGen.sample.value)
    val shortenResult = UrlShortService.service.shortenUrl(uriRequest)

    val response = myRuntime.unsafeRun(shortenResult)
    val retrievedResult = UrlShortService.service.getShortUrl(response.short)

    myRuntime.unsafeRun(retrievedResult).value shouldBe uriRequest.uri
  }

  it should "get the statistics correctly" in {
    val uriRequest = URIRequest(statisticsGen.sample.value)
    val shortenResult = UrlShortService.service.shortenUrl(uriRequest)

    val response = myRuntime.unsafeRun(shortenResult)
    val getResult = UrlShortService.service.getShortUrl(response.short)
    myRuntime.unsafeRun(getResult)
    val statisticsResult = UrlShortService.service.getStatistics(response.short)
    myRuntime.unsafeRun(statisticsResult).value shouldBe Statistics(1L)

    val againGetResult = UrlShortService.service.getShortUrl(response.short)
    myRuntime.unsafeRun(againGetResult)

    val increasedResult = UrlShortService.service.getStatistics(response.short)
    myRuntime.unsafeRun(increasedResult).value shouldBe Statistics(2L)

  }
}
