package co.yaggle.simpleci.server;

import co.yaggle.simpleci.server.exception.CyclicTaskDependencyException;
import co.yaggle.simpleci.server.exception.DuplicateDependencyException;
import co.yaggle.simpleci.server.exception.MissingConfigurationFileException;
import co.yaggle.simpleci.server.model.Pipeline;
import co.yaggle.simpleci.server.model.Task;
import co.yaggle.simpleci.server.parser.PipelineElement;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static co.yaggle.simpleci.server.converter.TaskElementsToTasksConverter.*;
import static co.yaggle.simpleci.server.parser.PipelineParser.*;

public class ConfigFileReader {

    /**
     * Load a project's build configuration.
     *
     * @param rootProjectDirectory the project's root directory
     * @return the project's build configuration
     * @throws SAXException if the build configuration's XML is not valid
     * @throws IOException if an error occurred while reading the build configuration file
     * @throws MissingConfigurationFileException if the project doesn't have a build configuration file
     * @throws CyclicTaskDependencyException if there are cyclic tasks in the build configuration file
     * @throws DuplicateDependencyException if one or more of the build configuration's tasks depends on the same task more than once
     */
    public Pipeline getBuildConfiguration(File rootProjectDirectory) throws SAXException, IOException, MissingConfigurationFileException, CyclicTaskDependencyException, DuplicateDependencyException {
        validatePipeline(rootProjectDirectory);
        PipelineElement pipelineElement = parsePipeline(rootProjectDirectory);
        List<Task> tasks = taskElementsToTasks(pipelineElement.getTasks());

        return Pipeline
                .builder()
                .image(pipelineElement.getImage())
                .tasks(tasks)
                .build();
    }
}
