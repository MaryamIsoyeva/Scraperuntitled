package actors

import akka.actor.{Actor, ActorLogging, Props, Status, Timers}
import akka.routing.RoundRobinPool
import repository.Repository
import scrap.{ArticleRef, ScrapNewArticles}
import ScraperActor.ProcessLink

import scala.concurrent.duration._
//import slick.jdbc.PostgresProfile.api._
import akka.pattern.pipe

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}


class SourceCheckerActor(scrapNewArticles: ScrapNewArticles, repository: Repository)(implicit ec: ExecutionContext)  extends Actor with ActorLogging with Timers {


  import SourceCheckerActor._
  timers.startPeriodicTimer(TickKey, Scrap, 1.hour)

  val router = context.actorOf(RoundRobinPool(1).props(ScraperActor.props(scrapNewArticles.scraper, repository)), "ScrapRouter")

  override def receive: Receive = {
    case Scrap =>
            repository.getLastProcessedArticle(scrapNewArticles.scraper.name).pipeTo(self)
      println("Scrap received")

    case Status.Failure(e) =>
      log.error(e, "Failed to get last articles")
    case lastArticle: ArticleRef =>
      println("scraping new articles...")
      println("article: " + lastArticle)
      val list = scrapNewArticles.getNewArticles(lastArticle) match {
        case Success(newArticles) => newArticles
        case Failure(e) =>
          log.error(e, "failed to get new articles")
          Nil
      }

      println(s"New articles: $list")
      list.foreach(article => router ! ProcessLink(article.url))

  }

  override def postStop(): Unit = {
  }
}

object SourceCheckerActor {
  def props(scrapNewArticles: ScrapNewArticles, repository: Repository)(implicit ec: ExecutionContext): Props = Props(new SourceCheckerActor(scrapNewArticles, repository))

  private case object TickKey

  private case object Tick

  case object Scrap

}

