package com.logdown.mycodetub

import com.google.inject.Stage
import com.twitter.finatra.http.test.EmbeddedHttpServer
import com.twitter.inject.server.FeatureTest

/**
  * Created by Pajace on 2016/6/5.
  */
class BookStoreStartupTest extends FeatureTest {

    override val server = new EmbeddedHttpServer(
        twitterServer = new BookStoreServer,
        stage = Stage.DEVELOPMENT,
        verbose = true)

    "Server" should {
        "startup" in {
            server.assertHealthy()
        }
    }

}
