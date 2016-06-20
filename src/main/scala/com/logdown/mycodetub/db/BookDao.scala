package com.logdown.mycodetub.db

import com.logdown.mycodetub.data.Book

/**
  * Created by Pajace on 2016/6/5.
  */
/**
  * Database
  */
trait BookDao {

    def insertBook(value: Book): String

    def deleteBook(isbn: String): String

    def updateBook(books: Book): String

    def findByIsbn(isbn: String): Option[Book]

    def listAll(): List[Book]
}

object BookDao extends Enumeration {
    val Result_Success = Value("RESULT_SUCCESS")
    val Result_Failed = Value("RESULT_FAILED")
}
