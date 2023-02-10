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
