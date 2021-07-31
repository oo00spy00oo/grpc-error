package io.github.oo00spy00oo.intercept;

import io.grpc.Metadata;
import io.grpc.ServerCall;
import io.grpc.ServerCallHandler;
import io.grpc.ServerInterceptor;
// import io.micronaut.core.order.Ordered;
import io.github.oo00spy00oo.ex.ExceptionData;

import javax.inject.Inject;

public abstract class AbstractBackendExceptionInterceptor implements ServerInterceptor
    // , Ordered
    {

    @Inject
    ExceptionData exceptionData;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(ServerCall<ReqT, RespT> serverCall,
                                                                 Metadata metadata,
                                                                 ServerCallHandler<ReqT, RespT> serverCallHandler) {
        ServerCall.Listener<ReqT> listener = serverCallHandler.startCall(serverCall, metadata);
        if (exceptionData != null)
            return new ExceptionHandlingServerCallListener<>(listener, serverCall, metadata, exceptionData);

        return new ExceptionHandlingServerCallListener<>(listener, serverCall, metadata);
    }

    // @Override
    // public int getOrder() {
    //     return 1;
    // }

    static class ExceptionHandlingServerCallListener<ReqT, RespT> extends AbstractExceptionHandlingServerCallListener<ReqT, RespT> {

        ExceptionHandlingServerCallListener(ServerCall.Listener<ReqT> listener, ServerCall<ReqT, RespT> serverCall, Metadata metadata) {
            super(listener, serverCall, metadata);
        }

        ExceptionHandlingServerCallListener(ServerCall.Listener<ReqT> listener, ServerCall<ReqT, RespT> serverCall, Metadata metadata, ExceptionData exceptionData) {
            super(listener, serverCall, metadata);
            super.setAutoWrapThrowable(exceptionData.getAutoWrapThrowable());
        }
    }

}
