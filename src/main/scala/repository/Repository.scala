package repository

import scrap._
import PostgresProfile.api._

import scala.concurrent.ExecutionContext

class Repository(val db: Database)(implicit ec: ExecutionContext) {
  //val db = Database.forConfig("")
  def createSchema() = {
    db.run((ArticlesTable.table.schema ++
      SourceTable.table.schema).create)
  }

  def createSource(src: Source) = {
    db.run(SourceTable.table += src)
  }

  def createSources(srcs: List[Source]) = db.run(SourceTable.table ++= srcs)

  def createArticle(article: Articles) = //{println(article);
    db.run(ArticlesTable.table += article) //Compiled?
  def createArticles(articles: List[Articles]) = db.run(ArticlesTable.table ++= articles) //insertOrUpdate???

  def getLastProcessedArticle(source: String) = {
    val articlesBySource = ArticlesTable.table
      .filter(_.sourceId === source)

    val maxPublishDate = articlesBySource.map(_.publishDate).max
    val query = articlesBySource
      .filter(_.publishDate === maxPublishDate)
      .map(x => (x.url, x.publishDate))
      .result.headOption


    db.run(query).map {
      case Some((url, publishDate)) => ArticleRef(url, publishDate)
      case None => ArticleRef.Never
    }
  }


}

