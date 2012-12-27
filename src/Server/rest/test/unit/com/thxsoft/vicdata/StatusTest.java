package com.thxsoft.vicdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import com.thxsoft.json.JSONArray;
import com.thxsoft.json.JSONObject;

public class StatusTest {
	private static String testUserEmail;
	private static long testUserId;
	private static String testUserIdStr;
	private static String accessToken;

	@BeforeClass
	public static void prepare() {
		testUserEmail = UUID.randomUUID().toString() + "@test.test";

		createAccount();
		login();
	}

	private static void createAccount() {
		Map<String, String> params = new HashMap<String, String>();

		String login_id = testUserEmail;
		params.put("login_id", login_id);
		params.put("password", login_id);
		params.put("name", login_id);
		params.put("status", login_id);

		try {
			String result = VdHttpConnect.postMultiPart(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_CREATE_ACCOUNT, params);
			testUserIdStr = result.trim();
			System.out.println(testUserIdStr);
			testUserId = Long.valueOf(testUserIdStr);
			assertTrue(testUserId > 0);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	public static void login() {
		Map<String, String> params = new HashMap<String, String>();

		String login_id = testUserEmail;
		params.put("login_id", login_id);
		params.put("password", login_id);
		params.put("client_version", Long.toString(1));

		try {
			String result = VdHttpConnect.post(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_LOGIN, params);
			JSONObject userWithSignature = new JSONObject(result);

			assertEquals(testUserId, userWithSignature.getLong("id"));
			assertEquals(testUserIdStr, userWithSignature.getString("id_str"));

			accessToken = testUserIdStr + ":"
					+ userWithSignature.getString("signature");
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testUpdate() {
		Map<String, String> params = new HashMap<String, String>();

		params.put("access_token", accessToken);
		params.put("since_id", "-1");
		params.put("max_id", "-1");

		try {
			// get timeline.
			String result = VdHttpConnect.get(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_UPDATE_PROFILE, params);

			JSONArray tweets = new JSONArray(result);
			assertTrue(tweets.length() == 0);

			// post a message
			params.clear();
			params.put("access_token", accessToken);
			params.put("status", "hello");
			result = VdHttpConnect.post(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_POST_MESSAGE, params);

			JSONObject tweet = new JSONObject(result);
			long tweetId = tweet.getLong("id");
			long replyToStatusId = tweet.getLong("in_reply_to_status_id");
			long replyToUserId = tweet.getLong("in_reply_to_user_id");
			assertTrue(tweetId > 0);
			assertEquals(Long.toString(tweetId), tweet.getString("id_str"));
			assertTrue(replyToStatusId > tweetId);
			assertEquals(Long.toString(replyToStatusId),
					tweet.getString("in_reply_to_status_id_str"));
			assertEquals(testUserId, replyToUserId);
			assertEquals("hello", tweet.getString("text"));

			JSONObject user = tweet.getJSONObject("user");
			long userId = user.getLong("id");
			String userIdStr = user.getString("id_str");
			String userName = user.getString("name");
			String userEmail = user.getString("screen_name");
			assertEquals(testUserId, userId);
			assertEquals(testUserIdStr, userIdStr);
			assertEquals(testUserEmail, userName);
			assertEquals(testUserEmail, userEmail);

			// get timeline
			result = VdHttpConnect.get(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_UPDATE_PROFILE, params);
			
			tweets = new JSONArray(result);
			assertTrue(tweets.length() == 1);
			JSONObject tweet2 = tweets.getJSONObject(0);
			assertEquals(tweet.getLong("id"), tweet2.getLong("id"));
			assertEquals(tweet.getString("id_str"), tweet2.getString("id_str"));
			assertEquals(tweet.getLong("in_reply_to_status_id"), tweet2.getLong("in_reply_to_status_id"));
			assertEquals(tweet.getString("in_reply_to_status_id_str"), tweet2.getString("in_reply_to_status_id_str"));
			assertEquals(tweet.getLong("in_reply_to_user_id"), tweet2.getLong("in_reply_to_user_id"));
			assertEquals(tweet.getString("in_reply_to_user_id_str"), tweet2.getString("in_reply_to_user_id_str"));
			assertEquals(tweet.getString("text"), tweet2.getString("text"));
			
			JSONObject user2 = tweet2.getJSONObject("user");
			assertEquals(user.getLong("id"), user2.getLong("id"));
			assertEquals(user.getString("id_str"), user2.getString("id_str"));
			assertEquals(user.getString("name"), user2.getString("name"));
			assertEquals(user.getString("screen_name"), user2.getString("screen_name"));

		} catch (IOException e) {
			fail(e.getMessage());
		}
	}
}
