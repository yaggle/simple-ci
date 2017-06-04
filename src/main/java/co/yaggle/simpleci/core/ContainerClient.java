package co.yaggle.simpleci.core;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.ContainerState;
import com.spotify.docker.client.messages.HostConfig;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import static com.spotify.docker.client.DockerClient.ExecCreateParam.*;

public class ContainerClient {

    public ContainerClient() throws DockerCertificateException {
        docker = DefaultDockerClient.fromEnv().build();
    }


    /**
     * Create a container from an image
     *
     * @param image              the container's image ID from DockerHub
     * @param mountFromDirectory the absolute path to the local directory to mount
     * @param mountToDirectory   the absolute path to the target (inside the container) directory to mount
     * @return the new container's ID
     * @throws DockerException      if an error occurred while creating the container
     * @throws InterruptedException if the thread was interrupted while creating the container
     */
    public String createContainer(String image, String mountFromDirectory, String mountToDirectory) throws DockerException, InterruptedException {
        HostConfig hostConfig = HostConfig
                .builder()
                .appendBinds(HostConfig.Bind
                                     .from(mountFromDirectory)
                                     .to(mountToDirectory)
                                     .readOnly(false)
                                     .build())
                .build();

        docker.pull(image);

        String containerId = docker
                .createContainer(ContainerConfig
                                         .builder()
                                         .image(image)
                                         .hostConfig(hostConfig)
                                         .build())
                .id();

        docker.startContainer(containerId);

        return containerId;
    }


    /**
     * Get the state of a container.
     *
     * @param containerId the id of a container
     * @return the container's state
     * @throws DockerException      if an error occurred, such as the container does not exist
     * @throws InterruptedException if the thread is interrupted
     */
    public ContainerState getContainerState(String containerId) throws DockerException, InterruptedException {
        return docker.inspectContainer(containerId).state();
    }


    /**
     * Execute a command on a running Docker container.
     *
     * @param containerId the container's ID
     * @param command     the command to execute
     * @param stdout      the stream to consume {@code stdout} output
     * @param stderr      the stream to consume {@code stderr} output
     * @return the execution ID
     * @throws DockerException      if a Docker server error occurs
     * @throws InterruptedException if the thread is interrupted
     * @throws IOException          if an output stream I/O error occurs
     */
    public String execCommand(String containerId, String command, OutputStream stdout, OutputStream stderr) throws DockerException, InterruptedException, IOException {
        final String execId = docker.execCreate(containerId, new String[]{command}, tty(), attachStdin(), attachStdout(), attachStderr()).id();

        try (final LogStream stream = docker.execStart(execId)) {
            stream.attach(stdout, stderr);
        }

        return execId;
    }


    /**
     * Get the exit code of a completed command.
     *
     * @param execId the command's execution ID (returned from {@link #execCommand(String, String, OutputStream, OutputStream)})
     * @return the command's exit code
     * @throws DockerException      if a Docker server error occurs
     * @throws InterruptedException if the thread is interrupted
     */
    public int getCommandExitCode(String execId) throws DockerException, InterruptedException {
        return docker.execInspect(execId).exitCode();
    }


    private final DockerClient docker;
}
