package com.logdown.mycodetub.db

import com.twitter.finatra.validation.NotEmpty

/**
  * Created by pajace_chen on 2016/6/4.
  */
case class Book(@NotEmpty isbn: String,
                name: String,
                author: String,
                publishing: String,
                version: String,
                price: Double){

}

object Book {
    val Key_Isbn = "isbn"
    val Key_Author = "author"
    val Key_Publishing = "publishing"
    val Key_Version = "version"
    val Key_Price = "price"
}
