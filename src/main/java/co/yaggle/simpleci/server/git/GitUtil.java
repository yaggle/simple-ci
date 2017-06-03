package co.yaggle.simpleci.server.git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.JschConfigSessionFactory;
import org.eclipse.jgit.transport.OpenSshConfig;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.util.FS;

import java.nio.charset.Charset;
import java.util.Optional;

public class GitUtil {

    /**
     * Create a {@link TransportConfigCallback} to authenticate with a Git repository via SSH.
     *
     * @param privateKey the private key text
     * @param publicKey  the public key text
     * @param passphrase an optional passphrase if the key requires one
     * @return a new {@link TransportConfigCallback}
     */
    public static TransportConfigCallback transportConfigCallbackForSsh(String privateKey, String publicKey, Optional<String> passphrase) {
        return (Transport transport) -> {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(new JschConfigSessionFactory() {
                @Override
                protected void configure(OpenSshConfig.Host hc, Session session) {
                }


                @Override
                protected JSch createDefaultJSch(FS fs) throws JSchException {
                    JSch jsch = super.createDefaultJSch(fs);
                    jsch.removeAllIdentity();
                    jsch.addIdentity("default",
                                     privateKey.getBytes(UTF8),
                                     publicKey.getBytes(UTF8),
                                     passphrase.map(s -> s.getBytes(UTF8)).orElse(null));
                    return jsch;
                }
            });
        };
    }


    /**
     * This {@link TransportConfigCallback} strips out all SSH identity to prevent accidental disclosure
     * of SSH private keys. This should be used for all remote Git repository interaction that doesn't
     * specifically require an SSH key pair.
     */
    public static final TransportConfigCallback SECURE_ANONYMOUS_TRANSPORT_CONFIG_CALLBACK = transport -> {
        if (transport instanceof SshTransport) {
            SshTransport sshTransport = (SshTransport) transport;
            sshTransport.setSshSessionFactory(new JschConfigSessionFactory() {
                @Override
                protected void configure(OpenSshConfig.Host hc, Session session) {
                }


                @Override
                protected JSch createDefaultJSch(FS fs) throws JSchException {
                    JSch jsch = super.createDefaultJSch(fs);
                    jsch.removeAllIdentity();
                    return jsch;
                }
            });
        }
    };


    private static final Charset UTF8 = Charset.forName("UTF-8");
}
