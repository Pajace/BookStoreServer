package com.logdown.mycodetub.db

/**
  * Created by Pajace on 2016/6/5.
  */
/**
  * Database
  * @tparam T value type
  */
trait Database[T] {
    def addData(key: String, value: String): Any

    def deleteDataByKey(key: String): Any

    def updateData(key: String, value: String): Any

    def getDataByKey(key: String): String

    def listData(): List[T]
}
