package com.logdown.mycodetub.controller

import com.google.gson.Gson
import com.logdown.mycodetub.data.Book
import com.logdown.mycodetub.{Database, MemoryDatabase}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

/**
  * Created by pajace_chen on 2016/6/6.
  */
class BookStoreController extends Controller {

    val db: Database = new MemoryDatabase()
    val gson = new Gson

    post("/bookstore/add") {
        book: Book =>
            db.createData(book.isbn, gson.toJson(book))
            response.created.location(s"/bookstore/${book.isbn}")
    }

    get("/bookstore/:isbn") {
        request: Request =>
            val bookJsonString = db.getDataByKey(request.params("isbn"))
            gson.fromJson[Book](bookJsonString, classOf[Book])
    }

}
