package com.logdown.mycodetub.controller

import com.google.inject.{Inject, Singleton}
import com.logdown.mycodetub.data.Book
import com.logdown.mycodetub.db.{DbOperation, MongodbOperation}
import com.twitter.finagle.http.Request
import com.twitter.finatra.http.Controller
import com.twitter.finatra.utils.FuturePools

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


    //see http://twitter.github.io/finatra/user-guide/build-new-http-server/controller.html#requests
    private val futurePool = FuturePools.unboundedPool("CallbackConverter")

    get(BookStoreApi.path_get(":isbn")) {
        request: Request =>
            futurePool {
                db.findByIsbn(request.params("isbn")) match {
                    case Some(b) => b
                    case None => response.notFound
                }
            }
    }

    get(BookStoreApi.path_list) {
        request: Request =>
            futurePool {
                response.ok
                db.listAll()
            }

    }

    get(BookStoreApi.path_find_by_name) {
        request: Request =>
            futurePool {
                val findName = request.params.getOrElse("name", "")
                val result = db.findByName(findName)
                result
            }
    }

    get(BookStoreApi.path_find_by_include_name) {
        request: Request =>
            futurePool {
                val includeName = request.params.getOrElse("name", "")
                info("query with include name: " + includeName)
                val result = db.findByIncludeName(includeName)
                result
            }
    }

    post(BookStoreApi.path_create) {
        book: Book =>
            futurePool {
                val result = db.insertBook(book)
                if (result.isRight) response.created.location(s"/bookstore/${book.isbn}").body(DbOperation.ResultSuccess)
                else response.created.location(s"/bookstore/${book.isbn}").body(DbOperation.ResultFailed)

            }
    }

    post(BookStoreApi.path_add_many) {
        books: List[Book] =>
            futurePool {
                val result = db.insertManyBooks(books)
                val resultLocationList = books.map((b: Book) => BookStoreApi.path_get(b.isbn))
                if (result.isRight)
                    response.created.body(resultLocationList)
                else
                    response.serviceUnavailable.body("batch add failed")
            }
    }

    put(BookStoreApi.path_update) {
        book: Book =>
            futurePool {
                val updateResult = db.updateBook(book)
                if (updateResult.isRight)
                    response.accepted.location(s"/bookstore/${book.isbn}")
                else
                    response.notFound.body(s"${book.isbn} not found")
            }
    }

    delete(BookStoreApi.path_delete(":isbn")) {
        request: Request =>
            futurePool {
                val key = request.params("isbn")

                if (db.deleteBook(key).isRight) response.accepted
                else response.notFound
            }
    }

}
