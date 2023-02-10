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

import com.javamex.emutil.*;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.CRC32;

public class BPSPatcher extends UtilFunction<BPSPatchParams> {
    private static final int AX_COPY_FROM_SAME_OFFSET = 0;
    private static final int AX_COPY_PATCH_HYTES_TO_TARGET = 1;
    private static final int AX_COPY_FROM_ARBITRARY_SOURCE_POS = 2;
    private static final int AX_CLONE_DATA_FROM_TARGET = 3;

    private static final int[] HEADER_BYTES = {0x42, 0x50, 0x53, 0x31};
    private ProgressConsumer progressConsumer;

    /*public static void main(String[] args) {
        try {
            Path dir = Paths.get("/Users/neilcoffey/IdeaProjects/MSEmulator/ROMS");
            Path smsFile = dir.resolve("PhantasyStarJapanese.sms");
            Path patchFile = dir.resolve("PhantasyStarTransPatch.bps");

            BPSPatcher patcher = new BPSPatcher(smsFile, patchFile);
            Path output = dir.resolve("PhantasyStarNewTrans.sms");
            patcher.createPatchedFile(output);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }*/

    public BPSPatcher(BPSPatchParams params) {
        super(UtilFunctionType.PATCH_BPS, params);
    }

    @Override
    public void runFunction(ProgressConsumer progressConsumer) {
        this.progressConsumer = progressConsumer;
        try {
            createPatchedFile(progressConsumer);
        } catch (Throwable t) {
            progressConsumer.onFatalError(t);
        } finally {
            this.progressConsumer = null;
        }
    }

    public void createPatchedFile(ProgressConsumer progressConsumer) throws IOException {
        Path inputFile = getParams().getInput(BPSPatchParams.SPEC_INPUT_ROM);
        Path patchFile = getParams().getInput(BPSPatchParams.SPEC_PATCH_FILE);

        byte[] origData = Files.readAllBytes(inputFile);
        ByteBuffer patchDef = ByteBuffer.wrap(Files.readAllBytes(patchFile));
        patchDef.order(ByteOrder.LITTLE_ENDIAN);
        expect(BPSPatchParams.SPEC_PATCH_FILE, patchDef, HEADER_BYTES);

        long expectedLen = readInteger(patchDef);
        if (expectedLen != origData.length) {
            throw new IllegalArgumentException("Invalid patch definition: expected length " + expectedLen + " but we have " + origData.length);
        }

        long outputLen = readInteger(patchDef);
        progressConsumer.onDebugMessage("Output file size: " + outputLen);

        byte[] output = new byte[(int) outputLen];
        long metaLen = readInteger(patchDef);
        progressConsumer.onDebugMessage("Meta data len: " + metaLen);
        if (metaLen > 0) {
            patchDef.position(patchDef.position() + (int) metaLen);
        }

        int outputPos = 0;
        int inReadPos = 0;
        int outReadPos = 0;
        while (patchDef.position() < (patchDef.limit() - 12)) {
            // Each successive 'action' encoded as an action type in the lower
            // two bits and a 'length' in the remaining bits. (This generally
            // determines how many bytes are copied to the target file.)
            long instr = readInteger(patchDef);
            int action = (int) instr & 0x3;
            int len = (int) (instr >>> 2) + 1;

            switch (action) {
                case AX_COPY_FROM_SAME_OFFSET:
                    // Copy source from current pos
                    progressConsumer.onDebugMessage("Copy " + len + " from same pos (" + outputPos + ")");
                    for (int i = 0; i < len; i++) {
                        output[outputPos] = origData[outputPos];
                        outputPos++;
                    }
                    break;
                case AX_COPY_PATCH_HYTES_TO_TARGET:
                    // Read target
                    progressConsumer.onDebugMessage("Read " + len + " bytes of target");
                    for (int i = 0; i < len; i++) {
                        output[outputPos++] = patchDef.get();
                    }
                    break;
                case AX_COPY_FROM_ARBITRARY_SOURCE_POS :
                    // Copy source from specified pos
                    inReadPos += (int) readOffset(patchDef);
                    progressConsumer.onDebugMessage("Copy " + len + " bytes from 0x" + Integer.toUnsignedString(inReadPos, 16) + " to " + outputPos);
                    for (int i = 0; i < len; i++) {
                        output[outputPos++] = origData[inReadPos++];
                    }
                    break;
                case AX_CLONE_DATA_FROM_TARGET :
                    // Clone data from target
                    outReadPos += (int) readOffset(patchDef);
                    progressConsumer.onDebugMessage("Clone " + len + " bytes from " + outReadPos + " to " + outputPos);
                    for (int i = 0; i < len; i++) {
                        output[outputPos++] = output[outReadPos++];
                    }
                    break;
            }
        }

        //long sourceCRC = (long) patchDef.getInt() & 0xffffffffL;
        long targetCRC = (long) patchDef.getInt() & 0xffffffffL;

        CRC32 crc = new CRC32();
        crc.update(output);
        long actualCRC = crc.getValue();
        progressConsumer.onDebugMessage("Expected CRC = " + actualCRC + "; got " + targetCRC);
        if (actualCRC != targetCRC) {
            progressConsumer.onSourceError(BPSPatchParams.SPEC_PATCH_FILE, inReadPos, "CRC mismatch");
        }

        Path out = getParams().getOutput(BPSPatchParams.SPEC_OUTPUT);
        Files.write(out, output);

        progressConsumer.onDebugMessage("Written data to " + out);
    }

    /**
     * Read variable multi-byte integer from patch definition file. The value is encoded
     * as a series of bytes in which the bottom 7 bits are part of the target value and the
     * top bit is an 'end-of-value' marker.
     */
    private long readInteger(ByteBuffer bb) {
        long ret = 0;
        int sh = 0;

        // Repeatedly read bytes and append to the value until the top bit is set
        while (true) {
            int i8 = readU8(bb);
            ret += ((long) i8 & 0x7f) << sh;
            if ((i8 & 0x80) != 0) {
                // End of value is marked by top bit being set
                return ret;
            }
            sh += 7;
            // In reality, an additional byte always encodes 'n+1' (there would
            // be no point in appending an extra byte to encode zero!)
            ret += (1L << sh);

            if (sh > (64 - 7)) {
                progressConsumer.onSourceError(BPSPatchParams.SPEC_PATCH_FILE, bb.position(),
                        "Value would have exceeded 8 bytes");
                throw new IllegalArgumentException("Value too big");
            }
        }
    }

    private long readOffset(ByteBuffer bb) {
        long raw = readInteger(bb);
        return (((raw & 1) == 1) ? -1 : 1) * (raw >>> 1);
    }

    private int readU8(ByteBuffer bb) {
        return bb.get() & 0xff;
    }

    private void expect(FileSpec file, ByteBuffer bb, int... bytes) {
        for (int i = 0; i < bytes.length; i++) {
            if (readU8(bb) != bytes[i]) {
                progressConsumer.onSourceError(file, i, "Invalid data");
                throw new IllegalArgumentException("Invalid data at offset " + i);
            }
        }
    }

}
