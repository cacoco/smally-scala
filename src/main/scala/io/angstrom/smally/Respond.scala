package io.angstrom.smally

import com.google.common.net.MediaType
import com.twitter.finagle.Service
import com.twitter.finagle.http.{Response, Request}
import com.twitter.finagle.redis.Client
import com.twitter.finagle.redis.util.{CBToString, StringToChannelBuffer}
import com.twitter.util.Future
import org.jboss.netty.handler.codec.http.HttpResponseStatus._
import org.jboss.netty.handler.codec.http.HttpVersion.HTTP_1_1
import org.jboss.netty.handler.codec.http.{HttpResponseStatus, DefaultHttpResponse}
import scala.Some
import scala.util.parsing.json.JSONObject
import util.Counter


object Respond {
  private val EncodingRadix = 32
  private val key = "url-"

  protected[smally] def fullyQualifyUrl(url: String): String = {
    if (!(url.startsWith("http"))) {
      "http://"+url
    } else {
      url
    }
  }

  protected[smally] def toJson(fields: (String, String)*): String = {
    val _map = fields.foldLeft(Map[String, String]())((m, t) => m + t)
    JSONObject(_map).toString()
  }

  private[Respond] def handleResponse(
    status: HttpResponseStatus
  ): Future[Response] = {
    handleResponse(status, None, None)
  }

  private[Respond] def handleResponse(
    status: HttpResponseStatus,
    headers: (String, String)*
  ): Future[Response] = {
    handleResponse(status, None, None, headers:_*)
  }

  private[Respond] def handleResponse(
    status: HttpResponseStatus,
    content: Option[String],
    mediaType: Option[MediaType],
    headers: (String, String)*
  ): Future[Response] = {
    val response = Response(new DefaultHttpResponse(HTTP_1_1, status))
    for (content <- content) { response.write(content + '\n') }
    for (mediaType <- mediaType) { response.mediaType = mediaType.toString }
    headers.foreach(t => response.addHeader(t._1, t._2))

    Future.value(response)
  }
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
              case Some(a) => handleResponse(FOUND, ("Location" -> CBToString(a)))
              case None => handleResponse(NOT_FOUND)
            }
          case Some(url) =>
            // set the value in cache
            val _counter = counter.next
            redis.set(StringToChannelBuffer("%s%s".format(key, _counter.toString)),
              StringToChannelBuffer(url))
            val base = request.host getOrElse "localhost"
            handleResponse(OK,
              Some(toJson("smally-url" -> "%s/%s".format(base, java.lang.Long.toString(_counter, EncodingRadix)))),
              Some(MediaType.JSON_UTF_8))
        }
      case None =>
        handleResponse(SERVICE_UNAVAILABLE)
    }
  }
}
