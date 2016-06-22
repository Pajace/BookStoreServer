package com.logdown.mycodetub

import com.google.gson.Gson
import com.google.inject.Stage
import com.google.inject.testing.fieldbinder.Bind
import com.logdown.mycodetub.controller.BookStoreApi
import com.logdown.mycodetub.data.Book
import com.logdown.mycodetub.db.DbOperation
import com.logdown.mycodetub.db.dao.{BookDao, MongoDbBookDao}
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.finatra.json.FinatraObjectMapper
import com.twitter.inject.server.FeatureTest
import org.scalamock.scalatest._


/**
  * Created by pajace_chen on 2016/6/6.
  */
class BookStoreControllerTest extends FeatureTest with MockFactory {

    override val server = new EmbeddedHttpServer(
        twitterServer = new BookStoreServer,
        stage = Stage.DEVELOPMENT,
        verbose = true)

    implicit class ImproveToJson(any: Any) {
        def toJsonStringByUsingJackson: String = {
            val mapper = injector.instance[FinatraObjectMapper]
            mapper.writeValueAsString(any)
        }
    }

    @Bind
    @MongoDbBookDao
    val stubBookDao = stub[BookDao]

    "GET /bookstore/list" should {
        "return json string of book's list" in {
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

            (stubBookDao.listAll _).when().returns(expectedResult)

            server.httpGetJson[List[Book]](
                path = BookStoreApi.path_list,
                andExpect = Status.Ok,
                withJsonBody = s"[${book1}, ${book2}, ${book3}]"
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

            (stubBookDao.findByIsbn _).when(book.isbn).returns(Option(book))
            val bookJson = book.toJsonStringByUsingJackson

            // get data and assert
            server.httpGetJson[Book](
                path = BookStoreApi.path_get(book.isbn),
                withJsonBody = bookJson
            )
        }

        "response NotFound, if book's isbn of request is not exist" in {
            val notFoundIsbn = "1234567890123"
            (stubBookDao.findByIsbn _).when(notFoundIsbn).returns(None)

            server.httpGet(
                path = BookStoreApi.path_get(notFoundIsbn),
                andExpect = Status.NotFound)
        }
    }

    "GET /bookstore/findByName" should {
        "return matched book list json string" in {
            val bookName = "7個習慣決定未來 : 柯維給年輕人的成長藍圖"
            val expectedBook = new Book(isbn = "9789863208112",
                name = s"${bookName}",
                author = "柯維",
                publishing = "遠見天下文化出版",
                version = "第一版",
                price = 380)

            val expectedJsonResult = List[Book](expectedBook).toJsonStringByUsingJackson
            val inputPath = (BookStoreApi.path_find_by_name + "?name=" + bookName).replace(" ", "%20")

            (stubBookDao.findByName _).when(bookName).returns(List[Book](expectedBook))

            server.httpGetJson[List[Book]](
                path = inputPath,
                andExpect = Status.Ok,
                withJsonBody = expectedJsonResult
            )
        }
    }

    "Get /bookstore/findByIncludeName" should {
        "return matched book's json string list" in {
            val searchString = "test"
            val expectedList: List[Book] = List(
                new Book(isbn = "9789862168219",
                    name = "脈絡思考創新 = : 喚醒設計思維的3個原點",
                    author = "蕭瑞麟",
                    publishing = "天下遠見",
                    version = "第一版",
                    price = 350),
                new Book(isbn = "9789862728956",
                    name = "像工程師一樣思考",
                    author = "馬德哈文 ; 陳雅莉",
                    publishing = "商周出版",
                    version = "初版",
                    price = 300),
                new Book(isbn = "9789866031793",
                    name = "系統思考 : 克服盲點、面對複雜性、見樹又見林的整體思考",
                    author = "麥道斯 ; 邱昭良",
                    publishing = "經濟新潮社出版",
                    version = "初版",
                    price = 450)
            )

            (stubBookDao.findByIncludeName _).when(searchString).returns(expectedList)
            val mapper = injector.instance[FinatraObjectMapper]
            val expectedJsonResult = mapper.writeValueAsString(expectedList)

            val inputPath = BookStoreApi.path_find_by_include_name + "?name=" + searchString

            server.httpGetJson[List[Book]](
                path = inputPath,
                andExpect = Status.Ok,
                withJsonBody = expectedJsonResult
            )
        }
    }

    "POST /bookstore/add" should {
        "response created and GET location when request for add is made" in {

            (stubBookDao.insertBook _).when(*).returns(true)

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
                withBody = DbOperation.ResultSuccess
            )
        }
    }

    "PUT /bookstore/update" should {
        "response Accepted and GET path after book's information is updated" in {
            (stubBookDao.updateBook _).when(*).returns(true)

            // update data
            server.httpPut(
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
        }

        "response NotFound, if there is not exist book for update" in {
            (stubBookDao.updateBook _).when(*).returns(false)

            // update data
            server.httpPut(
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
                andExpect = Status.NotFound,
                withBody = "9789869279000 not found"
            )
        }
    }

    "DELETE /bookstore/delete/:isbn" should {
        "response Accepted and Delete_Success when DELETE is success" in {
            val expectedIsbn = "1234567890000"

            (stubBookDao.deleteBook _).when(expectedIsbn).returns(true)

            server.httpDelete(
                path = BookStoreApi.path_delete(expectedIsbn),
                andExpect = Status.Accepted
            )
        }

        "response \"NotFound\", when delete key isn't exist" in {
            val notExistKey = "1111111111111"
            (stubBookDao.deleteBook _).when(notExistKey).returns(false)

            server.httpDelete(
                path = BookStoreApi.path_delete(notExistKey),
                andExpect = Status.NotFound
            )
        }
    }
}
