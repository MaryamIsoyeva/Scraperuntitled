package classes


import java.time.{LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import scrap._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.util.{Success, Try}

class ItsiderScraper extends ScraperHasNextPage {
  override val name: String = "Itsider"

  override def parsePage(pageUrl: String): Try[PaginetedArticlesPage] = Try {
    val browser = JsoupBrowser()
    val doc = browser.get(pageUrl)
    println(pageUrl)
    val list = for {
      i <- doc >> elementList("article")
      time = Try {
        LocalDateTime.parse((i >> element("time")).attr("datetime"), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
      }
    } yield ArticleRef((i >> element(".post-url")).attr("href"),
      time match {
        case Success(date) => date
        case _ => LocalDateTime.MIN
      })
    PaginetedArticlesPage(list, (doc >?> element(".next")).map(_.attr("href")))
  }

  override def getContent(url: String): Try[Article] = Try {
    val browser = JsoupBrowser()
    val doc = browser.get(url)
    println(url)
    val content = (doc >> element("article") >> elementList("p")).map(_.text).mkString(" ")
    val date = LocalDateTime.parse((doc >> element("time")).attr("datetime"), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
    val title = (doc >> element(".post-title")).text
    Article(None, url, title, date, content)


  }
}
