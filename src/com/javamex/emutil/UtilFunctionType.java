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
