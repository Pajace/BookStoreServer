package com.logdown.mycodetub.controller

import com.google.inject.{Inject, Singleton}
import com.logdown.mycodetub.data.Book
import com.logdown.mycodetub.db.DbOperation
import com.logdown.mycodetub.db.dao.BookDao
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
class BookStoreController @Inject()(db: BookDao) extends Controller {

    get(BookStoreApi.path_get(":isbn")) {
        request: Request =>
            db.findByIsbn(request.params("isbn")) match {
                case Some(b) => b
                case None => response.notFound
            }
    }

    get(BookStoreApi.path_list) {
        request: Request =>
            response.ok
            db.listAll()
    }

    post(BookStoreApi.path_create) {
        book: Book =>
            val result = db.insertBook(book)
            if (result) response.created.location(s"/bookstore/${book.isbn}").body(DbOperation.ResultSuccess)
            else response.created.location(s"/bookstore/${book.isbn}").body(DbOperation.ResultFailed)
    }

    put(BookStoreApi.path_update) {
        book: Book =>
            if (db.updateBook(book))
                response.accepted.location(s"/bookstore/${book.isbn}")
            else
                response.notFound.body(s"${book.isbn} not found")
    }

    delete(BookStoreApi.path_delete(":isbn")) {
        request: Request =>
            val key = request.params("isbn")

            if (db.deleteBook(key)) response.accepted
            else response.notFound
    }

}
