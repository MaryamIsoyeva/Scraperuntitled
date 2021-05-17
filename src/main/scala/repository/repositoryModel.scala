package repository

import java.time.LocalDateTime

import io.circe.Json

case class Source(id: String, url: String, lastProcessedArticleUrl: String, lastProcessedArticleDate: LocalDateTime)

case class Articles(id: Long, sourceId: String, url: String, title: String, publishDate: LocalDateTime, content: String)
