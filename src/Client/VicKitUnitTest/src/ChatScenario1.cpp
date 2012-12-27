/*
 * TestScenario1.cpp
 *
 *  Created on: Oct 7, 2012
 *      Author: ladmin
 */

#include <VicKit/VicKit.h>
#include "ChatScenario1.h"
#include <string.h>
#include <time.h>

///////////////////////////////////////////////////////////////////////////////////////////
//                                                                                       //
//                         Implementation of ChatScenario 1A                             //
//                                                                                       //
///////////////////////////////////////////////////////////////////////////////////////////

void ChatScenario1A::run() {
	printf("[A] Initializing System. \n");
	VKMatchmaker * matchmaker = VKMatchmaker::sharedMatchmaker();
	matchmaker->inviteHandler(this);
	VicKitSystem::initialize("1A");

	printf("[A] Authenticate User %s\n", USER_EMAIL_1A);

	VKLocalPlayer * localPlayer = VKLocalPlayer::localPlayer();
	initState();
	localPlayer->authenticate(email, password, this);
	ASSERT_TRUE( waitUntilFinished() == PASSED );

	printf("[A] wait for invitation from B\n");
	while (match_ == NULL);

	// B: create(find) a match with A.
	// B: send a message to A.
	// B: wait for a reply message from A.

	printf("[A] wait for message from B\n");
	// A: receive a message("Hello") from B.
	initState();
	ASSERT_TRUE( waitUntilFinished() == PASSED );

	printf("[A] search profile of B\n");
	// find Peer ID from email
	initState();
	localPlayer->searchUserByEmail(USER_EMAIL_1B, this);
	waitUntilFinished();
	EXPECT_TRUE(isPassed());
	EXPECT_TRUE(searchedUserProfile_.uid != "");

	TxString inviterID( searchedUserProfile_.uid );

	printf("[A] check message from B\n");

	EXPECT_TRUE(strncmp((char *)lastMessage->bytes(), "Hello", lastMessage->length()) == 0);
	EXPECT_TRUE(lastSenderID.compare(inviterID) == 0);

	// A: reply("Chat") to B.
	char msgBody[] = "Chat";

	printf("[A] send message to B\n");
	EXPECT_TRUE( sendMessageToAll(msgBody) );

	// B: receive a message from A, then quit.

	printf("[A] destroy the VicKitSystem.\n");

	VicKitSystem::destroy();

	printf("[A] Quit.\n");
}

void ChatScenario1A::onAuthenticate(VKError * error)
{
	if (error != NULL) {
		fail(error->errorCode(), error->errorMessage());
		return;
	}

	pass();
}

void ChatScenario1A::onSearchUser(const std::vector<VKLocalPlayer::TxUserProfile> & userProfiles, VKError * error)
{
	if (error != NULL) {
		fail(error->errorCode(), error->errorMessage());
		return;
	}

	if ( userProfiles.size() != 1)
	{
		printf("userProfiles count mismatch. expected : 1, actual : %ld\n", userProfiles.size());
		fail(VKErrorUnknown, "[ChatScenario1A::onSearchUser] userProfiles count mismatch. ");

		return;
	}

	searchedUserProfile_ = userProfiles.at(0);

	pass();
}


void ChatScenario1A::onInvite(VKInvite * acceptedInvite,
		TxStringArray * playersToInvite) {

	if (acceptedInvite == NULL)
	{
		fail(VKErrorUnknown, "[ChatScenario1A::onInvite] acceptedInvite is NULL in ChatScenario1A::onInvite");
		return;
	}

	printf("[ChatScenario1A::onInvite] called.\n");

	matchmaker->matchForInvite(acceptedInvite, this);

	pass();
}

void ChatScenario1A::onMatchForInvite(VKMatch * match, VKError * error)
{
	if (error != NULL) {
		fail(error->errorCode(), error->errorMessage());
		return;
	}

	printf("[ChatScenario1A::onMatchForInvite] called.\n");

	match->delegate(this);

//	std::atomic_thread_fence(std::memory_order_release);

	ChatClient::match_ = match;
}

void ChatScenario1A::onReceiveData(VKMatch * match, const TxData & data, const TxString & playerID)
{
	std::string message((const char*)data.bytes(), data.length());
	printf("[ChatScenario1A::onReceiveData] called. data : %s\n", message.c_str());

	lastSenderID = playerID;

	lastMessage = new TxData(data);
	pass();
}

///////////////////////////////////////////////////////////////////////////////////////////
//                                                                                       //
//                         Implementation of ChatScenario 1B                             //
//                                                                                       //
///////////////////////////////////////////////////////////////////////////////////////////

void ChatScenario1B::run() {
	printf("[B] Initializing System.\n");

	VicKitSystem::initialize("1B");
	VKLocalPlayer * localPlayer = VKLocalPlayer::localPlayer();

	printf("[B] Authenticate User %s\n", USER_EMAIL_1B);

	initState();
	localPlayer->authenticate(email, password, this);
	ASSERT_TRUE( waitUntilFinished() == PASSED );

	// A: wait for invitation from B.

	printf("[B] sleep a second to insure the A started and is waiting.\n");
	sleep(1);



	printf("[B] search user profile of A.\n");
	// find Peer ID from email
	initState();
	localPlayer->searchUserByEmail(USER_EMAIL_1A, this);
	waitUntilFinished();
	EXPECT_TRUE(isPassed());
	EXPECT_TRUE(searchedUserProfile_.uid != "");



	printf("[B] create(find) a match against A.\n");
	TxStringArray inviteeIDs;
	TxString friendID(searchedUserProfile_.uid);
	VKMatchRequest request;
	inviteeIDs.push_back( friendID );
	request.playersToInvite(inviteeIDs);

	initState();
	matchmaker->findMatch(request, this);
	ASSERT_TRUE( waitUntilFinished() == PASSED );


	// Wait for match object created on onFindMatch
	while (match_==NULL);

	// B: wait for establishing.
	// kmkim : expectedPlayerCount not implemented yet.
	//while (match->expectedPlayerCount() != 0);

	printf("[B] wait for 1 second for A to join the match.\n");
	sleep(1);

	// B: send a message("Hello") to A.
	const char * msgBody = "Hello";
///	printf("[DEBUG]msgBody => %s\n", msgBody);

	printf("[B] send data to A.\n");
	EXPECT_TRUE( sendMessageToAll(msgBody) );

	printf("[B] wait for a reply message from A.\n");
	initState();
	ASSERT_TRUE( waitUntilFinished() == PASSED );

	// A: receive a message from B.
	// A: reply("Chat") to B.

	printf("[B] check reply message from A.\n");
	EXPECT_TRUE( strncmp((char *)lastMessage->bytes(), "Chat", lastMessage->length()) == 0);
	EXPECT_TRUE( lastSenderID.compare(friendID) == 0);

	printf("[B] destroy the VicKitSystem.\n");
	VicKitSystem::destroy();

	printf("[B] Quit.\n");
}

void ChatScenario1B::onAuthenticate(VKError * error)
{
	if (error != NULL) {
		fail(error->errorCode(), error->errorMessage());
		return;
	}

	pass();
}

void ChatScenario1B::onSearchUser(const std::vector<VKLocalPlayer::TxUserProfile> & userProfiles, VKError * error)
{
	if (error != NULL) {
		fail(error->errorCode(), error->errorMessage());
		return;
	}

	if ( userProfiles.size() != 1)
	{
		printf("userProfiles count mismatch. expected : 1, actual : %ld\n", userProfiles.size());
		fail(VKErrorUnknown, "[ChatScenario1B::onSearchUser] userProfiles count mismatch. ");

		return;
	}

	searchedUserProfile_ = userProfiles.at(0);

	pass();
}

void ChatScenario1B::onFindMatch(VKMatch * match, VKError * error)
{
	if (error != NULL) {
		fail(error->errorCode(), error->errorMessage());
		return;
	}

	ASSERT_TRUE(match->getMatchId() > 0);
	printf("[ChatScenario1B::onFindMatch] called. matchId = %ld\n", match->getMatchId());

	match->delegate(this);

	//	std::atomic_thread_fence(std::memory_order_release);

	ChatScenario1B::match_ = match;

	pass();
}

void ChatScenario1B::onReceiveData(VKMatch * match, const TxData & data, const TxString & playerID)
{
	std::string message((const char *)data.bytes(), data.length());
	printf("[ChatScenario1B::onReceiveData] called. data : %s\n", message.c_str());

	lastSenderID = playerID;
	lastMessage = new TxData(data);

	pass();
}
;
