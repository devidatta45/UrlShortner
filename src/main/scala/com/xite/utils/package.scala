package com.xite

import java.nio.charset.{CharacterCodingException, Charset, StandardCharsets}
import java.nio.{ByteBuffer, CharBuffer}
import java.security.MessageDigest
import java.util.Base64

package object utils {

  def urlSafeEncode64(input: String): String =
    Base64.getUrlEncoder.encodeToString(input.getBytes(StandardCharsets.UTF_8))

  def isValidUTF8(input: Array[Byte]): Option[CharBuffer] = {
    val cs = Charset.forName("UTF-8").newDecoder
    try {
      Some(cs.decode(ByteBuffer.wrap(input)))
    } catch {
      case _: CharacterCodingException => None
    }
  }

  def md5(str: String): Array[Byte] = MessageDigest.getInstance("MD5").digest(str.getBytes)

  def base64(str: String): String = Base64.getEncoder.encodeToString(str.getBytes(StandardCharsets.UTF_8))

  object KeyUtils {
    val UrlKeyTemplate = "xite:url:%s"
    val CodeKeyTemplate = "xite:code:%s"
    val StatsKeyTemplate = "xite:stats:call:%s"

    def codeAsKey(code: String): String = CodeKeyTemplate.format(code)

    def urlAsKey(url: String): String = UrlKeyTemplate.format(url)

    def codeAsStatsKey(code: String): String = StatsKeyTemplate.format(code)
  }
}
