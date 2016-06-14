package com.logdown.mycodetub

import com.github.simplyscala.{MongoEmbedDatabase, MongodProps}
import com.logdown.mycodetub.db.{Book, Database, MongoDb}
import org.scalatest.{BeforeAndAfter, FlatSpec, Matchers}
import spray.json._

/**
  * Created by pajace_chen on 2016/6/14.
  */
class MongoDbTest extends FlatSpec with Matchers with MongoEmbedDatabase with BeforeAndAfter {

    var mongoInstance: MongodProps = null

    override protected def before(fun: => Any): Unit = {
        try {
            mongoInstance = mongoStart(27017)
        } catch {
            case ex: Exception =>
        }
    }

    override protected def after(fun: => Any): Unit = {
        mongoStop(mongoInstance)
    }

    val mongoDb: Database[Book] = new MongoDb


    "addData" should "add data into mongo db" in {
        val expectedBookJsonString =
            """
              |{
              |"isbn":"9789863791621",
              |"name":"奠定大數據的基石 : NoSQL資料庫技術",
              |"author":"皮雄軍",
              |"publishing":"佳魁資訊",
              |"version":"初版",
              |"price":560
              |}
            """.stripMargin
        mongoDb.addData("", expectedBookJsonString)

        val expected = expectedBookJsonString.parseJson
        val actual = mongoDb.getDataByKey("9789863791621").parseJson

        expected should be(actual)
    }

    "updateData" should "update book data " in {
        val bookIsbn = "9789863791621"
        val bookJson =
            s"""
               |{
               |"isbn":"${bookIsbn}",
               |"name":"奠定大數據的基石 : NoSQL資料庫技術",
               |"author":"皮雄軍",
               |"publishing":"佳魁資訊",
               |"version":"初版",
               |"price":560
               |}
            """.stripMargin
        mongoDb.addData(bookIsbn, bookJson)
        mongoDb.getDataByKey(bookIsbn).parseJson should be (bookJson.parseJson)

        val expected = bookJson.replace("初版", "再版").replace("560", "888")
        mongoDb.updateData(bookIsbn, expected)
        mongoDb.getDataByKey(bookIsbn).parseJson should be (expected.parseJson)
    }
}
