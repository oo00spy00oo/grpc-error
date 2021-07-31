package io.github.oo00spy00oo.ex;

import io.grpc.Status;

import java.util.Map;

public interface ExceptionData {

    @SuppressWarnings("unused")
    Map<Class<? extends Throwable>, Status> getAutoWrapThrowable();

}
