package com.logdown.mycodetub.db

/**
  * Created by Pajace on 2016/6/5.
  */
/**
  * Database
  * @tparam T value type
  */
trait Database[T] {
    def addBooks(isbn: String, value: String): String

    def deleteBooksByIsbn(isbn: String): String

    def updateBooksInfo(isbn: String, value: String): String

    def getBooksByIsbn(isbn: String): String

    def listAllBooks(): List[T]
}
