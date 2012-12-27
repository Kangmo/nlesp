package com.thxsoft.vicdata;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;

class VdHttpConnect {
	public static final String ROOT_URL_PRODUCTION = "http://nhnsoft.com:8080/rest/1";
	public static final String ROOT_URL = ROOT_URL_PRODUCTION;

	public static final String URL_CREATE_ACCOUNT = "/account/create_user_account.json";
	public static final String URL_LOGIN = "/account/login.json";
	public static final String URL_UPDATE_PROFILE = "/account/update_profile.json";
	
	public static final String URL_SHOW = "/users/show.json";
	public static final String URL_LOOKUP = "/users/lookup.json";
	public static final String URL_SEARCH = "/users/search.json";
	
	public static final String URL_CREATE_FRIENDSHIP = "/friendships/create.json";
	public static final String URL_DESTROY_FRIENDSHIP ="/friendships/destroy.json";
	public static final String URL_GET_FOLLOWERS = "/followers/ids.json";
	public static final String URL_GET_FOLLOWINGS = "/friends/ids.json";
	
	public static final String URL_TIMELINE = "/statuses/home_timeline.json";
	public static final String URL_POST_MESSAGE = "/statuses/update.json";
	
	public static final String URL_RESOURCES_USER = "/resources/user/";

	public static String get(String urlString, Map<String, String> params)
			throws IOException {
		String body = buildParams(params);

		URL url = new URL(urlString + "?" + body);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();

		connection.setRequestMethod("GET");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);

		String response = getRequest(connection);
		// System.out.println("Response: " + response);
		return response;
	}

	private static final String LINE_END = "\r\n";
	private static final String TWO_HYPHENS = "--";
	private static final String BOUNDARY = "*****";

	public static String postMultiPart(String urlString,
			Map<String, String> params) throws IOException {

		// generate body.
		byte[] body = buildBody(params);

		// create connection and config.
		HttpURLConnection connection = (HttpURLConnection) new URL(urlString)
				.openConnection();
		connection.setDoInput(true);
		connection.setDoOutput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("POST");
		connection.setInstanceFollowRedirects(false);

		// write body info.
		connection.setRequestProperty("Connection", "Keep-Alive");
		connection.setRequestProperty("Content-Type",
				"multipart/form-data; charset=UTF-8; boundary=" + BOUNDARY);
		connection.setRequestProperty("Content-Length",
				Integer.toString(body.length));

		OutputStream dos = connection.getOutputStream();
		dos.write(body);
		dos.flush();

		String response = getRequest(connection);
		// System.out.println("Response: " + response);
		return response;
	}

	public static String post(String urlString, Map<String, String> params)
			throws IOException {

		// generate body.
		String body = buildParams(params);
		System.out.println(body);
		URL url = new URL(urlString);

		// create connection and config.
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setDoInput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		// connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Content-Length",
				"" + Integer.toString(body.getBytes().length));
		connection.setUseCaches(false);

		DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
		wr.write(body.getBytes());
		wr.flush();
		wr.close();

		String response = getRequest(connection);
		// System.out.println("Response: " + response);
		return response;
	}

	private static byte[] buildBody(Map<String, String> params)
			throws IOException {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(byteOut);

		for (Iterator<String> i = params.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();

			dos.writeBytes(TWO_HYPHENS + BOUNDARY + LINE_END);
			dos.writeBytes("content-disposition: form-data; name=\"" + key
					+ "\"" + LINE_END);
			dos.writeBytes(LINE_END);
			dos.writeUTF(String.valueOf(params.get(key)));
			dos.writeBytes(LINE_END);
		}

		dos.writeBytes(TWO_HYPHENS + BOUNDARY + TWO_HYPHENS + LINE_END);

		return byteOut.toByteArray();
	}

	private static String buildParams(Map<String, String> params) {
		StringBuilder builder = new StringBuilder("");

		for (Iterator<String> i = params.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();

			builder.append(key);
			builder.append('=');
			builder.append(params.get(key));
			if (i.hasNext()) {
				builder.append('&');
			}
		}

		return builder.toString();
	}

	private static String getRequest(HttpURLConnection connection)
			throws UnsupportedEncodingException, IOException {
		InputStream in = null;

		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		byte[] buf = new byte[2048];
		try {

			System.currentTimeMillis();

			in = connection.getInputStream();

			while (true) {
				int readlen = in.read(buf);
				if (readlen < 1)
					break;
				bos.write(buf, 0, readlen);
			}

			return new String(bos.toByteArray(), "UTF-8");
		} catch (IOException e) {
			if (connection.getResponseCode() == 500) {
				bos.reset();
				InputStream err = connection.getErrorStream();
				while (true) {
					int readlen = err.read(buf);

					if (readlen < 1)
						break;
					bos.write(buf, 0, readlen);
				}

				String output = new String(bos.toByteArray(), "UTF-8");

				System.err.println(output);
			}

			throw e;

		} finally {
			if (in != null)
				in.close();

			if (connection != null)
				connection.disconnect();
		}
	}

}
