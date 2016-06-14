package com.logdown.mycodetub.db

import java.util.concurrent.TimeUnit

import com.mongodb.async.client.Observable
import org.mongodb.scala.bson.collection.immutable.Document
import org.mongodb.scala.model.Filters
import org.mongodb.scala.{Completed, MongoClient}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

import com.mongodb.client.model.UpdateOptions
import org.mongodb.scala._
import org.mongodb.scala.bson.BsonString
import org.mongodb.scala.model.Filters._
import org.mongodb.scala.model.Projections._
import org.mongodb.scala.model.Updates._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


object DBConnector {
    val mongoClient = MongoClient("mongodb://10.8.33.30:27017/")

    val database = mongoClient.getDatabase("bookstore")

    def fetchCollection(collectionName: String) = database.getCollection(collectionName)
}

/**
  * Created by pajace_chen on 2016/6/13.
  */
class MongoDb extends Database[Book] {

    val CollectionName = "books"

    override def addData(isbn: String, bookJsonString: String): String = {
        try {
            val collection = DBConnector.fetchCollection(CollectionName)
            val bookDocument = Document.apply(bookJsonString)
            val insertFuture = collection.insertOne(bookDocument).toFuture()

            Await.result(insertFuture, Duration(10, TimeUnit.SECONDS))
            "INSERT_OK"
        } catch {
            case ex: Exception => ex.getMessage
        }
    }

    override def deleteDataByKey(isbn: String): Observable[Completed] = ???

    override def updateData(isbn: String, value: String): String = {
        val document :Document = Document.apply(value)

        val collection = DBConnector.fetchCollection(CollectionName)
        val update = collection.replaceOne(Filters.eq("isbn", isbn), document)

        Await.result(update.head(), Duration(10, TimeUnit.SECONDS)).toString
    }

    override def listData(): List[Book] = ???

    override def getDataByKey(isbn: String): String = {
        val collection = DBConnector.fetchCollection(CollectionName)
        val findFuture = collection.find(Filters.eq("isbn", isbn))
            .projection(fields(excludeId()))

        Await.result(findFuture.head, Duration(10, TimeUnit.SECONDS)).toJson()
    }
}
