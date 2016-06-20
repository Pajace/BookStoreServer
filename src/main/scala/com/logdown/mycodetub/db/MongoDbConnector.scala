package com.logdown.mycodetub.db

import com.logdown.mycodetub.BookStoreServerMain
import org.mongodb.scala.MongoClient

/**
  * Created by pajace_chen on 2016/6/20.
  */
object MongoDbConnector {
    private val mongoClient = MongoClient("mongodb://" + BookStoreServerMain.DefaultMongoDBUrl)

    private val database = mongoClient.getDatabase("bookstore")

    def fetchCollection(collectionName: String) = database.getCollection(collectionName)
}
