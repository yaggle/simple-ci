package co.yaggle.simpleci.core.pipeline.parser;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * This type of exception is caused by bugs and is therefore not expected
 * to be thrown.
 */
@RequiredArgsConstructor
public class UnexpectedPipelineParserException extends RuntimeException {

    @RequiredArgsConstructor
    public enum Type {
        PROJECT_ROOT_NOT_A_DIRECTORY("The project root is not a directory."),
        PROJECT_CONFIG_NOT_VALIDATED("An error occurred while reading the project's pipeline which should have already been validated.")
        ;

        public UnexpectedPipelineParserException newException() {
            throw new UnexpectedPipelineParserException(this);
        }

        @Getter
        private final String description;
    }

    @Getter
    private final Type type;
}
