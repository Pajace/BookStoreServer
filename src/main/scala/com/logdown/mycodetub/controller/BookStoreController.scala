package com.logdown.mycodetub.controller

import com.google.gson.Gson
import com.google.inject.{Inject, Singleton}
import com.logdown.mycodetub.db.{Book, Database}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

/**
  * Created by pajace_chen on 2016/6/6.
  */
object BookStoreApi {
    def path_create = "/bookstore/add"

    def path_get(isbn: String) = s"/bookstore/$isbn"

    val path_list = "/bookstore/list"

    val path_update = "/bookstore/update"

    def path_delete(isbn: String) = s"/bookstore/delete/$isbn"
}

@Singleton
class BookStoreController @Inject()(db: Database[Book]) extends Controller {

    post(BookStoreApi.path_create) {
        book: Book =>
            val result = db.addBooks(book)
            response.created.location(s"/bookstore/${book.isbn}").body(result)
    }

    get(BookStoreApi.path_get(":isbn")) {
        request: Request =>
            db.getBooksByIsbn(request.params("isbn")) match {
                case Some(b) => b
                case None => response.notFound
            }
    }
    //
    //    get(BookStoreApi.path_list) {
    //        request: Request =>
    //            response.ok
    //            db.listAllBooks()
    //    }
    //
    //    put(BookStoreApi.path_update) {
    //        book: Book =>
    //            val bookJsonString = gson.toJson(book)
    //            db.updateBooksInfo(book.isbn, bookJsonString)
    //            response.accepted.location(s"/bookstore/${book.isbn}")
    //    }
    //
    //    delete(BookStoreApi.path_delete(":isbn")) {
    //        request: Request =>
    //            val key = request.params("isbn")
    //            db.deleteBooksByIsbn(key) match {
    //                case "DELETE_FAILED" => response.badRequest
    //                case "DELETE_SUCCESS" => response.accepted
    //            }
    //    }

}
