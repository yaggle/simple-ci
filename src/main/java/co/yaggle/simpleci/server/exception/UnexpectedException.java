package co.yaggle.simpleci.server.exception;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class UnexpectedException extends RuntimeException {

    @RequiredArgsConstructor
    public enum Type {
        PROJECT_ROOT_NOT_A_DIRECTORY("The project root is not a directory."),
        PROJECT_CONFIG_NOT_VALIDATED("An error occurred while reading the project's Simple CI configuration which should have already been validated.")
        ;

        public UnexpectedException newException() {
            throw new UnexpectedException(this);
        }

        private final String description;
    }

    private final Type type;
}
