package com.logdown.mycodetub.db

import java.util.concurrent.TimeUnit

import com.google.gson.Gson
import org.mongodb.scala.{MongoClient, _}
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Projections._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


object MongoDbConnector {
    private val mongoClient = MongoClient("mongodb://10.8.33.30:27017/")

    private val database = mongoClient.getDatabase("bookstore")

    def fetchCollection(collectionName: String) = database.getCollection(collectionName)
}

/**
  * Created by pajace_chen on 2016/6/13.
  */
class MongoDb(collection: MongoCollection[Document] = MongoDbConnector.fetchCollection("books")) extends Database[Book] {

    override def addData(isbn: String, bookJsonString: String): String = {
        try {
            val bookDocument = Document.apply(bookJsonString)
            val insertFuture = collection.insertOne(bookDocument).toFuture()

            Await.result(insertFuture, Duration(10, TimeUnit.SECONDS))
            "INSERT_OK"
        } catch {
            case ex: Exception => ex.getMessage
        }
    }

    override def deleteDataByKey(isbn: String): String = {
        val deleteOne = collection.deleteOne(Filters.eq("isbn", isbn))
        val deleteResult = Await.result(deleteOne.toFuture(), Duration(10, TimeUnit.SECONDS))
        if (deleteResult.head.getDeletedCount == 1) "Delete_Success" else ""
    }

    override def updateData(isbn: String, value: String): String = {
        val document: Document = Document.apply(value)

        val update = collection.replaceOne(Filters.eq("isbn", isbn), document)

        Await.result(update.head(), Duration(10, TimeUnit.SECONDS)).toString
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

        Await.result(findFuture.head, Duration(10, TimeUnit.SECONDS)).toJson()
    }
}
