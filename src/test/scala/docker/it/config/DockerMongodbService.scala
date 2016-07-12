package docker.it.config

import com.whisk.docker.DockerContainer
import com.whisk.docker.config.DockerKitConfig

trait DockerMongodbService extends DockerKitConfig {

    val mongodbContainer = configureDockerContainer("docker.mongodb")

    abstract override def dockerContainers: List[DockerContainer] =
        mongodbContainer :: super.dockerContainers
}
