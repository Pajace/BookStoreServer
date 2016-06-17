package com.logdown.mycodetub.db

/**
  * Created by Pajace on 2016/6/5.
  */
/**
  * Database
  *
  * @tparam T value type
  */
trait Database[T] {

    def addBooks(value: T): String

    def deleteBooksByIsbn(isbn: String): String

    def updateBooksInfo(books: T): String

    def getBooksByIsbn(isbn: String): Option[T]

    def listAllBooks(): List[T]
}

object Database extends Enumeration {
    val Result_Success = Value("RESULT_SUCCESS")
    val Result_Failed = Value("RESULT_FAILED")
}
