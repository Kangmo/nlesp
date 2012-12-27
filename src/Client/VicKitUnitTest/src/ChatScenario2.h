/*
 * TestScenario2.h
 *
 *  Created on: Oct 7, 2012
 *      Author: ladmin
 */

#ifndef TESTSCENARIO2_H_
#define TESTSCENARIO2_H_

#include "TestBase.h"

#define USER_EMAIL_2A ("test03@thankyousoft.com")
#define USER_EMAIL_2B ("test04@thankyousoft.com")

class ChatScenario2A :
		public ChatClient,
		public VKLocalPlayer::AuthenticateHandler,
		public VKLocalPlayer::SearchUserHandler,
		public VKMatchmaker::FindMatchHandler
{
public:

	ChatScenario2A() : ChatClient() { match_ = NULL; lastMessage = NULL; email = TxString(USER_EMAIL_2A); password = TxString("encrypted password"); };
	virtual ~ChatScenario2A() {};

	virtual void onAuthenticate(VKError * error);
    virtual void onSearchUser(const std::vector<VKLocalPlayer::TxUserProfile> & userProfiles, VKError * error);

	virtual void onFindMatch(VKMatch * match, VKError * error);

	void run();

private :
	VKLocalPlayer::TxUserProfile searchedUserProfile_;
};


class ChatScenario2B :
		public ChatClient,
		public VKLocalPlayer::AuthenticateHandler,
		public VKLocalPlayer::SearchUserHandler,
		public VKMatchDelegate,
		public VKMatchmaker::InviteHandler,
		public VKMatchmaker::MatchForInviteHandler
{
public:

	ChatScenario2B() : ChatClient() { match_ = NULL; lastMessage = NULL; email = TxString(USER_EMAIL_2B); password = TxString("encrypted password"); };
	virtual ~ChatScenario2B() {};

	virtual void onAuthenticate(VKError * error);
    virtual void onSearchUser(const std::vector<VKLocalPlayer::TxUserProfile> & userProfiles, VKError * error);

	virtual void onReceiveData(VKMatch * match, const TxData & data, const TxString & playerID);
	virtual void onInvite(VKInvite * acceptedInvite, TxStringArray * playersToInvite);
	virtual void onMatchForInvite(VKMatch * match, VKError * error);

	void run();

private :
	VKLocalPlayer::TxUserProfile searchedUserProfile_;
};


#endif /* TESTSCENARIO2_H_ */

