package io.angstrom.scala.smally


object HttpServer {
//  val log = Logger.get(getClass.getName)
//
//  val Properties = new java.util.Properties()
//  val in = getClass.getResourceAsStream("server.properties")
//  try { Properties.load(in) } finally { in.close() }

//  val client = TransactionalClient(
//    ClientBuilder()
//      .codec(new Redis())
//      .hosts("%s:%s".format(Properties.getProperty("redis.host"), Properties.getProperty("redis.port")))
//      .hostConnectionLimit(1)
//      .buildFactory())
//  client.flushDB()()

  def main(args: Array[String]) {
//    val handleExceptions = new HandleExceptions
//    val respond = new Respond(client, Counter(client))
//    val smallyService: Service[Request, Response] = handleExceptions andThen respond
//
//    val service: Service[Request, Response] = handleExceptions andThen respond
//
//    val runtime = RuntimeEnvironment(this, args)
//    val server = runtime.loadRuntimeConfig[SmallyServer]()
//    log.info("Starting smally server.")
//
//    try {
//      server.start()
//    } catch {
//      case e: Exception =>
//      log.error(e, "Unexpected exception: %s", e.getMessage)
//      System.exit(0)
//    }
//
//    val server: Server = ServerBuilder()
//      .codec(RichHttp[Request](Http()))
//      .bindTo(new InetSocketAddress(8080))
//      .name("httpserver")
//      .build(smallyService)
  }
}
