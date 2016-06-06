package com.logdown.mycodetub.controller

import com.google.gson.Gson
import com.logdown.mycodetub.data.Book
import com.logdown.mycodetub.{Database, MemoryDatabase}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

/**
  * Created by pajace_chen on 2016/6/6.
  */
object BookStoreApi {
    def path_create = "/bookstore/add"

    def path_get(isbn: String) = s"/bookstore/${isbn}"

    def path_update = "/bookstore/update"

    def path_delete(isbn: String) = s"/bookstore/delete/${isbn}"
}

class BookStoreController extends Controller {

    val db: Database[Book] = new MemoryDatabase()
    val gson = new Gson

    post(BookStoreApi.path_create) {
        book: Book =>
            db.createData(book.isbn, gson.toJson(book))
            response.created.location(s"/bookstore/${book.isbn}")
    }

    get(BookStoreApi.path_get(":isbn")) {
        request: Request =>
            val bookJsonString = db.getDataByKey(request.params("isbn"))
            gson.fromJson[Book](bookJsonString, classOf[Book])
    }

    put(BookStoreApi.path_update) {
        book: Book =>
            val bookJsonString = gson.toJson(book)
            db.updateData(book.isbn, bookJsonString)
            response.accepted.location(s"/bookstore/${book.isbn}")
    }

    delete(BookStoreApi.path_delete(":isbn")) {
        request: Request =>
            val key = request.params("isbn")
            val deletedJson = db.deleteDataByKey(key)
            if (deletedJson == "") response.badRequest
            else response.accepted.body(deletedJson)
    }

}
