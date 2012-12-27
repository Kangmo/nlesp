import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

public class StressTest {
	public static void main(String[] args) throws InterruptedException {
		Unit[] units = new Unit[100];
		if (args.length > 0) {
			units = new Unit[Integer.valueOf(args[0])];
		}
		for (int i = 0; i < units.length; i++) {
			units[i] = new Unit(i + 1);
			units[i].start();
		}

		for (int i = 0; i < units.length; i++) {
			units[i].join();
		}

		long sum = 0L;
		for (int i = 0; i < units.length; i++) {
			sum += units[i].elaspedTime;
		}

		System.out.println("AVG: " + (sum / units.length));
	}
}

class Unit extends Thread {
	private final long id;
	long elaspedTime;

	Unit(int id) {
		this.id = id;
	}

	public void run() {
		String login_id = String.format(StressTestSetup.format, id);
		String myId = Long.toString(id + StressTestSetup.OFFSET);
		String access_token = myId + ":" + myId;

		long startTime = System.currentTimeMillis();
		long endTime = 0L;
		try {
			// login
			login(login_id);

			// get timeline (-1, 19)
			getTimeline(access_token, -1, 19);

			// get followers list
			String followers = getFollowers(access_token);
			followers = parseIds(followers);
			// System.out.println(followers);

			// get followers' profiles.
			getUsers(access_token, followers);

			// get followings list
			String followings = getFollowings(access_token);
			followings = parseIds(followings);

			// get followings' profiles.
			getUsers(access_token, followings);

			// sleep 10 secs.
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// post a message
			// postMessage(access_token);

			// sleep .5 secs.
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// get timeline (20, 40);
			getTimeline(access_token, 20, 40);
			endTime = System.currentTimeMillis();
		} catch (IOException e1) {
			endTime = startTime + 10000000;
			e1.printStackTrace();
		}

		elaspedTime = endTime - startTime;
		System.err.println(id);
	}

	private String parseIds(String ids) {
		StringTokenizer tokenizer = new StringTokenizer(ids, "[,] \n\t\r");
		StringBuilder builder = new StringBuilder();
		while (tokenizer.hasMoreTokens()) {
			String id = tokenizer.nextToken();
			builder.append(id);
			if (tokenizer.hasMoreTokens()) {
				builder.append(',');
			}
		}

		return builder.toString();
	}

	private void postMessage(String access_token) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", access_token);
		params.put("status", id + "' new message");

		String result = VdHttpConnect.post(VdHttpConnect.VD_ROOT_URL
				+ VdHttpConnect.VD_URL_POST_MESSAGE, params);
		// System.out.println(result);
	}

	private void getUsers(String access_token, String followers)
			throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", access_token);
		params.put("user_id", followers);

		String result = VdHttpConnect.get(VdHttpConnect.VD_ROOT_URL
				+ VdHttpConnect.VD_URL_GET_USERS, params);
		// System.out.println(result);
	}

	private String getFollowings(String access_token) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", access_token);

		String result = VdHttpConnect.get(VdHttpConnect.VD_ROOT_URL
				+ VdHttpConnect.VD_URL_GET_FOLLOWINGS, params);
		// System.out.println(result);
		return result;
	}

	private String getFollowers(String access_token) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", access_token);

		String result = VdHttpConnect.get(VdHttpConnect.VD_ROOT_URL
				+ VdHttpConnect.VD_URL_GET_FOLLOWERS, params);
		// System.out.println(result);

		return result;
	}

	private void getTimeline(String access_token, long sinceId, long maxId)
			throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", access_token);
		params.put("since_id", Long.toString(sinceId));
		params.put("max_id", Long.toString(maxId));

		String result = VdHttpConnect.get(VdHttpConnect.VD_ROOT_URL
				+ VdHttpConnect.VD_URL_TIMELINE, params);
		// System.out.println(result);
	}

	private void login(String login_id) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		params.put("login_id", login_id);
		params.put("password", login_id);
		params.put("client_version", Integer.toString(1));

		String result = VdHttpConnect.post(VdHttpConnect.VD_ROOT_URL
				+ VdHttpConnect.VD_URL_LOGIN, params);
		// System.out.println(result);
	}
}

class VdHttpConnect {
//	public static String VD_ROOT_URL_LOCAL = null;
//	static {
//		String localIp;
//		try {
//			localIp = InetAddress.getLocalHost().getHostAddress();
//			VD_ROOT_URL_LOCAL = "http://" + localIp + ":8080/rest/1";
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//	}
	public static final String VD_ROOT_URL_PRODUCTION = "http://nhnsoft.com:8080/rest/1";
	public static final String VD_ROOT_URL = VD_ROOT_URL_PRODUCTION;

	public static final String VD_URL_CREATE_ACCOUNT = "/account/create_user_account.json";
	public static final String VD_URL_LOGIN = "/account/login.json";
	public static final String VD_URL_CREATE_FRIENDSHIP = "/friendships/create.json";
	public static final String VD_URL_GET_FOLLOWERS = "/followers/ids.json";
	public static final String VD_URL_GET_FOLLOWINGS = "/friends/ids.json";
	public static final String VD_URL_GET_USERS = "/users/lookup.json";
	public static final String VD_URL_TIMELINE = "/statuses/home_timeline.json";
	public static final String VD_URL_POST_MESSAGE = "/statuses/update.json";

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
