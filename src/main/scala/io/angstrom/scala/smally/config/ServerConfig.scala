package io.angstrom.scala.smally
package config

import io.angstrom.scala.smally.SmallyServer
import com.twitter.util.Config

class ServerConfig extends Config[SmallyServer] {

  var port = required[Int]
  var name = required[String]

  var redisHost = required[String]
  var redisPort = required[Int]

  def apply() = {
    new SmallyServer(this)
  }
}
