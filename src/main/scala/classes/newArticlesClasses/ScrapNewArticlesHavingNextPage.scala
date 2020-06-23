package classes.newArticlesClasses

import scrap._

import scala.util.{Failure, Success, Try}



class ScrapNewArticlesHavingNextPage(val scraper: ScraperHasNextPage, initUrl: String) extends ScrapNewArticles {
  type ScraperType = ScraperHasNextPage

  override def getNewArticles(lastProcessedArticle: ArticleRef): Try[List[ArticleRef]] = {
    val initPage: Try[PaginetedArticlesPage] = scraper.parsePage(initUrl)

    def getNewlist(page: PaginetedArticlesPage, newArticles: List[ArticleRef]): List[ArticleRef] = {
      val newP = newArticles ::: page.refs.takeWhile(x => (x.publishData isAfter lastProcessedArticle.publishData)
        && (!x.url.equals(lastProcessedArticle.url))) //::: newArticles
      page.nextPageUrl match {
        case Some(next) if page.refs.last.publishData isAfter lastProcessedArticle.publishData => scraper.parsePage(next) match {
          case Success(nextpage) => getNewlist(nextpage, newP)
          case Failure(e) => newP


        }
        case None => newP
        case _ => newP
      }
    }

    initPage match {
      case Success(x) => Try {
        getNewlist(x, Nil)
      }
      case Failure(e) => println("in next page")
        Failure(e)
    }
  }



}
