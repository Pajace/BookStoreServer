package com.logdown.mycodetub.db

import com.logdown.mycodetub.MongodbConfig
import org.mongodb.scala.MongoClient

/**
  * Created by pajace_chen on 2016/6/20.
  */
object MongoDbConnector extends MongodbConfig {

    // "mongodb://127.0.0.1:27017"
    private val mongoClient = MongoClient(s"mongodb://${mongodb.hostAddress}:${mongodb.hostPort}")

    private val database = mongoClient.getDatabase("bookstore")

    def fetchCollection(collectionName: String) = database.getCollection(collectionName)
}
