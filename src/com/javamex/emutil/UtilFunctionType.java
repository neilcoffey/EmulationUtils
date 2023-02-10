package com.javamex.emutil;

import com.javamex.emutil.inspect.SMSInspector;
import com.javamex.emutil.inspect.SMSInspectorParams;
import com.javamex.emutil.patch.BPSPatchParams;
import com.javamex.emutil.patch.BPSPatcher;

public enum UtilFunctionType {

    PATCH_BPS("Apply BPS Patch", BPSPatcher.class, BPSPatchParams.class),
    INSPECT_SMS_ROM("Inspect SMS/Game Gear ROM", SMSInspector.class, SMSInspectorParams.class);

    private final String displayTitle;
    private final Class<? extends UtilFunctionParams> paramsClass;
    private final Class<? extends UtilFunction> functionClass;

    UtilFunctionType(String displayTitle,
                     Class<? extends UtilFunction> functionClass,
                     Class<? extends UtilFunctionParams> paramsClass) {
        this.displayTitle = displayTitle;
        this.functionClass = functionClass;
        this.paramsClass = paramsClass;
    }

    public String getDisplayTitle() {
        return this.displayTitle;
    }

    public Class<? extends UtilFunctionParams> getParamsClass() {
        return this.paramsClass;
    }

    public Class<? extends UtilFunction> getFunctionClass() {
        return this.functionClass;
    }

    @Override
    public String toString() {
        return displayTitle;
    }
}
