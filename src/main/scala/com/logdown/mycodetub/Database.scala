package com.logdown.mycodetub

/**
  * Created by Pajace on 2016/6/5.
  */
trait Database {
    def createData(key: String, value: String): String

    def deleteDataByKey(key: String): String

    def updateData(key: String, value: String): String

    def getDataByKey(key: String): String

}
