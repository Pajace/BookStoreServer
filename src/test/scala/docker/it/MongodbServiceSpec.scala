package docker.it

import docker.it.typesafe.DockerMongodbService
import org.scalatest.{FlatSpec, Matchers}

/**
  * Created by pajace_chen on 2016/7/12.
  */
class MongodbServiceSpec extends FlatSpec with Matchers
    with DockerTestKit
    with DockerMongodbService {

}
