import com.twitter.finatra.validation._

case class Book(@NotEmpty isbn: String,
                name: String,
                author: String,
                publishing: String,
                version: String,
                price: Double) {
    val Key_Isbn = isbn.getClass.
}

val b = new Book("", "", "", "", "", 999)
b.Key_Isbn