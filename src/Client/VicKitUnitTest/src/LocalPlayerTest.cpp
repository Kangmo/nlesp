/*
 * UnitTest.cpp
 *
 *  Created on: Oct 8, 2012
 *      Author: ladmin
 */

#include <VicKit/VicKit.h>
#include "LocalPlayerTest.h"
//#include "Handlers4UnitTest.h"

using namespace std;

///////////////////////////////////////////////////////////////////////////////////////////
//                                                                                       //
//                          Implementation of LocalPlayerTest                            //
//                                                                                       //
///////////////////////////////////////////////////////////////////////////////////////////

void LocalPlayerTest::SetUp() {
	TestBase::SetUp();
	localPlayer = VKLocalPlayer::localPlayer();
	handlers = new LocalPlayerHandlers(*this);
	defaultEmail = TxString("test01@thankyousoft.com");
	defaultPassword = TxString("encrypted password");
}

bool LocalPlayerTest::authenticate()
{

	initState();
	localPlayer->authenticate(defaultEmail, defaultPassword, handlers);

	waitUntilFinished();

	initState();
	return localPlayer->isAuthenticated();
}

bool LocalPlayerTest::authenticateWithIdAndPwd(const TxString & id, const TxString & pwd)
{
	initState();
	localPlayer->authenticate(id, pwd, handlers);
	waitUntilFinished();

	initState();
	return localPlayer->isAuthenticated();
	return false;
}

TxString & LocalPlayerTest::createUser(const TxString & email) {
	VKLocalPlayer::TxUserProfile newProfile;
	newProfile.uid = "0";
	newProfile.email = email;
	newProfile.encryptedPassword = defaultPassword;
	newProfile.name = email;
	newProfile.statusMessage = "I'm a friend.";
	newProfile.photo = TxImage();

	initState();
	localPlayer->createUserProfile(newProfile, handlers);
	waitUntilFinished();

	return createdUserID;
}

///////////////////////////////////////////////////////////////////////////////////////////
//                                                                                       //
//                        Implementation of LocalPlayerHandlers                          //
//                                                                                       //
///////////////////////////////////////////////////////////////////////////////////////////

void LocalPlayerHandlers::onAuthenticate(VKError * error)
{
	if (error != NULL) {
		// TODO: implement TxError, then use it.
		testBase->fail(error->errorCode(), error->errorMessage());
		return;
	}

	testBase->pass();
}

void LocalPlayerHandlers::onCreateUserProfile(const TxString & uid, VKError * error)
{
	if (error != NULL) {
		// TODO: implement TxError, then use it.
		testBase->fail(error->errorCode(), error->errorMessage());
		return;
	}

	cout << "Created UID: " << uid;
	if (uid.length() > 0) {
		testBase->createdUserID = uid;
		testBase->pass();
	} else {
		testBase->fail(VKErrorUnknown, "gtest error : uid is empty in onCreateUserProfile");
	}
}

void LocalPlayerHandlers::onLoadUserProfile(const VKLocalPlayer::TxUserProfile & userProfile, VKError * error)
{
	if (error != NULL) {
		// TODO: implement TxError, then use it.
		testBase->fail(error->errorCode(), error->errorMessage());
		return;
	}

	VKLocalPlayer::TxUserProfile * expected = (VKLocalPlayer::TxUserProfile *)testData;

	if ( expected == NULL )
	{
		testBase->fail(VKErrorUnknown, "[LocalPlayerHandlers::onLoadUserProfile] : the pointer to the expected user profile is NULL.");
		return;
	}

	int mismatchCount = 0;
	if (expected->uid.compare(userProfile.uid) != 0)
	{
		std::cout << "[LocalPlayerHandlers::onLoadUserProfile] uid mismatch. expected:" << expected->uid << ", actual:"<< userProfile.uid << "\n";
		mismatchCount++;
	}
	if (expected->name.compare(userProfile.name) != 0)
	{
		std::cout << "[LocalPlayerHandlers::onLoadUserProfile] name mismatch. expected:" << expected->name << ", actual:"<< userProfile.name << "\n";
		mismatchCount++;
	}
	if (expected->email.compare(userProfile.email) != 0)
	{
		std::cout << "[LocalPlayerHandlers::onLoadUserProfile] email mismatch. expected:" << expected->email << ", actual:"<< userProfile.email << "\n";
		mismatchCount++;
	}
	// Server scrubs the encrypted password field.
	if (userProfile.encryptedPassword != "")
	{
		std::cout << "[LocalPlayerHandlers::onLoadUserProfile] encryptedPassword mismatch. expected:\"\"" << ", actual:\""<< userProfile.encryptedPassword << "\"\n";
		mismatchCount++;
	}
	if (expected->statusMessage.compare(userProfile.statusMessage) != 0)
	{
		std::cout << "[LocalPlayerHandlers::onLoadUserProfile] statusMessage mismatch. expected:" << expected->statusMessage << ", actual:"<< userProfile.statusMessage << "\n";
		mismatchCount++;
	}

	if (expected->photo.length() != userProfile.photo.length())
	{
		std::cout << "[LocalPlayerHandlers::onLoadUserProfile] photo.length() mismatch. expected:" << expected->photo.length() << ", actual:"<< userProfile.photo.length() << "\n";
		mismatchCount++;
	}

	if (memcmp(expected->photo.bytes(), userProfile.photo.bytes(), userProfile.photo.length()) != 0)
	{
		std::cout << "[LocalPlayerHandlers::onLoadUserProfile] photo.length() mismatch. expected:" << expected->photo.length() << ", actual:"<< userProfile.photo.length() << "\n";
		mismatchCount++;
	}

	if ( mismatchCount > 0 )
	{
		testBase->fail(VKErrorUnknown, "[LocalPlayerHandlers::onLoadUserProfile] user profile mismatch in LocalPlayerHandlers::onLoadUserProfile.");
	}
	else
	{
		testBase->pass();
	}
}

void LocalPlayerHandlers::onUpdateUserProfile(VKError * error)
{
	if (error != NULL) {
		// TODO: implement TxError, then use it.
		testBase->fail(error->errorCode(), error->errorMessage());
		return;
	}

	testBase->pass();
}


void LocalPlayerHandlers::onLoadFriendProfiles(const std::vector<VKLocalPlayer::TxUserProfile> friendProfiles, VKError * error)
{
	if (error != NULL) {
		// TODO: implement TxError, then use it.
		testBase->fail(error->errorCode(), error->errorMessage());
		return;
	}

	std::vector<TxString> * friendIDs = (std::vector<TxString> *)testData;
	if (friendProfiles.size() == friendIDs->size()) {
		testBase->pass();
	} else {
        cout << "friend element count mismatch. expected:" << friendIDs->size() << ", actual:" << friendProfiles.size() << "\n";
		testBase->fail(VKErrorUnknown, "[LocalPlayerHandlers::onLoadFriendProfiles] : friend element count mismatch.");
	}
}

void LocalPlayerHandlers::onRequestFriend(const VKLocalPlayer::TxUserProfile & friendProfile, VKError * error)
{
	if (error != NULL) {
		// TODO: implement TxError, then use it.
		testBase->fail(error->errorCode(), error->errorMessage());
		return;
	}

	std::vector<TxString> * friendIDs = (std::vector<TxString> *)testData;

	if (friendIDs == NULL)
	{
		testBase->fail(VKErrorUnknown, "[LocalPlayerHandlers::onRequestFriend] : friendIDs is NULL in ");
		return;
	}

	// kmkim : Need to check the case that no friend exists
	if (friendIDs->size() == 0)
	{
		testBase->fail(VKErrorUnknown, "[LocalPlayerHandlers::onRequestFriend] : no friend found.");
		return;
	}

	if (friendIDs->back().compare(friendProfile.uid) == 0) {
		testBase->pass();
	} else {
		testBase->fail(VKErrorUnknown, "[LocalPlayerHandlers::onRequestFriend] : friend id mismatch in onRequestFriend");
	}
}

void LocalPlayerHandlers::onCancelFriend(const TxString & uid, VKError * error)
{
	if (error != NULL) {
		// TODO: implement TxError, then use it.
		testBase->fail(error->errorCode(), error->errorMessage());
		return;
	}

	TxString * removedFriendID = (TxString *)testData;
	testBase->createdUserID = uid;
	if (removedFriendID->compare(uid) == 0) {
		testBase->pass();
	} else {
		testBase->fail(VKErrorUnknown, "gtest error : user id mismatch in onCancelFriend");
	}
}


void LocalPlayerHandlers::onSearchUser(const std::vector<VKLocalPlayer::TxUserProfile> & userProfiles, VKError * error)
{
	if (error != NULL) {
		// TODO: implement TxError, then use it.
		testBase->fail(error->errorCode(), error->errorMessage());
		return;
	}

	TxString * emailToSearch = (TxString *)testData;

	// TODO: compare the entire profile instead of email.
	if (emailToSearch == NULL) {
		if (userProfiles.size() == 0) {
			testBase->pass();
		} else {
			testBase->fail(VKErrorUnknown, "gtest error : Expected(userProfiles.size() == 0) in onSearchUser");
		}
	} else {
		if (userProfiles.back().email.compare(*emailToSearch) == 0) {
			testBase->pass();
		} else {
			testBase->fail(VKErrorUnknown, "gtest error : email mismatch in onSearchUser");
		}
	}
}

///////////////////////////////////////////////////////////////////////////////////////////
//                                                                                       //
//                                     Test Cases                                        //
//                                                                                       //
///////////////////////////////////////////////////////////////////////////////////////////
TEST_F(LocalPlayerTest, CreateProfile) {
	VKLocalPlayer::TxUserProfile newProfile;
	newProfile.uid = ""; // kmkim : uid is not used.
	// kmkim : change test10 to test 01
	newProfile.email = "test01@thankyousoft.com";
	newProfile.encryptedPassword = defaultPassword;
	newProfile.name = "test user";
	newProfile.statusMessage = "just created.";
	newProfile.photo = TxImage();

	initState();
	localPlayer->createUserProfile(newProfile, handlers);
	waitUntilFinished();

	ASSERT_TRUE(isPassed());

	ASSERT_TRUE(authenticate());

// kmkim :
//	Client can't assume any value on the uid, because server assigns it.
//  But we can assume that playerID() is not an empty string.
	EXPECT_TRUE(localPlayer->playerID() != "" );
	newProfile.uid = localPlayer->playerID();

	handlers->setTestData((void *)&newProfile);
	initState();
	localPlayer->loadUserProfile(newProfile.uid, handlers);
	waitUntilFinished();
	EXPECT_TRUE(isPassed());
}

TEST_F(LocalPlayerTest, CreateTestUsers) {
	createUser(TxString("test02@thankyousoft.com"));
	ASSERT_TRUE(isPassed());

	createUser(TxString("test03@thankyousoft.com"));
	ASSERT_TRUE(isPassed());

	createUser(TxString("test04@thankyousoft.com"));
	ASSERT_TRUE(isPassed());
}

TEST_F(LocalPlayerTest, Authentication_With_Invalid_ID) {
//  Because we have authenticated in CreateProfile, isAuthenticated returns true.
//	ASSERT_FALSE(localPlayer->isAuthenticated());
//	ASSERT_FALSE(localPlayer->authenticated());

	TxString invalidId("invalid@thankyousoft.com");

	initState();
	localPlayer->authenticate(invalidId, defaultPassword, handlers);
	waitUntilFinished();

	ASSERT_FALSE(isPassed());
	ASSERT_FALSE(localPlayer->isAuthenticated());
	ASSERT_FALSE(localPlayer->authenticated());
}

TEST_F(LocalPlayerTest, Authentication_With_Wrong_Password) {
	ASSERT_FALSE(localPlayer->isAuthenticated());
	ASSERT_FALSE(localPlayer->authenticated());

	TxString wrongPwd("wrong password");

	initState();
	localPlayer->authenticate(defaultEmail, wrongPwd, handlers);
	waitUntilFinished();

	ASSERT_FALSE(isPassed());
	ASSERT_FALSE(localPlayer->isAuthenticated());
	ASSERT_FALSE(localPlayer->authenticated());
}

// REMIND: authenticate with cached info is not supported yet.
//TEST_F(LocalPlayerTest, Authentication) {
//	ASSERT_FALSE(localPlayer->isAuthenticated());
//	ASSERT_FALSE(localPlayer->authenticated());
//
//	initState();
//	localPlayer->authenticate(handlers);
//	waitUntilFinished();
//
//	ASSERT_TRUE(isPassed());
//	ASSERT_TRUE(localPlayer->isAuthenticated());
//	ASSERT_TRUE(localPlayer->authenticated());
//}

TEST_F(LocalPlayerTest, Authentication) {
	ASSERT_FALSE(localPlayer->isAuthenticated());
	ASSERT_FALSE(localPlayer->authenticated());

	initState();
	localPlayer->authenticate(defaultEmail, defaultPassword, handlers);

	waitUntilFinished();

	ASSERT_TRUE(isPassed());
	ASSERT_TRUE(localPlayer->isAuthenticated());
	ASSERT_TRUE(localPlayer->authenticated());
}

TEST_F(LocalPlayerTest, CreateProfile_With_Empty_Email) {
	VKLocalPlayer::TxUserProfile newProfile;
	newProfile.uid = "0";
	newProfile.email = "";
	newProfile.encryptedPassword = defaultPassword;
	newProfile.name = "test user";
	newProfile.statusMessage = "just created.";
	newProfile.photo = TxImage();

	initState();
	localPlayer->createUserProfile(newProfile, handlers);
	waitUntilFinished();

	ASSERT_FALSE(isPassed());
}

TEST_F(LocalPlayerTest, CreateProfile_With_Empty_Password) {
	VKLocalPlayer::TxUserProfile newProfile;
	newProfile.uid = "0";
	newProfile.email = "test99@thankyousoft.com";
	newProfile.encryptedPassword = "";
	newProfile.name = "test user";
	newProfile.statusMessage = "just created.";
	newProfile.photo = TxImage();

	initState();
	localPlayer->createUserProfile(newProfile, handlers);
	waitUntilFinished();

	ASSERT_FALSE(isPassed());
}

TEST_F(LocalPlayerTest, UpdateProfile) {

	// TODO : Add a new test case to update the password of a separate user that is not used for other test cases.
	// TODO : Add a new test case that the server rejects to change email field in the UserProfile.

	ASSERT_TRUE(authenticate());

	VKLocalPlayer::TxUserProfile newProfile;
	newProfile.uid = localPlayer->playerID();
	// kmkim : We should not change the email while we update a profile.
	// TODO : Add an additional test case that fails to update user email.
	newProfile.email = "test01@thankyousoft.com";
	newProfile.encryptedPassword = ""; // empty string means not to change the password

	newProfile.name = "(modified) test user";
	newProfile.statusMessage = "(modified) status message";
	newProfile.photo = TxImage();	// TODO: change photo.

	// update profile.
	initState();
	localPlayer->updateUserProfile(newProfile, handlers);
	waitUntilFinished();

	ASSERT_TRUE(isPassed());

	// BUGBUG : Server Hangs. Figure out why.
	// confirm.
	handlers->setTestData((void *)&newProfile);

	initState();
	localPlayer->loadUserProfile(newProfile.uid, handlers);
	waitUntilFinished();

	EXPECT_TRUE(isPassed());
}


TEST_F(LocalPlayerTest, UpdateProfile_With_Empty_ID) {
	ASSERT_TRUE(authenticate());

	VKLocalPlayer::TxUserProfile newProfile;
	newProfile.uid = "";
	newProfile.email = "test99@thankyousoft.com";
	newProfile.encryptedPassword = "(modified) encrypted password";
	newProfile.name = "(modified) test user";
	newProfile.statusMessage = "(modified) just created.";
	newProfile.photo = TxImage();	// TODO: change photo.

	// update profile.
	initState();
	localPlayer->updateUserProfile(newProfile, handlers);
	waitUntilFinished();

	ASSERT_FALSE(isPassed());
}

TEST_F(LocalPlayerTest, UpdateProfile_With_Empty_Email) {
	ASSERT_TRUE(authenticate());

	VKLocalPlayer::TxUserProfile newProfile;
	newProfile.uid = localPlayer->playerID();
	newProfile.email = "";
	newProfile.encryptedPassword = "(modified) encrypted password";
	newProfile.name = "(modified) test user";
	newProfile.statusMessage = "(modified) just created.";
	newProfile.photo = TxImage();	// TODO: change photo.

	// update profile.
	initState();
	localPlayer->updateUserProfile(newProfile, handlers);
	waitUntilFinished();

	ASSERT_FALSE(isPassed());
}

TEST_F(LocalPlayerTest, Friendship) {
	ASSERT_TRUE(authenticate());
	std::vector<TxString> friendIDs;

	// get friend list (empty)
	handlers->setTestData(&friendIDs);
	initState();
	localPlayer->loadFriendProfiles(localPlayer->playerID(), handlers);
	waitUntilFinished();
	EXPECT_TRUE(isPassed());

	// add friend #1
	TxString friend1 = createUser("friend.1@email.com");
	friendIDs.push_back(friend1);
	handlers->setTestData(&friendIDs);
	initState();
	localPlayer->requestFriend(friend1, handlers);
	waitUntilFinished();
	EXPECT_TRUE(isPassed());

	// add friend #2
	TxString friend2 = createUser("friend.2@email.com");
	friendIDs.push_back(friend2);
	handlers->setTestData(&friendIDs);
	initState();
	localPlayer->requestFriend(friend2, handlers);
	waitUntilFinished();
	EXPECT_TRUE(isPassed());

	// get friend list (2 friends)
	handlers->setTestData(&friendIDs);
	initState();
	localPlayer->loadFriendProfiles(localPlayer->playerID(), handlers);
	waitUntilFinished();
	EXPECT_TRUE(isPassed());

	// cancel friend #2
	initState();
	friendIDs.pop_back();
	handlers->setTestData(&friend2);
	localPlayer->cancelFriend(friend2, handlers);
	waitUntilFinished();
	EXPECT_TRUE(isPassed());

	// cancel friend #1
	initState();
	friendIDs.pop_back();
	handlers->setTestData(&friend1);
	localPlayer->cancelFriend(friend1, handlers);
	waitUntilFinished();
	EXPECT_TRUE(isPassed());

	// get friend list (empty)
	handlers->setTestData(&friendIDs);
	initState();
	localPlayer->loadFriendProfiles(localPlayer->playerID(), handlers);
	waitUntilFinished();
	EXPECT_TRUE(isPassed());
}

TEST_F(LocalPlayerTest, RequestFriend_To_Not_Existing_Player) {
	ASSERT_TRUE(authenticate());

	TxString notExistingId = "999999999";
	initState();
	localPlayer->requestFriend(notExistingId, handlers);
	waitUntilFinished();
	ASSERT_FALSE(isPassed());
}

TEST_F(LocalPlayerTest, RequestFriend_To_Myself) {
	ASSERT_TRUE(authenticate());

	initState();
	localPlayer->requestFriend(localPlayer->playerID(), handlers);
	waitUntilFinished();
	ASSERT_FALSE(isPassed());
}

TEST_F(LocalPlayerTest, CancelFriend_With_Stranger) {
	ASSERT_TRUE(authenticate());

	TxString strangerId = "999999999";
	initState();
	localPlayer->requestFriend(strangerId, handlers);
	waitUntilFinished();
	ASSERT_FALSE(isPassed());
}

TEST_F(LocalPlayerTest, SearchUser) {
	ASSERT_TRUE(authenticate());

	// search non-existing user.
	TxString notExistingEmail = "notExisting@thankyousoft.com";

	handlers->setTestData(NULL);
	initState();
	localPlayer->searchUserByEmail(notExistingEmail, handlers);
	waitUntilFinished();
	EXPECT_TRUE(isPassed());

	// TODO search myself.
	handlers->setTestData(&defaultEmail);
	initState();
	localPlayer->searchUserByEmail(defaultEmail, handlers);
	waitUntilFinished();
	EXPECT_TRUE(isPassed());
}

