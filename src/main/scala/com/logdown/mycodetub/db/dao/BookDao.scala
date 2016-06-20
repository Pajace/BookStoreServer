package com.logdown.mycodetub.db.dao

import com.logdown.mycodetub.data.Book

/**
  * Created by Pajace on 2016/6/5.
  */
trait BookDao {

    def insertBook(value: Book): Boolean

    def deleteBook(isbn: String): Boolean

    def updateBook(books: Book): Boolean

    def findByIsbn(isbn: String): Option[Book]

    def findByName(name: String): List[Book]

    def listAll(): List[Book]
}
