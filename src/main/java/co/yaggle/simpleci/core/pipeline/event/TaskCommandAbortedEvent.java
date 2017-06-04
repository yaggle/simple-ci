package co.yaggle.simpleci.core.pipeline.event;

import com.spotify.docker.client.exceptions.DockerException;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.time.ZonedDateTime;

/**
 * Event fired when a pipeline's task has aborted unexpectedly.
 */
@Builder
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskCommandAbortedEvent implements TaskEvent {

    public enum ErrorSource {
        DOCKER,
        THREAD,
        IO,
        UNKNOWN,
        ;

        public static ErrorSource forException(Exception e) {
            if (e instanceof DockerException) {
                return DOCKER;
            } else if (e instanceof InterruptedException) {
                return THREAD;
            } else if (e instanceof IOException) {
                return IO;
            } else {
                return UNKNOWN;
            }
        }
    }

    @NonNull
    @Getter(onMethod = @__(@Override))
    private final ZonedDateTime timestamp;

    @NonNull
    @Getter(onMethod = @__(@Override))
    private final String containerId;

    @NonNull
    @Getter(onMethod = @__(@Override))
    private final String taskId;

    @NonNull
    @Getter
    private final Integer commandIndex;

    @NonNull
    @Getter
    private final ErrorSource errorSource;

    @NonNull
    @Getter
    private final String errorMessage;
}
