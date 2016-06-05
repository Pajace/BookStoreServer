package com.logdown.mycodetub

/**
  * Created by Pajace on 2016/6/5.
  */
trait Database {
    def createData(key: String, value: String): String

    def removeData(key: String): String

    def updateData(key: String, value: String): String

    def deleteData(key: String): String

}
