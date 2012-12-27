package com.thxsoft.vds;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.NavigableMap;
import java.util.Set;

import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.TSerializer;

import com.thxsoft.vds.thrift.AuthSignature;
import com.thxsoft.vds.thrift.ContextInvitationMessage;
import com.thxsoft.vds.thrift.ContextMessage;
import com.thxsoft.vds.thrift.ContextMessageContent;
import com.thxsoft.vds.thrift.DetailedErrorCode;
import com.thxsoft.vds.thrift.ErrorCode;
import com.thxsoft.vds.thrift.ErrorDesc;
import com.thxsoft.vds.thrift.ErrorDetail;
import com.thxsoft.vds.thrift.MessageComment;
import com.thxsoft.vds.thrift.MessageReplyContent;
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
import com.thxsoft.vds.thrift.ResCreateContext;
import com.thxsoft.vds.thrift.ResCreateUserProfile;
import com.thxsoft.vds.thrift.ResEvalMessage;
import com.thxsoft.vds.thrift.ResLoadComments;
import com.thxsoft.vds.thrift.ResLoadFollowerUIDs;
import com.thxsoft.vds.thrift.ResLoadFriendProfiles;
import com.thxsoft.vds.thrift.ResLoadFriendUIDs;
import com.thxsoft.vds.thrift.ResLoadUserProfiles;
import com.thxsoft.vds.thrift.ResPullMessages;
import com.thxsoft.vds.thrift.ResRequestFriend;
import com.thxsoft.vds.thrift.ResSearchUsers;
import com.thxsoft.vds.thrift.ResSendMessage;
import com.thxsoft.vds.thrift.ResUpdateUserProfile;
import com.thxsoft.vds.thrift.UserProfile;
import com.thxsoft.vds.thrift.VicDataService;

public class VicDataServiceImpl extends HBaseAccess implements VicDataService.Iface{
    
    private ErrorDesc noSuchUserError(String uidString)
    {
    	return new ErrorDesc(ErrorCode.VKErrorInvalidParameter, "User not found with the given user identifier : " + uidString, 
				             new ErrorDetail(DetailedErrorCode.VKErrorDetailUserNotFound, "The user with the given user identifier is not found.", null));
    }

    private ErrorDesc invalidUserIdentifier(String uidString)
    {
		return new ErrorDesc(ErrorCode.VKErrorInvalidParameter, "Invalid user identifier : " + uidString,
				             new ErrorDetail(DetailedErrorCode.VKErrorDetailInvalidUserIdentifierFormat, "Invalid user identifier format. The user identifier should be a string containing numeric values only.", null));
    }
    
    private ErrorDesc friendIsRequester(String uidString)
    {
		return new ErrorDesc(ErrorCode.VKErrorInvalidParameter, "The friend identifier is same to the requester : " + uidString,
	             			 new ErrorDetail(DetailedErrorCode.VKErrorDetailFriendIsRequester, "The user identifier of the requested friend is same to the one of the requester.", null));
    }
    
	public ResAuthenticateUser authenticateUser(AuthSignature sig, ReqAuthenticateUser authReq) throws org.apache.thrift.TException
    {
		// The sig parameter is not used in authenticateUser, but we keep it in the parameter list to make client code simple.
		sig = null;
		
		ResAuthenticateUser response = new ResAuthenticateUser();
		
		byte[] rawEmail = Bytes.toBytes( authReq.email );
		
		byte[] rawUid = get(HBaseAccess.T_USERID_BY_EMAIL(), 
				 			rawEmail, 
							CF_UBE_USER, 
							CQ_UBE_USER_ID);
		
		if ( rawUid == null ) // The email is not found.
		{
			response.error = new ErrorDesc(ErrorCode.VKErrorInvalidCredentials, "Invalid email.",
        			 					   new ErrorDetail(DetailedErrorCode.VKErrorDetailEmailNotFound, "The email is not registered yet.", null));
			return response;
		}
		
		long requesterUid = Bytes.toLong(rawUid);				
		UserProfile userProfile = getUserProfileWithPassword(rawUid);
		
		if (userProfile == null) // The email exists but the user profile does not exist.
		{
			response.error = new ErrorDesc(ErrorCode.VKErrorInvalidCredentials, "Invalid email.",
                   						   new ErrorDetail(DetailedErrorCode.VKErrorDetailEmailNotFound, "The email is not registered yet.", null));
			return response;
		}
		
		// Check if the encrypted password matches
		if ( userProfile.encryptedPassword != null && 
			 userProfile.encryptedPassword.equals( authReq.encryptedPassword ) )
		{
			response.authSignature = new AuthSignature();
			response.authSignature.uid = Long.toString(requesterUid);
			// TODO : Need to generate session key to set signature.
			// TODO : Need to have a mapping from uid to the session key.
			response.authSignature.signature = Long.toString(requesterUid);
		}
		else
		{
			response.error = new ErrorDesc(ErrorCode.VKErrorInvalidCredentials, "Invalid password.",
					                       new ErrorDetail(DetailedErrorCode.VKErrorDetailInvalidPassword, "The password is incorrect.", null));
			return response;
		}
		
		// Scrub password, to return it as part of the response.
		userProfile.encryptedPassword = "";
		response.userProfile = userProfile;
		
		// Create the "InBox" user context if it does not exist.
		// BUGBUG : Use the serviceId provided by the client.
		if ( ! hasUserContext(HARDCODED_SERVICE_ID, requesterUid ) )
		{
			createUserContext(HARDCODED_SERVICE_ID, requesterUid );
		}
		
    	return response;
    }
	
	// Check if the authenication signature is valid. 
	private boolean isValidAuthSignature(AuthSignature sig)
	{
		// TODO : Check the mapping from uid to the session key(sig.signature).
		if ( sig.uid == null || sig.uid.equals(""))
			return false;
		
		if ( sig.signature == null || sig.signature.equals(""))
			return false;
		
		if ( ! sig.uid.equals(sig.signature) )
			return false;
		
		return true;
	}
	
	// Check if the authenication signature is valid. 
	// Return an error descriptor if the authentication signature is invalid.
	// For each API except authenticateUser, we check the argument to the AuthSignature is valid.
	private ErrorDesc checkAuthSignature(AuthSignature sig)
	{
		if ( isValidAuthSignature(sig) )
			return null;
		return new ErrorDesc( ErrorCode.VKErrorNotAuthenticated, "Failed to authenticate the user.", null );
	}

	
	private void putUserProfile(long userId, UserProfile profile) throws org.apache.thrift.TException
	{
        byte[] profileData = null;

        profileData = serializer_.serialize(profile);

        // TODO : need to put a ServiceId-UserId mapping to vds_service_users

        // Step 1 : Put into secondary indexes
        put(T_USERID_BY_EMAIL(), Bytes.toBytes(profile.email), CF_UBE_USER, CQ_UBE_USER_ID, Bytes.toBytes(userId));
        
		// Step 2 : Put into the vds_users table
        put(T_USERS(), Bytes.toBytes(userId), CF_USERS_PROFILE, CQ_U_PROFILE, profileData);
	}

	private UserProfile deserializeUserProfile(byte[] profileData) throws org.apache.thrift.TException
	{
        UserProfile profile = new UserProfile();
        deserializer_.deserialize(profile, profileData);
        return profile;
	}
	
	// Return the user profile object if one exists for the given user id.
	private UserProfile getUserProfileWithPassword(byte [] rawUserId) throws org.apache.thrift.TException
	{
        byte[] rawProfile = null;
        
        rawProfile = get(T_USERS(), rawUserId, CF_USERS_PROFILE, CQ_U_PROFILE);
        
    	if ( rawProfile == null ) // The profile is not found
    	{
    		return null;
    	}
    	
    	UserProfile profile = deserializeUserProfile(rawProfile);
        
        return profile;
	}

	private UserProfile getUserProfile(byte [] rawUserId) throws org.apache.thrift.TException
	{
		UserProfile p = getUserProfileWithPassword(rawUserId) ;
		// Scrub the password
		if ( p != null)
			p.encryptedPassword = "";
		return p;
	}	
	
	private UserProfile getUserProfile(long userId) throws org.apache.thrift.TException
	{
		return getUserProfile( Bytes.toBytes(userId) );
	}


    private List<byte[]> getRawUids(List<String> uids) throws IllegalArgumentException
    {
    	List<byte[]> rawUids = new ArrayList<byte[]> ();
    	
		for (String uidString : uids) {
			try {
            	long userId = Long.valueOf( uidString );
               	rawUids.add( Bytes.toBytes( userId ));
			} catch (NumberFormatException e) {
				throw new IllegalArgumentException("Invalid user identifier :"+uidString); 
			}
   		}

    	return rawUids;
    }

	// Return the user profile object if one exists for the given user id.
	private List<UserProfile> getUserProfiles(Collection<byte []> rawUserIdList) throws org.apache.thrift.TException
	{
		List<UserProfile> userProfiles = new ArrayList<UserProfile>(); 
		
        List<byte[]> rawProfileList = null;
        
        rawProfileList = get(T_USERS(), rawUserIdList, CF_USERS_PROFILE, CQ_U_PROFILE);
        
        for ( byte[] rawProfile : rawProfileList )
        {
        	if ( rawProfile != null )
        	{
        	   	UserProfile profile = deserializeUserProfile(rawProfile);

        	   	// We should not send encrypted password of users.
        	   	profile.encryptedPassword = "";

        	   	userProfiles.add( profile );
        	}
        }
        
        return userProfiles;
	}

	
	private UserProfile userProfileWithPasswordByEmail(String email) throws org.apache.thrift.TException
	{
		byte[] rawUid = get(T_USERID_BY_EMAIL(), Bytes.toBytes( email ), CF_UBE_USER, CQ_UBE_USER_ID);

		if ( rawUid != null ) // The email already exists.
		{
			UserProfile profile = getUserProfileWithPassword(rawUid);
			return profile;
		}
		else
		{
			// The user profile does not exist. This means Hbase was crashed between it put T_USERID_BY_EMAIL and T_USERS.
			// Treat as if the user does not exist.
			//
			// Do nothing
		}
		return null;
	}
	
	private UserProfile userProfileByEmail(String email) throws org.apache.thrift.TException
	{
		UserProfile profile = userProfileWithPasswordByEmail( email );
		// Scrub the password.
		if (profile != null)
			profile.encryptedPassword = "";
		return profile;
	}	
	
	private ErrorDesc getCreateProfileError( UserProfile profile )
	{
		if ( profile.email == null || profile.email.equals("") )
		{
			return new ErrorDesc(ErrorCode.VKErrorInvalidParameter, "Empty email on a user profile",
                                 new ErrorDetail(DetailedErrorCode.VKErrorDetailEmptyEmailOnUserProfile, "A mandatory field, email is empty.", null));
		}
		
		if ( profile.encryptedPassword == null || profile.encryptedPassword.equals("") )
		{
			return new ErrorDesc(ErrorCode.VKErrorInvalidParameter, "Empty password on a user profile",
                    			 new ErrorDetail(DetailedErrorCode.VKErrorDetailEmptyPasswordOnUserProfile, "A mandatory field, password is empty.", null));
		}
		return null;
	}
	
	private ErrorDesc getUpdateProfileError( UserProfile profile )
	{
		// profile.encryptedPassword can be "". This means "don't change my password"
		
		if ( profile.email == null || profile.email.equals("") )
		{
			return new ErrorDesc(ErrorCode.VKErrorInvalidParameter, "Empty email on a user profile",
       			 				 new ErrorDetail(DetailedErrorCode.VKErrorDetailEmptyEmailOnUserProfile, "A mandatory field, email is empty.", null));
		}
		
		if ( profile.uid == null || profile.uid.equals("") )
		{
			return new ErrorDesc(ErrorCode.VKErrorInvalidParameter, "Empty user identifier on a user profile to update.",
       			 				 new ErrorDetail(DetailedErrorCode.VKErrorDetailEmptyUserIdentifierOnUserProfile, "A mandatory field, user identifier is empty.", null));
		}
		
		return null;
	}
	
    public ResCreateUserProfile createUserProfile(AuthSignature sig, UserProfile profile) throws org.apache.thrift.TException
    {
    	// Authentication signature, sig parameter is not used in createUserProfile, 
    	// because we can't authenticate before a user is created.
    	sig = null;
    	
    	ResCreateUserProfile response = new ResCreateUserProfile();

    	if ( (response.error = getCreateProfileError(profile)) != null )
    		return response;
    	
    	if ( userProfileByEmail(profile.email) != null)
    	{
    		response.error = new ErrorDesc(ErrorCode.VKErrorInvalidParameter, "A user with the given email already exists",
       			 						   new ErrorDetail(DetailedErrorCode.VKErrorDetailEmailAlreadyExists, "The email is already registered.", null));
			return response;
    	}
    	
    	long userId = getNextId(CQ_I_NEXT_USER_ID);
    	profile.uid = String.valueOf(userId);

    	putUserProfile(userId, profile);
		
    	response.createdUserId = profile.uid;
		return response;
    }


    public ResUpdateUserProfile updateUserProfile(AuthSignature sig, UserProfile profile) throws org.apache.thrift.TException
    {
    	ResUpdateUserProfile response = new ResUpdateUserProfile();

    	if ( (response.error = checkAuthSignature(sig)) != null )
    		return response;
    	
    	if ( (response.error = getUpdateProfileError(profile)) != null )
    		return response;
    	
    	if ( ! sig.uid.equals(profile.uid))
    	{
    		response.error = new ErrorDesc(ErrorCode.VKErrorInvalidParameter, "Unable to change profile of another person.",
       			 						   new ErrorDetail(DetailedErrorCode.VKErrorDetailUnableToUpdateOtherUserProfile, "A user can not update other user's profile.", null));
    	}
    	
    	UserProfile currentUserProfile = userProfileWithPasswordByEmail(profile.email);
    	
    	if ( currentUserProfile != null ) {
        	if ( ! profile.email.equals( currentUserProfile.email) ) 
        	{
        		response.error = new ErrorDesc(ErrorCode.VKErrorInvalidParameter, "Unable to change email field.",
           			 						   new ErrorDetail(DetailedErrorCode.VKErrorDetailUnableToChangeEmail, "Unable to change the email field.", null));		 
        	}
    		
        	try {
            	long userId = Long.valueOf( profile.uid );
            	
            	// In case the encryptedPassword is empty, don't change the password.
            	if (profile.encryptedPassword == null ||
           			profile.encryptedPassword.equals(""))
            		profile.encryptedPassword = currentUserProfile.encryptedPassword;
            	
            	putUserProfile(userId, profile);
        	} catch (NumberFormatException e) {
        		response.error = new ErrorDesc(ErrorCode.VKErrorInvalidParameter, "Invalid user identifier : " + profile.uid + ", email:" + profile.email,
           			                           new ErrorDetail(DetailedErrorCode.VKErrorDetailInvalidUserIdentifierFormat, "Invalid user identifier format. The user identifier should be a string containing numeric values only.", null));
        	}
    	} else	{
    		response.error = new ErrorDesc(ErrorCode.VKErrorInvalidParameter, "A user with the given email does not exist. email : " + profile.email,
       			                           new ErrorDetail(DetailedErrorCode.VKErrorDetailEmailNotFound, "The email is not registered yet.", null));
    	}
    	
		return response;
    }

    
    public ResLoadUserProfiles loadUserProfiles(AuthSignature sig, List<String> uids) throws org.apache.thrift.TException
    {
    	ResLoadUserProfiles response = new ResLoadUserProfiles();

    	if ( (response.error = checkAuthSignature(sig)) != null )
    		return response;
    	
    	try {
    		
    		List<byte[]> rawUids = null;
    		
    		try {
            	rawUids = getRawUids(uids);
            	response.userProfiles = getUserProfiles(rawUids);
    		} 
    		catch (IllegalArgumentException e)
    		{
        		response.error = new ErrorDesc(ErrorCode.VKErrorInvalidParameter, e.getMessage(),
           			                           new ErrorDetail(DetailedErrorCode.VKErrorDetailInvalidUserIdentifierFormat, "Invalid user identifier format. The user identifier should be a string containing numeric values only.", null));
    		}
    	} catch (org.apache.thrift.TException e) {
    		// BUGBUG : do logging and setting response.error object instead of throwing exception.
    		throw e; 
    	}
    	
    	return response;
    }

    public ResRequestFriend requestFriend(AuthSignature sig, String friendUidString) throws org.apache.thrift.TException
    {
    	ResRequestFriend response = new ResRequestFriend();

    	if ( (response.error = checkAuthSignature(sig)) != null )
    		return response;

    	long requesterUserId = -1;
    	long friendUserId = -1;
    	
    	try {
    		requesterUserId = Long.valueOf(sig.uid);
    	} catch (NumberFormatException e) {
    		// This should never happen, because we already checked authentication signature
    		assert(false);
    	}

    	try {
        	friendUserId = Long.valueOf( friendUidString );
    	} catch (NumberFormatException e) {
			response.error = invalidUserIdentifier(friendUidString);
			return response;			
    	}

    	if ( friendUserId == requesterUserId )
		{
			response.error = friendIsRequester(friendUidString);
			return response;
		}
    	
    	UserProfile profile = getUserProfile(friendUserId);
    	
    	if (profile == null) {
    		response.error = noSuchUserError(friendUidString);
    	} else {
    		// Step 2 : Put into the friend user id  table
            put(T_USERS(), Bytes.toBytes(requesterUserId), CF_USERS_FRIENDS, cqUsersFriends(friendUserId), Bytes.toBytes(""));
        	
            // Put into the recipient list of "InBox" context.
            // Why? Followers need to receive messages posted by a user that they follow.
            {
            	// First, check if the friend has  "InBox" context.
        		if ( ! hasUserContext(HARDCODED_SERVICE_ID, friendUserId ) )
        		{
        			createUserContext(HARDCODED_SERVICE_ID, friendUserId );
        		}            
                
                // Add requester to the recipient list of "InBox" context of the ex-friend so that the requester does not receive any posting from the ex-friend. 
                addContextRecipient(HARDCODED_SERVICE_ID, friendUserId, requesterUserId);
            }
            
        	response.friendProfile = profile;
    	}
    	
    	return response;
    }
    
    // Return a set of UIDs for friends of a user with the given uid
    // Return null if no such user is found in T_USERS table.
    private Set<byte[]> getFriendUids(long userId) throws org.apache.thrift.TException
    {
		HTable table = T_USERS();
		try {
			ResultScanner scanner = null;
			
			try {
		    	Set<byte[]> friendUids = null;

	    		Get get = new Get(Bytes.toBytes(userId));
	    		get.addFamily(CF_USERS_FRIENDS);
			
				Result result = table.get(get);
				if ( result.isEmpty())
					return null;
				
				java.util.NavigableMap<byte[],byte[]> friendUIDmap = 
						result.getFamilyMap( CF_USERS_FRIENDS );
				
				friendUids = friendUIDmap.keySet();

				return friendUids;
				
			} finally {
				if (scanner != null)
					scanner.close();
			}
		} catch (IOException e) {
    		throw new org.apache.thrift.TApplicationException("IOException while executing getFriendUids. "+
		                                                      "Table:"+ table.getTableName() +
		                                                      ", ColumnFamily:" + Bytes.toString(CF_USERS_FRIENDS) ); 
		}    	
    }

    
    // Return a set of UIDs that follows of a user with the given uid
    // Return null if no such user is found in T_USERS table.
    // Caution : This method returns a uid with 0(=meta context row) as well. Make sure you filter it out to use it as a list of followers of a user.
    private Set<byte[]> getContextRecipientUidsIncludingZeroUid(long userId) throws org.apache.thrift.TException
    {
		HTable table = T_SERVICE_CONTEXT_MESSAGES();
		
		try {
			ResultScanner scanner = null;
			
			try {
		    	Set<byte[]> followerUids = null;

				// The row key of the "Inbox" Context of the given user.
				// People who receive messages through "Inbox" Context are followers of the given user.
				byte[] userInboxContextRowKey = rkServiceContext(HARDCODED_SERVICE_ID, userId);
	    		Get get = new Get( userInboxContextRowKey );
	    		get.addFamily(CF_SCM_CONTEXT);
			
				Result result = table.get(get);
				if ( result.isEmpty())
					return null;
				
				java.util.NavigableMap<byte[],byte[]> followerUIDmap = 
						result.getFamilyMap( CF_SCM_CONTEXT );
				
				followerUids = followerUIDmap.keySet();

				return followerUids;
				
			} finally {
				if (scanner != null)
					scanner.close();
			}
		} catch (IOException e) {
    		throw new org.apache.thrift.TApplicationException("IOException while executing getContextRecipientUserIds. "+
		                                                      "Table:"+table.getTableName()+
		                                                      ", ColumnFamily:" + Bytes.toString(CF_SCM_CONTEXT) ); 
		}  
    }
    
    public ResLoadFriendProfiles loadFriendProfiles(AuthSignature sig, String uidString) throws org.apache.thrift.TException
    {
    	ResLoadFriendProfiles response = new ResLoadFriendProfiles();

    	if ( (response.error = checkAuthSignature(sig)) != null )
    		return response;
    	
		try {
        	long userId = Long.valueOf( uidString );

        	Set<byte[]> friendUIDs = getFriendUids(userId);
        	
        	// No friends.
        	if (friendUIDs == null)
        	{
            	List<UserProfile> friendProfiles = new ArrayList<UserProfile>();
            	response.friendProfiles = friendProfiles;

        	}
        	else
        	{
            	List<UserProfile> friendProfiles = getUserProfiles(friendUIDs);
            	
    			response.friendProfiles = friendProfiles;
        	}
		} catch (NumberFormatException e) {
			response.error = invalidUserIdentifier(uidString);
		}
		
		return response;
    }
    

    public ResLoadFriendUIDs loadFriendUIDs(AuthSignature sig, String uidString) throws org.apache.thrift.TException
    {
    	ResLoadFriendUIDs response = new ResLoadFriendUIDs();

    	if ( (response.error = checkAuthSignature(sig)) != null )
    		return response;
    	
		try {
        	long userId = Long.valueOf( uidString );

        	Set<byte[]> friendUIDs = getFriendUids(userId);
        	
        	List<String> friendUIDList = new ArrayList<String>();
        	if (friendUIDs == null) // no friends are found. 
        	{
        		// do thing.
        	}
        	else
        	{
            	for(byte[] rawUid : friendUIDs)
            	{
            		long uid = Bytes.toLong(rawUid);
            		friendUIDList.add( Long.toString( uid ) );
            	}
        	}
			response.friendUIDs = friendUIDList;
			
		} catch (NumberFormatException e) {
			response.error = invalidUserIdentifier(uidString);
		}
		
		return response;
    }
    

    public ResCancelFriend cancelFriend(AuthSignature sig, String friendUidString) throws org.apache.thrift.TException
    {
    	ResCancelFriend response = new ResCancelFriend();

    	if ( (response.error = checkAuthSignature(sig)) != null )
    		return response;
    	
    	long requesterUserId = -1;
    	long friendUserId = -1;
    	
    	try {
    		requesterUserId = Long.valueOf(sig.uid);
    	} catch (NumberFormatException e) {
    		// This should never happen, because we already checked authentication signature
    		assert(false);
    	}

    	try {
        	friendUserId = Long.valueOf( friendUidString );
    	} catch (NumberFormatException e) {
			response.error = invalidUserIdentifier(friendUidString);
    	}

    	UserProfile profile = getUserProfile(friendUserId);
    	
    	if ( profile == null )
    	{
    		response.error = noSuchUserError(friendUidString);
    	}
    	else
    	{
            delete(T_USERS(), Bytes.toBytes(requesterUserId), CF_USERS_FRIENDS, Bytes.toBytes(friendUserId));

            // Remove requester from the recipient list of "InBox" context of the ex-friend so that the requester does not receive any posting from the ex-friend. 
            removeContextRecipient(HARDCODED_SERVICE_ID, friendUserId, requesterUserId);
            
            response.canceldFriendProfile = profile;
    	}
    	
    	return response;
    }


    public ResSearchUsers searchUserByEmail(AuthSignature sig, String email) throws org.apache.thrift.TException
    {
    	ResSearchUsers response = new ResSearchUsers();

    	if ( (response.error = checkAuthSignature(sig)) != null )
    		return response;
    	
        byte[] userIdData = get(T_USERID_BY_EMAIL(), Bytes.toBytes(email), CF_UBE_USER, CQ_UBE_USER_ID);
        
    	
        List<UserProfile> profiles = new java.util.Vector<UserProfile>();
        
        if ( userIdData != null )
        {
        	UserProfile profile = getUserProfile(userIdData);

        	if ( profile != null )
        	{
            	profiles.add(profile);
        	}
        }
    	
    	response.userProfiles = profiles;
        
        return response;
    }

	// BUGBUG need to get the service ID from API key generated when the tenant registered a new service in web front end.
    public static long HARDCODED_SERVICE_ID = 1;

    // Get the next contextId for a service
    private long getNextContextId(long serviceId)  throws org.apache.thrift.TException
    {
    	// Get the row key of meta row for the service by specifying META_IDs to context id and message id.
    	byte[] metaServiceRowKey = rkService(serviceId);
    	
    	byte[] metaService = get(T_SERVICE_CONTEXT_MESSAGES(), metaServiceRowKey, CF_SCM_SERVICE, CQ_SCM_NEXT_CONTEXT_ID);
    	// TODO : Move meta service record creation code to Ruby front-end
    	if ( metaService == null ) 
    	{
    		// The meta service row that has the next context id is not existing. Create one.
    		// When we create a service, we need to put a record like below.
    		// The value is half of the max(signed int 64). We get a new context id by subtracting one from the value.
    		// The context Id above the value is used for specific contexts such as location based contexts

    		put(T_SERVICE_CONTEXT_MESSAGES(), metaServiceRowKey, CF_SCM_SERVICE, CQ_SCM_NEXT_CONTEXT_ID, Bytes.toBytes((Long.MAX_VALUE / 2)));
    	}
    	
    	long nextContextId = getIncreasedLong( T_SERVICE_CONTEXT_MESSAGES(), metaServiceRowKey, CF_SCM_SERVICE, CQ_SCM_NEXT_CONTEXT_ID, -1);
    	
    	return nextContextId;
    }

    // Get the next messageId for a context in a service 
    private long getNextMessageId(long serviceId, long contextId)  throws org.apache.thrift.TException
    {
    	// Get the row key of meta row for the service by specifying META_IDs to context id and message id. 
    	byte[] rowKey = rkServiceContext(serviceId, contextId);
    	
    	long nextMessageId = getIncreasedLong( T_SERVICE_CONTEXT_MESSAGES(), rowKey, CF_SCM_CONTEXT, CQ_SCM_NEXT_MESSAGE_ID, -1);
    	
    	return nextMessageId;
    }

    
    // Create the "InBox" context for each user. All messages to the user is sent to the context.
    private void createUserContext(long serviceId, long userId)  throws org.apache.thrift.TException
    {
    	// The contextId of the "InBox" context for a user is same to the userId
    	long contextId = userId;
    	
    	createContext(serviceId, contextId);
    }

    // check if the user has the "InBox" context for each user. 
    private boolean hasUserContext(long serviceId, long userId)  throws org.apache.thrift.TException
    {
    	// The contextId of the "InBox" context for a user is same to the userId
    	long contextId = userId;
    	
    	return isContextExisting( serviceId, contextId );
    }
    
    // return false and set error code to error object if any error happend.
    // return the context row key.
    private byte[] createContext(long serviceId, long contextId)  throws org.apache.thrift.TException
    {
    	// The row key for the meta row of a context. It has message id MESSAGE_ID_FOR_META_CONTEXT.
    	byte[] contextRowKey = rkServiceContext(serviceId, contextId);
    	
    	// Message Id decreases from max(signed 64 bit int)
    	// It is necessary to get recent messages first with HBase Scanner, which does not provide backward scanning feature.
    	long initialMessageId = MAX_MESSAGE_ID;
    	
    	put(T_SERVICE_CONTEXT_MESSAGES(), contextRowKey, CF_SCM_CONTEXT, CQ_SCM_NEXT_MESSAGE_ID, Bytes.toBytes(initialMessageId));
    	
    	return contextRowKey;
    }
    
    private boolean isContextExisting(long serviceId, long contextId)  throws org.apache.thrift.TException
    {
    	// The row key for the meta row of a context. It has message id MESSAGE_ID_FOR_META_CONTEXT.
		byte[] contextRowKey = rkServiceContext(serviceId, contextId);

    	byte[] metaContext = get(T_SERVICE_CONTEXT_MESSAGES(), contextRowKey, CF_SCM_CONTEXT, CQ_SCM_NEXT_MESSAGE_ID);

    	if ( metaContext == null )
    		return false;
    	else 
    		return true;
    }
    
    private ContextMessageContent deserializeMessageContent(byte[] rawMessageContent) throws org.apache.thrift.TException
    {
        ContextMessageContent messageContent = new ContextMessageContent();
        deserializer_.deserialize(messageContent, rawMessageContent);
        
        return messageContent;
    }

    private byte[] serializeMessageContent(ContextMessageContent messageContent) throws org.apache.thrift.TException
    {
        byte[] rawMessageContent = null;

        rawMessageContent = serializer_.serialize(messageContent);
        
        return rawMessageContent;
    }
    
    // Client sees message id increasing from 1 monotonously, but server uses message id decreased from MAX_MESSAGE_ID.
    // Server does so to scan ServiceContextMessages table with descreasing order on message id.
    // ( Recent messages are more important, we need to scan them first with HBase scanner which does not provide backward scanning. )
    private long convertToClientMessageId(long serverMessageId)
    {
    	// MessageId with negative value has special meaning. Don't convert any of them.
    	if ( serverMessageId < 0 )
    		return serverMessageId;
    	
    	return MAX_MESSAGE_ID - serverMessageId;
    }

    private long convertToServerMessageId(long clientMessageId)
    {
    	// MessageId with negative value has special meaning. Don't convert any of them.
    	if ( clientMessageId < 0 )
    		return clientMessageId;
    	
    	return MAX_MESSAGE_ID - clientMessageId;
    }
    
    // contextId : the destiation context id to put a message
    // sender context id : the id of the original context where the sending user sent a message
    public long putMessageToContext(
    		        long serviceId, 
    		        long contextId, 
    		        long senderContextId, 
    		        long senderMessageId, 
    		        String senderUserId, 
    		        long sentTime, 
    		        MessageType messageType, 
    		        ByteBuffer message ) throws org.apache.thrift.TException
    {
    	
		ContextMessageContent messageContent = new ContextMessageContent();
		messageContent.senderContextId = senderContextId;
		messageContent.senderMessageId = senderMessageId;
		messageContent.senderUID = senderUserId;
		messageContent.sentTime = sentTime;
		messageContent.messageType = messageType;
		messageContent.message = message;
		
		byte[] rawMessageContent = serializeMessageContent(messageContent);
		
		// Get the next message Id.
		long mesasgeId = getNextMessageId(serviceId, contextId);
		
		byte[] messageRowKey = rkServiceContextMessages(serviceId, contextId, mesasgeId);

		// TODO : Optimize to use hbase.Put object directly.
    	ArrayList<PutRequest> putRequests = new ArrayList<PutRequest>();
    	putRequests.add( new PutRequest(CF_SCM_MESSAGE, CQ_SCM_MESSAGE,       rawMessageContent) );
    	putRequests.add( new PutRequest(CF_SCM_MESSAGE, CQ_SCM_LIKE_COUNT,    Bytes.toBytes(0L)) );
    	putRequests.add( new PutRequest(CF_SCM_MESSAGE, CQ_SCM_DISLIKE_COUNT, Bytes.toBytes(0L)) );
    	putRequests.add( new PutRequest(CF_SCM_MESSAGE, CQ_SCM_COMMENT_COUNT, Bytes.toBytes(0L)) );
    	
    	put(T_SERVICE_CONTEXT_MESSAGES(), messageRowKey, putRequests);

    	return mesasgeId;
    }

    // Add a recipient to the list of users who will receive messages from the context.
    private void addContextRecipient(long serviceId, long contextId, long recipientUserId)  throws org.apache.thrift.TException
    {
       	// The row key for the meta row of a context. It has message id MESSAGE_ID_FOR_META_CONTEXT.
       	byte[] contextRowKey = rkServiceContext(serviceId, contextId);
       	
    	// Value is the endpoint descriptor of each recipient. 
		// TODO : Update endpoint descriptor when the recipient accepts the invitiation. 
    	put(T_SERVICE_CONTEXT_MESSAGES(), 
    		contextRowKey, 
    		CF_SCM_CONTEXT, 
    		cqServiceContextRecipient(recipientUserId), 
    		null); 
    }
    
    // Remove a recipient from the list of users who will receive messages from the context.
    private void removeContextRecipient(long serviceId, long contextId, long recipientUserId)  throws org.apache.thrift.TException
    {
       	// The row key for the meta row of a context. It has message id MESSAGE_ID_FOR_META_CONTEXT.
       	byte[] contextRowKey = rkServiceContext(serviceId, contextId);
    	
       	delete(T_SERVICE_CONTEXT_MESSAGES(), 
        		  contextRowKey, 
        		  CF_SCM_CONTEXT, 
        		  cqServiceContextRecipient(recipientUserId) ); 
    }
    
    public ResCreateContext createContext(AuthSignature sig, List<String> recipientUids) throws org.apache.thrift.TException
    {
    	ResCreateContext response = new ResCreateContext();

    	if ( (response.error = checkAuthSignature(sig)) != null )
    		return response;
    	
    	long sentTime = System.currentTimeMillis();

    	// BUGBUG : Instead of using hard-coded service id, get the service id from client.
    	long contextId = getNextContextId(HARDCODED_SERVICE_ID);
    	
    	response.createdContextId = contextId;
    	
    	byte[] contextRowKey = createContext(HARDCODED_SERVICE_ID, contextId);

    	// Add the requester who created the context as a recipient.
    	long requesterUserId = -1;
    	try {
    		requesterUserId = Long.valueOf(sig.uid);
    	} catch (NumberFormatException e) {
    		// This should never happen, because we already checked authentication signature
    		assert(false);
    	}
    	
    	// Add the requester user id to recipientUids if not included
    	{
    		String requesterUid = sig.uid;
    		boolean requesterIncluded = false;
        	for (String uidString : recipientUids)
        	{
        		if ( uidString.equals(requesterUid))
        		{
            		requesterIncluded = true;
        			break;
        		}
        	}
        	if (! requesterIncluded)
        		recipientUids.add(requesterUid);
    	}
    	
    	for (String uidString : recipientUids) {
    		try {
    			long receivingUid = Long.valueOf(uidString);

    	    	// Value is the endpoint descriptor of each recipient. 
    			// TODO : Update endpoint descriptor when the recipient accepts the invitiation. 
    	    	addContextRecipient(HARDCODED_SERVICE_ID, contextId, receivingUid);
    			
    			// Skip putting the invitiation message to the user InBox if the requester is in the recipient list,
    	    	// Because we don't need to send invitation to the creator of the context.
    			if ( receivingUid == requesterUserId )
    				continue;
    	    	
        		// Create the "InBox" user context if it does not exist.
        		// BUGBUG : Use the serviceId provided by the client.
        		if ( ! hasUserContext(HARDCODED_SERVICE_ID, receivingUid ) )
        		{
        			createUserContext(HARDCODED_SERVICE_ID, receivingUid );
        		}
        		
        		// Put the "MT_CONTEXT_INVITATION" to all recipients by serializing ContextInvitationMessage which has playersToInvite.
        		ContextInvitationMessage invitationMessage = new ContextInvitationMessage();
        		// BUGBUG : Optimize not to store playersToInvite in MT_CONTEXT_INVITATION.
        		// The space required is O(N^2) because we put playersToInvite for each user in uids.
        		invitationMessage.playersToInvite =  recipientUids;
        		byte[] rawContextInvitiationMessage = serializer_.serialize(invitationMessage);
        		
        		putMessageToContext(HARDCODED_SERVICE_ID, 
        				            receivingUid, /* The context for "InBox" of the receiving user */ 
        				            contextId,    /* The original context */
        				            0L,            /* No original message ID. */
        				            sig.uid, 
        				            sentTime, 
        				            MessageType.MT_CONTEXT_INVITATION, 
        				            ByteBuffer.wrap(rawContextInvitiationMessage) );
    			
    		} catch (NumberFormatException e) {
    			response.error = invalidUserIdentifier(uidString);
    			break;
    		}
    	}
    	
    	return response;
    }
/*
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
  */  
    public ResSendMessage sendMessage(AuthSignature sig, ReqSendMessage req) throws org.apache.thrift.TException
    {
    	ResSendMessage response = new ResSendMessage();

    	if ( (response.error = checkAuthSignature(sig)) != null )
    		return response;
    	
    	if ( req.cid <= 0 )
    	{
    		response.error = new ErrorDesc(ErrorCode.VKErrorInvalidParameter, "Invalid context(match) identifier : " + req.cid,
       			                           new ErrorDetail(DetailedErrorCode.VKErrorDetailInvalidContextIdentifier, "Invalid context identifier value. The context identifier format should be greater than zero.", null));
    		return response;
    	}
    	
    	long requesterUserId = -1;
    	try {
    		requesterUserId = Long.valueOf(sig.uid);
    	} catch (NumberFormatException e) {
    		// This should never happen, because we already checked authentication signature
    		assert(false);
    	}
    	
/*    	
    	{
    		String data = "";
    	    Charset charset = Charset.forName("US-ASCII");
    	    CharsetDecoder decoder = charset.newDecoder();
    		try {
        		data = decoder.decode(req.message).toString();
    		} catch ( CharacterCodingException e) {
            	System.out.println( "CharacterCodingException : " + e.getMessage() );
    		}
        	System.out.println( "The message to send : " + data );
    	}
*/
    	long sentTime = System.currentTimeMillis();
  
    	// Put the message into the context
		long newMessageId = 
	    	putMessageToContext(HARDCODED_SERVICE_ID, 
	    			req.cid, // The id of the context that the user wants to send message
                    req.cid, // the original context id
                    0L,      // the original message id - 0 means this is the original.
                    sig.uid, 
                    sentTime, 
                    MessageType.MT_CONTEXT_MESSAGE,
                    req.message );
		response.createdMessageId = convertToClientMessageId(newMessageId);

		byte[] originalMessageRowKey = rkServiceContextMessages(HARDCODED_SERVICE_ID, req.cid, newMessageId);
		
		HTable table = T_SERVICE_CONTEXT_MESSAGES();
		
		try {
			ResultScanner scanner = null;
			
	    	byte[] contextRowKey = rkServiceContext( HARDCODED_SERVICE_ID, req.cid);
			
			try {

	    		Get get = new Get(contextRowKey);
	    		get.addFamily(CF_SCM_CONTEXT);
			
				Result hbaseResult = table.get(get);
				NavigableMap<byte[],byte[]> columnQualifierToValueMap = hbaseResult.getFamilyMap(CF_SCM_CONTEXT);

				if ( columnQualifierToValueMap == null )
				{
		    		response.error = new ErrorDesc(ErrorCode.VKErrorInvalidParameter, "Invalid context(match) identifier : " + req.cid,
               			                           new ErrorDetail(DetailedErrorCode.VKErrorDetailContextNotFound, "The context with the given context identifier is not found.", null));
		    		return response;
				}
				
				// loop for each column qualifier in the meta context row.
				// BUGBUG : Optimize to keep the mapping from ContextId to list of recipient IDs 
				for( byte[] rawRecipientUserId : columnQualifierToValueMap.keySet())
				{
					long userId = getServiceContextRecipientId( rawRecipientUserId );
					if ( userId == SCM_META_CONTEXT_ROW_RECIPIENT_USER_ID ) // The column quantifier should not be "0" which has the meta row for a context.
						continue;
					if ( userId == requesterUserId ) // Don't put the message into the sending user's InBox.
						continue;
					
					long propagatedMessageId = 
			    	putMessageToContext(HARDCODED_SERVICE_ID, 
			    			userId,  // The context id of "InBox" context for the recipient.
                            req.cid, // the original context id
                            newMessageId, // the original message id
                            sig.uid, 
                            sentTime, 
                            MessageType.MT_CONTEXT_MESSAGE,
                            req.message );
			    	
					// Add to the list of propagated messages to other contexts. 
					// This will be used to propagate the number of replies in the original message in commentMessage service function.
			    	put(T_SERVICE_CONTEXT_MESSAGES(), originalMessageRowKey, CF_SCM_PROPAGATED_MESSAGES, cqServiceContextMessagePropagatedMessage(userId), Bytes.toBytes(propagatedMessageId));
				}

			} finally {
				if (scanner != null)
					scanner.close();
			}
		} catch (IOException e) {
    		throw new org.apache.thrift.TApplicationException("IOException while executing sendMessage. "+
		                                                      "Table:"+table.getTableName()+
		                                                      ", ColumnFamily:d"); 

		}
    	
    	return response;
    }

    public void sendOnewayMessage(AuthSignature sig, ReqSendMessage req) throws org.apache.thrift.TException
    {
    	sendMessage(sig, req);
	}
    
    public ResPullMessages pullMessages(AuthSignature sig, ReqPullMessages req) throws org.apache.thrift.TException
//    public PullDataResponse pullData(PullDataRequest req) throws org.apache.thrift.TException
    {
    	ResPullMessages response = new ResPullMessages();

    	if ( (response.error = checkAuthSignature(sig)) != null )
    		return response;
    	
    	List<ContextMessage> messageList = new ArrayList<ContextMessage>();
    	
    	long minServerMessageId = -1;
    	
    	long requesterUserId = -1;
    	try {
    		requesterUserId = Long.valueOf(sig.uid);
    	} catch (NumberFormatException e) {
    		// This should never happen, because we already checked authentication signature
    		assert(false);
    	}
    	
    	long clientStartMessageId = req.startMessageID ; // Inclusive
    	long clientStopMessageId  = req.stopMessageID ;  // Exclusive
    	
    	
    	if (clientStartMessageId <= 0)
    		clientStartMessageId = 0;
    	
    	if (clientStopMessageId <= 0)
    		clientStopMessageId = MAX_MESSAGE_ID;
    	
    	// Add 1 to make serverStartMessageId inclusive.
    	long serverStartMessageId = convertToServerMessageId( clientStopMessageId ) + 1; // Inclusive
    	// Add 1 to make serverStartMessageId exclusive.
    	long serverStopMessageId = convertToServerMessageId( clientStartMessageId ); 
    	if ( serverStopMessageId < MAX_MESSAGE_ID) // Exclusive
    		serverStopMessageId++;
    	
    	byte[] startRowKey = rkServiceContextMessages( HARDCODED_SERVICE_ID, requesterUserId, serverStartMessageId);
    	byte[] stopRowKey  = rkServiceContextMessages( HARDCODED_SERVICE_ID, requesterUserId, serverStopMessageId);
    	
		HTable table = T_SERVICE_CONTEXT_MESSAGES();
		
		try {
			ResultScanner scanner = null;
			
			try {
				Scan scan = new Scan();
				scan.setStartRow( startRowKey );
				scan.setStopRow( stopRowKey );
				scan.addFamily( CF_SCM_MESSAGE );
				
				scanner = table.getScanner(scan);
				
				for (Result res : scanner.next(MAX_PULL_MESSAGE_COUNT)) {
					byte [] rowKey = res.getRow();
					// Get messageId from the row key.
					long messageId = getMessageId(rowKey);
					
					if (minServerMessageId < 0)
						minServerMessageId = messageId;
					
					byte [] rawMessageContent = res.getValue(CF_SCM_MESSAGE, CQ_SCM_MESSAGE);
					ContextMessageContent messageContent = deserializeMessageContent( rawMessageContent );

					// Convert to client message ID.
					messageContent.senderMessageId = this.convertToClientMessageId(messageContent.senderMessageId);
					
					ContextMessage contextMessage = new ContextMessage();
					contextMessage.messageID = convertToClientMessageId( messageId ); 
					contextMessage.messageContent = messageContent;
					contextMessage.commentCount = Bytes.toLong( res.getValue(CF_SCM_MESSAGE, CQ_SCM_COMMENT_COUNT) );
					contextMessage.likeCount = Bytes.toLong( res.getValue(CF_SCM_MESSAGE, CQ_SCM_LIKE_COUNT) );
					contextMessage.dislikeCount = Bytes.toLong( res.getValue(CF_SCM_MESSAGE, CQ_SCM_DISLIKE_COUNT) );
					
					messageList.add(contextMessage);
				}

				if (messageList.size() > 0 )
				{
					response.maxMessageID = convertToClientMessageId( minServerMessageId );
				}
				response.messageList = messageList;
				return response;
			} finally {
				if (scanner != null)
					scanner.close();
			}
		} catch (IOException e) {
    		throw new org.apache.thrift.TApplicationException("IOException while executing pullData. "+
		                                                      "Table:"+table.getTableName()+
		                                                      ", ColumnFamily:d"); 
		} 
    }

    // Error if the same user evaluated message twice. (Including "Like" after "Dislike". Including "Dislike" after "Like". )
    // Error if the user cancels "Like" without previous "Like".
    // Error if the user cancels "Dislike" without previous "Dislike".
    public ResEvalMessage evalMessage(AuthSignature sig, ReqEvalMessage req) throws org.apache.thrift.TException
    {
    	ResEvalMessage response = new ResEvalMessage();

    	if ( (response.error = checkAuthSignature(sig)) != null )
    		return response;
    	
    	long contextID = req.contextID;
    	long serverMessageID = convertToServerMessageId(req.messageID);
    	
    	return response;
    }

    // TODO : Abort all changes if only part of the modification was successful in this service method.
    public ResCommentMessage commentMessage(AuthSignature sig, ReqCommentMessage req) throws org.apache.thrift.TException
    {
    	ResCommentMessage response = new ResCommentMessage();

    	if ( (response.error = checkAuthSignature(sig)) != null )
    		return response;
    	
    	long serverMessageID = convertToServerMessageId(req.messageID);
    	
		byte[] messageRowKey = rkServiceContextMessages(HARDCODED_SERVICE_ID, req.contextID, serverMessageID);
		
		
    	// Replies are stored only in the original context. The content of the reply is not propagated to corresponding messages in "InBox" contexts of recipients of the origianl context.
    	// However, the count of replies are propagated to corresponding messages in "InBox" contexts of recipients of the original context.
    	// Why? By design, SNS client will show the number of replies, not the content of the replies in the list of news feeds for a user.
    	// To see replies, the user has to open up a separate dialog, which in turn request replies stored in the original context.
    	//
    	// Open Issue : In case a user has a huge number of followers, we have two issues
    	//   1) The number of replies should be propagated to a massive number of messages in the "InBox" contexts of all followers. 
    	//   2) To see the actual reply text, huge number of followers will have to access the same HBase Region Server which has the message in the original context.
		{
	    	MessageReplyContent replyContent = new MessageReplyContent();
	    	replyContent.authorUserId = Long.valueOf(sig.uid);
	    	replyContent.reply = req.comment;
	    	
	    	byte[] rawReplyContent = serializer_.serialize(replyContent);
			
			// Add comment to the message in the original context.
			long timestamp = System.currentTimeMillis();
	    	put(T_SERVICE_CONTEXT_MESSAGES(), messageRowKey, CF_SCM_REPLY, cqServiceContextMessageReply(timestamp), rawReplyContent);

			    	
			// Increase the reply count in the original context
	    	getIncreasedLong( T_SERVICE_CONTEXT_MESSAGES(), messageRowKey, CF_SCM_MESSAGE, CQ_SCM_COMMENT_COUNT, 1);
		}
		
    	// Propagate the increment to corresponding messages in the "InBox" contexts of all recipients.
		HTable table = T_SERVICE_CONTEXT_MESSAGES();
		
		try {
			ResultScanner scanner = null;
			
			try {

	    		Get get = new Get(messageRowKey);
	    		get.addFamily(CF_SCM_PROPAGATED_MESSAGES);
			
				Result hbaseResult = table.get(get);
				
				if (hbaseResult.isEmpty())
				{
					// TODO : Check assumption : Even though a row with the row key exists, if no value was put into the column family,  hbaseResult.isEmpty() evaluates to true.
					
					//
					// Do nothing.
					//
					// The message was not propagated to any other messages. 
					// In SNS, if a user does not have any follower, no message is propagated to any user.
				}
				
				NavigableMap<byte[],byte[]> columnQualifierToValueMap = hbaseResult.getFamilyMap(CF_SCM_PROPAGATED_MESSAGES);

				if ( columnQualifierToValueMap == null )
				{
					//
					// Do nothing.
					//
					// The message was not propagated to any other messages. 
					// In SNS, if a user does not have any follower, no message is propagated to any user.
				}
				else
				{
					// TODO : Optimize to send request to HBase only once.
					// loop for each propagated message
					for( Entry<byte[],byte[]> entry : columnQualifierToValueMap.entrySet())
					{
						
						long propagatedContextId = Bytes.toLong( entry.getKey() ); // The key is user id. The ID of "InBox" context of the user is same to the user ID.
						long propagatedMessageId = Bytes.toLong( entry.getValue() );

						byte[] propagatedMessageRowKey =  rkServiceContextMessages(HARDCODED_SERVICE_ID, propagatedContextId, propagatedMessageId);
						// Increase the reply count in the original context
				    	getIncreasedLong( T_SERVICE_CONTEXT_MESSAGES(), propagatedMessageRowKey, CF_SCM_MESSAGE, CQ_SCM_COMMENT_COUNT, 1);
					}
				}

			} finally {
				if (scanner != null)
					scanner.close();
			}
		} catch (IOException e) {
    		throw new org.apache.thrift.TApplicationException("IOException while executing commentMessage. "+
		                                                      "Table:"+table.getTableName()+
		                                                      ", ColumnFamily:"+CF_SCM_PROPAGATED_MESSAGES); 

		}	    	

    	return response;
    }

    public ResLoadComments loadComments(AuthSignature sig, ReqLoadComments req) throws org.apache.thrift.TException
    {
    	ResLoadComments response = new ResLoadComments();

    	if ( (response.error = checkAuthSignature(sig)) != null )
    		return response;

    	long serverMessageID = convertToServerMessageId(req.senderMessageID);
    	
    	response.senderContextID = req.senderContextID;
    	response.senderMessageID = req.senderMessageID;
    	
		byte[] messageRowKey = rkServiceContextMessages(HARDCODED_SERVICE_ID, req.senderContextID, serverMessageID);

		
		response.commentList = new ArrayList<MessageComment>();
		
    	// Propagate the increment to corresponding messages in the "InBox" contexts of all recipients.
		HTable table = T_SERVICE_CONTEXT_MESSAGES();
		try {
			ResultScanner scanner = null;
			
			try {
	    		Get get = new Get(messageRowKey);
	    		get.addFamily(CF_SCM_REPLY);

				Result hbaseResult = table.get(get);
				if (hbaseResult.isEmpty())
				{
					// TODO : Check assumption : Even though a row with the row key exists, if no value was put into the column family,  hbaseResult.isEmpty() evaluates to true.
					// Do nothing, no comment available.
				}
				
				NavigableMap<byte[],byte[]> columnQualifierToValueMap = hbaseResult.getFamilyMap(CF_SCM_REPLY);

				if ( columnQualifierToValueMap == null )
				{
					// Do nothing.
					// No comment available.
				}
				else
				{
					// loop for each reply
					for( Entry<byte[],byte[]> entry : columnQualifierToValueMap.entrySet())
					{
						
						long commentedTime = Bytes.toLong( entry.getKey() ); // The key is timestamp
						byte[] rawReplyContent = entry.getValue();
						
				    	MessageReplyContent replyContent = new MessageReplyContent();
				    	deserializer_.deserialize(replyContent, rawReplyContent);
				    	
				    	MessageComment comment = new MessageComment();
				    	comment.comment = replyContent.reply;
				    	comment.commenterUID = Long.toString(replyContent.authorUserId);
				    	comment.commentTime = commentedTime;
				    	response.commentList.add(comment);
					}
				}

			} finally {
				if (scanner != null)
					scanner.close();
			}
		} catch (IOException e) {
    		throw new org.apache.thrift.TApplicationException("IOException while executing loadComments. "+
		                                                      "Table:"+table.getTableName()+
		                                                      ", ColumnFamily:"+CF_SCM_REPLY);

		}	    	
    	
    	return response;
    }

    
    public ResLoadFollowerUIDs loadFollowerUIDs(AuthSignature sig, String uidString) throws org.apache.thrift.TException
    {
    	ResLoadFollowerUIDs response = new ResLoadFollowerUIDs();

    	if ( (response.error = checkAuthSignature(sig)) != null )
    		return response;
    	
		try {
        	long userId = Long.valueOf( uidString );

        	Set<byte[]> rawFollowerUIDs = getContextRecipientUidsIncludingZeroUid(userId);
        	
        	List<String> followerUIDList = new ArrayList<String>();
        	if (rawFollowerUIDs == null) // no friends are found. 
        	{
        		// do thing.
        	}
        	else
        	{
            	for(byte[] rawUid : rawFollowerUIDs)
            	{
            		long followerUid = Bytes.toLong(rawUid);
            		if ( followerUid != 0)
                		followerUIDList.add( Long.toString(followerUid) );
            	}
        	}
			response.followerUIDs = followerUIDList;
			
		} catch (NumberFormatException e) {
			response.error = invalidUserIdentifier(uidString);
		}
		
		return response;
    	
    }

    
    private TSerializer  serializer_ = new TSerializer();
    private TDeserializer deserializer_ = new TDeserializer();

}
