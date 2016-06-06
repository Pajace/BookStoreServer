package com.logdown.mycodetub

import com.google.inject.Stage
import com.logdown.mycodetub.data.Book
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

        "list book's information whe GET request is made" in {
            val response = server.httpPost(
                path = "/bookstore/add",
                postBody =
                    """
                      |{
                      |"isbn":"9789869279987",
                      |"name":"Growth Hack",
                      |"author":"Xdite",
                      |"publishing":"PCuSER電腦人文化",
                      |"version":"初版",
                      |"price":360.0
                      |}
                    """.stripMargin,
                andExpect = Status.Created,
                withLocation = "/bookstore/9789869279987"
            )

            server.httpGetJson[Book](
                path = response.location.get,
                withJsonBody =
                    """
                      |{
                      |"isbn":"9789869279987",
                      |"name":"Growth Hack",
                      |"author":"Xdite",
                      |"publishing":"PCuSER電腦人文化",
                      |"version":"初版",
                      |"price":360.0
                      |}
                    """.stripMargin
            )
        }
    }
}
