package co.yaggle.simpleci.core.pipeline.parser;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static co.yaggle.simpleci.core.TestUtil.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


public class PipelineParserTest {

    @Test
    public void testValidatePipeline() throws Exception {
        PipelineParser.validatePipeline(getDirectory("/test-case-1"));
    }


    @Test
    public void testParsePipeline1() throws Exception {
        PipelineElement pipelineElement = PipelineParser.parsePipeline(getDirectory("/test-case-1"));

        assertThat(pipelineElement.getImage(), is("node:7.10.0"));
        assertThat(pipelineElement.getVolume(), is("/root"));
        assertThat(pipelineElement.getTasks().size(), is(9));

        List<TaskElement> expectedTaskElements = asList(
            TaskElement
                .builder()
                .id("install")
                .name("Install")
                .dependsOn(new ArrayList<>())
                .branch(null)
                .commands(singletonList("yarn"))
                .build(),
            TaskElement
                .builder()
                .id("eslint")
                .name("ES Lint")
                .dependsOn(singletonList("install"))
                .branch(null)
                .commands(singletonList("npm run eslint"))
                .build(),
            TaskElement
                .builder()
                .id("sasslint")
                .name("Sass Lint")
                .dependsOn(singletonList("install"))
                .branch(null)
                .commands(singletonList("npm run sasslint"))
                .build(),
            TaskElement
                .builder()
                .id("preCompileTest")
                .name("Pre-compile Test")
                .dependsOn(singletonList("install"))
                .branch(null)
                .commands(singletonList("npm run test-source"))
                .build(),
            TaskElement
                .builder()
                .id("compile")
                .name("Compile")
                .dependsOn(singletonList("install"))
                .branch(null)
                .commands(singletonList("npm run build"))
                .build(),
            TaskElement
                .builder()
                .id("postCompileTest")
                .name("Post-compile Test")
                .dependsOn(singletonList("compile"))
                .branch(null)
                .commands(singletonList("npm run test-bundle"))
                .build(),
            TaskElement
                .builder()
                .id("uploadArtifacts")
                .name("Upload artifacts")
                .dependsOn(asList("eslint", "sasslint", "preCompileTest", "postCompileTest"))
                .branch("^(master|develop|qa)$")
                .commands(asList(
                    "tar zcf mylib-$GIT_HASH.tar.gz dist",
                    "scp -i $PRIVATE_KEY_PATH mylib-$GIT_HASH.tar.gz artifacts@upload.example.com"))
                .build(),
            TaskElement
                .builder()
                .id("deployNonProd")
                .name("Deploy (non-prod)")
                .dependsOn(singletonList("uploadArtifacts"))
                .branch("^(develop|qa)$")
                .commands(singletonList("./deploy.sh"))
                .build(),
            TaskElement
                .builder()
                .id("deployProd")
                .name("Deploy to prod")
                .dependsOn(singletonList("uploadArtifacts"))
                .branch("^master$")
                .commands(singletonList("./deploy-prod.sh"))
                .build());

        for (int i = 0; i < 1; ++i) {
            assertSame(expectedTaskElements.get(i), pipelineElement.getTasks().get(i));
        }
    }


    @Test
    public void testParsePipeline2() throws Exception {
        PipelineElement pipelineElement = PipelineParser.parsePipeline(getDirectory("/test-case-2"));

        assertThat(pipelineElement.getImage(), is("alpine:3.6"));
        assertThat(pipelineElement.getVolume(), is("/root/simple-ci"));
        assertThat(pipelineElement.getTasks().size(), is(2));

        List<TaskElement> expectedTaskElements = asList(
            TaskElement
                .builder()
                .id("hello")
                .name("Hello")
                .dependsOn(new ArrayList<>())
                .branch(null)
                .commands(asList(
                    "echo \"hello\"",
                    "echo \"hello\" > /root/simple-ci/hello.txt"
                ))
                .build(),
            TaskElement
                .builder()
                .id("goodbye")
                .name("Goodbye")
                .dependsOn(singletonList("hello"))
                .branch(null)
                .commands(asList(
                    "echo \"goodbye\"",
                    "echo \"goodbye\" > /root/simple-ci/goodbye.txt"
                ))
                .build()
        );

        for (int i = 0; i < 1; ++i) {
            assertSame(expectedTaskElements.get(i), pipelineElement.getTasks().get(i));
        }
    }


    private void assertSame(TaskElement expected, TaskElement actual) {
        assertThat(actual.getId(), is(expected.getId()));
        assertThat(actual.getName(), is(expected.getName()));
        assertThat(actual.getDependsOn(), is(expected.getDependsOn()));
        assertThat(actual.getBranch(), is(expected.getBranch()));
        assertThat(actual.getCommands(), is(expected.getCommands()));
    }
}
