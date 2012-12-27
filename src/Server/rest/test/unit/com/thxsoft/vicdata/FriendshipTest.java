package com.thxsoft.vicdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import org.junit.BeforeClass;
import org.junit.Test;

import com.thxsoft.json.JSONArray;
import com.thxsoft.json.JSONObject;

public class FriendshipTest {
	private static final String testUserEmail = "user.test@test.com";
	private static final String testUserPassword = "password";
	private static final String testUserName = "user.test";
	private static final String testUserStatus = "I'm OK!";
	private static long testUserId;
	private static String testUserIdStr;
	private static String accessToken;

	private static String friendEmail01 = UUID.randomUUID().toString()
			+ "@test.test";
	private static String friendEmail02 = UUID.randomUUID().toString()
			+ "@test.test";
	private static String friendId01;
	private static String friendId02;

	@BeforeClass
	public static void prepare() {
		friendId01 = createAccount(friendEmail01);
		friendId02 = createAccount(friendEmail02);

		if (!login()) {
			createAccount();
			login();
		}
	}

	private static String createAccount(String email) {
		Map<String, String> params = new HashMap<String, String>();

		params.put("login_id", email);
		params.put("password", email);
		params.put("name", email);
		params.put("status", email);

		try {
			String result = VdHttpConnect.postMultiPart(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_CREATE_ACCOUNT, params);
			return result.trim();
		} catch (IOException e) {
			fail(e.getMessage());
		}
		return null;
	}

	private static void createAccount() {
		Map<String, String> params = new HashMap<String, String>();

		String login_id = testUserEmail;
		params.put("login_id", login_id);
		params.put("password", testUserPassword);
		params.put("name", testUserName);
		params.put("status", testUserStatus);

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

	public static boolean login() {
		Map<String, String> params = new HashMap<String, String>();

		String login_id = testUserEmail;
		params.put("login_id", login_id);
		params.put("password", testUserPassword);
		params.put("client_version", Long.toString(1));

		try {
			String result = VdHttpConnect.post(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_LOGIN, params);
			JSONObject userWithSignature = new JSONObject(result);

			testUserId = userWithSignature.getLong("id");
			testUserIdStr = userWithSignature.getString("id_str");
			accessToken = testUserIdStr + ":"
					+ userWithSignature.getString("signature");
		} catch (IOException e) {
			return false;
		}
		return true;
	}

	@Test
	public void testFollowings() {
		Map<String, String> params = new HashMap<String, String>();

		try {
			// check followings
			params.put("access_token", accessToken);
			String result = VdHttpConnect.get(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_GET_FOLLOWINGS, params);
			JSONArray users = new JSONArray(result);
			assertTrue(users.length() == 0);

			// follow friend01
			params.clear();
			params.put("access_token", accessToken);
			params.put("user_id", friendEmail01);
			result = VdHttpConnect.post(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_CREATE_FRIENDSHIP, params);
			JSONObject user = new JSONObject(result);
			assertEquals(friendId01, user.getString("id_str"));
			assertEquals(friendEmail01, user.getString("screen_name"));

			// check followings
			result = VdHttpConnect.get(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_GET_FOLLOWINGS, params);
			StringTokenizer tokenizer = new StringTokenizer(result, "[, ]");
			assertTrue(tokenizer.countTokens() == 1);
			assertEquals(friendId01, tokenizer.nextToken());

			// follow friend02
			params.clear();
			params.put("access_token", accessToken);
			params.put("user_id", friendEmail02);
			result = VdHttpConnect.post(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_CREATE_FRIENDSHIP, params);
			user = new JSONObject(result);
			assertEquals(friendId02, user.getString("id_str"));
			assertEquals(friendEmail02, user.getString("screen_name"));

			// check followings
			result = VdHttpConnect.get(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_GET_FOLLOWINGS, params);
			tokenizer = new StringTokenizer(result, "[, ]");
			assertTrue(tokenizer.countTokens() == 2);
			String id01 = tokenizer.nextToken();
			String id02 = tokenizer.nextToken();
			assertTrue((id01.equals(friendId01) || id01.equals(friendId02)));
			if (id01.equals(friendId01)) {
				assertTrue(id02.equals(friendId02));
			} else {
				assertTrue(id02.equals(friendId01));
			}

			// destory friend01
			params.clear();
			params.put("access_token", accessToken);
			params.put("user_id", friendEmail01);
			result = VdHttpConnect.post(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_DESTROY_FRIENDSHIP, params);
			user = new JSONObject(result);
			assertEquals(friendId01, user.getString("id_str"));
			assertEquals(friendEmail01, user.getString("screen_name"));

			// check followings
			result = VdHttpConnect.get(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_GET_FOLLOWINGS, params);
			tokenizer = new StringTokenizer(result, "[, ]");
			assertTrue(tokenizer.countTokens() == 2);
			String id = tokenizer.nextToken();
			assertTrue(id.equals(friendId02));
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testFollowers() {
		Map<String, String> params = new HashMap<String, String>();

		try {
			// check followings
			params.put("access_token", accessToken);
			String result = VdHttpConnect.get(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_GET_FOLLOWERS, params);
			StringTokenizer tokenizer = new StringTokenizer(result, "[, ]");
			assertTrue(tokenizer.countTokens() == 0);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testSearch() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);
		params.put("q", testUserEmail);

		try {
			String result = VdHttpConnect.post(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_SHOW, params);
			JSONArray users = new JSONArray(result);
			assertTrue(users.length() == 1);
			JSONObject user = users.getJSONObject(0);

			assertUser(user);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	@Test
	public void testLookup() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);
		params.put("user_id", testUserIdStr);

		try {
			String result = VdHttpConnect.post(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_LOOKUP, params);
			JSONArray users = new JSONArray(result);
			assertTrue(users.length() == 1);
			JSONObject user = users.getJSONObject(0);

			assertUser(user);
		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	private void assertUser(JSONObject user) {
		assertEquals(testUserId, user.getLong("id"));
		assertEquals(testUserIdStr, user.getString("id_str"));
		assertEquals(testUserEmail, user.getString("screen_name"));
		assertEquals(testUserName, user.getString("name"));
		assertEquals(testUserStatus, user.getString("description"));
		assertTrue(user.getBoolean("default_profile_image"));
	}
}
