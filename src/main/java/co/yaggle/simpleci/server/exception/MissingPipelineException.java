package co.yaggle.simpleci.server.exception;

public class MissingPipelineException extends SimpleCiException {
    public MissingPipelineException() {
        super("This branch has no simple-ci.xml configuration file in its root.");
    }
}
