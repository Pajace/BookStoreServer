package com.logdown.mycodetub.controller

import com.google.inject.{Inject, Singleton}
import com.logdown.mycodetub.db.BookDao._
import com.logdown.mycodetub.db.{Book, BookDao}
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
class BookStoreController @Inject()(db: BookDao[Book]) extends Controller {

    post(BookStoreApi.path_create) {
        book: Book =>
            val result = db.insertBook(book)
            response.created.location(s"/bookstore/${book.isbn}").body(result)
    }

    get(BookStoreApi.path_get(":isbn")) {
        request: Request =>
            db.getBooksByIsbn(request.params("isbn")) match {
                case Some(b) => b
                case None => response.notFound
            }
    }

    get(BookStoreApi.path_list) {
        request: Request =>
            response.ok
            db.listAllBooks()
    }

    put(BookStoreApi.path_update) {
        val updateSuccess = Result_Success.toString

        book: Book =>
            db.updateBook(book) match {
                case `updateSuccess` => response.accepted.location(s"/bookstore/${book.isbn}")
                case _ => response.notFound.body(book.isbn + " not found")
            }

    }

    delete(BookStoreApi.path_delete(":isbn")) {
        request: Request =>
            val key = request.params("isbn")
            db.deleteBook(key) match {
                case "RESULT_FAILED" => response.notFound
                case "RESULT_SUCCESS" => response.accepted
            }
    }

}
