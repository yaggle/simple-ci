package co.yaggle.simpleci.core;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.ContainerConfig;
import com.spotify.docker.client.messages.HostConfig;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.val;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Consumer;
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
     * @param stdout the output stream to attach to the container's stdout
     * @param stderr the output stream to attach to the container's stderr
     * @return the command's container ID and exit code
     *
     * @throws DockerException
     * @throws InterruptedException
     * @throws IOException
     */
    public Result runCommand(
        String command,
        Function<String, OutputStream> stdout,
        Function<String, OutputStream> stderr,
        Consumer<String> containerCreated,
        Consumer<String> containerStarted,
        Consumer<String> containerStopped,
        Consumer<String> containerDeleted
    ) throws DockerException, InterruptedException, IOException {

        val hostConfig = HostConfig
            .builder()
            .appendBinds(binds)
            .build();

        val containerId = docker
            .createContainer(
                ContainerConfig
                    .builder()
                    .image(image)
                    .hostConfig(hostConfig)
                    .entrypoint("/bin/sh", "-c")
                    .cmd(command)
                    .build()
            )
            .id();
        containerCreated.accept(containerId);

        docker.startContainer(containerId);
        containerStarted.accept(containerId);

        try (val logStream = docker.logs(containerId, DockerClient.LogsParam.stdout(), DockerClient.LogsParam.stderr())) {
            logStream.attach(stdout.apply(containerId), stderr.apply(containerId));

            val exitCode = docker.waitContainer(containerId).statusCode();

            containerStopped.accept(containerId);

            return Result
                .builder()
                .containerId(containerId)
                .exitCode(exitCode)
                .build();
        } finally {
            docker.removeContainer(containerId);
            containerDeleted.accept(containerId);
        }
    }


    @Getter
    @Builder
    @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Result {

        @NonNull
        private final String containerId;

        @NonNull
        private final Integer exitCode;
    }


    private final DockerClient docker;
    private final String image;
    private final HostConfig.Bind[] binds;
}
