package io.github.oo00spy00oo.ex;

public class DataNotFoundException extends RuntimeException {

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
