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
