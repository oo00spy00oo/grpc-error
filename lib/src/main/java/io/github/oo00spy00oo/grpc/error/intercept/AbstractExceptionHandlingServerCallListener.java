package io.github.oo00spy00oo.intercept;

import io.grpc.ForwardingServerCallListener;
import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.Status;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public abstract class AbstractExceptionHandlingServerCallListener<ReqT, RespT> extends ForwardingServerCallListener.SimpleForwardingServerCallListener<ReqT> {

    @Setter
    private Map<Class<? extends Throwable>, Status> autoWrapThrowable = new ConcurrentHashMap<>();

    private final ServerCall<ReqT, RespT> serverCall;
    private final Metadata metadata;

    protected AbstractExceptionHandlingServerCallListener(ServerCall.Listener<ReqT> listener, ServerCall<ReqT, RespT> serverCall, Metadata metadata) {
        super(listener);
        this.serverCall = serverCall;
        this.metadata = metadata;
    }

    @Override
    public void onHalfClose() {
        try {
            super.onHalfClose();
        } catch (Exception ex) {
            handle(ex);
        }
    }

    @Override
    public void onReady() {
        try {
            super.onReady();
        } catch (Exception ex) {
            handle(ex);
        }
    }

    final void handle(Exception ex) {
        if (ex instanceof RuntimeException) {
            log.warn("Runtime Exception [{}]: {}", ex.getClass().getName(), Optional.ofNullable(ex.getMessage()).orElse(""));
            handleRuntimeException(ex);
        } else {
            log.error("Unknown Exception!", ex);
            handleException();
        }
    }

    public void handleRuntimeException(Exception ex) {
        Optional<Status> status = autoWrapThrowable.entrySet().stream()
            .filter(e -> e.getKey().equals(ex.getClass()))
            .map(Map.Entry::getValue)
            .findFirst();

        serverCall.close(status.orElse(Status.UNKNOWN).withDescription(ex.getMessage()), metadata);
    }

    public void handleException() {
        serverCall.close(Status.INTERNAL, metadata);
    }
}