package com.logdown.mycodetub

import com.google.inject.Stage
import com.twitter.finagle.http.Status
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest

/**
  * Created by pajace_chen on 2016/6/6.
  */
class BookStoreControllerTest extends FeatureTest {

    override val server = new EmbeddedHttpServer(
        twitterServer = new BookStoreServer,
        stage = Stage.DEVELOPMENT,
        verbose = true)

    "BookStoreController" should {
        "response created when POST request for add is made" in {
            server.httpPost(
                path = "/bookstore/add",
                postBody =
                    """
                      |{
                      |"isbn":"9787512387744",
                      |"name":"Scala 學習手冊",
                      |"author":"Swartz, J.",
                      |"publishing":"OREILLY",
                      |"version":"初版",
                      |"price":48.00
                      |}
                    """.stripMargin,
                andExpect = Status.Created,
                withLocation = "/bookstore/9787512387744"
            )
        }
    }
}
