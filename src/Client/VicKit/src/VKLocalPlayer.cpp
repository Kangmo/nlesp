
#include <VicKit/VKLocalPlayer.h>
#include "VKInternal.h"
#include "AsyncRequester.h"
#include "VicKit_types.h"
#include <VicKit/VKError.h>

TxString VKPlayerAuthenticationDidChangeNotificationName;

static VKLocalPlayer * theLocalPlayer = NULL;

VKLocalPlayer * VKLocalPlayer::localPlayer()
{
    if ( theLocalPlayer == NULL ) 
    {
        theLocalPlayer = new VKLocalPlayer();
    }
    return theLocalPlayer;
}

VKLocalPlayer::VKLocalPlayer()
{
	underage_ = false;
    authenticated_ = false;

	authChangeHandler_ = NULL;
}

VKLocalPlayer::~VKLocalPlayer()
{
    
}

//------------------------------------------------------------------------------------
//                                 authenticate
//------------------------------------------------------------------------------------
class __AuthenticateUserHandler : public InternalCompletionHandler<VicKit::ResAuthenticateUser> {
public :
	__AuthenticateUserHandler( VKLocalPlayer::AuthenticateHandler * hanlder, bool * ptrLoaclPlayerAuthenticated, TxString * ptrPlayerID, TxString * ptrPlayerAlias ) {
		VK_ASSERT(ptrLoaclPlayerAuthenticated);

		handler_ = hanlder;
		ptrLoaclPlayerAuthenticated_ = ptrLoaclPlayerAuthenticated;
		ptrPlayerID_    = ptrPlayerID;
		ptrPlayerAlias_ = ptrPlayerAlias;
	};

	virtual ~__AuthenticateUserHandler() {};

	virtual void onComplete( const VicKit::ResAuthenticateUser & response) {
		if (response.error.code != VicKit::ErrorCode::VKSuccess )
		{
			VKError error((VKErrorCode)response.error.code, response.error.message);

			// Clear authentication signature
			VicKit::AuthSignature authSignature;
			authSignature.uid = "";
			authSignature.signature = "";

			VKInternal::setAuthSignature( authSignature );

			*ptrLoaclPlayerAuthenticated_ = false;
			*ptrPlayerID_ = "";
			*ptrPlayerAlias_ = "";

			handler_->onAuthenticate( & error );

//			printf("[DEBUG] Authentication Failed.\n");
		}
		else
		{
			// Keep the authentication signature to pass as the first argument of each service request after the successful authentication.
			VKInternal::setAuthSignature( response.authSignature );

			// BUGBUG : Cache Session Key for the user.
			*ptrLoaclPlayerAuthenticated_ = true;
			*ptrPlayerID_ = response.userProfile.uid;
			*ptrPlayerAlias_ = response.userProfile.name;

			handler_->onAuthenticate( NULL );
//			printf("[DEBUG] Authentication Succeeded.\n");
		}
	};
private :
	VKLocalPlayer::AuthenticateHandler * handler_;
	bool * ptrLoaclPlayerAuthenticated_;
	TxString * ptrPlayerID_;
	TxString * ptrPlayerAlias_;
};

void VKLocalPlayer::authenticate(const TxString & email, const TxString & password, AuthenticateHandler * handler)
{
    VK_ASSERT(handler);

	boost::shared_ptr<__AuthenticateUserHandler> internalHandler ( new __AuthenticateUserHandler(handler, & authenticated_, &playerID_, &alias_) );

	VicKit::ReqAuthenticateUser req;
	req.email = email;
	// BUGBUG : Need to encrypt the password
	req.encryptedPassword = password;

	VicKit::AuthSignature dummyAuthSignature;

    boost::shared_ptr<RequestingTask<VicKit::ReqAuthenticateUser, VicKit::ResAuthenticateUser> > requestingTask(
        new RequestingTask<VicKit::ReqAuthenticateUser, VicKit::ResAuthenticateUser>
        (  &VicKit::VicDataServiceClient::authenticateUser,
           dummyAuthSignature, /* No authentication signature at this point. This parameter is ignored. */
           req,
           internalHandler)
    );

    theAsyncRequester.Request(requestingTask);
}

//------------------------------------------------------------------------------------
//                                 loadFriends
//------------------------------------------------------------------------------------

class __LoadFriendsHandler: public InternalCompletionHandler< VicKit::ResLoadFriendUIDs > {
public :
	__LoadFriendsHandler( VKLocalPlayer::LoadFriendsHandler * hanlder ) {
		handler_ = hanlder;
	};

	virtual ~__LoadFriendsHandler() {};

	virtual void onComplete( const VicKit::ResLoadFriendUIDs & response ) {

		std::vector<VKLocalPlayer::TxUserProfile> profiles;

		if (response.error.code != VicKit::ErrorCode::VKSuccess )
		{
			VKError error((VKErrorCode)response.error.code, response.error.message);
			handler_->onLoadFriends(response.friendUIDs, & error );
		}
		else
		{
			handler_->onLoadFriends(response.friendUIDs, NULL);
		}
	};
private :
	VKLocalPlayer::LoadFriendsHandler * handler_;
};


void VKLocalPlayer::loadFriends(LoadFriendsHandler * handler)
{
    VK_ASSERT(handler);

	boost::shared_ptr<__LoadFriendsHandler> internalHandler ( new __LoadFriendsHandler(handler) );

	const VicKit::UID & myUserId = VKInternal::myUID();

    boost::shared_ptr<RequestingTask< VicKit::UID, VicKit::ResLoadFriendUIDs > > requestingTask(
        new RequestingTask<VicKit::UID, VicKit::ResLoadFriendUIDs >

        (  &VicKit::VicDataServiceClient::loadFriendUIDs,
       	   VKInternal::authSignature(),
       	   myUserId,
           internalHandler)
    );

    theAsyncRequester.Request(requestingTask);
}

//------------------------------------------------------------------------------------
//                                 createUserProfile
//------------------------------------------------------------------------------------

static inline VicKit::UserProfile ConvertToThrift( const VKLocalPlayer::TxUserProfile & userProfile )
{
	VicKit::UserProfile p;

	// The unique user id that monotonously increase from 1
	p.uid = userProfile.uid;
	// Email address
	p.email = userProfile.email;
	// Encrypted Password : Need to encrypt the password that the user typed
	p.encryptedPassword = userProfile.encryptedPassword;
	// Name of the user
	p.name = userProfile.name;
	// The status message to show in the friend list.
	p.statusMessage = userProfile.statusMessage;
	// The blob containing photo to show on "my profile", and list of friends.
	p.photo = ConvertToThrift( userProfile.photo );

	return p;
}
static inline VKLocalPlayer::TxUserProfile ConvertFromThrift( const VicKit::UserProfile & userProfile )
{
	VKLocalPlayer::TxUserProfile p;


	// The unique user id that monotonously increase from 1
	p.uid = userProfile.uid;
	// Email address
	p.email = userProfile.email;
	// Encrypted Password : Need to encrypt the password that the user typed
	p.encryptedPassword = userProfile.encryptedPassword;
	// Name of the user
	p.name = userProfile.name;
	// The status message to show in the friend list.
	p.statusMessage = userProfile.statusMessage;
	// The blob containing photo to show on "my profile", and list of friends.
	p.photo = ConvertFromThrift( userProfile.photo );

	return p;
}

class __CreateUserProfileHandler : public InternalCompletionHandler<VicKit::ResCreateUserProfile> {
public :
	__CreateUserProfileHandler( VKLocalPlayer::CreateUserProfileHandler * hanlder ) {
		handler_ = hanlder;
	};

	virtual ~__CreateUserProfileHandler() {};

	virtual void onComplete( const VicKit::ResCreateUserProfile & response) {
		if (response.error.code != VicKit::ErrorCode::VKSuccess )
		{
			VKError error((VKErrorCode)response.error.code, response.error.message);
			handler_->onCreateUserProfile("", & error );
		}
		else
		{
			handler_->onCreateUserProfile(response.createdUserId, NULL);
		}
	};
private :
	VKLocalPlayer::CreateUserProfileHandler * handler_;
};

// Register me as a new user
void VKLocalPlayer::createUserProfile(const TxUserProfile & userProfile, CreateUserProfileHandler * handler)
{
	boost::shared_ptr<__CreateUserProfileHandler> internalHandler ( new __CreateUserProfileHandler(handler) );

	VicKit::UserProfile req;
	req = ConvertToThrift(userProfile);

    boost::shared_ptr<RequestingTask<VicKit::UserProfile, VicKit::ResCreateUserProfile> > requestingTask(
        new RequestingTask<VicKit::UserProfile, VicKit::ResCreateUserProfile>
        (  &VicKit::VicDataServiceClient::createUserProfile,
    	   VKInternal::authSignature(),
           req,
           internalHandler)
    );

    theAsyncRequester.Request(requestingTask);
}


//------------------------------------------------------------------------------------
//                                 updateUserProfile
//------------------------------------------------------------------------------------

class __UpdateUserProfileHandler : public InternalCompletionHandler<VicKit::ResUpdateUserProfile> {
public :
	__UpdateUserProfileHandler( VKLocalPlayer::UpdateUserProfileHandler * hanlder ) {
		handler_ = hanlder;
	};

	virtual ~__UpdateUserProfileHandler() {};

	virtual void onComplete( const VicKit::ResUpdateUserProfile & response) {
		if (response.error.code != VicKit::ErrorCode::VKSuccess )
		{
			VKError error((VKErrorCode)response.error.code, response.error.message);

			handler_->onUpdateUserProfile( &error );
		}
		else
		{
			handler_->onUpdateUserProfile(NULL);
		}
	};
private :
	VKLocalPlayer::UpdateUserProfileHandler * handler_;
};

// [1.4.2] Update my profile
void VKLocalPlayer::updateUserProfile(const TxUserProfile & userProfile, UpdateUserProfileHandler * handler)
{
	boost::shared_ptr<__UpdateUserProfileHandler> internalHandler ( new __UpdateUserProfileHandler(handler) );

	VicKit::UserProfile req;
	req = ConvertToThrift(userProfile);

    boost::shared_ptr<RequestingTask<VicKit::UserProfile, VicKit::ResUpdateUserProfile> > requestingTask(
        new RequestingTask<VicKit::UserProfile, VicKit::ResUpdateUserProfile>
        (  &VicKit::VicDataServiceClient::updateUserProfile,
       	   VKInternal::authSignature(),
           req,
           internalHandler)
    );

    theAsyncRequester.Request(requestingTask);
}

//------------------------------------------------------------------------------------
//                                 loadUserProfile
//------------------------------------------------------------------------------------

class __LoadUserProfileHandler : public InternalCompletionHandler<VicKit::ResLoadUserProfiles> {
public :
	__LoadUserProfileHandler( VKLocalPlayer::LoadUserProfileHandler * hanlder ) {
		handler_ = hanlder;
	};

	virtual ~__LoadUserProfileHandler() {};

	virtual void onComplete( const VicKit::ResLoadUserProfiles & response) {

		VKLocalPlayer::TxUserProfile profile;

		if (response.error.code != VicKit::ErrorCode::VKSuccess )
		{
			VKError error((VKErrorCode)response.error.code, response.error.message);

			handler_->onLoadUserProfile(profile, & error );
		}
		else
		{
			if ( response.userProfiles.size() == 1 )
			{
				VicKit::UserProfile profileFromServer = response.userProfiles.at(0);
				profile = ConvertFromThrift( profileFromServer );
				handler_->onLoadUserProfile(profile, NULL);
			}
			else
			{
				VKError error(VKErrorInvalidParameter, "No such user is found.");
				handler_->onLoadUserProfile(profile, & error );
			}
		}
	};
private :
	VKLocalPlayer::LoadUserProfileHandler * handler_;
};

// [1.4] Get my profile - set my UID ( unique user ID that monotonously increases from 1 )
void VKLocalPlayer::loadUserProfile(const TxString & uid, LoadUserProfileHandler * handler)
{
	boost::shared_ptr<__LoadUserProfileHandler> internalHandler ( new __LoadUserProfileHandler(handler) );

	std::vector<VicKit::UID> uids;
	uids.push_back(uid);

    boost::shared_ptr<RequestingTask<std::vector<VicKit::UID>, VicKit::ResLoadUserProfiles> > requestingTask(
        new RequestingTask<std::vector<VicKit::UID>, VicKit::ResLoadUserProfiles>
        (  &VicKit::VicDataServiceClient::loadUserProfiles,
       	   VKInternal::authSignature(),
       	   uids,
           internalHandler)
    );

    theAsyncRequester.Request(requestingTask);
}


//------------------------------------------------------------------------------------
//                                 loadFriendProfiles
//------------------------------------------------------------------------------------
static inline std::vector<VKLocalPlayer::TxUserProfile> ConvertFromThrift( std::vector<VicKit::UserProfile> response )
{
	std::vector<VKLocalPlayer::TxUserProfile> profiles;

	for (size_t i=0; i< response.size(); i++) {
		VKLocalPlayer::TxUserProfile p = ConvertFromThrift( response[i] );
		profiles.push_back( p );
	}
	return profiles;
}


class __LoadFriendProfilesHandler: public InternalCompletionHandler< VicKit::ResLoadFriendProfiles > {
public :
	__LoadFriendProfilesHandler( VKLocalPlayer::LoadFriendProfilesHandler * hanlder ) {
		handler_ = hanlder;
	};

	virtual ~__LoadFriendProfilesHandler() {};

	virtual void onComplete( const VicKit::ResLoadFriendProfiles & response ) {

		std::vector<VKLocalPlayer::TxUserProfile> profiles;

		if (response.error.code != VicKit::ErrorCode::VKSuccess )
		{
			VKError error((VKErrorCode)response.error.code, response.error.message);
			handler_->onLoadFriendProfiles(profiles, & error );
		}
		else
		{
			profiles = ConvertFromThrift( response.friendProfiles );

			handler_->onLoadFriendProfiles(profiles, NULL);
		}
	};
private :
	VKLocalPlayer::LoadFriendProfilesHandler * handler_;
};

// [1] get the list of friend profiles
void VKLocalPlayer::loadFriendProfiles(const TxString & uid, LoadFriendProfilesHandler * handler)
{
	boost::shared_ptr<__LoadFriendProfilesHandler> internalHandler ( new __LoadFriendProfilesHandler(handler) );

    boost::shared_ptr<RequestingTask< TxString, VicKit::ResLoadFriendProfiles > > requestingTask(
        new RequestingTask<TxString, VicKit::ResLoadFriendProfiles >
        (  &VicKit::VicDataServiceClient::loadFriendProfiles,
       	   VKInternal::authSignature(),
           uid,
           internalHandler)
    );

    theAsyncRequester.Request(requestingTask);
}


//------------------------------------------------------------------------------------
//                                 requestFriend
//------------------------------------------------------------------------------------
class __RequestFriendHandler : public InternalCompletionHandler<VicKit::ResRequestFriend> {
public :
	__RequestFriendHandler( VKLocalPlayer::RequestFriendHandler * hanlder ) {
		handler_ = hanlder;
	};

	virtual ~__RequestFriendHandler() {};

	virtual void onComplete( const VicKit::ResRequestFriend & response) {

		if (response.error.code != VicKit::ErrorCode::VKSuccess )
		{
			VKError error((VKErrorCode)response.error.code, response.error.message);

			VKLocalPlayer::TxUserProfile profile;
			handler_->onRequestFriend(profile, & error);
		}
		else
		{
			VKLocalPlayer::TxUserProfile profile = ConvertFromThrift(response.friendProfile);
			handler_->onRequestFriend(profile, NULL);
		}
	};
private :
	VKLocalPlayer::RequestFriendHandler * handler_;
};


// [1.2] Request to become a friend using his UID
void VKLocalPlayer::requestFriend(const TxString & uid, RequestFriendHandler * handler)
{
	boost::shared_ptr<__RequestFriendHandler> internalHandler ( new __RequestFriendHandler(handler) );

    boost::shared_ptr<RequestingTask<VicKit::UID, VicKit::ResRequestFriend> > requestingTask(
        new RequestingTask<VicKit::UID, VicKit::ResRequestFriend>
        (  &VicKit::VicDataServiceClient::requestFriend,
       	   VKInternal::authSignature(),
           uid,
           internalHandler)
    );

    theAsyncRequester.Request(requestingTask);
}

//------------------------------------------------------------------------------------
//                                 cancelFriend
//------------------------------------------------------------------------------------

class __CancelFriendHandler : public InternalCompletionHandler<VicKit::ResCancelFriend> {
public :
	__CancelFriendHandler( const TxString & uid, VKLocalPlayer::CancelFriendHandler * hanlder ) {
		handler_ = hanlder;
		uid_ = uid;
	};

	virtual ~__CancelFriendHandler() {};

	virtual void onComplete( const VicKit::ResCancelFriend & response) {
		if (response.error.code != VicKit::ErrorCode::VKSuccess )
		{
			VKError error((VKErrorCode)response.error.code, response.error.message);

			handler_->onCancelFriend(uid_, & error );
		}
		else
		{
			handler_->onCancelFriend(uid_, NULL);
		}
	};
private :
	VKLocalPlayer::CancelFriendHandler * handler_;
	TxString uid_;
};


// [1.1] Cancel the friendship
void VKLocalPlayer::cancelFriend(const TxString & uid, CancelFriendHandler * handler)
{
	boost::shared_ptr<__CancelFriendHandler> internalHandler ( new __CancelFriendHandler(uid, handler) );

    boost::shared_ptr<RequestingTask<VicKit::UID, VicKit::ResCancelFriend> > requestingTask(
        new RequestingTask<VicKit::UID, VicKit::ResCancelFriend>
        (  &VicKit::VicDataServiceClient::cancelFriend,
       	   VKInternal::authSignature(),
           uid,
           internalHandler)
    );

    theAsyncRequester.Request(requestingTask);
}


//------------------------------------------------------------------------------------
//                                 searchUserByEmail
//------------------------------------------------------------------------------------
class __SearchUserHandler: public InternalCompletionHandler< VicKit::ResSearchUsers > {
public :
	__SearchUserHandler( VKLocalPlayer::SearchUserHandler * hanlder ) {
		handler_ = hanlder;
	};

	virtual ~__SearchUserHandler() {};

	virtual void onComplete( const VicKit::ResSearchUsers & response ) {

		std::vector<VKLocalPlayer::TxUserProfile> profiles;

		if (response.error.code != VicKit::ErrorCode::VKSuccess )
		{
			VKError error((VKErrorCode)response.error.code, response.error.message);

			handler_->onSearchUser(profiles, & error);
		}
		else
		{
			profiles = ConvertFromThrift( response.userProfiles );
			handler_->onSearchUser(profiles, NULL);
		}
	};
private :
	VKLocalPlayer::SearchUserHandler * handler_;
};

// [1.2] : Request to become a friend.
void VKLocalPlayer::searchUserByEmail(const TxString & email, SearchUserHandler * handler)
{
	boost::shared_ptr<__SearchUserHandler> internalHandler ( new __SearchUserHandler(handler) );

    boost::shared_ptr<RequestingTask< TxString, VicKit::ResSearchUsers > > requestingTask(
        new RequestingTask<TxString, VicKit::ResSearchUsers >
        (  &VicKit::VicDataServiceClient::searchUserByEmail,
       	   VKInternal::authSignature(),
           email,
           internalHandler)
    );

    theAsyncRequester.Request(requestingTask);
}
