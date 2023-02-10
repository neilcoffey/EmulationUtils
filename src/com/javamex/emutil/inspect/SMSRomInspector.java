package com.javamex.emutil.inspect;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class SMSRomInspector {

    private static final long MAX_ROM_SIZE = 1024 * 1024;
    private final Path romPath;
    private int actualROMSize;
    private int headerOffset;
    private int localeSizeCode;
    private char calculatedChecksum;
    private char specifiedChecksum;
    private final boolean expectedGameGear;
    private final byte[] romBytes;

    public enum ValidationError {
        CHECKSUM_MISMATCH("CKS", "ROM checksum error"),
        ROM_SIZE_MISMATCH("RSZ", "ROM size code does not match actual file size");
        private final String code;
        private final String userMessage;

        private ValidationError(String code, String userMessage) {
            this.code = code;
            this.userMessage = userMessage;
        }

        public String getCode() {
            return this.code;
        }

        public String getUserMessage() {
            return this.userMessage;
        }
    }

    public SMSRomInspector(Path romPath, boolean gameGear, byte[] romBytes) {
        this.romPath = romPath;
        this.expectedGameGear = gameGear;
        this.romBytes = romBytes;
    }

    public char getChecksumFromHeader() {
        return this.specifiedChecksum;
    }

    public char getCalculatedChecksum() {
        return this.calculatedChecksum;
    }

    public int getDeclaredROMSize() {
        return getStandardROMSize(this.localeSizeCode);
    }

    public boolean hasChecksumMatch() {
        return specifiedChecksum == calculatedChecksum;
    }

    public int getSizeAndRegionCode() {
        return this.localeSizeCode;
    }

    public List<ValidationError> getValidationErrors() {
        List<ValidationError> ret = new ArrayList<>();
        if (expectedGameGear) {
            if (!hasChecksumMatch()) {
                ret.add(ValidationError.CHECKSUM_MISMATCH);
            }
        }
        if (actualROMSize != getStandardROMSize(localeSizeCode)) {
            ret.add(ValidationError.ROM_SIZE_MISMATCH);
        }
        return ret;
    }

    public void readHeaderInfo() throws IOException {
        long fSize = Files.size(romPath);
        if (fSize > MAX_ROM_SIZE) {
            throw new IOException("Files size exceeds maximum permitted ROM size");
        }
        actualROMSize = (int) fSize;

        byte[] bytes = (romBytes == null) ? Files.readAllBytes(romPath) : romBytes;
        int headerOffset;

        if (bytes.length == 8192) {
            headerOffset = 0x1ff0;
        } else if (bytes.length == 16384) {
            headerOffset = 0x3ff0;
        } else {
            headerOffset = 0x7ff0;
        }

        ByteBuffer bb = ByteBuffer.wrap(bytes);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        specifiedChecksum = bb.getChar(headerOffset + 0xA);
        localeSizeCode = bb.get(headerOffset + 0xf) & 0xff;

        int totalRomSize = getStandardROMSize(localeSizeCode);
        if (totalRomSize > 0) {
            char checksum = 0;

            //See https://www.smspower.org/Development/BIOSes

            // Checksum includes bytes up to the header
            for (int i = 0; i < headerOffset; i ++) {
                char word = (char) (bb.get(i) & 0xff);
                checksum += word;
            }

            if (totalRomSize > (headerOffset + 16)) {
                for (int i = headerOffset+16; i < totalRomSize; i++) {
                    char word = (char) (bb.get(i) & 0xff);
                    checksum += word;
                }
            }

            this.calculatedChecksum = checksum;
        }
    }

    public static int getStandardROMSize(int sizeCode) {
        switch (sizeCode & 0xf) {
            case 0xa :
                return 8 * 1024;
            case 0xb :
                return 16 * 1024;
            case 0xc :
                return 32 * 1024;
            case 0xd :
                return 48 * 1024;
            case 0xe :
                return 64 * 1024;
            case 0xf :
                return 128 * 1024;
            case 0 :
                return 256 * 1024;
            case 1 :
                return 512 * 1024;
            case 2 :
                return 1024 * 1024;
            default:
                return -1;
        }
    }

}
