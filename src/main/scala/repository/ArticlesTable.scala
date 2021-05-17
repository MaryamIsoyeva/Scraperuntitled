package repository

import java.time.LocalDateTime

import io.circe.Json
//import slick.jdbc.PostgresProfile.api._
import PostgresProfile.api._

class ArticlesTable(tag: Tag) extends Table[Articles](tag, "Articles") {
  val id = column[Long]("articles_id", O.PrimaryKey, O.AutoInc)
  val sourceId = column[String]("articles_source_id")
  val url = column[String]("articles_url")
  val title = column[String]("article_title")
  val publishDate = column[LocalDateTime]("articles_pub_date")
  val content = column[String]("articles_content")

  val sourceIdForeignKey = foreignKey(
    "source_id_fk", sourceId, SourceTable.table)(_.id,
    ForeignKeyAction.Cascade, ForeignKeyAction.Cascade)

  override def * = (id, sourceId, url, title, publishDate, content) <> ((Articles.apply _).tupled, Articles.unapply)

}

object ArticlesTable {
  val table = TableQuery[ArticlesTable]
}