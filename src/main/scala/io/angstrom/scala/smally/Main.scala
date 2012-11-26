package io.angstrom.scala.smally

import com.twitter.ostrich.admin.{ServiceTracker, RuntimeEnvironment}
import com.twitter.logging.Logger


object Main {
  val log = Logger.get(getClass)

  def main(args: Array[String]) {
    val runtime = RuntimeEnvironment(this, args)
    val server = runtime.loadRuntimeConfig[SmallyServer]()
    try {
      log.info("Starting service")
      server.start()
    } catch {
      case e: Exception =>
        log.error(e, "Failed starting service, exiting")
        ServiceTracker.shutdown()
        System.exit(1)
    }
  }
}
