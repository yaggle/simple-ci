package co.yaggle.simpleci.server.exception;

public class DuplicateDependencyException extends SimpleCiException {
    public DuplicateDependencyException() {
        super("Some tasks have duplicate dependencies");
    }
}
