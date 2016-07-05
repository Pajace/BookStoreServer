package com.logdown.mycodetub

import java.util.concurrent.TimeUnit

import com.google.gson.Gson
import com.logdown.mycodetub.data.Book
import com.logdown.mycodetub.db.MongoDbConnector._
import com.logdown.mycodetub.db.MongodbHelper
import org.mongodb.scala._
import org.mongodb.scala.bson.collection.immutable.Document
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, FlatSpec, Matchers}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by pajace_chen on 2016/6/14.
  */
class MongodbHelperTest extends FlatSpec
    with Matchers with BeforeAndAfterEach with BeforeAndAfterAll {
    val EMPTY_STRING = ""
    val gson: Gson = new Gson

    var mongoClient: MongoClient = null
    var MongoDb: MongodbHelper = null
    var dbCollection: MongoCollection[Document] = null

    override def beforeAll(): Unit = {
        super.beforeAll()

        mongoClient = MongoClient(s"mongodb://${mongodb.hostAddress}:${mongodb.hostPort}")
        val database = mongoClient.getDatabase("integration_test_database")
        dbCollection = database.getCollection[Document]("booksTestCollection")

        MongoDb = new MongodbHelper(dbCollection)
    }

    override protected def beforeEach(): Unit = {
        super.beforeEach()
        val dropFuture = dbCollection.drop().toFuture()
        Await.result(dropFuture, Duration(10, TimeUnit.SECONDS))
    }

    override protected def afterEach(): Unit = {
        super.beforeEach()
        val dropFuture = dbCollection.drop().toFuture()
        Await.result(dropFuture, Duration(10, TimeUnit.SECONDS))
    }

    override def afterAll(): Unit = {
        super.afterAll()
        mongoClient.close()
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

    "insertBook" should "return right, after insert book success" in {
        val expectedBook: Book = new Book(
            isbn = "9789863791621",
            name = "奠定大數據的基石 : NoSQL資料庫技術",
            author = "皮雄軍",
            publishing = "佳魁資訊",
            version = "初版",
            price = 560.0)

        val addResult = MongoDb.insertBook(expectedBook)
        addResult shouldBe Right("successfully")

        val actual = MongoDb.findByIsbn(expectedBook.isbn).get
        actual should be(expectedBook)
    }

    it should "return false, if inserted book is null" in {
        MongoDb.insertBook(null).isLeft shouldBe true
    }

    "updateBook" should "return Right after update success" in {
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

        updateResult.isRight shouldBe true
    }

    it should "return Left after no data for update" in {
        val noThisBookInDb = new Book("1234567890123", "", "", "", "", 0)
        val updateResult = MongoDb.updateBook(noThisBookInDb)

        updateResult.isLeft shouldBe true
    }

    it should "return Left if books is null" in {
        val updateResult = MongoDb.updateBook(null)
        updateResult.isLeft shouldBe true
    }

    "deleteData" should "return Right after delete success" in {
        info("add book(isbn=9789863476733) in MongoDB")
        MongoDb.insertBook(gson.fromJson(booksData(0), classOf[Book])) shouldBe Right("successfully")

        info("delete book from MongoDB")
        MongoDb.deleteBook("9789863476733").isRight shouldBe true
    }

    it should "return Left, after delete failed" in {
        MongoDb.deleteBook("non_exist_key").isLeft shouldBe true
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

    "findByName" should "return matched books" in {
        info("add 10 books into mongo db")
        val expectedBookList: List[Book] = Add10BooksIntoMongoDbAndReturnBooksList()

        val booksListFromDb: List[Book] = MongoDb.findByName("Agile學習手冊 : Scrum、XP、精實和看板方法")

        booksListFromDb.size should be(1)
    }

    it should "return empty list if no books matched" in {
        info("add 10 books into mongo db")
        Add10BooksIntoMongoDbAndReturnBooksList()

        val booksListFromDb: List[Book] = MongoDb.findByName("Agile")

        booksListFromDb.size should be(0)
    }

    "findByIncludeName" should "return matched books" in {
        info("add 10 books into mongo db")
        val expectedResult = Add10BooksIntoMongoDbAndReturnBooksList().filter(_.name.contains("設計"))

        val actualResult: List[Book] = MongoDb.findByIncludeName("設計")

        actualResult.size should be(2)
        actualResult.map(expectedResult.contains(_))
    }

    it should "return empty list, if no books is matched" in {
        info("add 10 books into mongo db")
        Add10BooksIntoMongoDbAndReturnBooksList().filter(_.name.contains("abcde"))

        val actualResult = MongoDb.findByIncludeName("abcde")

        actualResult.size should be(0)
    }

    "insertManyBook" should "return true, after inserting book success" in {
        val expectedResult = booksData.map(gson.fromJson(_, classOf[Book]))

        val result = MongoDb.insertManyBooks(expectedResult)

        result.isRight shouldBe true
    }

    private def Add10BooksIntoMongoDbAndReturnBooksList() = {
        val bookList = booksData.map((b: String) => gson.fromJson(b, classOf[Book]))
        val insertFuture = dbCollection.insertMany(bookList.map((b: Book) => Document(
            "_id" -> b.isbn,
            Book.Key_Isbn -> b.isbn,
            Book.Key_Name -> b.name,
            Book.Key_Author -> b.author,
            Book.Key_Publishing -> b.publishing,
            Book.Key_Version -> b.version,
            Book.Key_Price -> b.price
        ))).toFuture()
        Await.result(insertFuture, Duration(10, TimeUnit.SECONDS)).head.toString().split(" ") should contain("successfully")

        bookList
    }
}
