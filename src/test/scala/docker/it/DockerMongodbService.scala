package docker.it

import com.whisk.docker.{DockerContainer, DockerKit, DockerReadyChecker}

/**
  * Created by pajace on 2016/7/1.
  */
trait DockerMongodbService extends DockerKit {

    val DefaultMongodbPort = 27017

    val mongodbContainer = DockerContainer("mongo:3.3.6")
        .withPorts(DefaultMongodbPort -> Option(27017))
        .withReadyChecker(DockerReadyChecker.LogLineContains("waiting for connections on port"))
        .withCommand("mongod", "--nojournal", "--smallfiles", "--syncdelay", "0")

    abstract override def dockerContainers: List[DockerContainer] =
        mongodbContainer :: super.dockerContainers
}
