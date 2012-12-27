/*
 * UnitTest.h
 *
 *  Created on: Oct 8, 2012
 *      Author: ladmin
 */

#ifndef UNITTEST_H_
#define UNITTEST_H_

#include "TestBase.h"

class LocalPlayerHandlers :
		public VKLocalPlayer::AuthenticateHandler,
		public VKLocalPlayer::CreateUserProfileHandler,
		public VKLocalPlayer::LoadUserProfileHandler,
		public VKLocalPlayer::UpdateUserProfileHandler,
		public VKLocalPlayer::LoadFriendProfilesHandler,
		public VKLocalPlayer::RequestFriendHandler,
		public VKLocalPlayer::CancelFriendHandler,
		public VKLocalPlayer::SearchUserHandler
{
protected:
	TestBase * testBase;
	void * testData;

public:
	LocalPlayerHandlers(TestBase & base) { this->testBase = &base; testData = NULL; };
	virtual ~LocalPlayerHandlers() {};

    virtual void onAuthenticate( VKError * error );
    virtual void onCreateUserProfile(const TxString & uid, VKError * error);
    virtual void onLoadUserProfile(const VKLocalPlayer::TxUserProfile & userProfile, VKError * error);
    virtual void onUpdateUserProfile(VKError * error);
    virtual void onLoadFriendProfiles(const std::vector<VKLocalPlayer::TxUserProfile> friendProfiles, VKError * error);
    virtual void onRequestFriend(const VKLocalPlayer::TxUserProfile & friendProfile, VKError * error);
    virtual void onCancelFriend(const TxString & uid, VKError * error);
    virtual void onSearchUser(const std::vector<VKLocalPlayer::TxUserProfile> & userProfiles, VKError * error);

    void setTestData(void * data) { testData = data; };
};


class LocalPlayerTest : public TestBase {
public:
	VKLocalPlayer * localPlayer;
	LocalPlayerHandlers * handlers;
	TxString defaultEmail;
	TxString defaultPassword;


	virtual void SetUp();
	bool authenticate();
	bool authenticateWithIdAndPwd(const TxString & id, const TxString & pwd);
	TxString & createUser(const TxString & email);

	// REMIND: dummy function to instanciate.
	void TestBody() {};
};

//int runUnitTest();

#endif /* UNITTEST_H_ */
