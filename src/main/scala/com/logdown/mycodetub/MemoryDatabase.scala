package com.logdown.mycodetub

import com.google.gson.Gson
import com.logdown.mycodetub.data.Book

import scala.collection.mutable

/**
  * Created by Pajace on 2016/6/5.
  */
class MemoryDatabase(localMemoryDb: mutable.Map[String, String] =
                     mutable.Map[String, String]()) extends Database[Book] {

    override def createData(key: String, value: String): String = {
        localMemoryDb.put(key, value)
        localMemoryDb.getOrElse(key, "")
    }

    override def deleteDataByKey(key: String): String = {
        localMemoryDb.remove(key) match {
            case Some(deletedData) => deletedData
            case None => ""
        }
    }

    override def updateData(key: String, value: String): String = {
        val updateResult = localMemoryDb.put(key, value) match {
            case None => ""
            case _ => localMemoryDb.getOrElse(key, "")
        }
        updateResult
    }

    override def getDataByKey(key: String): String = localMemoryDb.getOrElse(key, "")

    override def listData(): List[Book] = {
        val gson = new Gson
        val books = for (key <- localMemoryDb.keys) yield
            gson.fromJson[Book](localMemoryDb.get(key).get, classOf[Book])
        books.toList
    }

}
