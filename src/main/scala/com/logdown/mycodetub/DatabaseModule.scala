package com.logdown.mycodetub

import com.google.inject.{Provides, Singleton}
import com.logdown.mycodetub.data.Book
import com.twitter.inject.TwitterModule

/**
  * Created by pajace_chen on 2016/6/8.
  */
object DatabaseModule extends TwitterModule {

    @Singleton
    @Provides
    def providesDatabase: Database[Book] = {
        new MemoryDatabase()
    }
}
