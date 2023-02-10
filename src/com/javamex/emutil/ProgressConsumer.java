package com.javamex.emutil;

public interface ProgressConsumer {

    void onDebugMessage(String msg);

    void onSourceError(FileSpec file, long fileOffset, String message);

    void onFatalError(Throwable t);

}
