package classes

import java.time.{LocalDateTime, ZoneOffset}
import java.time.format.DateTimeFormatter

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import scrap._
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.util.Try

class SegonyaScraper extends ScraperHasNextPage  {
  override val name: String = "Segonya"
  //st__news-list
  override def parsePage(pageUrl: String): Try[PaginetedArticlesPage] = Try {
    val browser = JsoupBrowser()
    val doc = browser.get(pageUrl)
    val list = for{
      i <- (doc >> element(".st__news-list") >> elementList("li"))
      href = "https://ukr.segodnya.ua" + (i >> element( "a")).attr("href")
      time = (i  >> element("span")).attr("data-timestamp")
      timestamp = LocalDateTime.ofEpochSecond(time.toLong, 0, ZoneOffset.UTC)
    } yield ArticleRef(href, timestamp)
    val next = (doc >?> element(".pagination-next")) match {
      case None => None
      case Some(v) => Some("https://ukr.segodnya.ua" + v.attr("href"))
    }
    PaginetedArticlesPage(list, next)
  }

  override def getContent(url: String): Try[Article] = Try {
    val browser = JsoupBrowser()
    val doc = browser.get(url)
    println("Segodnya " + url)
    val content = (doc >> element(".article__body") >> elementList("p"))
      .map(_.text).drop(1).dropRight(5).mkString(" ")
      .replace("Реклама", "")
    val date = (doc >> element(".time")).attr("data-timestamp")
    val timestamp = LocalDateTime.ofEpochSecond(date.toLong, 0, ZoneOffset.UTC)
    val title = (doc >> element(".article__header_title")).text
    Article(None, url, title, timestamp, content)
  }
}
