package co.yaggle.simpleci.core.pipeline;

import co.yaggle.simpleci.core.pipeline.event.PipelineEvent;
import co.yaggle.simpleci.core.pipeline.event.TaskCommandOutputEvent;
import co.yaggle.simpleci.core.pipeline.event.TaskOutputChannel;
import lombok.Builder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.time.ZonedDateTime;
import java.util.concurrent.BlockingQueue;

import static com.google.common.base.Preconditions.*;
import static org.apache.commons.lang.StringUtils.*;

public class TaskOutputStream extends OutputStream {

    @Builder
    private TaskOutputStream(String containerId,
                             String taskId,
                             Integer commandIndex,
                             TaskOutputChannel outputChannel,
                             BlockingQueue<PipelineEvent> eventQueue) {
        checkArgument(!isBlank(containerId));
        checkArgument(!isBlank(taskId));
        checkNotNull(commandIndex);
        checkNotNull(outputChannel);
        checkNotNull(eventQueue);

        this.containerId = containerId;
        this.taskId = taskId;
        this.commandIndex = commandIndex;
        this.outputChannel = outputChannel;
        this.eventQueue = eventQueue;
    }

    @Override
    public void write(int b) throws IOException {
        bytes.write(b);
    }


    @Override
    public void flush() throws IOException {
        if (bytes.size() > 0) {
            String characters = new String(bytes.toByteArray(), CHARSET);
            eventQueue.add(TaskCommandOutputEvent
                                   .builder()
                                   .timestamp(ZonedDateTime.now())
                                   .containerId(containerId)
                                   .taskId(taskId)
                                   .commandIndex(commandIndex)
                                   .channel(outputChannel)
                                   .characters(characters)
                                   .build());
            bytes = new ByteArrayOutputStream();
        }
    }


    @Override
    public void close() throws IOException {
        flush();
    }


    private ByteArrayOutputStream bytes = new ByteArrayOutputStream();
    private final String containerId;
    private final String taskId;
    private final int commandIndex;
    private final TaskOutputChannel outputChannel;
    private final BlockingQueue<PipelineEvent> eventQueue;

    private static final Charset CHARSET = Charset.forName("UTF-8");
}
