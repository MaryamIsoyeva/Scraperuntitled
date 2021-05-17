package repository

import com.github.tminglei.slickpg._

trait PostgresProfile extends ExPostgresProfile with PgDate2Support with PgEnumSupport/*with PgCirceJsonSupport */{
  object Api extends API with DateTimeImplicits  /*with JsonImplicits*/ {
  }

  override val api = Api
}

object PostgresProfile extends PostgresProfile
