package com.logdown.mycodetub

/**
  * Created by Pajace on 2016/6/5.
  */
class MemoryDatabase extends Database {

    val localMemoryDb = scala.collection.mutable.Map[String, String]()


    override def createData(key: String, value: String): String = {
        localMemoryDb.put(key, value)
        localMemoryDb.getOrElse(key, "")
    }

    override def removeData(key: String): String = ""

    override def updateData(key: String, value: String): String = ""

    override def deleteData(key: String): String = ""
}
