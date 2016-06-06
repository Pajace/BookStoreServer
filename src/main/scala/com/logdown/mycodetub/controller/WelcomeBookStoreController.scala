package com.logdown.mycodetub.controller

import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

/**
  * Created by Pajace on 2016/6/5.
  */
class WelcomeBookStoreController extends Controller {

    get("/welcome") {
        request: Request =>
            info("Welcome")
            "Welcome, " + request.params.getOrElse("name", "Nobody")
    }

}
