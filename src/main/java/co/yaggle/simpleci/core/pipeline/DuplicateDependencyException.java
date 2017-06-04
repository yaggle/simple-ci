package co.yaggle.simpleci.core.pipeline;

import co.yaggle.simpleci.core.exception.SimpleCiException;

public class DuplicateDependencyException extends SimpleCiException {
    public DuplicateDependencyException() {
        super("Some tasks have duplicate dependencies");
    }
}
