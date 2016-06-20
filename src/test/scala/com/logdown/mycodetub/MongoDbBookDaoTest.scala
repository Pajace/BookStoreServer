package com.logdown.mycodetub

import java.util.concurrent.TimeUnit

import com.google.gson.Gson
import com.logdown.mycodetub.data.Book
import com.logdown.mycodetub.db.dao.BookDao._
import com.logdown.mycodetub.db._
import com.logdown.mycodetub.db.dao.{BookDao, MongoDbBookDao}
import org.scalatest.{BeforeAndAfterEach, FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by pajace_chen on 2016/6/14.
  */
class MongoDbBookDaoTest extends FlatSpec with Matchers with BeforeAndAfterEach {

    val TestCollection = MongoDbConnector.fetchCollection("test")
    val MongoDb: BookDao = new MongoDbBookDao(TestCollection)
    val EmptyString = ""
    val gson: Gson = new Gson

    override protected def beforeEach(): Unit = {
        super.beforeEach()
        val dropFuture = TestCollection.drop().toFuture()
        Await.result(dropFuture, Duration(10, TimeUnit.SECONDS))
    }

    override protected def afterEach(): Unit = {
        super.beforeEach()
        val dropFuture = TestCollection.drop().toFuture()
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


    "addData" should "return Result_Success, after add data success" in {
        val expectedBook: Book = new Book(
            isbn = "9789863791621",
            name = "奠定大數據的基石 : NoSQL資料庫技術",
            author = "皮雄軍",
            publishing = "佳魁資訊",
            version = "初版",
            price = 560.0)

        val addResult = MongoDb.insertBook(expectedBook)
        addResult should be(Result_Success.toString)

        val actual = MongoDb.findByIsbn(expectedBook.isbn).get
        actual should be(expectedBook)
    }

    it should "return RESULT_FAILED, if input book is null" in {
        MongoDb.insertBook(null) should be(Result_Failed.toString)
    }


    "updateData" should "return RESULT_SUCCESS after update success" in {
        val book: Book = new Book(
            isbn = "9789863791621",
            name = "奠定大數據的基石 : NoSQL資料庫技術",
            author = "皮雄軍",
            publishing = "佳魁資訊",
            version = "初版",
            price = 560)

        val updatedBook: Book = new Book(
            isbn = "9789863791621",
            name = "奠定大數據的基石 : NoSQL資料庫技術",
            author = "皮雄軍",
            publishing = "佳魁資訊",
            version = "再版",
            price = 980)

        MongoDb.insertBook(book)
        MongoDb.findByIsbn(book.isbn).get should be(book)

        val updateResult = MongoDb.updateBook(updatedBook)
        MongoDb.findByIsbn(book.isbn).get should be(updatedBook)

        updateResult should be(Result_Success.toString)
    }

    it should "return RESULT_FAILED after no data for update" in {
        val noThisBookInDb = new Book("1234567890123", "", "", "", "", 0)
        val updateResult = MongoDb.updateBook(noThisBookInDb)

        updateResult.split(":") should contain(Result_Failed.toString)
    }

    it should "return RESULT_FAILED if books is null" in {
        val updateResult = MongoDb.updateBook(null)
        updateResult.split(":") should contain(Result_Failed.toString)
    }

    "listData" should "return all books list" in {
        info("add 10 books into mongo db")
        val expectedBookList: List[Book] = Add10BooksIntoMongoDbAndReturnBooksList()

        val booksListFromDB: List[Book] = MongoDb.listAll()

        booksListFromDB.size should be(10)
        booksListFromDB.foreach((b: Book) => expectedBookList should contain(b))
    }


    it should "return empty list, if there is no book" in {
        MongoDb.listAll().size should be(0)
    }

    "deleteData" should "return RESULT_SUCCESS after delete success" in {
        info("add book(isbn=9789863476733) in MongoDB")
        MongoDb.insertBook(gson.fromJson(booksData(0), classOf[Book])) should be(Result_Success.toString)

        info("delete book from MongoDB")
        MongoDb.deleteBook("9789863476733") should be(Result_Success.toString)
    }


    it should "return RESULT_FAILED, after delete failed" in {
        MongoDb.deleteBook("non_exist_key") should be(Result_Failed.toString)
    }

    private def Add10BooksIntoMongoDbAndReturnBooksList() = {
        val bookList = booksData.map((b: String) => gson.fromJson(b, classOf[Book]))
        bookList.foreach(MongoDb.insertBook)
        bookList
    }
}
