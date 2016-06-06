package com.logdown.mycodetub

import com.google.inject.Stage
import com.logdown.mycodetub.controller.BookStoreApi
import com.logdown.mycodetub.data.Book
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest

/**
  * Created by pajace_chen on 2016/6/6.
  */
class BookStoreListTest extends FeatureTest {

    override val server = new EmbeddedHttpServer(
        twitterServer = new BookStoreServer,
        stage = Stage.DEVELOPMENT,
        verbose = true)

    "GET" should {
        s"list all books information when GET ${BookStoreApi.path_list} request is made" in {
            val book1 =
                """
                  |{
                  |"isbn":"1111111111111",
                  |"name":"Growth Hack",
                  |"author":"Xdite",
                  |"publishing":"PCuSER電腦人文化",
                  |"version":"初版",
                  |"price":360.0
                  |}
                """.stripMargin
            val book2 =
                """
                  |{
                  |"isbn":"2222222222222",
                  |"name":"引誘科學",
                  |"author":"Ben Parr",
                  |"publishing":"三采",
                  |"version":"初版",
                  |"price":284.0
                  |}
                """.stripMargin
            val book3 =
                """
                  |{
                  |"isbn":"3333333333333",
                  |"name":"引誘科學2",
                  |"author":"Ben Parr",
                  |"publishing":"三采",
                  |"version":"初版",
                  |"price":284.0
                  |}
                """.stripMargin

            server.httpPost(path = BookStoreApi.path_create,
                postBody = book1, andExpect = Status.Created)
            server.httpPost(path = BookStoreApi.path_create,
                postBody = book2, andExpect = Status.Created)
            server.httpPost(path = BookStoreApi.path_create,
                postBody = book3, andExpect = Status.Created)

            server.httpGetJson[List[Book]](
                path = BookStoreApi.path_list,
                andExpect = Status.Ok,
                withJsonBody = s"[${book2}, ${book3}, ${book1}]"
            )
        }
    }

}
