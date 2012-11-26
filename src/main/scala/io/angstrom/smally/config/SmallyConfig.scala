package io.angstrom.smally
package config

import com.twitter.ostrich.admin.RuntimeEnvironment
import com.twitter.ostrich.admin.config.ServerConfig
import io.angstrom.smally.SmallyServer

class SmallyConfig extends ServerConfig[SmallyServer] {

  var port = required[Int]
  var name = required[String]

  var redisHost = required[String]
  var redisPort = required[Int]

  def apply(runtime: RuntimeEnvironment): SmallyServer = {
    new SmallyServer(this)
  }
}
