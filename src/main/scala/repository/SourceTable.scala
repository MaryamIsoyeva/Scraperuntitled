package repository

import java.time.LocalDateTime

//import slick.jdbc.PostgresProfile.api._
import PostgresProfile.api._

class SourceTable(tag: Tag) extends Table[Source](tag, "Sources") {
  val id = column[String]("source_id", O.PrimaryKey)
  val url = column[String]("source_url")
  val lastProcessedArticleUrl = column[String]("source_lastPrArtUrl")
  val lastProcessedArticleDate = column[LocalDateTime]("source_lastPrArtDate")

  override def * = (id, url, lastProcessedArticleUrl, lastProcessedArticleDate) <> ((Source.apply _).tupled, Source.unapply)
}

object SourceTable {
  val table = TableQuery[SourceTable]
}