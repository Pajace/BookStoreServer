package com.logdown.mycodetub

import com.google.gson.Gson
import com.google.inject.Stage
import com.google.inject.testing.fieldbinder.Bind
import com.logdown.mycodetub.controller.BookStoreApi
import com.logdown.mycodetub.db.{Book, Database, MemoryDatabase}
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.inject.Mockito
import com.twitter.inject.server.FeatureTest
import com.twitter.util.Future

/**
  * Created by pajace_chen on 2016/6/6.
  */
class BookStoreListTest extends FeatureTest with Mockito {

    override val server = new EmbeddedHttpServer(
        twitterServer = new BookStoreServer,
        stage = Stage.DEVELOPMENT,
        verbose = true)

    @Bind
    @MemoryDatabase
    val memoryDatabase = mock[Database[Book]]

    "GET" should {
        s"list all books information when GET ${BookStoreApi.path_list} request is made" in {
            val book1 =
                """
                  |{
                  |"isbn":"1111111111111",
                  |"name":"Programmin in Scala",
                  |"author":"Martin, Odersky, Lex Spoon, and Bill Venners",
                  |"publishing":"Artima",
                  |"version":"2nd ed.",
                  |"price":34.90
                  |}
                """.stripMargin
            val book2 =
                """
                  |{
                  |"isbn":"2222222222222",
                  |"name":"SCALA for the Impatient",
                  |"author":"Cay S. Horstmann",
                  |"publishing":"Addison-Wesley",
                  |"version":"1st ed.",
                  |"price":49.99
                  |}
                """.stripMargin
            val book3 =
                """
                  |{
                  |"isbn":"3333333333333",
                  |"name":"Functional Programmin in Scala",
                  |"author":"Paul Chiusano, Runar Bjarnason",
                  |"publishing":"Manning Publications",
                  |"version":"1 ed.",
                  |"price":44.99
                  |}
                """.stripMargin

            val gson = new Gson
            val expectedResult = List[Book](
                gson.fromJson(book1, classOf[Book]),
                gson.fromJson(book2, classOf[Book]),
                gson.fromJson(book3, classOf[Book]))

            memoryDatabase.listData().returns(expectedResult)

            server.httpGetJson[List[Book]](
                path = BookStoreApi.path_list,
                andExpect = Status.Ok,
                withJsonBody = s"[${book1}, ${book2}, ${book3}]"
            )
        }
    }

}
