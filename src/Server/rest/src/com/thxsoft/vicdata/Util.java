package com.thxsoft.vicdata;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

public class Util {
	private static final String LOG_HEADER = "%1$s : %2$s : %3$s : {%4$s}";
	public static Charset charset = Charset.forName("UTF-8");
	public static CharsetDecoder decoder = charset.newDecoder();
	public static CharsetEncoder encoder = charset.newEncoder();

	public static String bb_to_str(ByteBuffer buffer) {
		String data = "";
		try {
			int old_position = buffer.position();
			data = decoder.decode(buffer).toString();
			// reset buffer's position to its original so it is not altered:
			buffer.position(old_position);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return data;
	}

	public static long extractUserIdFromAccessToken(String accessToken) {
		StringTokenizer tokens = new StringTokenizer(accessToken, ":");
		return Long.valueOf(tokens.nextToken());
	}

	public static Date getTwitterDate(String date) throws ParseException {
		final String TWITTER_DATE_FORMAT = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";

		SimpleDateFormat sf = new SimpleDateFormat(TWITTER_DATE_FORMAT);
		sf.setLenient(true);

		return sf.parse(date);
	}

	public static ByteBuffer str_to_bb(String msg) {
		try {
			return encoder.encode(CharBuffer.wrap(msg));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static String toTwitterFormat(Date date) {
		// VKIT_FIX addition should be calculated based on server's location.
		Calendar cal = Calendar.getInstance(Locale.ROOT);
		cal.setTime(date);
		Locale locale = Locale.ROOT;
		String twitterTime = String.format(
				"%s %s %02d %02d:%02d:%02d +0900 %04d", cal.getDisplayName(
						Calendar.DAY_OF_WEEK, Calendar.SHORT, locale),
				cal.getDisplayName(Calendar.MONDAY, Calendar.SHORT, locale),
				cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY),
				cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND), cal
						.get(Calendar.YEAR));
		return twitterTime;
	}

	public static String getLog(HttpServletRequest request)
			throws IOException, IllegalStateException {
		String remoteAddress = request.getRemoteAddr();
		String uri = ((HttpServletRequest) request).getRequestURI();
		String protocol = request.getMethod();

		Enumeration<String> paramNames = request.getParameterNames();
		StringBuilder paramBuilder = new StringBuilder("");
		while (paramNames.hasMoreElements()) {
			String paramName = paramNames.nextElement();
			paramBuilder.append(paramName);
			paramBuilder.append('=');
			paramBuilder.append(request.getParameter(paramName));
			if (paramNames.hasMoreElements()) {
				paramBuilder.append(',');
				paramBuilder.append(' ');
			}
		}

		return String.format(LOG_HEADER, remoteAddress, protocol, uri,
				paramBuilder.toString());
	}

}
