//
//  VKLocalPlayer.h
//

#ifndef __O_VK_LOCALPLAYER_H__
#define __O_VK_LOCALPLAYER_H__ (1)

#include <VicKit/Basement.h>
#include <VicKit/VKPlayer.h>
#include <VicKit/VKDefines.h>

VK_EXTERN_CLASS class VKLocalPlayer : public VKPlayer {

public :
	VKLocalPlayer();
	virtual ~VKLocalPlayer();
// Obtain the VKLocalPlayer object.
// The player is only available for offline play until logged in.
// A temporary player is created if no account is set up.
public :
	static VKLocalPlayer * localPlayer();

private :
	bool authenticated_; // Authentication state
	bool underage_;		// Underage state
public :
	const bool authenticated() const   { return authenticated_; };
	const bool isAuthenticated() const { return authenticated_; };
	const bool isUnderage() const	   { return underage_; };

// Authenticate the player for access to player details and game statistics. This may present UI to the user if necessary to login or create an account. The user must be authenticated in order to use other APIs. This should be called for each launch of the application as soon as the UI is ready.
// Authentication happens automatically on return to foreground, and the completion handler will be called again. Game Center UI may be presented during this authentication as well. Apps should check the local player's authenticated and player ID properties to determine if the local player has changed.
// Possible reasons for error:
// 1. Communications problem
// 2. User credentials invalid
// 3. User cancelled
	class AuthenticateHandler {
    public :
		virtual ~AuthenticateHandler() {};
        virtual void onAuthenticate( VKError * error ) = 0;
    };
	void authenticate(const TxString & email, const TxString & password, AuthenticateHandler * handler);

private :
	TxStringArray friends_;
public :
	// Array of player identifiers of friends for the local player. Not valid until loadFriendsWithCompletionHandler: has completed.
	const TxStringArray & friends() const { return friends_; };

// Asynchronously load the friends list as an array of player identifiers. Calls completionHandler when finished. Error will be nil on success.
// Possible reasons for error:
// 1. Communications problem
// 2. Unauthenticated player
	class LoadFriendsHandler {
    public :
		virtual ~LoadFriendsHandler() {};
        virtual void onLoadFriends(const TxStringArray & friends, VKError * error) = 0;
    };

	void loadFriends(LoadFriendsHandler * handler);

	// The profile of a user
	typedef struct TxUserProfile {
		// The unique user id that monotonously increase from 1
		// For registerUserProfile, set this field to 0
		// For updateUserProfile, set this field to the UID of user that is being updated.
		TxString uid;
		// Email address
		TxString email;
		// Encrypted Password : Need to encrypt the password that the user typed
		TxString encryptedPassword;
		// Name of the user
		TxString name;
		// The status message to show in the friend list.
		TxString statusMessage;
		// The blob containing photo to show on "my profile", and list of friends.
		TxImage  photo;
	} TxUserProfile ;

	class CreateUserProfileHandler {
    public :
		virtual ~CreateUserProfileHandler() {};
		// The callback indicating if the registration was successful(error==NULL) or not(error!=NULL).
        virtual void onCreateUserProfile(const TxString & uid, VKError * error) = 0;
    };

	// Register me as a new user
	void createUserProfile(const TxUserProfile & userProfile, CreateUserProfileHandler * handler);


	class UpdateUserProfileHandler {
    public :
		virtual ~UpdateUserProfileHandler() {};
		// The callback indicating if the update was successful(error==NULL) or not(error!=NULL).
        virtual void onUpdateUserProfile(VKError * error) = 0;
    };

	// [1.4.2] Update my profile
	void updateUserProfile(const TxUserProfile & userProfile, UpdateUserProfileHandler * handler);


	class LoadUserProfileHandler {
    public :
		virtual ~LoadUserProfileHandler() {};
		// The callback with the user profile.
        virtual void onLoadUserProfile(const TxUserProfile & userProfile, VKError * error) = 0;
    };

	// [1.4] Get my profile - set my UID ( unique user ID that monotonously increases from 1 )
	void loadUserProfile(const TxString & uid, LoadUserProfileHandler * handler);


	class LoadFriendProfilesHandler {
    public :
		virtual ~LoadFriendProfilesHandler() {};
		// The callback with the user profile.
        virtual void onLoadFriendProfiles(const std::vector<TxUserProfile> friendProfiles, VKError * error) = 0;
    };

	// [1] get the list of friend profiles
	void loadFriendProfiles(const TxString & uid, LoadFriendProfilesHandler * handler);


	class RequestFriendHandler {
    public :
		virtual ~RequestFriendHandler() {};
		// called when the request was successfully sent to the user. (error == NULL)
		// called when the request was not successfully sent to the user. (error != NULL)
        virtual void onRequestFriend(const TxUserProfile & uid, VKError * error) = 0;
    };
/*
	class FriendApprovalHandler {
    public :
		virtual ~FriendApprovalHandler() {};
		// The another user approved to become a friend.(error == NULL)
        virtual void onApproveFriend(const TxUserProfile & friendProfile, TxError * error) = 0;
    };
*/
	// [1.2] Request to become a friend using his UID
	void requestFriend(const TxString & uid, RequestFriendHandler * handler);


	class CancelFriendHandler {
	public :
		virtual ~CancelFriendHandler() {};
		// The another user approved to become a friend.(error == NULL)
	    virtual void onCancelFriend(const TxString & uid, VKError * error) = 0;
	};

	// [1.1] Cancel the friendship
	void cancelFriend(const TxString & uid, CancelFriendHandler * handler);


	class SearchUserHandler {
    public :
		virtual ~SearchUserHandler() {};
		// The callback with the searched result.
		// If the user is not found, userProfiles.size() comes with 0, with error == NULL
        virtual void onSearchUser(const std::vector<TxUserProfile> & userProfiles, VKError * error) = 0;
    };

	// [1.2] : Request to become a friend.
    void searchUserByEmail(const TxString & email, SearchUserHandler * handler);
public :
    class AuthenticateChangeHandler {
    public:
    	virtual ~AuthenticateChangeHandler() {};
        virtual void onChangeAuthentication() = 0;
    };

private :
    AuthenticateChangeHandler * authChangeHandler_;

public :    
    // Notification will be posted whenever authentication status changes.
    AuthenticateChangeHandler * authChangeHandler() { return authChangeHandler_; };
    void authChangeHandler(AuthenticateChangeHandler * arg) { authChangeHandler_ = arg; };
    // authChangeHandler replaces VKPlayerAuthenticationDidChangeNotificationName.
    //    VK_EXTERN TxString VKPlayerAuthenticationDidChangeNotificationName;

};

#endif /* __O_VK_LOCALPLAYER_H__ */
