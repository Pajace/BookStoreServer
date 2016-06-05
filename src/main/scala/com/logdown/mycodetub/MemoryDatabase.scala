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

    override def removeData(key: String): String = localMemoryDb.getOrElse(key, "")

    override def updateData(key: String, value: String): String = ""

    override def deleteData(key: String): String = ""
}
