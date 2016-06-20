package com.logdown.mycodetub.db

import java.util.concurrent.TimeUnit

import com.google.gson.Gson
import com.logdown.mycodetub.BookStoreServerMain
import com.logdown.mycodetub.db.BookDao._
import com.twitter.inject.Logging
import org.bson.BsonInvalidOperationException
import org.bson.json.JsonParseException
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.{MongoClient, _}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object MongoDbConnector {
    private val mongoClient = MongoClient("mongodb://" + BookStoreServerMain.DefaultMongoDBUrl)

    private val database = mongoClient.getDatabase("bookstore")

    def fetchCollection(collectionName: String) = database.getCollection(collectionName)
}

/**
  * Created by pajace_chen on 2016/6/13.
  */
class MongoDb(collection: MongoCollection[Document] = MongoDbConnector.fetchCollection("books")) extends Logging with BookDao {

    val gson: Gson = new Gson

    override def insertBook(book: Book): String = {

        val bookJsonString = gson.toJson(book)
        val bookDocument = createDocumentByJsonString(bookJsonString).orNull
        if (bookDocument == null) return Result_Failed.toString

        val insertFuture = collection.insertOne(bookDocument).toFuture()

        val addResult = Await.result(insertFuture, Duration(10, TimeUnit.SECONDS)).head.toString().split(" ")
        if (addResult.contains("successfully"))
            BookDao.Result_Success.toString
        else
            BookDao.Result_Failed.toString
    }

    override def deleteBook(isbn: String): String = {
        val deleteOne = collection.deleteOne(Filters.eq("isbn", isbn))
        val deleteResult = Await.result(deleteOne.toFuture(), Duration(10, TimeUnit.SECONDS))
        if (deleteResult.head.getDeletedCount == 1) Result_Success.toString else Result_Failed.toString
    }

    override def updateBook(book: Book): String = {
        val value = gson.toJson(book)
        val document: Document = createDocumentByJsonString(value).orNull
        if (document == null) return Result_Failed.toString + ": json parse failed."

        val update = collection.replaceOne(Filters.eq("isbn", book.isbn), document)

        val updateResult = Await.result(update.head(), Duration(10, TimeUnit.SECONDS))
        (updateResult.getMatchedCount, updateResult.getModifiedCount) match {
            case (1, 1) => Result_Success.toString
            case _ => Result_Failed.toString
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
            Option(gson.fromJson(Await.result(findFuture.head, Duration(10, TimeUnit.SECONDS)).toJson(), classOf[Book]))
        } catch {
            case illEx: IllegalStateException =>
                println("Error" + illEx.getMessage)
                None
        }
    }

    private def createDocumentByJsonString(jsonString: String): Option[Document] = {
        try {
            Option.apply(Document.apply(jsonString))
        } catch {
            case ex: JsonParseException =>
                error("createDocumentByJsonString => " + ex.getMessage)
                None
            case ex: BsonInvalidOperationException =>
                error("createDocumentByJsonString => " + ex.getMessage)
                None
        }
    }

}
