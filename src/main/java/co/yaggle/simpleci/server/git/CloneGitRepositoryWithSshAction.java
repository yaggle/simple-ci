package co.yaggle.simpleci.server.git;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.util.Optional;

import static co.yaggle.simpleci.server.git.GitUtil.*;

/**
 * Action that clones a Git repository via authenticated SSH.
 */
@Value
@Builder
public class CloneGitRepositoryWithSshAction {

    public void cloneGitRepository() throws GitAPIException {
        Git.cloneRepository()
                .setURI(uri)
                .setBranch(branch)
                .setDirectory(directory)
                .setTransportConfigCallback(transportConfigCallbackForSsh(privateKey, publicKey, Optional.ofNullable(passphrase)))
                .call();
    }


    @NonNull
    private String uri;

    @NonNull
    private String branch;

    @NonNull
    private String privateKey;

    @NonNull
    private String publicKey;

    private String passphrase;

    @NonNull
    private File directory;
}
