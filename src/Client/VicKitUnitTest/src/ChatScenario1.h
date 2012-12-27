/*
 * TestScenario1.h
 *
 *  Created on: Oct 7, 2012
 *      Author: ladmin
 */

#ifndef TESTSCENARIOA1_H_
#define TESTSCENARIOA1_H_

#include "TestBase.h"

#define USER_EMAIL_1A ("test01@thankyousoft.com")
#define USER_EMAIL_1B ("test02@thankyousoft.com")

class ChatScenario1A :
		public ChatClient,
		public VKLocalPlayer::AuthenticateHandler,
		public VKLocalPlayer::SearchUserHandler,
		public VKMatchmaker::InviteHandler,
		public VKMatchDelegate,
		public VKMatchmaker::MatchForInviteHandler
{
public:
	ChatScenario1A() : ChatClient() { match_ = NULL; lastMessage = NULL; email = TxString(USER_EMAIL_1A); password = TxString("encrypted password"); };
	virtual ~ChatScenario1A() {};

	virtual void onAuthenticate(VKError * error);
    virtual void onSearchUser(const std::vector<VKLocalPlayer::TxUserProfile> & userProfiles, VKError * error);
	virtual void onInvite(VKInvite * acceptedInvite, TxStringArray * playersToInvite);

	virtual void onReceiveData(VKMatch * match, const TxData & data, const TxString & playerID);
	virtual void onMatchForInvite(VKMatch * match, VKError * error);

	void run();
private :
	VKLocalPlayer::TxUserProfile searchedUserProfile_;
};


class ChatScenario1B :
		public ChatClient,
		public VKLocalPlayer::AuthenticateHandler,
		public VKLocalPlayer::SearchUserHandler,
		public VKMatchmaker::FindMatchHandler,
		public VKMatchDelegate
{
public:

	ChatScenario1B() : ChatClient() { match_ = NULL; lastMessage = NULL; email = TxString( USER_EMAIL_1B ); password = TxString("encrypted password");  };
	virtual ~ChatScenario1B() {};

	virtual void onAuthenticate(VKError * error);
    virtual void onSearchUser(const std::vector<VKLocalPlayer::TxUserProfile> & userProfiles, VKError * error);

	virtual void onFindMatch(VKMatch * match, VKError * error);

	virtual void onReceiveData(VKMatch * match, const TxData & data, const TxString & playerID);


	void run();

private :
	VKLocalPlayer::TxUserProfile searchedUserProfile_;
};

#endif /* TESTSCENARIOA1_H_ */

