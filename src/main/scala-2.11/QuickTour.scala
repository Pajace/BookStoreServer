import java.util.concurrent.TimeUnit

import com.google.gson.Gson
import com.logdown.mycodetub.db.{Book, MongoDb}
import com.mongodb.client.result.DeleteResult
import org.mongodb.scala._
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.Projections._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future, Promise}

/**
  * Created by pajace_chen on 2016/6/13.
  */
object QuickTour {

    trait ImplicitObservable[C] {
        val observable: Observable[C]
        val converter: (C) => String

        def results(): Seq[C] = Await.result(observable.toFuture(), Duration(10, TimeUnit.SECONDS))

        def headResult() = Await.result(observable.head(), Duration(10, TimeUnit.SECONDS))

        def printResult(initial: String = "") = {
            if (initial.length > 0) print(initial)
            results().foreach(res => println(converter(res)))
        }

        def printHeadResult(initial: String = ""): Unit = println(s"${initial}${converter(headResult())}")
    }

    implicit class DocumentObservable[C](val observable: Observable[Document]) extends ImplicitObservable[Document] {
        override val converter: (Document) => String = _.toJson()
    }

    private def insertOne(collection: MongoCollection[Document], data: Document, promise: Promise[Completed]): Promise[Completed] = {
        collection.insertOne(data).subscribe(
            (completed: Completed) => promise.success(completed),
            (failed: Throwable) => promise.failure(failed)
        )
        promise
    }

    def insert10BooksOneByOne(collection: MongoCollection[Document]): Unit = {
        booksData.foreach(b =>
            showPinWheel(
                insertOne(collection, Document.apply(b), Promise[Completed]()).future
            )
        )
    }


    private def showPinWheel(someFuture: Future[_]): Unit = {
        val spinChars = List("|", "/", "-", "\\")
        while (!someFuture.isCompleted) {
            spinChars.foreach({
                case char =>
                    Console.err.print(char)
                    Thread.sleep(200)
                    Console.err.print("\b")
            })
        }
        Console.err.println("")
    }

    private def deleteOne(collection: MongoCollection[Document], deleteKey: String) = {
        println("*" * 300)
        val delete = collection.deleteOne(Filters.eq("isbn", "9789863475385"))
        val r = Await.result(delete.toFuture(), Duration(10, TimeUnit.SECONDS))
        println("delete result : getDeletedCount => " + r.head.getDeletedCount)
        println("*" * 300)
    }

    private def finalAll(collection: MongoCollection[Document]) = {
        val findAll = collection.find().projection(excludeId())
        val allDatas = Await.result(findAll.toFuture(), Duration(10, TimeUnit.SECONDS))
        //        allDatas.foreach(d => println(d.toJson()))
        val result = allDatas.map(_ toJson())
        result.foreach(println _)

        //        collection.find().subscribe((doc: Document) => println(doc.toJson()))


        //        collection.find().subscribe(new Observer[Document]{
        //            override def onNext(result: Document): Unit = {
        //                println("onNext -> Document=> " + result.toJson())
        //            }
        //
        //            override def onError(e: Throwable): Unit = {
        //                println("*"*100 + "=> onError")
        //            }
        //
        //            override def onComplete(): Unit = {
        //                println("*"*100 + "=> onComplete")
        //            }
        //        })


        // list ---
        //        collection.find().subscribe(new Observer[Document](){
        //
        //            var batchSize:Long = 15
        //            var seen:Long = 0
        //            var subscription:Option[Subscription] = None
        //
        //            override def onSubscribe(subscription: Subscription):Unit = {
        //                this.subscription = Some(subscription)
        //                subscription.request(batchSize)
        //                println("+"* 100)
        //            }
        //
        //            override def onNext(result: Document): Unit = {
        //                println(result.toJson())
        //                seen += 1
        //                if (seen == batchSize) {
        //                    seen = 0
        //                    subscription.get.request(batchSize)
        //                    println("-"*100)
        //                }
        //            }
        //
        //            override def onError(e: Throwable): Unit = println(s"Error: $e")
        //
        //            override def onComplete(): Unit = println("="* 100 + " Completed")
        //
        //        })
    }

    def main(args: Array[String]): Unit = {
        val mongoClient: MongoClient = MongoClient("mongodb://10.8.33.30:27017/")

        println("=====> " + mongoClient.settings.getClusterSettings.getHosts.get(0).toString)

        println("getDatabase(bookstore)")
        val database: MongoDatabase = mongoClient.getDatabase("bookstore")

        val collection: MongoCollection[Document] = database.getCollection("test")

        //        val doc: Document = Document(
        //            "name" -> "MongoDB",
        //            "type" -> "database",
        //            "count" -> 1,
        //            "info" -> Document("x" -> 203, "y" -> 102)
        //        )

        //        val booksDataDocuments = booksData.map(_ => Document.apply())


        val promise = Promise[Completed]




        //        collection.drop().subscribe(
        //            (completed:Completed)=>promise.success(completed),
        //            (failed:Throwable)=>promise.failure(failed)
        //        )
        //        showPinWheel(promise.future)
        //        collection.drop().toFuture()

        //        insert10BooksOneByOne(collection)

        //        finalAll(collection)

        //        val result = Await.result(collection.insertOne(Document.apply(booksData(0))).toFuture(), Duration(10, TimeUnit.SECONDS))
        //        println("XX"*100)
        //        println(result.foreach((f:Completed) => f.))
        //        println("XX"*100)


        //        val db = new MongoDb
        //        println("XX" * 100)
        //        println(db.addData("", booksData(2)))
        //        println("XX" * 100)

        //        val booksDocuments: List[Document] = booksData.map(b => Document.apply(b))
        //        val futures = collection.insertMany(booksDocuments).toFuture()
        //        showPinWheel(futures)

        //        val insertDocument = insertOne(collection, doc2, Promise[Completed]()).future
        //        showPinWheel(insertDocument)

        //        collection.insertMany(booksDataDocuments).subscribe(
        //            (completed:Completed) => promise.success(completed),
        //            (failed:Throwable) => promise.failure(failed)
        //        )
        //
        //        val insertManyPromise = promise.future
        //        showPinWheel(insertManyPromise)


        //        collection.insertOne(doc).subscribe(
        //            (completed:Completed) => promise.success(completed),
        //            (failed:Throwable) => promise.failure(failed)
        //        )

        //        val docForAdd = Document.apply(booksData(0))


        //        val db = new MongoDb
        //        println("XX" * 100)
        //        println(db.getDataByKey("9789862729717"))
        //        println("XX" * 100)

        // replace  ---
        //        val replaceBookDocument : Document = Document("price"->999.9)
        //        collection.replaceOne(Filters.eq("isbn", "9862763833"), replaceBookDocument).subscribe(
        //            (updateResult:UpdateResult)=>println(updateResult)
        //        )

        // update ---
        //        val addedBook = addTestData(3)
        //        collection.updateOne(Filters.eq("isbn", isbn), set[Double]("price", 888.9)).subscribe(
        //            (updateResult: UpdateResult) => println(updateResult.toString)
        //        )
        //        val db = new MongoDb
        //        println("XX" * 100)
        //        val gson = new Gson
        //        val bookJsonString = gson.toJson(addedBook).replace("380", "999")
        //        println(db.updateData(addedBook.isbn, bookJsonString))
        //        println("XX" * 100)


        //        showPinWheel()
        //        Thread.sleep(1000)
        //        val promise = Promise[Seq[T]]()
        //        collect().subscribe((l: Seq[T]) => promise.success(l), (t: Throwable) => promise.failure(t))
        //        promise.future

        //            .subscribe(
        //            (completed:Completed)=>promise.success(completed),
        //            (failed:Throwable)=>promise.failure(failed)
        //        ) //.foreach(doc=> println(doc.toJson()))

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

    def addTestData(index: Int): Book = {
        val db = new MongoDb
        println("XX" * 100)
        println(db.addData("", booksData(index)))
        println("XX" * 100)
        val gson = new Gson
        gson.fromJson(booksData(index), classOf[Book])
    }
}
