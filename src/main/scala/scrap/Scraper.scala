package scrap

import java.time.LocalDateTime
import scala.concurrent.Future
import scala.util.Try




case class ArticleRef(url: String, publishData: LocalDateTime)
object ArticleRef {
  // use when there are no 'last parsed article' in DB
  val Never: ArticleRef = ArticleRef("", LocalDateTime.MIN)
}

case class Article(id: Option[Long], url: String, title: String, publishDate: LocalDateTime, content: String)

trait Scraper {
  val name: String
  def getContent(url: String): Try[Article]
}


trait ScraperCanGetAllListing extends Scraper {
  def getList: Try[List[ArticleRef]]
}

case class PaginetedArticlesPage(refs: List[ArticleRef], nextPageUrl: Option[String])

trait ScraperHasNextPage extends Scraper {
  def parsePage(pageUrl: String): Try[PaginetedArticlesPage]
}



trait ScrapNewArticles { //для кожного типу джерел -> діставати "оновлення"
type ScraperType <: Scraper

  def scraper: ScraperType

  def getNewArticles(lastProcessedArticle: ArticleRef): Try[List[ArticleRef]]
}

