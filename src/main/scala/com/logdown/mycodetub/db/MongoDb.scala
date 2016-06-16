package com.logdown.mycodetub.db

import java.util.concurrent.TimeUnit

import com.google.gson.Gson
import org.bson.json.JsonParseException
import org.mongodb.scala.{MongoClient, _}
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Projections._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import com.twitter.inject.Logging
import org.bson.BsonInvalidOperationException

object MongoDbConnector {
    private val mongoClient = MongoClient("mongodb://10.8.33.30:27017/")

    private val database = mongoClient.getDatabase("bookstore")

    def fetchCollection(collectionName: String) = database.getCollection(collectionName)
}

/**
  * Created by pajace_chen on 2016/6/13.
  */
class MongoDb(collection: MongoCollection[Document] = MongoDbConnector.fetchCollection("books")) extends Logging with Database[Book] {

    override def addData(isbn: String, bookJsonString: String): String = {
        //        try {
        val bookDocument = createDocumentByJsonString(bookJsonString).orNull
        if (bookDocument == null) return "INSERT_FAILED"

        val insertFuture = collection.insertOne(bookDocument).toFuture()

        val addResult = Await.result(insertFuture, Duration(10, TimeUnit.SECONDS)).head.toString().split(" ")
        if (addResult.contains("successfully"))
            "INSERT_OK"
        else
            "INSERT_FAILED"
    }

    override def deleteDataByKey(isbn: String): String = {
        val deleteOne = collection.deleteOne(Filters.eq("isbn", isbn))
        val deleteResult = Await.result(deleteOne.toFuture(), Duration(10, TimeUnit.SECONDS))
        if (deleteResult.head.getDeletedCount == 1) "DELETE_SUCCESS" else "DELETE_FAILED"
    }

    override def updateData(isbn: String, value: String): String = {
        val document: Document = createDocumentByJsonString(value).orNull
        if (document == null) return "UPDATE_FAILED: json parse failed."

        val update = collection.replaceOne(Filters.eq("isbn", isbn), document)

        val updateResult = Await.result(update.head(), Duration(10, TimeUnit.SECONDS))
        (updateResult.getMatchedCount, updateResult.getModifiedCount) match {
            case (1, 1) => "UPDATE_SUCCESS"
            case _ => "UPDATE_FAILED"
        }
    }

    override def listData(): List[Book] = {
        val findAll: Future[Seq[Document]] = collection.find().projection(excludeId()).toFuture()
        val gson: Gson = new Gson
        val allData = Await.result(findAll, Duration(20, TimeUnit.SECONDS)).map(f => gson.fromJson(f.toJson(), classOf[Book]))
        allData.toList
    }

    override def getDataByKey(isbn: String): String = {

        val findFuture = collection.find(Filters.eq("isbn", isbn))
            .projection(fields(excludeId()))

        try {
            Await.result(findFuture.head, Duration(10, TimeUnit.SECONDS)).toJson()
        } catch {
            case illEx: IllegalStateException =>
                println("Error" + illEx.getMessage)
                ""
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
