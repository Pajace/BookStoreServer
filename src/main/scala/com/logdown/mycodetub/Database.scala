package com.logdown.mycodetub

/**
  * Created by Pajace on 2016/6/5.
  */
/**
  * Database
  * @tparam T value type
  * @tparam T1 operations return type
  */
trait Database[T, T1] {
    def addData(key: String, value: String): T1

    def deleteDataByKey(key: String): T1

    def updateData(key: String, value: String): T1

    def getDataByKey(key: String): String

    def listData(): List[T]
}
