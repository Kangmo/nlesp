/*
 * TestScenario2.cpp
 *
 *  Created on: Oct 7, 2012
 *      Author: ladmin
 */

#include <VicKit/VicKit.h>
#include "ChatScenario2.h"
#include <string.h>
#include <time.h>
#include <boost/foreach.hpp>
#include <stdio.h>

///////////////////////////////////////////////////////////////////////////////////////////
//                                                                                       //
//                         Implementation of ChatScenario 2A                             //
//                                                                                       //
///////////////////////////////////////////////////////////////////////////////////////////

void ChatScenario2A::run() {
	printf("[A] Initializing System.\n");

	VKLocalPlayer * localPlayer = VKLocalPlayer::localPlayer();
	VicKitSystem::initialize("2A");

	printf("[A] Authenticate User %s\n", USER_EMAIL_2A);

	initState();
	localPlayer->authenticate(email, password, this);
	ASSERT_TRUE( waitUntilFinished() == PASSED );

	printf("[A] search user profile of B.\n");
	initState();
	localPlayer->searchUserByEmail(USER_EMAIL_2B, this);
	waitUntilFinished();
	EXPECT_TRUE(isPassed());
	EXPECT_TRUE(searchedUserProfile_.uid != "");

	printf("[A] make match against B.\n");
	TxStringArray inviteeIDs;
	TxString friendID(searchedUserProfile_.uid);
	inviteeIDs.push_back(friendID);

	VKMatchRequest request;
	request.playersToInvite(inviteeIDs);

	initState();
	matchmaker->findMatch(request, this);

	ASSERT_TRUE( waitUntilFinished() == PASSED );

	printf("[A] send a message \"Hello\" to B, who is offline.\n");
	char msgBody[] = "Hello";

	EXPECT_TRUE( sendMessageToAll(msgBody) );
	// A: quit

	// B: sleep(1);
	// B: authenticate
	// B: check whether an invitation from 'A' came.
	// B: check the received message from A("Hello")
	// B: quit

	printf("[A] destroy the VicKitSystem.\n");
	VicKitSystem::destroy();

	printf("[A] quit.\n");
}

void ChatScenario2A::onAuthenticate(VKError * error)
{
	if (error != NULL) {
		fail(error->errorCode(), error->errorMessage());
		return;
	}

	pass();
}

void ChatScenario2A::onSearchUser(const std::vector<VKLocalPlayer::TxUserProfile> & userProfiles, VKError * error)
{
	if (error != NULL) {
		fail(error->errorCode(), error->errorMessage());
		return;
	}

	if ( userProfiles.size() != 1)
	{
		printf("userProfiles count mismatch. expected : 1, actual : %ld\n", userProfiles.size());
		fail(VKErrorUnknown, "[ChatScenario2A::onSearchUser] userProfiles count mismatch. ");

		return;
	}
	searchedUserProfile_ = userProfiles.at(0);

	pass();
}


void ChatScenario2A::onFindMatch(VKMatch * match, VKError * error)
{
	if (error != NULL) {
		fail(error->errorCode(), error->errorMessage());
		return;
	}

	ASSERT_TRUE(match->getMatchId() > 0);
	printf("[ChatScenario2A::onFindMatch] called. matchId = %ld\n", match->getMatchId());

	// We don't receive any message. No need to set delegate of match_ here.

	ChatScenario2A::match_ = match;

	pass();
}

///////////////////////////////////////////////////////////////////////////////////////////
//                                                                                       //
//                         Implementation of ChatScenario 2B                             //
//                                                                                       //
///////////////////////////////////////////////////////////////////////////////////////////

bool isMatchWith(TxString playerId, VKMatch * match) {
	TxStringArray memberIDs = match->playerIDs();

	BOOST_FOREACH(TxString memberID, memberIDs) {
		if (memberID.compare(playerId) == 0) {
			return true;
		}
	}

	return false;
}

void ChatScenario2B::run() {
	printf("[B] Wait a second to allow A send a message.\n");
	sleep(1);

	// A: send a message("Hello") to B, who is offline.
	// A: quit

	// B: sleep(1);
	printf("[B] Initializing System.\n");
	VKLocalPlayer * localPlayer = VKLocalPlayer::localPlayer();
	matchmaker->inviteHandler(this);

	VicKitSystem::initialize("2B");

	printf("[B] Authenticate User %s\n", USER_EMAIL_2B);

	initState();
	localPlayer->authenticate(email, password, this);

	ASSERT_TRUE( waitUntilFinished() == PASSED );

	printf("[B] search user profile of A\n");
	// find Peer ID from email
	initState();
	localPlayer->searchUserByEmail(USER_EMAIL_2A, this);
	waitUntilFinished();
	EXPECT_TRUE(isPassed());
	EXPECT_TRUE(searchedUserProfile_.uid != "");


	printf("[B] wait until an invitation from A comes\n");

	while (match_ == NULL);
	initState();

	printf("[B] check match peer and data\n");

	EXPECT_TRUE( isMatchWith( searchedUserProfile_.uid, match_) );

	EXPECT_TRUE( lastSenderID.compare( searchedUserProfile_.uid ) == 0);

	EXPECT_TRUE( memcmp(lastMessage->bytes(), "Hello", lastMessage->length()) == 0);

	// B: quit
	printf("[B] destroy the VicKitSystem.\n");
	VicKitSystem::destroy();

	printf("[B] quit.\n");
}

void ChatScenario2B::onAuthenticate(VKError * error)
{
	if (error != NULL) {
		fail(error->errorCode(), error->errorMessage());
		return;
	}

	pass();
}

void ChatScenario2B::onSearchUser(const std::vector<VKLocalPlayer::TxUserProfile> & userProfiles, VKError * error)
{
	if (error != NULL) {
		fail(error->errorCode(), error->errorMessage());
		return;
	}

	if ( userProfiles.size() != 1)
	{
		printf("userProfiles count mismatch. expected : 1, actual : %ld\n", userProfiles.size());
		fail(VKErrorUnknown, "[ChatScenario2B::onSearchUser] userProfiles count mismatch. ");

		return;
	}

	searchedUserProfile_ = userProfiles.at(0);

	pass();
}


void ChatScenario2B::onReceiveData(VKMatch * match, const TxData & data, const TxString & playerID)
{
	std::string message((const char*)data.bytes(), data.length());
	printf("[ChatScenario2B::onReceiveData] called. data : %s\n", message.c_str());

	lastSenderID = playerID;
	lastMessage = new TxData(data);
	printf("Received message %s\n", (char*) data.bytes());

	pass();
}

void ChatScenario2B::onInvite(VKInvite * acceptedInvite, TxStringArray * playersToInvite)
{
	if (acceptedInvite == NULL)
	{
		fail(VKErrorUnknown, "[ChatScenario2B::onInvite] acceptedInvite is NULL in ChatScenario1A::onInvite");
		return;
	}

	printf("[ChatScenario2B::onInvite] called.\n");

	matchmaker->matchForInvite(acceptedInvite, this);
}

void ChatScenario2B::onMatchForInvite(VKMatch * match, VKError * error)
{
	if (error != NULL) {
		fail(error->errorCode(), error->errorMessage());
		return;
	}

	printf("[ChatScenario2B::onMatchForInvite] called.\n");

	match->delegate(this);

	// std::atomic_thread_fence(std::memory_order_release);

	ChatScenario2B::match_ = match;
}
