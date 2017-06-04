package co.yaggle.simpleci.core;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import lombok.Builder;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.lang.StringUtils.*;

/**
 * This runs commands in a fresh container per command.
 * </p>
 * This is thread safe and can launch multiple containers that co-exist.
 */
public class DockerCommandRunner {

    @Builder
    private DockerCommandRunner(String image, List<HostConfig.Bind> binds) throws DockerCertificateException, DockerException, InterruptedException {
        checkArgument(!isBlank(image));

        docker = DefaultDockerClient.fromEnv().build();
        this.image = image;
        this.binds = binds.toArray(new HostConfig.Bind[binds.size()]);

        if (docker.listImages(DockerClient.ListImagesParam.byName(image)).isEmpty()) {
            docker.pull(image);
        }
    }


    /**
     * Run the specified command.
     *
     * @param command the command to run
     * @return the command's exit code
     * @throws DockerException
     * @throws InterruptedException
     */
    public int runCommand(String command, Function<String, OutputStream> stdout, Function<String, OutputStream> stderr) throws DockerException, InterruptedException, IOException {
        HostConfig hostConfig = HostConfig
                .builder()
                .appendBinds(binds)
                .build();

        String containerId = docker
                .createContainer(ContainerConfig
                                         .builder()
                                         .image(image)
                                         .hostConfig(hostConfig)
                                         .entrypoint("/bin/sh", "-c")
                                         .cmd(command)
                                         .build())
                .id();

        docker.startContainer(containerId);

        try (LogStream logStream = docker.logs(containerId, DockerClient.LogsParam.stdout(), DockerClient.LogsParam.stderr())) {
            logStream.attach(stdout.apply(containerId), stderr.apply(containerId));
        }
        int exitCode = docker.waitContainer(containerId).statusCode();
        docker.removeContainer(containerId);

        return exitCode;
    }


    private final DockerClient docker;
    private final String image;
    private final HostConfig.Bind[] binds;
}
