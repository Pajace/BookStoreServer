package com.logdown.mycodetub.db

import com.google.inject.{Provides, Singleton}
import com.twitter.inject.TwitterModule

/**
  * Created by pajace_chen on 2016/6/8.
  */
object DatabaseModule extends TwitterModule {

    @Singleton
    @Provides
    def providesDatabase: Database[Book, String] = {
        new MemoryDatabase()
    }
}
