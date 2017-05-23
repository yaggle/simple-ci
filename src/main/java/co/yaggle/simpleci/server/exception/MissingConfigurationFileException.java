package co.yaggle.simpleci.server.exception;

public class MissingConfigurationFileException extends SimpleCiException {
    public MissingConfigurationFileException() {
        super("This branch has no simple-ci.xml configuration file in its root.");
    }
}
