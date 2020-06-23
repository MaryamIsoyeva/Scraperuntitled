package classes.newArticlesClasses

import scrap._

import scala.util.Try

class ScrapNewArticlesHavingAllListings(val scraper: ScraperCanGetAllListing) extends ScrapNewArticles {
  type ScraperType = ScraperCanGetAllListing

  override def getNewArticles(lastProcessedArticle: ArticleRef): Try[List[ArticleRef]] = {
    val allRefs = scraper.getList


    allRefs.map(l => l.takeWhile(x => (x.publishData isAfter lastProcessedArticle.publishData)
      && ( x.url != lastProcessedArticle.url)))
  }


}
