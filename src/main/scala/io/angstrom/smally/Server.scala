package io.angstrom.smally

import com.twitter.conversions.time._
import com.twitter.finagle.builder.{ServerBuilder, Server => FinagleServer, ClientBuilder}
import com.twitter.finagle.redis.{Client => RedisClient, TransactionalClient, Redis}
import com.twitter.ostrich.admin.{Service => OstrichService}
import com.twitter.logging.Logger
import io.angstrom.smally.filters.HandleExceptionsFilter
import io.angstrom.smally.util.Counter
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Http, RichHttp, Response, Request}
import java.net.InetSocketAddress
import com.twitter.finagle.stats.OstrichStatsReceiver
import io.angstrom.smally.service.SmallyService


class Server(config: Config) extends OstrichService {
  require(config != null, "Config must be specified")

  val port = config.port.value
  val name = config.name.value

  val redisHost = config.redisHost.value
  val redisPort = config.redisPort.value

  val log = Logger.get(getClass)

  var server: Option[FinagleServer] = None
  var client: Option[RedisClient] = None

  // Don't initialize until after mixed in by another class
  lazy val handleExceptions = new HandleExceptionsFilter
  lazy val respond = new SmallyService(client, Counter(client))
  lazy val service: Service[Request, Response] = handleExceptions andThen respond

  lazy val serverSpec = ServerBuilder()
    .codec(RichHttp[Request](Http()))
    .bindTo(new InetSocketAddress(port))
    .name(name)
    .reportTo(new OstrichStatsReceiver)

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
