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

package com.javamex.emutil.inspect;

import com.javamex.emutil.FileSpec;
import com.javamex.emutil.FileType;
import com.javamex.emutil.UtilFunctionParams;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class SMSInspectorParams extends UtilFunctionParams {

    protected static final FileSpec INPUT_SPEC =
            new FileSpec("InputRom", FileType.ROM_FILE, "SMS/Game Gear ROM",
                    Pattern.compile(".+\\.(?:sms|rom|gg)", Pattern.CASE_INSENSITIVE), "SMS/Game Gear ROM (*.sms, *.rom, *.gg)", false);

    @Override
    public List<FileSpec> getInputSpecs() {
        return Collections.singletonList(INPUT_SPEC);
    }

    @Override
    public List<FileSpec> getOutputSpecs() {
        return Collections.emptyList();
    }
}
