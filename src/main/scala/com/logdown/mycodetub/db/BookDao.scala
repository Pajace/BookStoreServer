package com.logdown.mycodetub.db

/**
  * Created by Pajace on 2016/6/5.
  */
/**
  * Database
  *
  * @tparam T value type
  */
trait BookDao[T] {

    def insertBook(value: T): String

    def deleteBook(isbn: String): String

    def updateBook(books: T): String

    def getBooksByIsbn(isbn: String): Option[T]

    def listAllBooks(): List[T]
}

object BookDao extends Enumeration {
    val Result_Success = Value("RESULT_SUCCESS")
    val Result_Failed = Value("RESULT_FAILED")
}
