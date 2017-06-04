package co.yaggle.simpleci.core.pipeline.parser;

import co.yaggle.simpleci.core.exception.SimpleCiException;

public class MissingPipelineException extends SimpleCiException {
    public MissingPipelineException() {
        super("This branch has no simple-ci.xml configuration file in its root.");
    }
}
