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

import java.lang.reflect.Constructor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class UtilFunctionParams {

    private final Map<FileSpec, Path> inputs = new HashMap<>();
    private final Map<FileSpec, Path> outputs = new HashMap<>();

    public static UtilFunctionParams constructParams(UtilFunctionType type) throws Exception {
        Class<? extends UtilFunctionParams> paramsClass = type.getParamsClass();
        Constructor<? extends UtilFunctionParams> constructor = paramsClass.getConstructor();
        return constructor.newInstance();
    }

    public abstract List<FileSpec> getInputSpecs();

    public abstract List<FileSpec> getOutputSpecs();

    public void setInput(FileSpec spec, Path input) {
        inputs.put(spec, input);
    }

    public Path getInput(FileSpec spec) {
        return inputs.get(spec);
    }

    public void setOutput(FileSpec spec, Path output) {
        outputs.put(spec, output);
    }

    public Path getOutput(FileSpec spec) {
        return outputs.get(spec);
    }

    public void validate() throws ParameterValidationException {
        for (FileSpec fs : getInputSpecs()) {
            Path p = getInput(fs);
            if (p == null)
                throw new ParameterValidationException("Missing parameter: " + fs.getDisplayName());
            if (!Files.exists(p)) {
                throw new ParameterValidationException("The specified " + fs.getDisplayName() + " does not exist");
            }
            if (!Files.isReadable(p)) {
                throw new ParameterValidationException("The specified " + fs.getDisplayName() + " is not readable");
            }
        }

        for (FileSpec fs : getOutputSpecs()) {
            Path p = getOutput(fs);
            if (p == null)
                throw new ParameterValidationException("Missing parameter: " + fs.getDisplayName());
            Path parentDir = p.getParent();
            if (!Files.isWritable(parentDir)) {
                throw new ParameterValidationException("Cannot write to: " + parentDir);
            }
        }
    }

}
