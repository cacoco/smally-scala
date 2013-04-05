package io.angstrom.smally

import com.twitter.ostrich.admin.RuntimeEnvironment
import com.twitter.ostrich.admin.config.ServerConfig

class Config extends ServerConfig[Server] {

  var port = required[Int]
  var name = required[String]

  var redisHost = required[String]
  var redisPort = required[Int]

  def apply(runtime: RuntimeEnvironment): Server = {
    new Server(this)
  }
}
