package co.yaggle.simpleci.server.git;

import lombok.NonNull;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;

import static co.yaggle.simpleci.server.git.GitUtil.*;

/**
 * Action that clones a git repository anonymously.
 */
public class CloneGitRepositoryAction {

    public void cloneGitRepository() throws GitAPIException {
        Git.cloneRepository()
                .setURI(uri)
                .setBranch(branch)
                .setDirectory(directory)
                .setTransportConfigCallback(SECURE_ANONYMOUS_TRANSPORT_CONFIG_CALLBACK)
                .call();
    }


    @NonNull
    private String uri;

    @NonNull
    private String branch;

    @NonNull
    private File directory;
}
