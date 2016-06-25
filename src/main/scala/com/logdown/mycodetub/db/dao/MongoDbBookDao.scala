package com.logdown.mycodetub.db.dao

import java.util.concurrent.TimeUnit

import com.google.gson.Gson
import com.logdown.mycodetub.data.Book
import com.logdown.mycodetub.db.MongoDbConnector
import com.twitter.inject.Logging
import org.mongodb.scala._
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Projections._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


/**
  * Created by pajace_chen on 2016/6/13.
  */
class MongoDbBookDao(collection: MongoCollection[Document] =
                     MongoDbConnector.fetchCollection("books")
                    ) extends Logging with BookDao {

    val gson: Gson = new Gson

    override def insertBook(book: Book): Boolean = {


        val bookDocument = createDocumentByJsonString(book).orNull
        if (bookDocument == null) return false

        val insertFuture = collection.insertOne(bookDocument).toFuture()

        val addResult = Await.result(insertFuture, Duration(10, TimeUnit.SECONDS)).head.toString().split(" ")
        if (addResult.contains("successfully")) true else false
    }

    override def deleteBook(isbn: String): Boolean = {
        val deleteOne = collection.deleteOne(Filters.eq("isbn", isbn))
        val deleteResult = Await.result(deleteOne.toFuture(), Duration(10, TimeUnit.SECONDS))
        if (deleteResult.head.getDeletedCount == 1) true else false
    }

    override def updateBook(book: Book): Boolean = {
        val document: Document = createDocumentByJsonString(book).orNull
        if (document == null) {
            error("updateBook: json parse failed.")
            return false
        }

        val update = collection.replaceOne(Filters.eq("isbn", book.isbn), document)

        val updateResult = Await.result(update.head(), Duration(10, TimeUnit.SECONDS))
        (updateResult.getMatchedCount, updateResult.getModifiedCount) match {
            case (1, 1) => true
            case _ => false
        }
    }

    override def listAll(): List[Book] = {
        val findAll: Future[Seq[Document]] = collection.find().projection(excludeId()).toFuture()
        val allData = Await.result(findAll, Duration(20, TimeUnit.SECONDS)).map(f => gson.fromJson(f.toJson(), classOf[Book]))
        allData.toList
    }

    override def findByIsbn(isbn: String): Option[Book] = {
        val findFuture = collection.find(Filters.eq("isbn", isbn))
            .projection(fields(excludeId()))

        try {
            Option(gson.fromJson(Await.result(findFuture.head, secondsDuration(10))
                .toJson(), classOf[Book]))
        } catch {
            case illEx: IllegalStateException =>
                println("Error" + illEx.getMessage)
                None
        }
    }

    override def findByName(name: String): List[Book] = {
        val findFuture = collection.find(Filters.eq("name", name))
            .projection(fields(excludeId())).toFuture()

        val allMatchedBooks = Await.result(findFuture, secondsDuration(20))
            .map(b => gson.fromJson(b.toJson(), classOf[Book]))
        allMatchedBooks.toList
    }

    private def createDocumentByJsonString(book: Book): Option[Document] = {
        if (book == null) None
        else
            Option(Document(
                "_id" -> book.isbn,
                Book.Key_Isbn -> book.isbn,
                Book.Key_Author -> book.author,
                Book.Key_Name -> book.name,
                Book.Key_Price -> book.price,
                Book.Key_Publishing -> book.publishing,
                Book.Key_Version -> book.version))
    }

    private def secondsDuration(seconds: Long): Duration = {
        Duration(seconds, TimeUnit.SECONDS)
    }

    override def findByIncludeName(includeName: String): List[Book] = {
        val findFuture = collection.find(Filters.regex("name", s""".*${includeName}.*"""))
            .projection(fields(excludeId())).toFuture()
        val result = Await.result(findFuture, secondsDuration(20))
            .map(b => gson.fromJson(b.toJson(), classOf[Book]))
        result.toList
    }

    override def insertManyBooks(books: List[Book]): Boolean = {
        val documents = books.map(createDocumentByJsonString(_).get)

        val insertManyFuture = collection.insertMany(documents).toFuture()
        val insertResult = Await.result(insertManyFuture, secondsDuration(10)).head.toString().split(" ")

        if (insertResult.contains("successfully")) true else false
    }
}
