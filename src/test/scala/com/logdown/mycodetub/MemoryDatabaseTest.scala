package com.logdown.mycodetub

import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by Pajace on 2016/6/5.
  */
class MemoryDatabaseTest extends FlatSpec with Matchers {

    "MemoryDatabase" should "return created value after creat data in" in {
        val expectedData =
            """
              |{
              | "name":"Pajace",
              | "phone":"0912345678",
              | "sex":"M"
              |}
            """.stripMargin
        val expectedKey = "0001"

        val db = new MemoryDatabase
        val addedResult = db.createData(expectedKey, expectedData);

        addedResult should be(expectedData)

    }

}
