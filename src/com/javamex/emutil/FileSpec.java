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
