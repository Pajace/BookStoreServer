package com.logdown.mycodetub.db

import com.google.gson.Gson

import scala.collection.mutable

/**
  * Created by Pajace on 2016/6/5.
  */
class MemoryDatabase(localMemoryDb: mutable.Map[String, String] =
                     mutable.Map[String, String]()) extends Database[Book] {

    override def addBooks(key: String, value: String): String = {
        localMemoryDb.put(key, value)
        localMemoryDb.getOrElse(key, "")
    }

    override def deleteBooksByIsbn(key: String): String = {
        localMemoryDb.remove(key) match {
            case Some(deletedData) => deletedData
            case None => ""
        }
    }

    override def updateBooksInfo(key: String, value: String): String = {
        val updateResult = localMemoryDb.put(key, value) match {
            case None => ""
            case _ => localMemoryDb.getOrElse(key, "")
        }
        updateResult
    }

    override def getBooksByIsbn(key: String): String = localMemoryDb.getOrElse(key, "")

    override def listAllBooks(): List[Book] = {
        val gson = new Gson
        val books = for (key <- localMemoryDb.keys) yield
            gson.fromJson[Book](localMemoryDb.get(key).get, classOf[Book])
        books.toList
    }

}
