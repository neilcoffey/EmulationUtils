package com.javamex.emutil.inspect;

import com.javamex.emutil.ProgressConsumer;
import com.javamex.emutil.UtilFunction;
import com.javamex.emutil.UtilFunctionType;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class SMSInspector extends UtilFunction<SMSInspectorParams> {
    private static final long MAX_ROM_SIZE = 1024 * 1024 * 32;
    private static final Pattern GAME_GEAR_FILENAME_PATTERN = Pattern.compile(".+\\.gg", Pattern.CASE_INSENSITIVE);

    public SMSInspector(SMSInspectorParams params) {
        super(UtilFunctionType.INSPECT_SMS_ROM, params);
    }

    @Override
    public void runFunction(ProgressConsumer progressConsumer) {
        try {
            Path inputROM = getParams().getInput(SMSInspectorParams.INPUT_SPEC);
            long size = Files.size(inputROM);
            if (size > MAX_ROM_SIZE) {
                throw new IOException("Input file exceeds permitted ROM size");
            }

            progressConsumer.onDebugMessage("Inspecting: " + inputROM + " (" + size + " bytes)");

            // For now, assume we can just load all ROM bytes into memory - we're not
            // catering for ROMs that are so big!
            byte[] romBytes = Files.readAllBytes(inputROM);
            Stream.of("MD5", "SHA-1").forEach(scheme -> {
                try {
                    String digest = getDigestString(romBytes, scheme);
                    progressConsumer.onDebugMessage("ROM hash (" + scheme + "): " + digest);
                } catch (Exception e) {
                    progressConsumer.onDebugMessage("Scheme not available: " + scheme);
                    e.printStackTrace();
                }
            });

            SMSRomInspector ins = new SMSRomInspector(inputROM, isGameGearROM(inputROM), romBytes);
            ins.readHeaderInfo();
            progressConsumer.onDebugMessage("Declared checksum: " + Integer.toHexString(ins.getChecksumFromHeader()));
            progressConsumer.onDebugMessage("Actual checksum: " + Integer.toHexString(ins.getCalculatedChecksum()));
            int declSize = ins.getDeclaredROMSize();
            if (declSize == -1) {
                progressConsumer.onDebugMessage("Unknown ROM size specifier");
            } else {
                progressConsumer.onDebugMessage(String.format("Declared size: %d bytes (%d K)",
                        declSize, declSize / 1024));
            }
        } catch (Throwable t) {
            progressConsumer.onFatalError(t);
        }
    }

    private static boolean isGameGearROM(Path p) {
        Matcher m = GAME_GEAR_FILENAME_PATTERN.matcher(p.getFileName().toString());
        return m.matches();
    }

    private static String getDigestString(byte[] b, String scheme) throws Exception {
        MessageDigest md = MessageDigest.getInstance(scheme);
        byte[] dig = md.digest(b);
        StringBuilder sb = new StringBuilder(dig.length * 2);
        for (byte bt : dig) {
            sb.append(String.format("%02x", bt & 0xff));
        }
        return sb.toString();
    }

}
