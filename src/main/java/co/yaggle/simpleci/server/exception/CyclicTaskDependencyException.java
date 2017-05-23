package co.yaggle.simpleci.server.exception;

public class CyclicTaskDependencyException extends SimpleCiException {
    public CyclicTaskDependencyException() {
        super("Some tasks have cyclic dependencies");
    }
}
