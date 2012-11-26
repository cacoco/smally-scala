package io.angstrom.scala.smally

import config.ServerConfig

import com.twitter.conversions.time._
import com.twitter.ostrich.admin.{Service => OstrichService}
import com.twitter.finagle.builder.ClientBuilder
import com.twitter.finagle.redis.{TransactionalClient, Redis}
import com.twitter.finagle.http.Request


class SmallyServer(config: ServerConfig) extends OstrichService with SmallyService {
  require(config != null, "Config must be specified")

  val port = config.port.value
  val name = config.name.value

  val redisHost = config.redisHost.value
  val redisPort = config.redisPort.value

  override def start() {
    client = Some(TransactionalClient(
      ClientBuilder()
        .codec(new Redis())
        .hosts("%s:%s".format(redisHost, redisPort))
        .hostConnectionLimit(1)
        .buildFactory()))
    client.get.flushDB()()

    server = Some(serverSpec.build(service))
  }

  override def shutdown() {
    log.debug("Shutdown requested")
    server match {
      case None =>
        log.warning("Server not started, refusing to shutdown")
      case Some(server) =>
        try {
          server.close(0.seconds)
          log.info("Shutdown complete")
        } catch {
          case e: Exception =>
            log.error(e, "Error shutting down server %s listening on port %d", name, port)
        }
    } // server match
  }

  override def reload() {
    log.info("Reload requested, doing nothing but I could re-read the config or something")
  }
}
