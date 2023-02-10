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

package com.javamex.emutil.patch;

import com.javamex.emutil.FileSpec;
import com.javamex.emutil.FileType;
import com.javamex.emutil.UtilFunctionParams;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class BPSPatchParams extends UtilFunctionParams {

    protected static final FileSpec SPEC_INPUT_ROM =
            new FileSpec("InputRom", FileType.ROM_FILE, "ROM to patch", Pattern.compile(".+"), "Source ROM", false);
    protected static final FileSpec SPEC_PATCH_FILE =
            new FileSpec("InputPatch", FileType.PATCH_FILE, "Patch file", Pattern.compile(".+\\.bps", Pattern.CASE_INSENSITIVE),
                    "BPS patch definition (*.bps)", false);
    protected static final FileSpec SPEC_OUTPUT =
            new FileSpec("OutputRom", FileType.ROM_FILE, "Output ROM file", Pattern.compile(".+"), "Output ROM", true);

    private static final List<FileSpec> INPUT_SPECS = Collections.unmodifiableList(Arrays.asList(SPEC_INPUT_ROM, SPEC_PATCH_FILE));
    private static final List<FileSpec> OUTPUT_SPECS = Collections.singletonList(SPEC_OUTPUT);

    @Override
    public List<FileSpec> getInputSpecs() {
        return INPUT_SPECS;
    }

    @Override
    public List<FileSpec> getOutputSpecs() {
        return OUTPUT_SPECS;
    }

}
