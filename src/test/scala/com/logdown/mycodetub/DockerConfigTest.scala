package com.logdown.mycodetub

import com.whisk.docker.{DockerContainer, DockerReadyChecker}
import com.whisk.docker.config.DockerKitConfig
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by pajace_chen on 2016/7/12.
  */
class DockerConfigTest extends FlatSpec with Matchers with DockerKitConfig {

    "Config-based configurations" should "produce same containers as code-based ones" in {
        val mongodbExpected =
            DockerContainer("mongo:3.0.6")
                .withPorts(27017 -> Option(27017))
                .withReadyChecker(DockerReadyChecker.LogLineContains("waiting for connections on port"))
                .withCommand("mongod", "--nojournal", "--smallfiles", "--syncdelay", "0")

        configureDockerContainer("docker.mongodb") shouldBe mongodbExpected
    }

}
