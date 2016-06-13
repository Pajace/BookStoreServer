package com.logdown.mycodetub

import com.google.gson.Gson
import com.logdown.mycodetub.db.{Book, Database, MemoryDatabase}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

/**
  * Created by Pajace on 2016/6/5.
  */
class MemoryDatabaseTest extends FlatSpec with Matchers {

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
        val addedResult = db.addData(expectedKey, expectedData);

        addedResult should be(expectedData)
    }

    "DeleteDataByKey" should "not return empty string, if delete data is success " in {
        val expectedKey = "expectedKey"
        val expectedData = "AnyData"

        val fakeDb = mutable.Map[String, String]()
        fakeDb.put(expectedKey, expectedData)

        val db = new MemoryDatabase(fakeDb)
        val actualResult = db.deleteDataByKey(expectedKey)

        actualResult should not be EmptyString
    }

    it should "delete data, if delete data is success" in {
        val expectedKey = "expectedKey"
        val expectedData = "AnyData"

        val fakeDb = mutable.Map[String, String]()
        fakeDb.put(expectedKey, expectedData)

        val db = new MemoryDatabase(fakeDb)
        db.deleteDataByKey(expectedKey)

        db.getDataByKey(expectedKey) should be (EmptyString)
    }

    it should "return empty String, if delete data is failed" in {
        val db = new MemoryDatabase()
        val keyOfNoData = "whatEver"

        val actualResult = db.getDataByKey(keyOfNoData)

        actualResult should be(EmptyString)
    }

    "UpdateData" should "return updated data, if update is success" in {
        val expectedKey = "0001"
        val origionaldata =
            """
              |{
              | "name":"Pajace",
              | "phone":"0912345678",
              | "sex":"M"
              |}
            """.stripMargin

        val updatedData =
            """
              |{
              | "name":"Pajace",
              | "phone":"987654321",
              | "sex":"M"
              |}
            """.stripMargin

        val fakeDb = mutable.Map[String, String]()
        fakeDb.put(expectedKey, origionaldata)

        val db = new MemoryDatabase(fakeDb)
        val actualUpdatedData = db.updateData(expectedKey, updatedData)

        actualUpdatedData should be(updatedData)
    }

    it should "return empty String, if update failed" in {
        val expectedKey = "0001"
        val origionaldata =
            """
              |{
              | "name":"Pajace",
              | "phone":"0912345678",
              | "sex":"M"
              |}
            """.stripMargin

        val db = new MemoryDatabase()
        val actualUpdatedData = db.updateData(expectedKey, origionaldata)

        actualUpdatedData should be(EmptyString)
    }

    "getDataByKey" should "return data" in {
        val expectedKey = "0001"
        val expectedData =
            """
              |{
              | "name":"Pajace",
              | "phone":"0912345678",
              | "sex":"M"
              |}
            """.stripMargin

        val fakeDb = mutable.Map[String, String]()
        fakeDb.put(expectedKey, expectedData)

        val db = new MemoryDatabase(fakeDb)
        val actualResult = db.getDataByKey(expectedKey)

        actualResult should be(expectedData)
    }

    it should "return empty string if get data failed" in {
        val db: Database[Book, String] = new MemoryDatabase()

        val actualResult = db.getDataByKey("what ever")

        actualResult should be(EmptyString)
    }

    "listData" should "return data list" in {
        val fakeDb = mutable.Map[String, String]()
        val book1 = new Book(isbn = "9787512387744", name = "Scala 學習手冊", author = "Swartz, J.",
            publishing = "OREILLY", version = "1st", price = 48.00)
        val book2 = new Book(isbn = "9789869279987", name = "Growth Hack", author = "Xdite",
            publishing = "PCuSER電腦人文化", version = "初版", price = 360.00)
        val book3 = new Book(isbn = "9780981351656", name = "Programming in Scala, 2nd",
            author = "Martin Odersky, Lex Spoon, Bill Venners",
            publishing = "artima", version = "2nd", price = 69.95)
        val gson = new Gson()
        fakeDb.put(book1.isbn, gson.toJson(book1))
        fakeDb.put(book2.isbn, gson.toJson(book2))
        fakeDb.put(book3.isbn, gson.toJson(book3))

        val expectedBookList: List[Book] = List[Book](book1, book2, book3)

        val db: Database[Book, String] = new MemoryDatabase(fakeDb)
        val actualBookList = db.listData()

        for (book <- expectedBookList) {
            actualBookList should contain(book)
        }
    }
}
