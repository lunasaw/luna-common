package com.luna.common.media;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author luna
 */
public class PcmUtils {
    public PcmUtils() {}

    public static void convert2Wav(String inPcmFilePath, String outWavFilePath, int sampleRate, int channels, int bitNum) {
        convert2Wav(new File(inPcmFilePath), new File(outWavFilePath), sampleRate, channels, bitNum);
    }

    public static void convert2Wav(File inPcmFile, File outWavFile, int sampleRate, int channels, int bitNum) {
        FileInputStream in = null;
        FileOutputStream out = null;
        byte[] data = new byte[1024];

        try {
            long byteRate = (long) sampleRate * channels * bitNum / 8;
            in = new FileInputStream(inPcmFile);
            out = new FileOutputStream(outWavFile, false);
            long totalAudioLen = in.getChannel().size();
            long totalDataLen = totalAudioLen + 36L;
            writeWaveFileHeader(out, totalAudioLen, totalDataLen, sampleRate, channels, byteRate);
            boolean var14 = false;

            int length;
            while ((length = in.read(data)) > 0) {
                out.write(data, 0, length);
            }
        } catch (Exception var27) {
            var27.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException var26) {
                    var26.printStackTrace();
                }
            }

            if (out != null) {
                try {
                    out.close();
                } catch (IOException var25) {
                    var25.printStackTrace();
                }
            }

        }

    }

    public static void Pcm2WavBytes(byte[] pcm, byte[] wav, int sampleRate, int channels, int bitNum) {
        pcm2Wav(pcm, wav, sampleRate, channels, bitNum);
    }

    public static void pcm2Wav(byte[] pcm, byte[] wav, int sampleRate, int channels, int bitNum) {
        byte[] header = new byte[44];
        long byteRate = (long) sampleRate * channels * bitNum / 8;
        long totalAudioLen = pcm.length;
        long totalDataLen = totalAudioLen + 36L;
        writeWaveBytesHeader(header, totalAudioLen, totalDataLen, sampleRate, channels, byteRate);
        System.arraycopy(header, 0, wav, 0, header.length);
        System.arraycopy(pcm, 0, wav, header.length, pcm.length);
    }

    private static void writeWaveFileHeader(FileOutputStream out, long totalAudioLen, long totalDataLen, int sampleRate, int channels, long byteRate)
        throws IOException {
        byte[] header = new byte[44];
        writeWaveBytesHeader(header, totalAudioLen, totalDataLen, sampleRate, channels, byteRate);
        out.write(header, 0, 44);
    }

    private static void writeWaveBytesHeader(byte[] header, long totalAudioLen, long totalDataLen, int sampleRate, int channels, long byteRate) {
        header[0] = 82;
        header[1] = 73;
        header[2] = 70;
        header[3] = 70;
        header[4] = (byte)((int)(totalDataLen & 255L));
        header[5] = (byte)((int)(totalDataLen >> 8 & 255L));
        header[6] = (byte)((int)(totalDataLen >> 16 & 255L));
        header[7] = (byte)((int)(totalDataLen >> 24 & 255L));
        header[8] = 87;
        header[9] = 65;
        header[10] = 86;
        header[11] = 69;
        header[12] = 102;
        header[13] = 109;
        header[14] = 116;
        header[15] = 32;
        header[16] = 16;
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1;
        header[21] = 0;
        header[22] = (byte)channels;
        header[23] = 0;
        header[24] = (byte)(sampleRate & 255);
        header[25] = (byte)(sampleRate >> 8 & 255);
        header[26] = (byte)(sampleRate >> 16 & 255);
        header[27] = (byte)(sampleRate >> 24 & 255);
        header[28] = (byte)((int)(byteRate & 255L));
        header[29] = (byte)((int)(byteRate >> 8 & 255L));
        header[30] = (byte)((int)(byteRate >> 16 & 255L));
        header[31] = (byte)((int)(byteRate >> 24 & 255L));
        header[32] = (byte)(channels * 16 / 8);
        header[33] = 0;
        header[34] = 16;
        header[35] = 0;
        header[36] = 100;
        header[37] = 97;
        header[38] = 116;
        header[39] = 97;
        header[40] = (byte)((int)(totalAudioLen & 255L));
        header[41] = (byte)((int)(totalAudioLen >> 8 & 255L));
        header[42] = (byte)((int)(totalAudioLen >> 16 & 255L));
        header[43] = (byte)((int)(totalAudioLen >> 24 & 255L));
    }
}
