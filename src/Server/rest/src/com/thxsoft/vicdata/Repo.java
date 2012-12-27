package com.thxsoft.vicdata;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.ServletContext;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.thxsoft.json.JSONException;
import com.thxsoft.json.JSONStringer;
import com.thxsoft.json.JSONWriter;
import com.thxsoft.vds.thrift.AuthSignature;
import com.thxsoft.vds.thrift.ContextMessage;
import com.thxsoft.vds.thrift.ContextMessageContent;
import com.thxsoft.vds.thrift.ErrorDesc;
import com.thxsoft.vds.thrift.MessageComment;
import com.thxsoft.vds.thrift.ReqAuthenticateUser;
import com.thxsoft.vds.thrift.ReqCommentMessage;
import com.thxsoft.vds.thrift.ReqLoadComments;
import com.thxsoft.vds.thrift.ReqPullMessages;
import com.thxsoft.vds.thrift.ReqSendMessage;
import com.thxsoft.vds.thrift.ResAuthenticateUser;
import com.thxsoft.vds.thrift.ResCancelFriend;
import com.thxsoft.vds.thrift.ResCommentMessage;
import com.thxsoft.vds.thrift.ResCreateUserProfile;
import com.thxsoft.vds.thrift.ResLoadComments;
import com.thxsoft.vds.thrift.ResLoadFollowerUIDs;
import com.thxsoft.vds.thrift.ResLoadFriendUIDs;
import com.thxsoft.vds.thrift.ResLoadUserProfiles;
import com.thxsoft.vds.thrift.ResPullMessages;
import com.thxsoft.vds.thrift.ResRequestFriend;
import com.thxsoft.vds.thrift.ResSearchUsers;
import com.thxsoft.vds.thrift.ResSendMessage;
import com.thxsoft.vds.thrift.ResUpdateUserProfile;
import com.thxsoft.vds.thrift.UserProfile;
import com.thxsoft.vds.thrift.VicDataService;

public class Repo {
	private static final String SERVER_IP_THRIFT_PRODUCTION = "192.168.0.41";
	private static final String CHARSET_UTF_8 = "UTF-8";
	private static final String KEY_CODE = "code";
	private static final String KEY_COMMENT = "comment";
	private static final String KEY_CREATED_AT = "created_at";
	private static final String KEY_DEFAULT_PROFILE_IMAGE = "default_profile_image";
	private static final String KEY_DESCRIPTION = "description";
	private static final String KEY_DETAILED_CODE = "detailed_code";
	private static final String KEY_DETAILED_MESSAGE_ARGS = "detailed_message_args";
	private static final String KEY_DETAILED_MESSAGE_FORMAT = "detailed_message_format";
	private static final String KEY_ID = "id";
	private static final String KEY_ID_STR = "id_str";
	private static final String KEY_IN_REPLY_TO_STATUS_ID = "in_reply_to_status_id";
	private static final String KEY_IN_REPLY_TO_STATUS_ID_STR = "in_reply_to_status_id_str";
	private static final String KEY_IN_REPLY_TO_USER_ID = "in_reply_to_user_id";
	private static final String KEY_IN_REPLY_TO_USER_ID_STR = "in_reply_to_user_id_str";
	private static final String KEY_MESSAGE = "message";
	private static final String KEY_NAME = "name";
	private static final String KEY_PROFILE_IMAGE_URL = "profile_image_url";
	private static final String KEY_RETWEET_COUNT = "retweet_count";
	private static final String KEY_SCREEN_NAME = "screen_name";
	private static final String KEY_SIGNATURE2 = "signature";
	private static final String KEY_TEXT = "text";
	private static final String KEY_USER = "user";
	private static Map<VicDataService.Client, TTransport> transportMap = new HashMap<VicDataService.Client, TTransport>();
	private static Map<Long, UserProfile> profileMap = new HashMap<Long, UserProfile>();
	private static Map<Long, Long> profileTimestampMap = new HashMap<Long, Long>();

	private static void closeConnection(VicDataService.Client client) {
		if (client == null) {
			return;
		}

		TTransport transport = transportMap.get(client);
		if (transport != null) {
			transport.close();
		}
	}

	public static String createAccount(String loginId, String password,
			String name, String status, ByteBuffer photo, ServletContext context)
			throws ServerException {
		VicDataService.Client client = null;
		try {
			client = getThriftClient();

			UserProfile profile = new UserProfile();
			profile.email = loginId;
			profile.encryptedPassword = password;
			profile.name = name;
			profile.statusMessage = status;
			profile.photo = photo;
			
			ResCreateUserProfile res = client.createUserProfile(
					new AuthSignature("", ""), profile);
			if (res.error == null) {
				return res.getCreatedUserId();
			} else {
				throw new ServerException(toJson(res.error, context));

			}
		} catch (TException e) {
			context.log(e.getMessage(), e);
		} finally {
			closeConnection(client);
		}
		return null;
	}

	/**
	 * @return profile of friend.
	 * @throws ServerException
	 */
	public static String createFriendship(String accessToken, long friendId,
			ServletContext context) throws ServerException {
		VicDataService.Client client = null;
		try {
			client = getThriftClient();
			ResRequestFriend res = client.requestFriend(
					toAuthSignature(accessToken), Long.toString(friendId));
			if (res.error == null) {
//				context.log("New friend: ");
				return toJson(res.friendProfile, context);
			} else {
				throw new ServerException(toJson(res.error, context));

			}
		} catch (TException e) {
			context.log(e.getMessage(), e);
		} finally {
			closeConnection(client);
		}
		return null;
	}

	/**
	 * @return profile of destroyed friend.
	 * @throws ServerException
	 */
	public static String destroyFriendship(String accessToken, long friendId,
			ServletContext context) throws ServerException {
		VicDataService.Client client = null;
		try {
			client = getThriftClient();
			ResCancelFriend res = client.cancelFriend(
					toAuthSignature(accessToken), Long.toString(friendId));
			if (res.error == null) {
				return toJson(res.canceldFriendProfile, context);
			} else {
				throw new ServerException(toJson(res.error, context));

			}
		} catch (TException e) {
			context.log(e.getMessage(), e);
		} finally {
			closeConnection(client);
		}
		return null;
	}

	public static List<String> getComments(String accessToken, long tweetId,
			long tweetOwnerId, ServletContext context)
			throws NumberFormatException, JSONException,
			UnsupportedEncodingException, ServerException {
		VicDataService.Client client = null;
		try {
			client = getThriftClient();
			AuthSignature signature = toAuthSignature(accessToken);
			ReqLoadComments req = new ReqLoadComments();
			req.setSenderContextID(tweetOwnerId);
			req.setSenderMessageID(tweetId);

			ResLoadComments res = client.loadComments(signature, req);
			if (res.error == null) {
				List<MessageComment> commentList = res.getCommentList();
//				context.log("Number of pulled comments: " + commentList.size());
				List<String> jsonComments = new ArrayList<String>(
						commentList.size());
				for (MessageComment message : commentList) {
					jsonComments.add(toJson(signature, message, context));
				}
				return jsonComments;
			} else {
				throw new ServerException(toJson(res.error, context));

			}
		} catch (TException e) {
			context.log(e.getMessage(), e);
		} finally {
			closeConnection(client);
		}
		return null;
	}

	/**
	 * @return comma-separated user IDs.
	 * @throws ServerException
	 */
	public static String getFollowerIds(String accessToken,
			ServletContext context) throws ServerException {
		AuthSignature signature = toAuthSignature(accessToken);
		String ids = "";
		VicDataService.Client client = null;
		try {
			client = getThriftClient();
			ResLoadFollowerUIDs res = client.loadFollowerUIDs(signature,
					signature.uid);
			if (res.error == null) {
				List<String> friendIds = res.getFollowerUIDs();

				if (friendIds != null) {
					for (String friendId : friendIds) {
						ids += (friendId + ",");
					}
				}

				return ids;
			} else {
				throw new ServerException(toJson(res.error, context));

			}
		} catch (TException e) {
			context.log(e.getMessage(), e);
		} finally {
			closeConnection(client);
		}
		return ids;
	}

	/**
	 * @return comma-separated user IDs.
	 * @throws ServerException
	 */
	public static String getFriendIds(String accessToken, ServletContext context)
			throws ServerException {
		AuthSignature signature = toAuthSignature(accessToken);
		String ids = "";
		VicDataService.Client client = null;
		try {
			client = getThriftClient();
			ResLoadFriendUIDs res = client.loadFriendUIDs(signature,
					signature.uid);
			if (res.error == null) {
				List<String> friendIds = res.getFriendUIDs();

				if (friendIds != null) {
					for (String friendId : friendIds) {
						ids += (friendId + ",");
					}
				}

				return ids;
			} else {
				throw new ServerException(toJson(res.error, context));

			}
		} catch (TException e) {
			context.log(e.getMessage(), e);
		} finally {
			closeConnection(client);
		}
		return ids;
	}

	/**
	 * @return list of tweets.
	 * @throws UnsupportedEncodingException
	 * @throws JSONException
	 * @throws NumberFormatException
	 * @throws ServerException
	 */
	public static List<String> getHomeTimeline(String accessToken,
			long startId, long stopId, ServletContext context)
			throws NumberFormatException, JSONException,
			UnsupportedEncodingException, ServerException {
		List<String> tweets = new ArrayList<String>();
		VicDataService.Client client = null;
		try {
			client = getThriftClient();

			ReqPullMessages req = new ReqPullMessages();
			req.setStartMessageID(startId);
			req.setStopMessageID(stopId);

			AuthSignature signature = toAuthSignature(accessToken);

			ResPullMessages res = client.pullMessages(signature, req);
			if (res.error == null) {
				List<ContextMessage> messages = res.getMessageList();
//				context.log("Number of pulled messages: " + messages.size());
				for (ContextMessage message : messages) {
					tweets.add(toJson(signature, message, context));
				}
				return tweets;
			} else {
				throw new ServerException(toJson(res.error, context));
			}
		} catch (TException e) {
			context.log(e.getMessage(), e);
		} finally {
			closeConnection(client);
		}
		return tweets;
	}

	public static String getMyProfile(AuthSignature signature, String uid,
			ServletContext context) throws ServerException {
		VicDataService.Client client = null;
		try {
			client = getThriftClient();
			List<String> uids = new ArrayList<String>();
			uids.add(uid);

			ResLoadUserProfiles res = client.loadUserProfiles(signature, uids);
			if (res.error == null) {
				List<UserProfile> profiles = res.getUserProfiles();
				UserProfile profile = profiles.get(0);
//				context.log("My profile: ");
				return toJson(profile, signature.getSignature(), context);
			} else {
				throw new ServerException(toJson(res.error, context));

			}
		} catch (TException e) {
			context.log(e.getMessage(), e);
		} finally {
			closeConnection(client);
		}
		return null;
	}

	private static VicDataService.Client getThriftClient()
			throws TTransportException {
		// VKIT_FIX share instance if possible.
		// TTransport transport = new TFramedTransport(new TSocket(
		// "192.168.0.123", 9090));
		// TTransport transport = new TFramedTransport(new TSocket(
		// "nhnsoft.com", 9090));
		TTransport transport = new TFramedTransport(new TSocket(
				SERVER_IP_THRIFT_PRODUCTION, 9090));
		transport.open();
		TProtocol protocol = new TBinaryProtocol(transport);
		VicDataService.Client client = new VicDataService.Client(protocol);
		transportMap.put(client, transport);
		return client;
	}

	public static byte[] getUserImage(String accessToken, String uid,
			ServletContext context) throws ServerException {
		VicDataService.Client client = null;
		try {
			client = getThriftClient();
			List<String> uids = new ArrayList<String>();
			uids.add(uid);

			ResLoadUserProfiles res = client.loadUserProfiles(
					toAuthSignature(accessToken), uids);
			if (res.error == null) {
				List<UserProfile> profiles = res.getUserProfiles();
				UserProfile profile = profiles.get(0);
				return profile.getPhoto();
			} else {
				throw new ServerException(toJson(res.error, context));
			}
		} catch (TException e) {
			context.log(e.getMessage(), e);
		} finally {
			closeConnection(client);
		}
		return null;
	}

	private static String getUserProfile(AuthSignature signature, String uid,
			ServletContext context) throws ServerException {
		VicDataService.Client client = null;
		try {
			client = getThriftClient();
			List<String> uids = new ArrayList<String>();
			uids.add(uid);

			ResLoadUserProfiles res = client.loadUserProfiles(signature, uids);
			if (res.error == null) {
				List<UserProfile> profiles = res.getUserProfiles();
				UserProfile profile = profiles.get(0);
//				context.log("User profile: ");
				return toJson(profile, context);
			} else {
				throw new ServerException(toJson(res.error, context));
			}
		} catch (TException e) {
			context.log(e.getMessage(), e);
		} finally {
			closeConnection(client);
		}
		return null;
	}

	/**
	 * @return user profile.
	 * @throws ServerException
	 */
	public static String getUserProfile(String accessToken, long userId,
			ServletContext context) throws ServerException {
		return getUserProfile(toAuthSignature(accessToken),
				Long.toString(userId), context);
	}

	/**
	 * @return logined user profile.
	 * @throws ServerException
	 */
	public static String login(String loginId, String password,
			long clientVersion, ServletContext context) throws ServerException {
		VicDataService.Client client = null;
		try {
			client = getThriftClient();
			ReqAuthenticateUser req = new ReqAuthenticateUser();
			req.setEmail(loginId);
			req.setEncryptedPassword(password);
			req.setClientVersion(clientVersion);
			// VKIT auth signature should be ignored.
			ResAuthenticateUser res = client.authenticateUser(
					new AuthSignature(), req);
//			context.log("ResAuthenticateUser: error = " + res.error);
			if (res.error == null) {
				return getMyProfile(res.getAuthSignature(), res
						.getAuthSignature().getUid(), context);
			} else {
				throw new ServerException(toJson(res.error, context));

			}
		} catch (TException e) {
			context.log(e.getMessage(), e);
			// return null;
		} finally {
			closeConnection(client);
		}
		return null;
	}

	public static String postComment(String accessToken, String comment,
			Long replyToId, Long tweetOwnerId, ServletContext context)
			throws NumberFormatException, JSONException,
			UnsupportedEncodingException, ServerException {
		AuthSignature signature = toAuthSignature(accessToken);

		ReqCommentMessage req = new ReqCommentMessage();
		req.setContextID(tweetOwnerId);
		req.setMessageID(replyToId);
		req.setComment(Util.str_to_bb(comment));
//		context.log("Comment: context_ID = " + tweetOwnerId + ", message_ID = "
//				+ replyToId + ", comment = " + comment);
		VicDataService.Client client = null;
		try {
			client = getThriftClient();
			ResCommentMessage res = client.commentMessage(signature, req);
			if (res.error == null) {
				// get tweet
				List<String> tweets = getHomeTimeline(accessToken, replyToId,
						replyToId + 1, context);
//				context.log("-----------num of tweets: " + tweets.size());

				if (tweets.size() > 0) {
					return tweets.get(0);
				} else {
					return "unknown error";
				}
			} else {
				throw new ServerException(toJson(res.error, context));

			}
		} catch (TException e) {
			context.log(e.getMessage(), e);
		} finally {
			closeConnection(client);
		}
		return null;
	}

	/**
	 * @return posted tweet.
	 * @throws UnsupportedEncodingException
	 * @throws JSONException
	 * @throws NumberFormatException
	 * @throws ServerException
	 */
	public static String postTweet(String accessToken, String text,
			ServletContext context) throws NumberFormatException,
			JSONException, UnsupportedEncodingException, ServerException {
		AuthSignature signature = toAuthSignature(accessToken);

		ReqSendMessage req = new ReqSendMessage();
		req.setCid(Long.valueOf(signature.getUid()));
		req.setMessage(Util.str_to_bb(text));
		VicDataService.Client client = null;
		try {
			client = getThriftClient();
			ResSendMessage res = client.sendMessage(signature, req);
			if (res.error == null) {
				ContextMessage message = new ContextMessage();
				message.setMessageID(res.getCreatedMessageId());

				ContextMessageContent messageContent = new ContextMessageContent();
				messageContent.setMessage(text.getBytes(CHARSET_UTF_8));
				messageContent.setSenderUID(Long.toString(Util
						.extractUserIdFromAccessToken(accessToken)));
				messageContent.setSentTime(System.currentTimeMillis());
				messageContent.setSenderContextId(Long.valueOf(signature
						.getUid()));
				messageContent.setSenderMessageId(res.getCreatedMessageId());

				message.setMessageContent(messageContent);
				return toJson(signature, message, context);
			} else {
				throw new ServerException(toJson(res.error, context));

			}
		} catch (TException e) {
			context.log(e.getMessage(), e);
		} finally {
			closeConnection(client);
		}
		return null;
	}

	public static String searchUserByEmail(String accessToken, String query,
			ServletContext context) throws ServerException {
		VicDataService.Client client = null;
		try {
			client = getThriftClient();
			ResSearchUsers res = client.searchUserByEmail(
					toAuthSignature(accessToken), query);
			if (res.error == null) {
				StringBuilder json = new StringBuilder();
				json.append('[');
				List<UserProfile> profiles = res.getUserProfiles();
				context.log(Integer.toString(profiles.size()));
				for (UserProfile profile : profiles) {
					json.append(toJson(profile, context));
					json.append(',');
				}
				json.append(']');
				return json.toString();
			} else {
				throw new ServerException(toJson(res.error, context));
			}
		} catch (TException e) {
			context.log(e.getMessage(), e);
		} finally {
			closeConnection(client);
		}
		return null;
	}

	private static AuthSignature toAuthSignature(String accessToken) {
		StringTokenizer tokenizer = new StringTokenizer(accessToken, ":");
		return new AuthSignature(tokenizer.nextToken(), tokenizer.nextToken());
	}

	private static String toJson(AuthSignature signature,
			ContextMessage message, ServletContext context)
			throws NumberFormatException, JSONException,
			UnsupportedEncodingException {
		try {
			long senderId = Long.valueOf(message.getMessageContent()
					.getSenderUID());
			UserProfile profile = cacheUserProfile(signature, senderId);

			// VKIT_FIX fix it after server fixed.
			long senderMessageId = message.getMessageContent()
					.getSenderMessageId();
			if (senderMessageId == Long.MAX_VALUE) {
				senderMessageId = message.getMessageID();
			}
			// END

			JSONWriter writer = new JSONStringer()
					//
					.object()
					//
					.key(KEY_ID)
					.value(message.getMessageID())
					//
					.key(KEY_ID_STR)
					.value(Long.toString(message.getMessageID()))
					//
					.key(KEY_IN_REPLY_TO_STATUS_ID)
					.value(senderMessageId)
					//
					.key(KEY_IN_REPLY_TO_STATUS_ID_STR)
					.value(Long.toString(senderMessageId))
					//
					.key(KEY_IN_REPLY_TO_USER_ID)
					.value(message.getMessageContent().getSenderContextId())
					//
					.key(KEY_IN_REPLY_TO_USER_ID_STR)
					.value(Long.toString(message.getMessageContent()
							.getSenderContextId()))
					//
					.key(KEY_TEXT)
					.value(new String(message.getMessageContent().getMessage(),
							CHARSET_UTF_8))
					//
					.key(KEY_CREATED_AT)
					.value(Util.toTwitterFormat(new Date(message
							.getMessageContent().getSentTime())))
					//
					.key(KEY_RETWEET_COUNT).value(message.getCommentCount()) //
					.key(KEY_USER)//
					.object() //
					.key(KEY_ID).value(Long.valueOf(profile.getUid())) //
					.key(KEY_ID_STR).value(profile.getUid()) //
					.key(KEY_NAME).value(profile.getName()) //
					.key(KEY_SCREEN_NAME).value(profile.getEmail()) //
					.endObject() //
					.endObject(); //

//			context.log(writer.toString());
			return writer.toString();
		} catch (TException e) {
			context.log(e.getMessage(), e);
		}
		return null;
	}

	private static UserProfile cacheUserProfile(AuthSignature signature,
			long senderId) throws TException, TTransportException {
		UserProfile profile;
		profile = profileMap.get(senderId);
		if (profile == null) {
			List<String> uids = new ArrayList<String>();
			uids.add(Long.toString(senderId));

			ResLoadUserProfiles res = getThriftClient().loadUserProfiles(
					signature, uids);
			if (res.error == null) {
				List<UserProfile> profiles = res.getUserProfiles();
				profile = profiles.get(0);
			}
			profileMap.put(senderId, profile);
			profileTimestampMap.put(senderId, System.currentTimeMillis());
		} else {
			long timestamp = profileTimestampMap.get(senderId);
			if (System.currentTimeMillis() - timestamp > 1000) {
				List<String> uids = new ArrayList<String>();
				uids.add(Long.toString(senderId));

				ResLoadUserProfiles res = getThriftClient().loadUserProfiles(
						signature, uids);
				if (res.error == null) {
					List<UserProfile> profiles = res.getUserProfiles();
					profile = profiles.get(0);
				}
				profileMap.put(senderId, profile);
				profileTimestampMap.put(senderId, System.currentTimeMillis());
			}
		}
		return profile;
	}

	private static String toJson(AuthSignature signature,
			MessageComment message, ServletContext context)
			throws NumberFormatException, JSONException,
			UnsupportedEncodingException {
		try {
			List<String> uids = new ArrayList<String>();
			uids.add(message.getCommenterUID());

			UserProfile profile = cacheUserProfile(signature,
					Long.valueOf(message.getCommenterUID()));

			JSONWriter writer = new JSONStringer()
					//
					.object()
					//
					.key(KEY_COMMENT)
					.value(new String(message.getComment(), CHARSET_UTF_8))
					// .value(Util.bb_to_str(message.getMessage()))
					//
					.key(KEY_CREATED_AT)
					.value(Util.toTwitterFormat(new Date(message
							.getCommentTime())))
					//
					.key(KEY_USER)//
					.object() //
					.key(KEY_ID).value(Long.valueOf(profile.getUid())) //
					.key(KEY_ID_STR).value(profile.getUid()) //
					.key(KEY_NAME).value(profile.getName()) //
					.key(KEY_SCREEN_NAME).value(profile.getEmail()) //
					.endObject() //
					.endObject(); //

//			context.log(writer.toString());
			return writer.toString();
		} catch (TException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static String toJson(ErrorDesc error, ServletContext context) {
		JSONWriter writer = new JSONStringer()
				//
				.object()
				//
				.key(KEY_CODE)
				.value(error.code.getValue())
				//
				.key(KEY_MESSAGE)
				.value(error.message)
				//
				.key(KEY_DETAILED_CODE)
				.value(error.detail.detailedCode.getValue())
				//
				.key(KEY_DETAILED_MESSAGE_FORMAT)
				.value(error.detail.detailedMessageFormat);

		StringBuilder args = new StringBuilder("");
		if (error.detail.detailedMessageArgs != null) {
			for (String arg : error.detail.detailedMessageArgs) {
				args.append(arg);
				args.append(',');
			}
		}
		writer = writer.key(KEY_DETAILED_MESSAGE_ARGS).value(args.toString())
				.endObject();

//		context.log("Error: " + writer.toString());
		return writer.toString();
	}

	private static String toJson(UserProfile profile, ServletContext context) {
		JSONWriter writer = new JSONStringer();
		writer.object() //
				.key(KEY_ID).value(Long.valueOf(profile.getUid())) //
				.key(KEY_ID_STR).value(profile.getUid()) //
				.key(KEY_NAME).value(profile.getName()) //
				.key(KEY_SCREEN_NAME).value(profile.getEmail()) //
				.key(KEY_DESCRIPTION).value(profile.getStatusMessage());
		if (profile.getPhoto() != null) {
			writer.key(KEY_PROFILE_IMAGE_URL)
					.value(Resources.URL_USER_IMAGE + profile.getUid())
					.key(KEY_DEFAULT_PROFILE_IMAGE).value(false);
		} else {
			writer.key(KEY_DEFAULT_PROFILE_IMAGE).value(true);
		}
		writer.endObject();

//		context.log(writer.toString());
		return writer.toString();
	}

	private static String toJson(UserProfile profile, String signature,
			ServletContext context) {
		JSONWriter writer = new JSONStringer();
		writer.object() //
				.key(KEY_ID).value(Long.valueOf(profile.getUid())) //
				.key(KEY_ID_STR).value(profile.getUid()) //
				.key(KEY_NAME).value(profile.getName()) //
				.key(KEY_SCREEN_NAME).value(profile.getEmail()) //
				.key(KEY_DESCRIPTION).value(profile.getStatusMessage());
		if (profile.getPhoto() != null) {
			writer.key(KEY_PROFILE_IMAGE_URL)
					.value(Resources.URL_USER_IMAGE + profile.getUid())
					.key(KEY_DEFAULT_PROFILE_IMAGE).value(false);
		} else {
			writer.key(KEY_DEFAULT_PROFILE_IMAGE).value(true);
		}
		writer.key(KEY_SIGNATURE2).value(signature).endObject();

//		context.log(writer.toString());
		return writer.toString();
	}

	public static String updateProfile(String accessToken, String email,
			String name, String status, ByteBuffer photo, ServletContext context)
			throws ServerException {
		VicDataService.Client client = null;
		try {
			client = getThriftClient();
			AuthSignature sig = toAuthSignature(accessToken);
			UserProfile profile = new UserProfile();
			profile.uid = sig.getUid();
			profile.email = email;
			profile.name = name;
			profile.statusMessage = status;
			profile.photo = photo;

			context.log("updateProfile: uid = " + profile.uid + ", email = "
					+ profile.email + ", name = " + profile.name
					+ ", status = " + profile.statusMessage + ", photo = "
					+ profile.photo);
			if (profile.photo != null) {
				context.log("Photo size = " + profile.photo.limit());
			} else {
				context.log("Photo size = " + 0);
			}
			ResUpdateUserProfile res = client.updateUserProfile(sig, profile);
			context.log("ResUpdateUserProfile: error = " + res.error);
			if (res.error != null) {
				throw new ServerException(toJson(res.error, context));

			}
		} catch (TException e) {
			e.printStackTrace();
		} finally {
			closeConnection(client);
		}
		return "";
	}

}
