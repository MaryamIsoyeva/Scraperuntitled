package classes

import java.time.{LocalDate, LocalDateTime, LocalTime}
import java.time.format.{DateTimeFormatter, FormatStyle}
import java.util.Locale

import net.ruippeixotog.scalascraper.browser.JsoupBrowser
import scrap.{Article, ArticleRef, PaginetedArticlesPage, ScraperHasNextPage}
import net.ruippeixotog.scalascraper.dsl.DSL._
import net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import io.circe.generic.auto._
import io.circe.syntax._

import scala.util.{Failure, Success, Try}

class nvuaScraper extends ScraperHasNextPage {
  override val name: String = "nvua"
  val locale = new Locale("uk", "UA")
  val format = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(locale)

  override def parsePage(pageUrl: String): Try[PaginetedArticlesPage] = Try {
    val browser = JsoupBrowser()
    val doc = browser.get(pageUrl)
    val articlesElem = doc >> elementList(".one_result")

    val list = for {i <- articlesElem
                    datetime = (i >> element(".atom__additional_pubDate")).text.split(",")
                    href = (i >> element(".result")).attr("href")
                    date = LocalDate.parse(datetime(0) + " " + LocalDate.now().getYear + " р.", format)
                    time = LocalTime.parse(datetime(1).trim + ":00")
                    articleDate = LocalDateTime.of(date, time)} yield ArticleRef(href, articleDate)

    PaginetedArticlesPage(list, doc >?> element("a[rel=next]") map (_.attr("href")))
  }

  override def getContent(url: String): Try[Article] = Try {
    val browser = JsoupBrowser()
    val doc = browser.get(url)
    println(url)
    //    val article = doc >> element(".content_wrapper")
    val datetime = (doc >> element(".article__head__additional_published")).text.split(",")
    val date = LocalDate.parse(datetime(0) + " " + LocalDate.now().getYear + " р.", format)
    val time = LocalTime.parse(datetime(1).trim + ":00")
    val articleDate = LocalDateTime.of(date, time)

    val img = (doc >> element(".content_wrapper") >> elementList(".article__content__head_img-image_copyright")).map(_.text)
    val subtitle: String = (doc >> element(".subtitle")).text
    val articlecontent = Try {
      (doc >> elementList(".MsoNormal")).map(_.text).mkString(" ")
    } match {
      case Success("") => (doc >> element(".content_wrapper") >> elementList("p")).map(_.text).filterNot(img.contains(_)).dropRight(2).mkString(" ")
      case Success(content) => content
      case Failure(e) => println(e.getMessage)
        (doc >> element(".content_wrapper") >> elementList("p")).map(_.text).filterNot(img.contains(_)).dropRight(2).mkString(" ")
    }
    val title = (doc >> element(".article__content__head__text") >> element("h1")).text

    Article(None, url, title, articleDate, subtitle + articlecontent)
  }
}
