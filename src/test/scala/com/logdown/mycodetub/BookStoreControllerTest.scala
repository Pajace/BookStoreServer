package com.logdown.mycodetub

import com.google.gson.Gson
import com.google.inject.Stage
import com.google.inject.testing.fieldbinder.Bind
import com.logdown.mycodetub.controller.BookStoreApi
import com.logdown.mycodetub.db.{Book, Database, MongoDb}
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest
import org.scalamock.scalatest._
import org.scalatest._

/**
  * Created by pajace_chen on 2016/6/6.
  */
class BookStoreControllerTest extends FeatureTest with MockFactory {

    override val server = new EmbeddedHttpServer(
        twitterServer = new BookStoreServer,
        stage = Stage.DEVELOPMENT,
        verbose = true)

    val gson : Gson = new Gson()

    @Bind
    @MongoDb
    val mockMongoDb = stub[Database[Book]]

    "POST /bookstore/add" should {
        "response created and GET location when request for add is made" in {

            (mockMongoDb.addBooks _).when(*).returns(Database.Result_Success.toString)

            val expectedIsbn = "9787512387744"
            server.httpPost(
                path = BookStoreApi.path_create,
                postBody =
                    s"""
                       |{
                       |"isbn":"${expectedIsbn}",
                       |"name":"Scala 學習手冊",
                       |"author":"Swartz, J.",
                       |"publishing":"OREILLY",
                       |"version":"初版",
                       |"price":48.00
                       |}
                    """.stripMargin,
                andExpect = Status.Created,
                withLocation = BookStoreApi.path_get(expectedIsbn),
                withBody = Database.Result_Success.toString
            )
        }
    }

    "GET /bookstore/:isbn" should {
        s"return book's json string when GET ${BookStoreApi.path_get("isbn")} request is made" in {
            val book: Book = new Book(
                isbn = "9789863475385",
                name = "JavaScript應用程式開發實務",
                author = "艾里亞特 ; 楊仁和",
                publishing = "碁峰資訊",
                version = "初版",
                price = 480
            )

            (mockMongoDb.getBooksByIsbn _).when(book.isbn).returns(Option(book))
            val bookJson = gson.toJson(book, classOf[Book])

            // get data and assert
            server.httpGetJson[Book](
                path = BookStoreApi.path_get(book.isbn),
                withJsonBody = bookJson
            )
        }

        "response NotFound, if book's isbn of request is not exist" in {
            val notFoundIsbn = "1234567890123"
            (mockMongoDb.getBooksByIsbn _).when(notFoundIsbn).returns(None)

            server.httpGet(
                path = BookStoreApi.path_get(notFoundIsbn),
                andExpect = Status.NotFound)
        }
    }

    "PUT" should {
        "response Accepted and GET path after book's information is updated" in {
            // create data
            server.httpPost(
                path = BookStoreApi.path_create,
                postBody =
                    """
                      |{
                      |"isbn":"9789869279000",
                      |"name":"Growth Hack",
                      |"author":"Xdite",
                      |"publishing":"PCuSER電腦人文化",
                      |"version":"初版",
                      |"price":360.0
                      |}
                    """.stripMargin)

            // update data
            val response = server.httpPut(
                path = BookStoreApi.path_update,
                putBody =
                    """
                      |{
                      |"isbn":"9789869279000",
                      |"name":"Growth Hack",
                      |"author":"Xdite",
                      |"publishing":"PCuSER電腦人文化2",
                      |"version":"初版",
                      |"price":880.0
                      |}
                    """.stripMargin,
                andExpect = Status.Accepted,
                withLocation = "/bookstore/9789869279000"
            )

            // assert
            server.httpGetJson[Book](
                path = response.location.get,
                withJsonBody =
                    """
                      |{
                      |"isbn":"9789869279000",
                      |"name":"Growth Hack",
                      |"author":"Xdite",
                      |"publishing":"PCuSER電腦人文化2",
                      |"version":"初版",
                      |"price":880.0
                      |}
                    """.stripMargin
            )
        }
    }

    "DELETE" should {
        "response Accepted and Delete_Success when DELETE is success" in {
            val expectedIsbn = "9789869279000"
            val expectedBookData =
                """
                  |{
                  |"isbn":"9789869279000",
                  |"name":"Growth Hack",
                  |"author":"Xdite",
                  |"publishing":"PCuSER電腦人文化",
                  |"version":"初版",
                  |"price":360.0
                  |}
                """.stripMargin

            server.httpPost(
                path = BookStoreApi.path_create,
                postBody = expectedBookData,
                andExpect = Status.Created,
                withLocation = BookStoreApi.path_get(expectedIsbn))

            server.httpDelete(
                path = BookStoreApi.path_delete(expectedIsbn),
                andExpect = Status.Accepted
            )
        }

        "response \"BadRequest\", when delete key isn't exist" in {
            val notExistKey = "1111111111111"
            server.httpDelete(
                path = BookStoreApi.path_delete(notExistKey),
                andExpect = Status.BadRequest
            )
        }
    }
}
