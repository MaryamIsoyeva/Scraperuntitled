import actors.SourceCheckerActor
import actors.SourceCheckerActor.Scrap
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.actor.Actor._
import classes.newArticlesClasses._
import classes._
import com.github.tototoshi.csv.CSVWriter
import repository._
import repository.PostgresProfile.api._
import java.io._
import java.util.Properties

import scala.util.{Failure, Try}
import scala.concurrent.ExecutionContext
//import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.Success
import akka.http.scaladsl.model._
import akka.http.scaladsl.unmarshalling._

import com.github.tototoshi.csv._


object Main extends App {
  override def main(args: Array[String]): Unit = {
    implicit val ec  = ExecutionContext.global
    val db = Try {
      val default = scala.io.StdIn.readLine("Use default database connection? If yes, enter \"y\", else enter \"n\"")
      default.toLowerCase.equals("y") match {
        case true => Database.forConfig("postgresql")
        case _ => Try {
          val url = scala.io.StdIn.readLine("Enter url: ")
          val user = scala.io.StdIn.readLine("Enter user: ")
          val password = scala.io.StdIn.readLine("Enter password: ")
          val numth = scala.io.StdIn.readLine("Enter maximal number of connection threads: ")
          val timeout = scala.io.StdIn.readLine("Enter timeout for connection: ")
          val dr = new org.postgresql.Driver
          val prpo = new Properties()
          prpo.setProperty("numThreads", numth)
          prpo.setProperty("connectionTimeout", timeout)
          Database.forDriver(dr, url, user, password, prpo)

        } match {
          case Success(database) => database
          case Failure(e) => println("Failed to connect; using default connection instead")
            Database.forConfig("postgresql")
        }
      }

    } match {
      case Success(datab) => datab
      case Failure(e) => Database.forConfig("postgresql")
    }

    val repo = new Repository(db)

//    db.run(ArticlesTable.table.map(_.content).result).onComplete{
//      case Success(v) => println(v(0))
//      case Failure(e) => print(e)
//    }

    repo.createSchema()
    repo.createSources(repository.SourcesList.sources)
    implicit val system = ActorSystem("scrap")
    implicit val materializer = ActorMaterializer()

    val nvActor = system.actorOf(SourceCheckerActor.props(
      new ScrapNewArticlesHavingNextPage(new nvuaScraper, "https://nv.ua/ukr/allnews.html"), repo))
    nvActor ! Scrap

    val ItsiderActor = system.actorOf(SourceCheckerActor.props(
      new ScrapNewArticlesHavingNextPage(new ItsiderScraper, "https://itsider.com.ua/"), repo))
    ItsiderActor ! Scrap

    val CensorActor = system.actorOf(SourceCheckerActor.props(
      new ScrapNewArticlesHavingNextPage(new CensorScraper, "https://censor.net.ua/ua/news/all"), repo))
    CensorActor ! Scrap

    val SegonyaActor = system.actorOf(SourceCheckerActor.props(
      new ScrapNewArticlesHavingNextPage(new SegonyaScraper, "https://ukr.segodnya.ua/ukraine.html"), repo))
    SegonyaActor ! Scrap

//    db.run(ArticlesTable.table.result).onComplete {
//      case Success(v) => println(v.length)
//        val f = new File("articles.csv")
//        val writer = CSVWriter.open(f)
//        val l = v.map(Articles.unapply(_).toSeq)
//        writer.writeAll(l)
//        writer.close()
//        println(l.take(3))
//      case Failure(e) => print("articles" + e)
//    }

    sys.addShutdownHook {
      db.close()
    }
  }

}
