package com.xite.config

import com.typesafe.config.{Config, ConfigFactory}

object RedisConfig {
  val config: Config = ConfigFactory.load()
  val redisHost = config.getString("redis.host")
  val redisPort = config.getInt("redis.port")
}
