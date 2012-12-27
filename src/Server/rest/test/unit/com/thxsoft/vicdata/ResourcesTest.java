package com.thxsoft.vicdata;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.Test;

import com.thxsoft.json.JSONArray;
import com.thxsoft.json.JSONObject;

public class ResourcesTest {
	private static final String testUserEmail = "user.test@test.com";
	private static final String testUserPassword = "password";
	private static final String testUserName = "user.test";
	private static final String testUserStatus = "I'm OK!";
	private static long testUserId;
	private static String testUserIdStr;
	private static String accessToken;

	@BeforeClass
	public static void prepare() {
		if (!login()) {
			createAccount();
			login();
		}
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
			String result = VdHttpConnect.postMultiPart(VdHttpConnect.ROOT_URL
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
	public void testGetProfileImage() {
		Map<String, String> params = new HashMap<String, String>();
		params.put("access_token", accessToken);

		try {
			String result = VdHttpConnect.postMultiPart(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_RESOURCES_USER + testUserIdStr, params);
			
			System.out.println(result);
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
			String result = VdHttpConnect.postMultiPart(VdHttpConnect.ROOT_URL
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
			String result = VdHttpConnect.postMultiPart(VdHttpConnect.ROOT_URL
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
