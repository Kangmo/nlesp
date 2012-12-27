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

import com.thxsoft.json.JSONObject;

public class AccountTest {
	private static String testUserEmail;
	private static long testUserId;
	private static String testUserIdStr;
	private static String accessToken;

	private static final String updatedUserName = "new user name";
	private static final String updatedStatus = "new status";

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
	public void testUpdateProfile() {
		Map<String, String> params = new HashMap<String, String>();

		String login_id = testUserEmail;
		params.put("access_token", accessToken);
		params.put("login_id", login_id);
		params.put("name", updatedUserName);
		params.put("status", updatedStatus);

		try {
			VdHttpConnect.postMultiPart(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_UPDATE_PROFILE, params);

			params.clear();
			params.put("access_token", accessToken);
			params.put("user_id", testUserIdStr);

			String result = VdHttpConnect.postMultiPart(VdHttpConnect.ROOT_URL
					+ VdHttpConnect.URL_SHOW, params);
			JSONObject user = new JSONObject(result);
			assertUser(user);

		} catch (IOException e) {
			fail(e.getMessage());
		}
	}

	private void assertUser(JSONObject user) {
		assertEquals(testUserId, user.getLong("id"));
		assertEquals(testUserIdStr, user.getString("id_str"));
		assertEquals(testUserEmail, user.getString("screen_name"));
		assertEquals(updatedUserName, user.getString("name"));
		assertEquals(updatedStatus, user.getString("description"));
		assertTrue(user.getBoolean("default_profile_image"));
	}
}
