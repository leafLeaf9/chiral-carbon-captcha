package com.gousade.util;


import java.util.Base64;

public class ImageUtils {
	public static String toBase64DataUri(byte[] bytes, String format) {
		return String.format("data:image/%s;base64,%s", format, Base64.getEncoder().encodeToString(bytes));
	}
}
