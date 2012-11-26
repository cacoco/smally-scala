package io.angstrom.smally.util

import com.twitter.finagle.redis.Client
import com.twitter.finagle.redis.util._

object Counter {
  private val InitialValue = 10000000L; // (ten million)
  private val CounterKey = "smally:counter"

  def apply(client: Option[Client]) = new Counter(client.get)
}

class Counter(client: Client) {

  import Counter._

  private val _key = StringToChannelBuffer(CounterKey)

  def next: Long = {
    val current = client.get(_key)() match {
      case Some(n) =>
        NumberFormat.toLong(new String(n.array))
      case None =>
        InitialValue
    }
    val next = current + 1
    client.set(_key, StringToChannelBuffer(next.toString))
    next
  }
}
