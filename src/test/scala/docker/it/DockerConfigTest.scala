//package docker.it
//
//import com.whisk.docker.{DockerContainer, DockerReadyChecker}
//import org.scalatest.{FlatSpec, Matchers}
//
///**
//  * Created by pajace on 2016/7/1.
//  */
//class DockerConfigTest extends FlatSpec with Matchers with DockerMongodbService{
//
//    "Config-based configurations" should "produce same containers as code-base ones" in {
//        val mongodbExpected = DockerContainer("mongo:3.3.6")
//            .withPorts(27017 -> None)
//            .withReadyChecker(DockerReadyChecker.LogLineContains("waiting for connections on port"))
//            .withCommand("mongod", "--nojournal", "--smallfiles", "--syncdelay", "0")
//
//        configureDockerContainer("docker.mongodb") shouldBe mongodbExpected
//    }
//
//}
