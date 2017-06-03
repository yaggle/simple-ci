package co.yaggle.simpleci.server.git;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;

import static co.yaggle.simpleci.server.git.GitUtil.*;

/**
 * Action that clones a git repository using a username and password.
 */
@Value
@Builder
public class CloneGitRepositoryWithUsernameAndPasswordAction {

    public void cloneGitRepository() throws GitAPIException {
        Git.cloneRepository()
                .setURI(uri)
                .setBranch(branch)
                .setDirectory(directory)
                .setCredentialsProvider(new UsernamePasswordCredentialsProvider(username, password))
                .setTransportConfigCallback(SECURE_ANONYMOUS_TRANSPORT_CONFIG_CALLBACK)
                .call();
    }


    @NonNull
    private String uri;

    @NonNull
    private String branch;

    @NonNull
    private String username;

    @NonNull
    private String password;

    @NonNull
    private File directory;
}
