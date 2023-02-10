package com.javamex.emutil;

import java.util.Objects;
import java.util.regex.Pattern;

public class FileSpec {

    private final String key;
    private final FileType fileType;
    private final String displayName;
    private final Pattern fileNamePattern;
    private final String filePatternDesc;
    private final boolean output;

    public FileSpec(String key, FileType type, String displayName, Pattern fileNamePattern, String filePatternDesc, boolean output) {
        Objects.requireNonNull(key);
        Objects.requireNonNull(type);
        this.key = key;
        this.fileType = type;
        this.displayName = displayName;
        this.fileNamePattern = fileNamePattern;
        this.filePatternDesc = filePatternDesc;
        this.output = output;
    }

    public FileType getFileType() {
        return this.fileType;
    }

    public Pattern getFileNamePattern() {
        return this.fileNamePattern;
    }

    public String getFilePatternDesc() {
        return this.filePatternDesc;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isOutput() {
        return output;
    }

    @Override
    public int hashCode() {
        return fileType.hashCode() + 17 * key.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof FileSpec otherSpec) {
            return (otherSpec.fileType == this.fileType &&
                    otherSpec.key.equals(this.key));
        } else {
            return false;
        }
    }
}
