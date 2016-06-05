package com.logdown.mycodetub

import com.twitter.finatra.http.HttpServer
import com.twitter.finatra.http.filters.CommonFilters
import com.twitter.finatra.http.routing.HttpRouter

/**
  * Created by Pajace on 2016/6/5.
  */
object BookStoreServerMain extends BookStoreServer

class BookStoreServer extends HttpServer {

    override protected def configureHttp(router: HttpRouter): Unit = {
        router.filter[CommonFilters]
    }
}
