package co.yaggle.simpleci.server.exception;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class SimpleCiException extends Exception {

    public SimpleCiException(String message) {
        super(message);
    }
}
