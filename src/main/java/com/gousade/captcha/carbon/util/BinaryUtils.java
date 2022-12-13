package com.gousade.captcha.carbon.util;


import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class BinaryUtils {

	private BinaryUtils() {
		throw new IllegalStateException("no instance");
	}

	public static int readLe32(@NotNull byte[] buf, int index) {
		return buf[index] & 0xFF | (buf[index + 1] << 8) & 0xff00
				| (buf[index + 2] << 16) & 0xff0000 | (buf[index + 3] << 24) & 0xff000000;
	}

	public static int readLe16(@NotNull byte[] buf, int off) {
		return (buf[off] & 0xFF) | ((buf[off + 1] << 8) & 0xff00);
	}

	public static long readLe64(@NotNull byte[] buf, int off) {
		return ((long) readLe32(buf, off) & 0xFFFFFFFFL) | (((long) readLe32(buf, off + 4) & 0xFFFFFFFFL) << 32);
	}

	public static void writeLe16(@NotNull byte[] buf, int off, int value) {
		buf[off] = (byte) (value & 0xFF);
		buf[off + 1] = (byte) ((value >> 8) & 0xFF);
	}

	public static void writeLe32(@NotNull byte[] buf, int index, int value) {
		buf[index] = (byte) value;
		buf[index + 1] = (byte) (value >>> 8);
		buf[index + 2] = (byte) (value >>> 16);
		buf[index + 3] = (byte) (value >>> 24);
	}

	public static void writeLe64(@NotNull byte[] buf, int index, long value) {
		writeLe32(buf, index, (int) value);
		writeLe32(buf, index + 4, (int) (value >>> 32));
	}

	public static int readLe32(@NotNull InputStream is) throws IOException {
		byte b1 = (byte) is.read();
		byte b2 = (byte) is.read();
		byte b3 = (byte) is.read();
		byte b4 = (byte) is.read();
		return (b1 & 0xFF) | ((b2 << 8) & 0xff00) | ((b3 << 16) & 0xff0000) | ((b4 << 24) & 0xff000000);
	}

}
