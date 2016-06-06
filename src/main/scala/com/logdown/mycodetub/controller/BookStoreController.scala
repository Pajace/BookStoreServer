package com.logdown.mycodetub.controller

import com.logdown.mycodetub.data.Book
import com.logdown.mycodetub.{Database, MemoryDatabase}
import com.twitter.finatra.http.Controller

/**
  * Created by pajace_chen on 2016/6/6.
  */
class BookStoreController extends Controller {

    val db: Database = new MemoryDatabase()

    post("/bookstore/add") {
        book: Book =>
            db.createData(book.isbn, book.toString)
            response.created.location(s"/bookstore/${book.isbn}")
    }

}
