/*
BSD 2-Clause License

Copyright (c) 2023, Neil Coffey

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:

1. Redistributions of source code must retain the above copyright notice, this
   list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice,
   this list of conditions and the following disclaimer in the documentation
   and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
