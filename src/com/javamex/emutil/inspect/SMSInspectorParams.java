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
