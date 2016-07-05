package com.logdown.mycodetub.db

import com.logdown.mycodetub.data.Book

/**
  * Created by Pajace on 2016/6/5.
  */
trait MongodbOperation {

    def insertBook(value: Book): Either[Throwable, String]

    def insertManyBooks(books: List[Book]): Either[Throwable, String]

    def deleteBook(isbn: String): Either[Throwable, String]

    def updateBook(books: Book): Either[Throwable, String]

    def findByIsbn(isbn: String): Option[Book]

    def findByName(name: String): List[Book]

    def findByIncludeName(includeName: String): List[Book]

    def listAll(): List[Book]
}
