package io.angstrom.scala.smally

import com.twitter.finagle.builder.{ServerBuilder, Server}
import com.twitter.finagle.http.{Response, Http, Request, RichHttp}
import java.net.InetSocketAddress
import com.twitter.finagle.stats.OstrichStatsReceiver
import com.twitter.finagle.redis.Client
import com.twitter.logging.Logger
import com.twitter.finagle.Service

trait SmallyService {
  val port: Int
  val name: String
  val redisHost: String
  val redisPort: Int

  val log = Logger.get(getClass)

  var server: Option[Server] = None
  var client: Option[Client] = None

  // Don't initialize until after mixed in by another class
  lazy val handleExceptions = new HandleExceptions
  lazy val respond = new Respond(client, Counter(client))
  lazy val service: Service[Request, Response] = handleExceptions andThen respond

  lazy val serverSpec = ServerBuilder()
    .codec(RichHttp[Request](Http()))
    .bindTo(new InetSocketAddress(port))
    .name(name)
    .reportTo(new OstrichStatsReceiver)
}
