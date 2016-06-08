package com.logdown.mycodetub.db

/**
  * Created by Pajace on 2016/6/5.
  */
trait Database[T] {
    def addData(key: String, value: String): String

    def deleteDataByKey(key: String): String

    def updateData(key: String, value: String): String

    def getDataByKey(key: String): String

    def listData(): List[T]
}
