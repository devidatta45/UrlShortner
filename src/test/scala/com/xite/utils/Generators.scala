package com.xite.utils

import org.scalacheck.Gen

object Generators {

  def shortCodeGen = Gen.alphaUpperStr

  def urlGen = Gen.oneOf("https://www.lala.com/", "https://www.google.com/", "https://www.facebook.com/")
  def statisticsGen = Gen.oneOf("https://www.gmail.com/", "https://www.twitter.com/", "https://www.raja.com/")
}
