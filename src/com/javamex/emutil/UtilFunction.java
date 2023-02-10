package com.javamex.emutil;

import com.javamex.emutil.inspect.SMSInspector;
import com.javamex.emutil.inspect.SMSInspectorParams;
import com.javamex.emutil.patch.BPSPatchParams;
import com.javamex.emutil.patch.BPSPatcher;

public abstract class UtilFunction<P extends UtilFunctionParams> {

    private final UtilFunctionType functionType;
    private final P params;

    @SuppressWarnings("unchecked")
    public static <T extends UtilFunctionParams> UtilFunction<T> getFunction(UtilFunctionType type, T params) {
        UtilFunction<T> ret;
        switch (type) {
            case PATCH_BPS:
                ret = (UtilFunction<T>) new BPSPatcher((BPSPatchParams) params);
                break;
            case INSPECT_SMS_ROM:
                ret =  (UtilFunction<T>) new SMSInspector((SMSInspectorParams) params);
                break;
            default:
                throw new IllegalArgumentException("Unknown util function type");
        }
        return ret;
    }

    public abstract void runFunction(ProgressConsumer progressConsumer);

    protected P getParams() {
        return this.params;
    }

    protected UtilFunction(UtilFunctionType functionType, P params) {
        this.functionType = functionType;
        this.params = params;
    }

}
