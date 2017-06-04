package co.yaggle.simpleci.core.pipeline;

import co.yaggle.simpleci.core.exception.SimpleCiException;

public class CyclicTaskDependencyException extends SimpleCiException {
    public CyclicTaskDependencyException() {
        super("Some tasks have cyclic dependencies");
    }
}
