package io.github.oo00spy00oo.ex;

import com.google.common.base.VerifyException;
import io.grpc.Status;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractExceptionDataImpl implements ExceptionData {

    @Getter
    private final Map<Class<? extends Throwable>, Status> autoWrapThrowable = new ConcurrentHashMap<>();

    public AbstractExceptionDataImpl() {
        initException();
        backendException();
    }

    final void initException() {
        addException(VerifyException.class, Status.INVALID_ARGUMENT);
        addException(IllegalArgumentException.class, Status.INVALID_ARGUMENT);
    }

    protected void backendException() {
        addException(DataNotFoundException.class, Status.INVALID_ARGUMENT);
    }

    protected void addException(Class<? extends Throwable> ex, Status status) {
        autoWrapThrowable.put(ex, status);
    }
}
