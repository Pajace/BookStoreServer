package com.logdown.mycodetub

import scala.collection.mutable

/**
  * Created by Pajace on 2016/6/5.
  */
class MemoryDatabase(localMemoryDb: mutable.Map[String, String] =
                     mutable.Map[String, String]()) extends Database {

    override def createData(key: String, value: String): String = {
        localMemoryDb.put(key, value)
        localMemoryDb.getOrElse(key, "")
    }

    override def deleteDataByKey(key: String): String = localMemoryDb.getOrElse(key, "")

    override def updateData(key: String, value: String): String = {
        val updateResult = localMemoryDb.put(key, value) match {
            case None => ""
            case _ => localMemoryDb.getOrElse(key, "")
        }
        updateResult
    }

    override def getDataByKey(key: String): String = ""
}
