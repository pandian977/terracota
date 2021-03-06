package com.joshcummings.codeplay.terracotta.testng;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.PortBinding;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.DockerClientConfig;

public class DockerSupport {
	private static final String CONTAINER_NAME = "test-terracotta-bank";
	
	private DockerClientConfig config = DefaultDockerClientConfig.createDefaultConfigBuilder()
		    .withDockerHost(getenvWithDefault("DOCKER_HOST", "tcp://127.0.0.1"))
		    .withDockerTlsVerify(Boolean.valueOf(getenvWithDefault("DOCKER_TLS_VERIFY", "false")))
		    .withDockerCertPath(getenvWithDefault("DOCKER_CERT_PATH", "~/.docker/machine"))
		    .withApiVersion("1.23")
		    .withRegistryUrl("https://index.docker.io/v1/")
		    .build();
	
	private DockerClient docker = DockerClientBuilder.getInstance(config).build();

	private String getenvWithDefault(String name, String def) {
		return System.getenv(name) == null ? def : System.getenv(name);
	}

	public void startContainer() throws Exception {
		CreateContainerCmd cmd = docker.createContainerCmd("terracotta-bank");
		cmd.withPortBindings(PortBinding.parse("8080:8080"))
			.withNetworkMode("host")
			.withName(CONTAINER_NAME);
		cmd.exec();
		docker.startContainerCmd(CONTAINER_NAME).exec();
	}

	public void stopContainer() throws Exception {
		docker.stopContainerCmd(CONTAINER_NAME).exec();
		docker.removeContainerCmd(CONTAINER_NAME).exec();
	}
	
	public void startClamav() throws Exception {
		CreateContainerCmd cmd = docker.createContainerCmd("mkodockx/docker-clamav");
		cmd.withPortBindings(PortBinding.parse("3310:3310"))
			.withNetworkMode("host")
			.withName("clamav");
		cmd.exec();
		docker.startContainerCmd("clamav").exec();
	}
	
	public void stopClamav() throws Exception {
		docker.stopContainerCmd("clamav").exec();
		docker.removeContainerCmd("clamav").exec();
	}
}
