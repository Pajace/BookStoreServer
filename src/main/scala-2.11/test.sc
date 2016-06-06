
case class Book(isbn: String, name: String, author: String, publishing: String, version: String, price: Double)

val expectedBook = new Book(isbn = "9787512387744",
    name = "Scala 學習手冊",
    author = "Swartz, J.",
    publishing = "OREILLY",
    version = "初版",
    price = 48.00)

val bookString = expectedBook.toString
