import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

// 00001 = 19

public class StressTestSetup {
	static final int OFFSET = 18;
	static final String format = "%1$05d@test.com";

	public static void main(String[] args) throws IOException {
		// prefareAccounts(1, 10000);
		// follow(6855, 10000);
		// postMessage(101, 1000);
		for (int i = 1; i < 10000; i += 100) {
			final int start = i;
			final int stop = i + 99;
			Thread task = new Thread(new Runnable() {
				public void run() {
					try {
						postMessage(start, stop);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
			task.start();
		}
	}

	private static void prefareAccounts(int fromId, int toId)
			throws IOException {
		Map<String, String> params = new HashMap<String, String>();

		for (int i = fromId; i <= toId; i++) {
			String login_id = String.format(format, i);
			params.put("login_id", login_id);
			params.put("password", login_id);
			params.put("name", login_id);
			params.put("status", login_id);

			String result = VdHttpConnect.postMultiPart(
					VdHttpConnect.VD_ROOT_URL
							+ VdHttpConnect.VD_URL_CREATE_ACCOUNT, params);
			System.out.println(result);
		}
	}

	private static void follow(int fromId, int toId) throws IOException {
		for (int i = fromId; i <= toId; i++) {
			int diff = i % 10;
			if (diff == 0) {
				diff = 10;
			}
			int friendIdStart = i - diff + 1;

			int j = friendIdStart;
			if (j % 10 != 0) {
				for (; j < friendIdStart + 10; j++) {
					if (j != i) {
						makeFriend(i, j);
					}
				}
			} else {
				for (; j > friendIdStart - 10; j--) {
					if (j != i) {
						makeFriend(i, j);
					}
				}
			}
		}
	}

	private static void makeFriend(int me, int friend) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		String myId = Integer.toString(me + OFFSET);
		params.put("access_token", myId + ":" + myId);
		params.put("user_id", Integer.toString(friend + OFFSET));

		String result = VdHttpConnect.post(VdHttpConnect.VD_ROOT_URL
				+ VdHttpConnect.VD_URL_CREATE_FRIENDSHIP, params);
		System.out.println(result);
	}

	private static void postMessage(int fromId, int toId) throws IOException {
		Map<String, String> params = new HashMap<String, String>();
		for (int i = fromId; i <= toId; i++) {
			String myId = Integer.toString(i + OFFSET);
			params.put("access_token", myId + ":" + myId);
			for (int j = 0; j < 2; j++) {
				params.put("status", "It's " + myId + "'s " + j + "th posting.");

				String result = VdHttpConnect.post(VdHttpConnect.VD_ROOT_URL
						+ VdHttpConnect.VD_URL_POST_MESSAGE, params);
				// System.out.println(result);
			}
		}
	}
}
