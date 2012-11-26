package io.angstrom.smally

import org.scalamock.scalatest.MockFactory
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.junit.JUnitRunner
import org.junit.runner.RunWith
import org.scalatest.WordSpec

@RunWith(classOf[JUnitRunner])
class RespondSpec extends WordSpec with MockFactory with ShouldMatchers {

  "Fully Qualify URL" should {
    "add http://" in {
      val url = "www.twitter.com"

      Respond.fullyQualifyUrl(url) should equal("%s%s".format("http://", url))
    }

    "keep http://" in {
      val url = "http://www.twitter.com"
      Respond.fullyQualifyUrl(url) should equal(url)
    }

    "keep https://" in {
      val url = "https://www.twitter.com"
      Respond.fullyQualifyUrl(url) should equal(url)
    }
  }

  "To Json" should {
    "add keys to map" in {
      val fields = List("location" -> "www.twitter.com", "food" -> "bacon")

      Respond.toJson(fields:_*) should equal("{\"location\" : \"www.twitter.com\", \"food\" : \"bacon\"}")
    }
  }
}
