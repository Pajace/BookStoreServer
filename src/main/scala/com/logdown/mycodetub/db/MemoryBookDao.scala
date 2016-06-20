package com.logdown.mycodetub.db

import com.logdown.mycodetub.data.Book
import com.logdown.mycodetub.db.BookDao._

import scala.collection.mutable

/**
  * Created by Pajace on 2016/6/5.
  */
class MemoryBookDao(localMemoryDb: mutable.Map[String, Book] =
                     mutable.Map[String, Book]()) extends BookDao {

    override def insertBook(book: Book): String = {
        localMemoryDb.put(book.isbn, book)
        localMemoryDb.get(book.isbn) match {
            case Some(b) => Result_Success.toString
            case None => Result_Failed.toString
        }
    }

    override def deleteBook(key: String): String = {
        localMemoryDb.remove(key) match {
            case Some(deletedData) => Result_Success.toString
            case None => Result_Failed.toString
        }
    }

    override def updateBook(book: Book): String = {
        localMemoryDb.contains(book.isbn) match {
            case true =>
                localMemoryDb.put(book.isbn, book)
                Result_Success.toString
            case false =>
                Result_Failed.toString

        }
    }

    override def findByIsbn(key: String): Option[Book] = localMemoryDb.get(key)

    override def listAll(): List[Book] = localMemoryDb.values.toList


}
