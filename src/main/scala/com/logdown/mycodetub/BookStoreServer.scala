package com.logdown.mycodetub

import com.logdown.mycodetub.controller.{BookStoreController, WelcomeBookStoreController}
import com.logdown.mycodetub.db.DatabaseModule
import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter

/**
  * Created by Pajace on 2016/6/5.
  */

object BookStoreServerMain extends BookStoreServer {
    val DefaultMongoDBUrl = "127.0.0.1:27017"
}

class BookStoreServer extends HttpServer {

    override val modules = Seq(DatabaseModule)

    override protected def configureHttp(router: HttpRouter): Unit = {
        router
            .filter[CommonFilters]
            .add[WelcomeBookStoreController]
            .add[BookStoreController]
    }
}
