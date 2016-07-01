package com.logdown.mycodetub

import com.wacai.config.annotation.conf

/**
  * Created by pajace on 2016/7/1.
  */
@conf trait MongodbConfig {
    val mongodb = new {
        val hostAddress = "127.0.0.1"
        val hostPort = 27017
    }

}
