package com.logdown.mycodetub

import java.util.concurrent.TimeUnit

import com.google.gson.Gson
import com.logdown.mycodetub.db.{Book, Database, MongoDb, MongoDbConnector}
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}
import spray.json._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by pajace_chen on 2016/6/14.
  */
class MongoDbTest extends FlatSpec with Matchers with BeforeAndAfterEach {

    val testCollection = MongoDbConnector.fetchCollection("test")
    val mongoDb: Database[Book] = new MongoDb(testCollection)
    val EmptyString = ""

    override protected def beforeEach(): Unit = {
        super.beforeEach()
        val dropFuture = testCollection.drop().toFuture()
        Await.result(dropFuture, Duration(10, TimeUnit.SECONDS))
    }

    override protected def afterEach(): Unit = {
        super.beforeEach()
        val dropFuture = testCollection.drop().toFuture()
        Await.result(dropFuture, Duration(10, TimeUnit.SECONDS))
    }

    val booksData = List(
        """
          |{
          |"isbn":"9789863476733",
          |"name":"Agile學習手冊 : Scrum、XP、精實和看板方法",
          |"author":"史泰馬恩 ; 葛林 ; 陳佳新",
          |"publishing":"碁峰資訊",
          |"version":"初版",
          |"price":680.0
          |}
        """.stripMargin,
        """
          |{
          |"isbn":"9789862168219 ",
          |"name":"脈絡思考創新 = : 喚醒設計思維的3個原點",
          |"author":"蕭瑞麟",
          |"publishing":"天下遠見",
          |"version":"第一版",
          |"price":350
          |}
        """.stripMargin,
        """
          |{
          |"isbn":"9789862729717",
          |"name":"菁英力 : 職場素養進階課 = Professionalism",
          |"author":"陳嫦芬",
          |"publishing":"商周出版",
          |"version":"初版",
          |"price":420
          |}
        """.stripMargin,
        """
          |{
          |"isbn":"9789863208112",
          |"name":"7個習慣決定未來 : 柯維給年輕人的成長藍圖",
          |"author":"柯維",
          |"publishing":"遠見天下文化出版",
          |"version":"第一版",
          |"price":380
          |}
        """.stripMargin,
        """
          |{
          |"isbn":"9789862728956",
          |"name":"像工程師一樣思考",
          |"author":"馬德哈文 ; 陳雅莉",
          |"publishing":"商周出版",
          |"version":"初版",
          |"price":300
          |}
        """.stripMargin,
        """
          |{
          |"isbn":"9789866031793",
          |"name":"系統思考 : 克服盲點、面對複雜性、見樹又見林的整體思考",
          |"author":"麥道斯 ; 邱昭良",
          |"publishing":"經濟新潮社出版",
          |"version":"初版",
          |"price":450
          |}
        """.stripMargin,
        """
          |{
          |"isbn":"9789863475385",
          |"name":"JavaScript應用程式開發實務",
          |"author":"艾里亞特 ; 楊仁和",
          |"publishing":"碁峰資訊",
          |"version":"初版",
          |"price":480
          |}
        """.stripMargin,
        """
          |{
          |"isbn":"9789864340408",
          |"name":"JavaScript設計模式與開發實踐",
          |"author":"曾探",
          |"publishing":"博碩文化",
          |"version":"初版",
          |"price":460
          |}
        """.stripMargin,
        """
          |{
          |"isbn":"9789863791621",
          |"name":"奠定大數據的基石 : NoSQL資料庫技術",
          |"author":"皮雄軍",
          |"publishing":"佳魁資訊",
          |"version":"初版",
          |"price":560
          |}
        """.stripMargin,
        """
          |{
          |"isbn":"9862763833",
          |"name":"團隊之美",
          |"author":"史泰馬恩 ; 葛林 ; 鄭明輝",
          |"publishing":"	碁峰資訊",
          |"version":"初版",
          |"price":580
          |}
        """.stripMargin
    )


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
        mongoDb.getDataByKey(bookIsbn).parseJson should be(bookJson.parseJson)

        val expected = bookJson.replace("初版", "再版").replace("560", "888")
        mongoDb.updateData(bookIsbn, expected)
        mongoDb.getDataByKey(bookIsbn).parseJson should be(expected.parseJson)
    }

    "listData" should "return all books list" in {
        // add 10 books
        val gson: Gson = new Gson
        booksData.foreach(b => mongoDb.addData("", b))
        val expectedBookList: List[Book] = booksData.map((b: String) => gson.fromJson(b, classOf[Book]))

        val actualBookList: List[Book] = mongoDb.listData()

        actualBookList.size should be(10)
        actualBookList.foreach((b: Book) => expectedBookList should contain(b))
    }

    "deleteData" should "not return empty String if delete is success" in {
        mongoDb.addData("", booksData(0))

        val deleteResult = mongoDb.deleteDataByKey("9789863476733")

        deleteResult should not be EmptyString
    }

    it should "return empty string, if delete is failed" in {
        val deleteResult = mongoDb.deleteDataByKey("non_exist_key")

        deleteResult should be (EmptyString)
    }
}
