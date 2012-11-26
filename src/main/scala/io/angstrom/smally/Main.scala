package io.angstrom.smally

import com.twitter.logging.Logger
import com.twitter.ostrich.admin.{ServiceTracker, RuntimeEnvironment}


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
