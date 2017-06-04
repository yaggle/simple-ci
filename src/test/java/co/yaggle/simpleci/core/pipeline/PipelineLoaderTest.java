package co.yaggle.simpleci.core.pipeline;

import co.yaggle.simpleci.core.pipeline.parser.TaskElement;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static co.yaggle.simpleci.core.TestUtil.*;
import static java.util.Arrays.*;
import static java.util.Collections.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class PipelineLoaderTest {

    @Test
    public void testGetBuildConfiguration() throws Exception {
        Pipeline pipeline = PipelineLoader.loadPipeline(getDirectory("/test-case-1"));

        assertThat(pipeline.getImage(), is("node:7.10.0"));
        assertThat(pipeline.getTasks().size(), is(1));

        Task installTask = pipeline.getTasks().get(0);

        assertThat(installTask.getNextTasks().size(), is(4));

        Task eslintTask = installTask.getNextTasks().stream().filter(task -> task.getId().equals("eslint")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(eslintTask.getName(), is("ES Lint"));
        assertThat(eslintTask.getBranch(), is(nullValue()));
        assertThat(eslintTask.getCommands(), is(singletonList("npm run eslint")));
        assertThat(eslintTask.getNextTasks().size(), is(1));
        assertThat(eslintTask.getNextTasks().get(0).getId(), is("uploadArtifacts"));

        Task sasslintTask = installTask.getNextTasks().stream().filter(task -> task.getId().equals("sasslint")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(sasslintTask.getName(), is("Sass Lint"));
        assertThat(sasslintTask.getBranch(), is(nullValue()));
        assertThat(sasslintTask.getCommands(), is(singletonList("npm run sasslint")));
        assertThat(sasslintTask.getNextTasks().size(), is(1));
        assertThat(sasslintTask.getNextTasks().get(0).getId(), is("uploadArtifacts"));

        Task preCompileTestTask = installTask.getNextTasks().stream().filter(task -> task.getId().equals("preCompileTest")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(preCompileTestTask.getName(), is("Pre-compile Test"));
        assertThat(preCompileTestTask.getBranch(), is(nullValue()));
        assertThat(preCompileTestTask.getCommands(), is(singletonList("npm run test-source")));
        assertThat(preCompileTestTask.getNextTasks().size(), is(1));
        assertThat(preCompileTestTask.getNextTasks().get(0).getId(), is("uploadArtifacts"));

        Task compileTask = installTask.getNextTasks().stream().filter(task -> task.getId().equals("compile")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(compileTask.getName(), is("Compile"));
        assertThat(compileTask.getBranch(), is(nullValue()));
        assertThat(compileTask.getCommands(), is(singletonList("npm run build")));
        assertThat(compileTask.getNextTasks().size(), is(1));
        assertThat(compileTask.getNextTasks().get(0).getId(), is("postCompileTest"));

        Task postCompileTestTask = compileTask.getNextTasks().stream().filter(task -> task.getId().equals("postCompileTest")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(postCompileTestTask.getName(), is("Post-compile Test"));
        assertThat(postCompileTestTask.getBranch(), is(nullValue()));
        assertThat(postCompileTestTask.getCommands(), is(singletonList("npm run test-bundle")));
        assertThat(postCompileTestTask.getNextTasks().size(), is(1));
        assertThat(postCompileTestTask.getNextTasks().get(0).getId(), is("uploadArtifacts"));

        Task uploadArtifactsTask = postCompileTestTask.getNextTasks().stream().filter(task -> task.getId().equals("uploadArtifacts")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(uploadArtifactsTask.getName(), is("Upload artifacts"));
        assertThat(uploadArtifactsTask.getBranch(), is("^(master|develop|qa)$"));
        assertThat(uploadArtifactsTask.getCommands(), is(asList("tar zcf mylib-$GIT_HASH.tar.gz dist",
                                                                "scp -i $PRIVATE_KEY_PATH mylib-$GIT_HASH.tar.gz artifacts@upload.example.com")));
        assertThat(uploadArtifactsTask.getNextTasks().size(), is(2));
        assertThat(uploadArtifactsTask.getNextTasks().get(0).getId(), is("deployNonProd"));
        assertThat(uploadArtifactsTask.getNextTasks().get(1).getId(), is("deployProd"));

        Task deployNonProdTask = uploadArtifactsTask.getNextTasks().stream().filter(task -> task.getId().equals("deployNonProd")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(deployNonProdTask.getName(), is("Deploy (non-prod)"));
        assertThat(deployNonProdTask.getBranch(), is("^(develop|qa)$"));
        assertThat(deployNonProdTask.getCommands(), is(singletonList("./deploy.sh")));
        assertThat(deployNonProdTask.getNextTasks().size(), is(0));

        Task deployProdTask = uploadArtifactsTask.getNextTasks().stream().filter(task -> task.getId().equals("deployProd")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(deployProdTask.getName(), is("Deploy to prod"));
        assertThat(deployProdTask.getBranch(), is("^master$"));
        assertThat(deployProdTask.getCommands(), is(singletonList("./deploy-prod.sh")));
        assertThat(deployProdTask.getNextTasks().size(), is(0));
    }


    @Test
    public void testTaskElementsToTasks() throws Exception {
        List<TaskElement> taskElements = asList(
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

        List<Task> tasks = PipelineLoader.taskElementsToTasks(taskElements);

        Task installTask = tasks.get(0);

        assertThat(installTask.getNextTasks().size(), is(4));

        Task eslintTask = installTask.getNextTasks().stream().filter(task -> task.getId().equals("eslint")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(eslintTask.getName(), is("ES Lint"));
        assertThat(eslintTask.getBranch(), is(nullValue()));
        assertThat(eslintTask.getCommands(), is(singletonList("npm run eslint")));
        assertThat(eslintTask.getNextTasks().size(), is(1));
        assertThat(eslintTask.getNextTasks().get(0).getId(), is("uploadArtifacts"));

        Task sasslintTask = installTask.getNextTasks().stream().filter(task -> task.getId().equals("sasslint")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(sasslintTask.getName(), is("Sass Lint"));
        assertThat(sasslintTask.getBranch(), is(nullValue()));
        assertThat(sasslintTask.getCommands(), is(singletonList("npm run sasslint")));
        assertThat(sasslintTask.getNextTasks().size(), is(1));
        assertThat(sasslintTask.getNextTasks().get(0).getId(), is("uploadArtifacts"));

        Task preCompileTestTask = installTask.getNextTasks().stream().filter(task -> task.getId().equals("preCompileTest")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(preCompileTestTask.getName(), is("Pre-compile Test"));
        assertThat(preCompileTestTask.getBranch(), is(nullValue()));
        assertThat(preCompileTestTask.getCommands(), is(singletonList("npm run test-source")));
        assertThat(preCompileTestTask.getNextTasks().size(), is(1));
        assertThat(preCompileTestTask.getNextTasks().get(0).getId(), is("uploadArtifacts"));

        Task compileTask = installTask.getNextTasks().stream().filter(task -> task.getId().equals("compile")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(compileTask.getName(), is("Compile"));
        assertThat(compileTask.getBranch(), is(nullValue()));
        assertThat(compileTask.getCommands(), is(singletonList("npm run build")));
        assertThat(compileTask.getNextTasks().size(), is(1));
        assertThat(compileTask.getNextTasks().get(0).getId(), is("postCompileTest"));

        Task postCompileTestTask = compileTask.getNextTasks().stream().filter(task -> task.getId().equals("postCompileTest")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(postCompileTestTask.getName(), is("Post-compile Test"));
        assertThat(postCompileTestTask.getBranch(), is(nullValue()));
        assertThat(postCompileTestTask.getCommands(), is(singletonList("npm run test-bundle")));
        assertThat(postCompileTestTask.getNextTasks().size(), is(1));
        assertThat(postCompileTestTask.getNextTasks().get(0).getId(), is("uploadArtifacts"));

        Task uploadArtifactsTask = postCompileTestTask.getNextTasks().stream().filter(task -> task.getId().equals("uploadArtifacts")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(uploadArtifactsTask.getName(), is("Upload artifacts"));
        assertThat(uploadArtifactsTask.getBranch(), is("^(master|develop|qa)$"));
        assertThat(uploadArtifactsTask.getCommands(), is(asList("tar zcf mylib-$GIT_HASH.tar.gz dist",
                                                                "scp -i $PRIVATE_KEY_PATH mylib-$GIT_HASH.tar.gz artifacts@upload.example.com")));
        assertThat(uploadArtifactsTask.getNextTasks().size(), is(2));
        assertThat(uploadArtifactsTask.getNextTasks().get(0).getId(), is("deployNonProd"));
        assertThat(uploadArtifactsTask.getNextTasks().get(1).getId(), is("deployProd"));

        Task deployNonProdTask = uploadArtifactsTask.getNextTasks().stream().filter(task -> task.getId().equals("deployNonProd")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(deployNonProdTask.getName(), is("Deploy (non-prod)"));
        assertThat(deployNonProdTask.getBranch(), is("^(develop|qa)$"));
        assertThat(deployNonProdTask.getCommands(), is(singletonList("./deploy.sh")));
        assertThat(deployNonProdTask.getNextTasks().size(), is(0));

        Task deployProdTask = uploadArtifactsTask.getNextTasks().stream().filter(task -> task.getId().equals("deployProd")).findFirst().orElseThrow(RuntimeException::new);
        assertThat(deployProdTask.getName(), is("Deploy to prod"));
        assertThat(deployProdTask.getBranch(), is("^master$"));
        assertThat(deployProdTask.getCommands(), is(singletonList("./deploy-prod.sh")));
        assertThat(deployProdTask.getNextTasks().size(), is(0));
    }
}
