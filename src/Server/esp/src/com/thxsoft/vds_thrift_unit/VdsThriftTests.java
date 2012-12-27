package com.thxsoft.vds_thrift_unit;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.List;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.thxsoft.vds.thrift.AuthSignature;
import com.thxsoft.vds.thrift.ContextMessage;
import com.thxsoft.vds.thrift.ContextMessageContent;
import com.thxsoft.vds.thrift.ErrorCode;
import com.thxsoft.vds.thrift.ErrorDesc;
import com.thxsoft.vds.thrift.MessageComment;
import com.thxsoft.vds.thrift.MessageEvaluationType;
import com.thxsoft.vds.thrift.MessageType;
import com.thxsoft.vds.thrift.ReqAuthenticateUser;
import com.thxsoft.vds.thrift.ReqCommentMessage;
import com.thxsoft.vds.thrift.ReqEvalMessage;
import com.thxsoft.vds.thrift.ReqLoadComments;
import com.thxsoft.vds.thrift.ReqPullMessages;
import com.thxsoft.vds.thrift.ReqSendMessage;
import com.thxsoft.vds.thrift.ResAuthenticateUser;
import com.thxsoft.vds.thrift.ResCancelFriend;
import com.thxsoft.vds.thrift.ResCommentMessage;
import com.thxsoft.vds.thrift.ResCreateUserProfile;
import com.thxsoft.vds.thrift.ResEvalMessage;
import com.thxsoft.vds.thrift.ResLoadComments;
import com.thxsoft.vds.thrift.ResLoadFollowerUIDs;
import com.thxsoft.vds.thrift.ResLoadFriendUIDs;
import com.thxsoft.vds.thrift.ResPullMessages;
import com.thxsoft.vds.thrift.ResRequestFriend;
import com.thxsoft.vds.thrift.ResSearchUsers;
import com.thxsoft.vds.thrift.ResSendMessage;
import com.thxsoft.vds.thrift.UserProfile;
import com.thxsoft.vds.thrift.VicDataService;


public class VdsThriftTests {
	
	/* Utility functions */
	public static Charset charset = Charset.forName("UTF-8");
	public static CharsetEncoder encoder = charset.newEncoder();
	public static CharsetDecoder decoder = charset.newDecoder();
	
	public static ByteBuffer str_to_bb(String msg){
	  try{
	    return encoder.encode(CharBuffer.wrap(msg));
	  }catch(Exception e){e.printStackTrace();}
	  return null;
	}

	public static String bb_to_str(ByteBuffer buffer){
	  String data = "";
	  try{
	    int old_position = buffer.position();
	    data = decoder.decode(buffer).toString();
	    // reset buffer's position to its original so it is not altered:
	    buffer.position(old_position);  
	  }catch (Exception e){
	    e.printStackTrace();
	    return "";
	  }
	  return data;
	}

	
	public static String TEST_SERVER = "localhost";
	public static int TEST_PORT = 9090;
	
	private static TTransport  transport_ = null;
	private static VicDataService.Client client_ = null;
	private static AuthSignature authSig_ = null;
	
	private static String logonUID() {
		assert( authSig_ != null );
		return authSig_.uid;
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		transport_ = new TFramedTransport(new TSocket(TEST_SERVER, TEST_PORT));
        transport_.open();
        TProtocol protocol = new TBinaryProtocol(transport_);
        client_ = new VicDataService.Client(protocol);
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		transport_.close();
	}

	private static void printError(ErrorDesc error)
	{
		assert(error != null);
		
		System.out.println("Error[code="+error.code+"]"+error.message);
	}
	
	// Create a user, return the user id of the user.
	// Return null if no user is created.
	private static boolean createUser(String email, String password, String name, boolean ignoreDuplicateUsers)
	{
		UserProfile profile = new UserProfile();
		profile.email = email;
		profile.encryptedPassword = password;
		profile.name = name;
		profile.statusMessage = "";
		profile.photo = null;

		ResCreateUserProfile res = null;
		try {
			res = client_.createUserProfile( new AuthSignature("", ""), profile);
		} catch ( TException e) {
			e.printStackTrace();
			return false;
		}
		
		if (res.error == null) {
			return true;
//			return res.createdUserId;
		} else {
			printError(res.error);
			
			if (ignoreDuplicateUsers)
			{
				// Is the error because the user already exists?
				if (res.error.code == ErrorCode.VKErrorInvalidParameter && res.error.message.startsWith(""))
				{
					return true;
				}
			}
			
			return false;
		}
	}
	
	private static String authenticateUser(String email, String password)
	{
		ReqAuthenticateUser req = new ReqAuthenticateUser();
		req.email = email;
		req.encryptedPassword = password;
		
		// VKIT auth signature should be ignored.
		ResAuthenticateUser res = null;
		try {
			res = client_.authenticateUser( new AuthSignature(), req );
		} catch (TException e) {
			e.printStackTrace();
			return null;
		}
		
		if (res.error == null) {
			authSig_ = res.authSignature;
			return res.userProfile.uid;
		} else {
			printError(res.error);
			return null;
		}
	}
	private static boolean logout() {
		String uid = authenticateUser("test", "invalid password");
		assert(uid == null);
		authSig_ = null;
		return true;
	}

	private static int nextUserId = 1;
	private static String generateEmail()
	{
		return "kmkim" + (nextUserId ++) + "@thankyousoft.com";
	}
	
	private static String getPassword(String email)
	{
		return String.valueOf(email.hashCode());
	}
	
	private static boolean createTestUser(String email)
	{
		return createUser(email, getPassword(email), email, true/* Ignore if the user already exists */ );
	}

	private static String authTestUser(String email)
	{
		return authenticateUser(email, getPassword(email));
	}
	

	private static UserProfile searchUserByEmail(String friendEmail)
	{
		// VKIT auth signature should be ignored.
		ResSearchUsers res = null;
		try {
			res = client_.searchUserByEmail( authSig_, friendEmail );
		} catch (TException e) {
			e.printStackTrace();
			return null;
		}
		
		if (res.error == null) {
			assert( res.userProfiles.size() == 1);
			
			return res.userProfiles.get(0);
		} else {
			printError(res.error);
			return null;
		}		
	}
	
	// Request a friend, return the user profile of the friend.
	private static UserProfile requestFriend(String friendEmail)
	{
		UserProfile friendProfile = searchUserByEmail(friendEmail);
		
		// VKIT auth signature should be ignored.
		ResRequestFriend res = null;
		try {
			res = client_.requestFriend( authSig_, friendProfile.uid );
		} catch (TException e) {
			e.printStackTrace();
			return null;
		}
		
		if (res.error == null) {
			assert( res.friendProfile.uid.equals(friendProfile.uid));
			
			return res.friendProfile;
		} else {
			printError(res.error);
			return null;
		}		
	}
	
	// Request a friend, return the user profile of the friend.
	private static UserProfile cancelFriend(String friendEmail)
	{
		UserProfile friendProfile = searchUserByEmail(friendEmail);
		
		// VKIT auth signature should be ignored.
		ResCancelFriend res = null;
		try {
			res = client_.cancelFriend( authSig_, friendProfile.uid );
		} catch (TException e) {
			e.printStackTrace();
			return null;
		}
		
		if (res.error == null) {
			assert( res.canceldFriendProfile.uid.equals(friendProfile.uid));
			
			return res.canceldFriendProfile;
		} else {
			printError(res.error);
			return null;
		}		
	}	
	
 	// Return ID of message after the message was sent.
	// Return -1 for any failure
	private static long sendMessage(long contextId, String message)
	{
		ReqSendMessage req = new ReqSendMessage();
		req.cid = contextId;
		req.message = str_to_bb(message);
		
		ResSendMessage res = null;
		try {
			res = client_.sendMessage( authSig_, req );
		} catch (TException e) {
			e.printStackTrace();
			return -1;
		}
		
		if (res.error == null) {
			return res.createdMessageId;
		} else {
			printError(res.error);
			return -1;
		}		
	}
	
/*
		ReqXXX req = new ReqXXX();
		
		ResXXX res = null;
		try {
			res = client_.sendMessage( authSig_, req );
		} catch (TException e) {
			e.printStackTrace();
			return -1;
		}
		
		if (res.error == null) {
			return res.;
		} else {
			printError(res.error);
			return -1;
		}			
 */

	// Get all messages in a context
	private static List<ContextMessage> getAllMessages()
	{
		ReqPullMessages req = new ReqPullMessages();
		req.startMessageID = -1;
		req.stopMessageID = -1;
		
		ResPullMessages res = null;
		try {
			res = client_.pullMessages( authSig_, req );
		} catch (TException e) {
			e.printStackTrace();
			return null;
		}
		
		if (res.error == null) {
			return res.messageList;
		} else {
			printError(res.error);
			return null;
		}		
	}

	// Get all comments in a message
	private static List<MessageComment> getAllComments(long contextId, long messageId )
	{
		ReqLoadComments req = new ReqLoadComments();
		req.senderContextID = contextId;
		req.senderMessageID = messageId;
		
		ResLoadComments res = null;
		try {
			res = client_.loadComments( authSig_, req );
		} catch (TException e) {
			e.printStackTrace();
			return null;
		}
		
		if (res.error == null) {
			return res.commentList;
		} else {
			printError(res.error);
			return null;
		}			
	}
	
	//     public ResLoadFriendUIDs loadFriendUIDs(AuthSignature sig, String uid) throws org.apache.thrift.TException;

	private static List<String> getAllFriends(String uid)
	{
		
		ResLoadFriendUIDs res = null;
		try {
			res = client_.loadFriendUIDs( authSig_, uid );
		} catch (TException e) {
			e.printStackTrace();
			return null;
		}
		
		if (res.error == null) {
			return res.friendUIDs;
		} else {
			printError(res.error);
			return null;
		}			
	}

	private static List<String> getAllFollowers(String uid)
	{
		
		ResLoadFollowerUIDs res = null;
		try {
			res = client_.loadFollowerUIDs( authSig_, uid );
		} catch (TException e) {
			e.printStackTrace();
			return null;
		}
		
		if (res.error == null) {
			return res.followerUIDs;
		} else {
			printError(res.error);
			return null;
		}			
	}
	
    //public ResCommentMessage commentMessage(AuthSignature sig, ReqCommentMessage req) throws org.apache.thrift.TException;

	// Return true if it succeeds. return false otherwise.
	private static boolean commentMessage(long contextId, long messageId, String comment )
	{
		ReqCommentMessage req = new ReqCommentMessage();
		req.contextID = contextId;
		req.messageID = messageId;
		req.comment = str_to_bb( comment );
	
		// VKIT auth signature should be ignored.
		ResCommentMessage res = null;
		try {
			res = client_.commentMessage( authSig_, req );
		} catch (TException e) {
			e.printStackTrace();
			return false;
		}
		
		if (res.error == null) {
			return true;
		} else {
			printError(res.error);
			return false;
		}		
	}

	//public ResEvalMessage evalMessage(AuthSignature sig, ReqEvalMessage req) throws org.apache.thrift.TException;

	// Return true if it succeeds. return false otherwise.
	private static boolean evalMessage(long contextId, long messageId, MessageEvaluationType evalType)
	{
		ReqEvalMessage req = new ReqEvalMessage();
		req.contextID = contextId;
		req.messageID = messageId;
		req.type      = evalType;
		
		// VKIT auth signature should be ignored.
		ResEvalMessage res = null;
		try {
			res = client_.evalMessage( authSig_, req );
		} catch (TException e) {
			e.printStackTrace();
			return false;
		}
		
		if (res.error == null) {
			return true;
		} else {
			printError(res.error);
			return false;
		}		
	}

	
	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

    //public ResCreateUserProfile createUserProfile(AuthSignature sig, UserProfile profile) throws org.apache.thrift.TException;
	@Test
	public void testCreateUserProfile() {
		fail("Not yet implemented");
	}
	
    //public ResAuthenticateUser authenticateUser(AuthSignature sig, ReqAuthenticateUser authReq) throws org.apache.thrift.TException;
	@Test
	public void testAuthenticateUser() {
		fail("Not yet implemented");
	}

    //public ResUpdateUserProfile updateUserProfile(AuthSignature sig, UserProfile profile) throws org.apache.thrift.TException;
	@Test
	public void testUpdateUserProfile() {
		fail("Not yet implemented");
	}
	
    //public ResLoadUserProfiles loadUserProfiles(AuthSignature sig, List<String> uids) throws org.apache.thrift.TException;
	@Test
	public void testLoadUserProfiles() {
		fail("Not yet implemented");
	}
	
    //public ResLoadFriendProfiles loadFriendProfiles(AuthSignature sig, String uid) throws org.apache.thrift.TException;
	@Test
	public void testLoadFriendProfiles() {
		fail("Not yet implemented");
	}
	
    //public ResLoadFriendUIDs loadFriendUIDs(AuthSignature sig, String uid) throws org.apache.thrift.TException;
	@Test
	public void testLoadFriendUIDs() {
		fail("Not yet implemented");
	}
	
    //public ResRequestFriend requestFriend(AuthSignature sig, String uid) throws org.apache.thrift.TException;
	@Test
	public void testRequestFriend() {
		fail("Not yet implemented");
	}
	
    //public ResCancelFriend cancelFriend(AuthSignature sig, String uid) throws org.apache.thrift.TException;
	@Test
	public void testCancelFriend() {
		fail("Not yet implemented");
	}
	
    //public ResSearchUsers searchUserByEmail(AuthSignature sig, String email) throws org.apache.thrift.TException;
	@Test
	public void testSearchUserByEmail() {
		fail("Not yet implemented");
	}
	
    //public ResCreateContext createContext(AuthSignature sig, List<String> uids) throws org.apache.thrift.TException;
	@Test
	public void testCreateContext() {
		fail("Not yet implemented");
	}
	
    //public ResSendMessage sendMessage(AuthSignature sig, ReqSendMessage req) throws org.apache.thrift.TException;
	@Test
	public void testSendMessage() {
		String user1 = generateEmail();
		assertTrue( createTestUser(user1) );
		
		assertTrue( authTestUser(user1) != null );
		
		long contextId = Long.valueOf( logonUID() );
		long messageId = sendMessage( contextId, "Hello");
		assertTrue( messageId > 0 );
		
		List<ContextMessage> messages = getAllMessages();
		
		assertTrue( messages.size() == 0);

		
		messages = getAllMessages();
		
		
		assertTrue( messages.size() == 1);
		
		ContextMessage message = messages.get(0);
		
		assertTrue( message != null );
		assertTrue( message.commentCount == 0 );
		assertTrue( message.likeCount == 0 );
		assertTrue( message.dislikeCount == 0 );
		
		ContextMessageContent messageContent = message.messageContent;
		assertTrue( bb_to_str(messageContent.message).equals("Hello") );
		assertTrue( messageContent.messageType == MessageType.MT_CONTEXT_MESSAGE );
		assertTrue( messageContent.senderContextId == contextId );
		assertTrue( messageContent.senderUID.equals( logonUID() ));
		assertTrue( messageContent.sentTime > 0);
		
		assertTrue( logout() );
	}
	
    //public void sendOnewayMessage(AuthSignature sig, ReqSendMessage req) throws org.apache.thrift.TException;
	@Test
	public void testSendOnewayMessage() {
		fail("Not yet implemented");
	}
	
    //public ResPullMessages pullMessages(AuthSignature sig, ReqPullMessages req) throws org.apache.thrift.TException;
	@Test
	public void testPullMessages() {
		fail("Not yet implemented");
	}
	
	private ContextMessage getLatestMessage(int expectedMessageCount)
	{
		List<ContextMessage> messages = getAllMessages();
		
		assertTrue( messages.size() == expectedMessageCount);
		
		if ( messages.size() > 0 )
		{
			ContextMessage message = messages.get(0);
			
			assertTrue( message != null );
			
			return message;
		}
		
		return null;
	}
	
	@Test
	public void testEvalMessage() {
		String user1 = generateEmail();
		
		assertTrue( createTestUser(user1) );

		assertTrue( authTestUser(user1) != null );

		long contextId = Long.valueOf( logonUID() );
		long messageId = sendMessage( contextId, "Hello");

		// Should fail : "CancelLike" without "Like"
		assertTrue( evalMessage(contextId, messageId, MessageEvaluationType.CancelLike) == false);

		// Should fail : "CancelDislike" without "Dislike"
		assertTrue( evalMessage(contextId, messageId, MessageEvaluationType.CancelDislike) == false);

		ContextMessage message = getLatestMessage(1 /*Expected Message Count */);
		assertTrue( message.likeCount == 0 );
		assertTrue( message.dislikeCount == 0 );

		////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Test "Like"
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Success : "Like"
		assertTrue( evalMessage(contextId, messageId, MessageEvaluationType.Like) );
		
		message = getLatestMessage(1 /*Expected Message Count */);
		assertTrue( message.likeCount == 1 );
		assertTrue( message.dislikeCount == 0 );

		// Should fail : Can't "Like" twice
		assertTrue( evalMessage(contextId, messageId, MessageEvaluationType.Like) == false);
		
		// Should fail : "Dislike" after "Like" 
		assertTrue( evalMessage(contextId, messageId, MessageEvaluationType.Dislike) == false);

		////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Test "CancelLike"
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Success : "CancelLike"
		assertTrue( evalMessage(contextId, messageId, MessageEvaluationType.CancelLike) );

		message = getLatestMessage(1 /*Expected Message Count */);
		assertTrue( message.likeCount == 0 );
		assertTrue( message.dislikeCount == 0 );
		
		// Should fail : "CancelLike" without "Like"
		assertTrue( evalMessage(contextId, messageId, MessageEvaluationType.CancelLike) == false);

		// Should fail : "CancelDislike" without "Dislike"
		assertTrue( evalMessage(contextId, messageId, MessageEvaluationType.CancelDislike) == false);

		////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Test "Dislike"
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Success : "Dislike" 
		assertTrue( evalMessage(contextId, messageId, MessageEvaluationType.Dislike) );
		
		message = getLatestMessage(1 /*Expected Message Count */);
		assertTrue( message.likeCount == 0 );
		assertTrue( message.dislikeCount == 1 );
		

		// Should Fail : "Like" after "Dislike"
		assertTrue( evalMessage(contextId, messageId, MessageEvaluationType.Like) == false);
		
		// Should Fail : "CancelLike" after "Dislike"
		assertTrue( evalMessage(contextId, messageId, MessageEvaluationType.CancelLike) == false);

		////////////////////////////////////////////////////////////////////////////////////////////////////////
		// Test "CancelDislike"
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		
		// Success : "CancelDislike" 
		assertTrue( evalMessage(contextId, messageId, MessageEvaluationType.CancelDislike) );

		
		// Should Fail : "CancelDislike" twice 
		assertTrue( evalMessage(contextId, messageId, MessageEvaluationType.CancelDislike) == false);
		
		message = getLatestMessage(1 /*Expected Message Count */);
		assertTrue( message.likeCount == 0 );
		assertTrue( message.dislikeCount == 0 );
		
		assertTrue( logout() );
	}
	
/*	
    generateEmail();
	private static String createTestUser(String email)
	private static String authTestUser(String email)
	private static UserProfile searchUserByEmail(String friendEmail)
	private static UserProfile requestFriend(String friendEmail)
	private static long sendMessage(long contextId, String message)
	private static boolean commentMessage(long contextId, long messageId, String comment )
*/	
	
    //public ResCommentMessage commentMessage(AuthSignature sig, ReqCommentMessage req) throws org.apache.thrift.TException;
	@Test
	public void testCommentMessage() {
		String user1 = generateEmail();
		
		assertTrue( createTestUser(user1) );

		assertTrue( authTestUser(user1) != null );

		long contextId = Long.valueOf( logonUID() );
		long messageId = sendMessage( contextId, "Hello");

		ContextMessage message = getLatestMessage(1 /*Expected Message Count */);
		// check if comment count == 0
		assertTrue( message.commentCount == 0 );
		// check the message content
		assertTrue( bb_to_str(message.messageContent.message).equals("Hello") );

		// Leave a comment
		assertTrue( commentMessage(contextId, messageId, "Hello_Comment") );

		// check if comment count == 1 after adding a comment
		assertTrue( getLatestMessage(1 /*Expected Message Count */).commentCount == 1 );

		// check the content of the comment.
		List<MessageComment> comments = getAllComments(contextId, messageId);
		
		assertTrue( comments.size() == 1);
		
		MessageComment comment = comments.get(0);
		
		assertTrue( comment.comment != null);
		assertTrue( bb_to_str(comment.comment).equals("Hello_Comment"));
		
		assertTrue( logout() );
	}

	// TODO : Make sure the message is not propagated after canceling the friendship
	@Test
	public void testSendMessageToFriends() {
		// Use case : A fan follows a star.
		String starEmail = generateEmail();
		assertTrue( createTestUser(starEmail) );
		
		String fanEmail = generateEmail();
		
		assertTrue( createTestUser(fanEmail) );

		assertTrue( authTestUser(fanEmail) != null );

		// The fan follows the star
		UserProfile starProfile = requestFriend( starEmail );
		assertTrue( starProfile != null );
		
		long fanInBoxContextId = Long.valueOf( logonUID() );
		// The fan posts a message to his wall.
		assertTrue( sendMessage( fanInBoxContextId, "Hello") > 0 ); // check if Message Id > 0

		// Login as the star
		logout();
		assertTrue( authTestUser(starEmail) != null );

		{
			// Even though the fan posts a message, the star does not see it.
			assertTrue( getLatestMessage(0 /*Expected Message Count */) == null);
		}

		// The star posts a message so that the fan can see it.
		long starInBoxContextId = Long.valueOf( logonUID() );
		long starMessageId = sendMessage( starInBoxContextId, "Nihao"); // check if Message Id > 0

		// Login as the fan email
		logout();
		assertTrue( authTestUser(fanEmail) != null );
		
		{
			// The fan should be able to see the star's message as well as his own message. Total two messages.
			ContextMessage message = getLatestMessage(2 /*Expected Message Count */);
			// We don't have any comment yet. check if comment count == 0
			assertTrue( message.commentCount == 0 );
			assertTrue( bb_to_str(message.messageContent.message).equals("Nihao") );
		}

		// Login as the star 
		logout();
		assertTrue( authTestUser(starEmail) != null );
		{
			// Leave a comment.
			assertTrue( commentMessage(starInBoxContextId, starMessageId, "Nihao_Comment") );
		}

		// Login as the fan email
		logout();
		assertTrue( authTestUser(fanEmail) != null );

		{
			// check if comment count == 1
			// The comment count should have been propagated from the followed user's message to the following user's message in the following user's "InBox" context.
			ContextMessage starMessageInFanInbox = getLatestMessage(2 /*Expected Message Count */);
			assertTrue( starMessageInFanInbox.commentCount == 1 );
			
			// However, the fan should not be able to get any comment in the corresponding message in his "InBox" context.  
			List<MessageComment> comments = getAllComments(fanInBoxContextId, starMessageInFanInbox.messageID);
			
			assertTrue( comments.size() == 0);

			// The fan can get the comments from the star's "Inbox" 
			assertTrue( starMessageInFanInbox.messageContent.senderMessageId == starMessageId);
			comments = getAllComments(starInBoxContextId, starMessageId);
			assertTrue( comments.size() == 1);
			MessageComment messageComment = comments.get(0);
			assertTrue( bb_to_str(messageComment.comment).equals("Nihao_Comment") );

		}
	}

	
    //public ResLoadComments loadComments(AuthSignature sig, ReqLoadComments req) throws org.apache.thrift.TException;
	@Test
	public void testLoadComments() {
		fail("Not yet implemented");
	}
	
    // Request Friend, Cancel Friend
	@Test
	public void testLoadFollowerUIDs() {
		String friendEmail = generateEmail();
		String friendUid = null;
		assertTrue( createTestUser(friendEmail) );

		String user1 = generateEmail();
		assertTrue( createTestUser(user1)  );

		assertTrue( authTestUser(user1) != null );

		UserProfile friendProfile = searchUserByEmail(friendEmail);
		assertTrue( friendProfile != null );
		friendUid = friendProfile.uid;
		
		List<String> uids = null;
		{
			// Verify that there is no friendship at all 
			uids = getAllFollowers( logonUID() );
			assertTrue( uids.size() == 0 );
	
			uids = getAllFriends( logonUID() );
			assertTrue( uids.size() == 0 );

			uids = getAllFollowers( friendUid );
			assertTrue( uids.size() == 0 );

			uids = getAllFriends( friendUid );
			assertTrue( uids.size() == 0 );
		}
		
		friendProfile = requestFriend( friendEmail );
		assertTrue( friendProfile != null );
		
		friendProfile.uid.equals(friendUid);
		friendProfile.email.equals(friendEmail);

		{
			// Verify that there is no friendship at all 
			uids = getAllFollowers( logonUID() );
			assertTrue( uids.size() == 0 );
	
			uids = getAllFriends( logonUID() );
			assertTrue( uids.size() == 1 );
			assertTrue( uids.contains(friendUid) );

			uids = getAllFollowers( friendUid );
			assertTrue( uids.size() == 1 );
			assertTrue( uids.contains(logonUID()) );

			uids = getAllFriends( friendUid );
			assertTrue( uids.size() == 0 );
		}	
		
		friendProfile = cancelFriend( friendEmail );
		assertTrue( friendProfile != null );		
		
		{
			// Verify that there is no friendship at all 
			uids = getAllFollowers( logonUID() );
			assertTrue( uids.size() == 0 );
	
			uids = getAllFriends( logonUID() );
			assertTrue( uids.size() == 0 );

			uids = getAllFollowers( friendUid );
			assertTrue( uids.size() == 0 );

			uids = getAllFriends( friendUid );
			assertTrue( uids.size() == 0 );
		}		
	}
	
}
