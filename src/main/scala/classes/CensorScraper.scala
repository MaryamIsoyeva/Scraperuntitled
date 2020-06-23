package classes

import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import scrap.{Article, ArticleRef, PaginetedArticlesPage, ScraperHasNextPage}
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import io.circe.generic.auto._
import io.circe.syntax._

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import net.ruippeixotog.scalascraper.scraper.ContentExtractors.{element, elementList}

import scala.util.Try

class CensorScraper extends ScraperHasNextPage {
  override val name: String = "Censor"
  override def parsePage(pageUrl: String): Try[PaginetedArticlesPage] = Try {
    val browser = JsoupBrowser()
    val doc = browser.get(pageUrl)
    val list = doc >> element(".main") >> elementList("article")
    val lists = for (i <- list) yield ArticleRef((i >> element("a")).attr("href"),
      LocalDateTime.parse((i >> element("time")).attr("datetime"), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
    PaginetedArticlesPage(lists, (doc >?> element(".pag_next")).map(_.attr("href")))
  }


  override def getContent(url: String): Try[Article] = Try {
    val browser = JsoupBrowser()
    val doc = browser.get(url)
    println(url)
    val content = (doc >> element(".main") >> elementList("p")).map(_.text).mkString(" ")
    val desc = (doc >> element(".main") >> element("h2")).text

    Article(None, url, (doc >> element("title")).text.split("\\(")(0), LocalDateTime.parse((doc >> element("time")).attr("datetime"),
      DateTimeFormatter.ISO_OFFSET_DATE_TIME), desc + " " + content)
  }
}
