package actors

import actors.ScraperActor.ProcessLink
import akka.actor.{Actor, ActorLogging, Props}
import io.circe._
import repository.{Articles, Repository}
import scrap.{Article, ArticleRef, Scraper}
//import slick.jdbc.PostgresProfile.api._
//import repository.PostgresProfile.api._
import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success, Try}

class ScraperActor(scraper: Scraper, repository: Repository)(implicit ec: ExecutionContext) extends Actor with ActorLogging {

  override def receive: Receive = {
    case ProcessLink(url) => {
      val content: Try[Article] = scraper.getContent(url)
      content match {
        case Success(art) => saveIntoDb(art)
        case Failure(e) => log.error(e, "in scraper actor")
      }
    }
  }

  def saveIntoDb(content: Article) = repository.createArticle(Articles(content.id.getOrElse(0),
    scraper.name,
    content.url,
    content.title,
    content.publishDate, content.content))

  override def postStop(): Unit = {
//    db.close()
  }
}

object ScraperActor {

  case class ProcessLink(url: String)

  def props(scraper: Scraper, repository: Repository)(implicit ec: ExecutionContext): Props = Props(new ScraperActor(scraper, repository))
}
