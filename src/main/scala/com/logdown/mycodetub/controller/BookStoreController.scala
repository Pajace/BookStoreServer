package com.logdown.mycodetub.controller

import com.google.inject.{Inject, Singleton}
import com.logdown.mycodetub.data.Book
import com.logdown.mycodetub.db.{DbOperation, MongodbOperation}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller

/**
  * Created by pajace_chen on 2016/6/6.
  */
object BookStoreApi {
    val path_create = "/bookstore/add"

    def path_get(isbn: String) = s"/bookstore/$isbn"

    val path_list = "/bookstore/list"

    val path_update = "/bookstore/update"

    def path_delete(isbn: String) = s"/bookstore/delete/$isbn"

    val path_find_by_name = "/bookstore/find_by_name"

    val path_find_by_include_name = "/bookstore/find_by_include_name"

    val path_add_many = "/bookstore/addMany"

}

@Singleton
class BookStoreController @Inject()(db: MongodbOperation) extends Controller {

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

    get(BookStoreApi.path_find_by_name) {
        request: Request =>
            val findName = request.params.getOrElse("name", "")
            val result = db.findByName(findName)
            result
    }

    get(BookStoreApi.path_find_by_include_name) {
        request: Request =>
            val includeName = request.params.getOrElse("name", "")
            info("query with include name: " + includeName)
            val result = db.findByIncludeName(includeName)
            result
    }

    post(BookStoreApi.path_create) {
        book: Book =>
            val result = db.insertBook(book)
            if (result.isRight) response.created.location(s"/bookstore/${book.isbn}").body(DbOperation.ResultSuccess)
            else response.created.location(s"/bookstore/${book.isbn}").body(DbOperation.ResultFailed)
    }

    post(BookStoreApi.path_add_many) {
        books: List[Book] =>
            val result = db.insertManyBooks(books)
            info(s"batch insert books result => ${result}")
            val resultLocationList = books.map((b: Book) => BookStoreApi.path_get(b.isbn))
            if (result)
                response.created.body(resultLocationList)
            else
                response.serviceUnavailable.body("batch add failed")
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
