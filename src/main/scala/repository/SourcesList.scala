package repository

import scrap._
object SourcesList {
  val sources = List(
    Source("nvua", "https://nv.ua/ukr/allnews.html" , ArticleRef.Never.url, ArticleRef.Never.publishData),
    Source("Segonya", "https://ukr.segodnya.ua/ukraine.html" , ArticleRef.Never.url, ArticleRef.Never.publishData),
    Source("Itsider", "https://itsider.com.ua" , ArticleRef.Never.url, ArticleRef.Never.publishData),
    Source("Censor", "https://censor.net.ua/ua/news/all" , ArticleRef.Never.url, ArticleRef.Never.publishData)

  )

}
