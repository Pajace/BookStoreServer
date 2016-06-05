package com.logdown.mycodetub

import org.scalamock.scalatest.MockFactory
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

/**
  * Created by Pajace on 2016/6/5.
  */
class MemoryDatabaseTest extends FlatSpec with Matchers with MockFactory{

    val EmptyString = ""

    "CreateData" should "return created value after creat data in" in {
        val expectedData =
            """
              |{
              | "name":"Pajace",
              | "phone":"0912345678",
              | "sex":"M"
              |}
            """.stripMargin
        val expectedKey = "0001"

        val db = new MemoryDatabase
        val addedResult = db.createData(expectedKey, expectedData);

        addedResult should be(expectedData)
    }

    "RemoveData" should "not return empty string, if delete data is success in " in {
        val expectedKey = "expectedKey"
        val expectedData = "AnyData"

        val fakeDb = mutable.Map[String, String]()
        fakeDb.put(expectedKey, expectedData)

        val db = new MemoryDatabase(fakeDb)
        val actualResult = db.removeData(expectedKey)

        actualResult should not be EmptyString
    }

    it should "return empty String, if delete data is failed" in {
        val db = new MemoryDatabase()
        val keyOfNoData = "whatEver"

        val actualResult = db.deleteData(keyOfNoData)

        actualResult should be(EmptyString)
    }

}
