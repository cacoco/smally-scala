package io.angstrom.scala.smally

import com.twitter.util.Future
import com.twitter.finagle.Service
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import com.twitter.finagle.redis.Client
import org.jboss.netty.handler.codec.http.{HttpResponseStatus, DefaultHttpResponse}
import com.twitter.finagle.redis.util.{CBToString, StringToChannelBuffer}
import com.twitter.finagle.http.{Response, Request}
import scala.Some
import util.parsing.json.JSONObject
import com.google.common.net.MediaType

object Respond {
  private val EncodingRadix = 32
  private val key = "url-"
}

class Respond(client: Option[Client], counter: Counter)
  extends Service[Request, Response] {

  import Respond._

  def apply(request: Request) = {
    client match {
      case Some(redis) =>
        request.params.get("url") match {
          case None =>
            val _index = request.getUri().substring(1)
            val _counter = java.lang.Long.valueOf(_index, EncodingRadix)
            redis.get(StringToChannelBuffer("%s%s".format(key, _counter.toString)))() match {
              case Some(a) => handleResponse(Some(CBToString(a)), FOUND)
              case None => handleResponse(None, NOT_FOUND)
            }
          case Some(url) =>
            // set the value in cache
            val _counter = counter.next
            redis.set(StringToChannelBuffer("%s%s".format(key, _counter.toString)),
              StringToChannelBuffer(url))
            val base = request.host getOrElse "localhost"
            handleResponse(Some(toJson("smally-url", "%s/%s".format(base, java.lang.Long.toString(_counter, EncodingRadix)))), OK, Some(MediaType.JSON_UTF_8))
        }
      case None =>
        handleResponse(None, SERVICE_UNAVAILABLE)
    }
  }

  def toJson(key: String, content: String): String = {
    JSONObject(Map(key -> content)).toString()
  }

  def handleResponse(content: Option[String], status: HttpResponseStatus): Future[Response] = {
    handleResponse(content, status, None)
  }

  def handleResponse(content: Option[String], status: HttpResponseStatus, mediaType: Option[MediaType]): Future[Response] = {
    val response = Response(new DefaultHttpResponse(HTTP_1_1, status))
    content match {
      case Some(x) =>
        response.write(x + '\n')
      case None => // do nothing to response
    }
    mediaType match {
      case Some(x) => response.mediaType = x.toString
      case None => // do nothing to response
    }

    Future(response)
  }
}
