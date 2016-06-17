package com.logdown.mycodetub

import com.logdown.mycodetub.db.Database._
import com.logdown.mycodetub.db.{Book, Database, MemoryDatabase}
import org.scalatest.{FlatSpec, Matchers}

import scala.collection.mutable

/**
  * Created by Pajace on 2016/6/5.
  */
class MemoryDatabaseTest extends FlatSpec with Matchers {

    val EmptyString = ""

    "addData" should "return Result_Success, after add data success" in {
        val expectedBook: Book = new Book(
            isbn = "9789863791621",
            name = "奠定大數據的基石 : NoSQL資料庫技術",
            author = "皮雄軍",
            publishing = "佳魁資訊",
            version = "初版",
            price = 560.0)

        val db = new MemoryDatabase
        val addedResult = db.addBooks(expectedBook)

        addedResult should be(Database.Result_Success.toString)
    }

    "DeleteDataByKey" should "not return empty string, if delete data is success " in {
        val book = new Book("1234567890123", "", "", "", "", 0.0)

        val fakeDb = mutable.Map[String, Book]()
        fakeDb.put(book.isbn, book)

        val db = new MemoryDatabase(fakeDb)
        val actualResult = db.deleteBooksByIsbn(book.isbn)

        actualResult should be(Result_Success.toString)
    }

    it should "delete data, if delete data is success" in {
        val book = new Book("1234567890123", "", "", "", "", 0.0)

        val fakeDb = mutable.Map[String, Book]()
        fakeDb.put(book.isbn, book)

        val db = new MemoryDatabase(fakeDb)
        db.deleteBooksByIsbn(book.isbn)

        db.getBooksByIsbn(book.isbn) should be(None)
    }

    it should "return Result_Failed, if delete data is failed" in {
        val db = new MemoryDatabase()
        val keyOfNoData = "whatEver"

        db.getBooksByIsbn(keyOfNoData) should be(None)

        db.deleteBooksByIsbn(keyOfNoData) should be(Result_Failed.toString)
    }

    "UpdateData" should "return updated data, if update is success" in {
        val book = new Book(
            isbn = "9789864340408",
            name = "JavaScript設計模式與開發實踐",
            author = "曾探",
            publishing = "博碩文化",
            version = "初版",
            price = 460.0)

        val updatedBook = new Book(
            isbn = "9789864340408",
            name = "JavaScript設計模式與開發實踐",
            author = "曾探",
            publishing = "博碩文化",
            version = "再版",
            price = 999.0)

        val fakeDb = mutable.Map[String, Book]()
        fakeDb.put(book.isbn, book)

        val db = new MemoryDatabase(fakeDb)
        val updateResult = db.updateBooksInfo(updatedBook)

        updateResult should be(Result_Success.toString)
    }

    it should "return Result_Failed, if update failed" in {
        val book = new Book("123456789999", "", "", "", "", 123.0)

        val db = new MemoryDatabase()
        val updateResult = db.updateBooksInfo(book)

        updateResult should be(Result_Failed.toString)
    }

    "getDataByKey" should "return data" in {
        val expectedBook = new Book(
            isbn = "9862763833",
            name = "團隊之美",
            author = "史泰馬恩 ; 葛林 ; 鄭明輝",
            publishing = "	碁峰資訊",
            version = "初版",
            price = 580)

        val fakeDb = mutable.Map[String, Book]()
        fakeDb.put(expectedBook.isbn, expectedBook)

        val db = new MemoryDatabase(fakeDb)
        val actualResult = db.getBooksByIsbn(expectedBook.isbn)

        actualResult should be(Some[Book](expectedBook))
    }

    it should "return None if book's isbn isn't exist" in {
        val db: Database[Book] = new MemoryDatabase()

        val actualResult = db.getBooksByIsbn("what ever")

        actualResult should be(None)
    }

    "listData" should "return data list" in {
        val fakeDb = mutable.Map[String, Book]()

        val book1 = new Book(isbn = "9787512387744", name = "Scala 學習手冊", author = "Swartz, J.",
            publishing = "OREILLY", version = "1st", price = 48.00)
        val book2 = new Book(isbn = "9789869279987", name = "Growth Hack", author = "Xdite",
            publishing = "PCuSER電腦人文化", version = "初版", price = 360.00)
        val book3 = new Book(isbn = "9780981351656", name = "Programming in Scala, 2nd",
            author = "Martin Odersky, Lex Spoon, Bill Venners",
            publishing = "artima", version = "2nd", price = 69.95)

        val expectedBookList: List[Book] = List[Book](book1, book2, book3)
        expectedBookList.foreach((b: Book) => fakeDb.put(b.isbn, b))

        val db: Database[Book] = new MemoryDatabase(fakeDb)
        val actualBookList = db.listAllBooks()

        for (book <- expectedBookList) {
            actualBookList should contain(book)
        }
    }
}
