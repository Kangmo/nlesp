/*
 * TestBase.h
 *
 *  Created on: Oct 8, 2012
 *      Author: ladmin
 */

#ifndef TESTBASE_H_
#define TESTBASE_H_

#include <gtest/gtest.h>
#include <VicKit/VicKit.h>

class TestBase : public testing::Test {
public:
	enum TestResult {
		PASSED,
		FAILED,
		RUNNING
	};

	TestResult result;
	VKErrorCode errorCode;
	std::string errorMessage;
	TxString createdUserID;

	virtual void SetUp() { initState(); }
	TestResult waitUntilFinished() { while(result == RUNNING); return result; }
	void pass() { result = PASSED; }
	void fail(VKErrorCode code, const std::string & message) {
		result = FAILED; errorCode = code; errorMessage = message;
		std::cout << "Error [code:" << code << "] " << errorMessage.c_str() << "\n";
	}
	bool isPassed() { return result == PASSED; }
	VKErrorCode getLastError() { return errorCode; }
	const std::string & getLastErrorMessage() { return errorMessage; }

	void initState() { result = RUNNING; };
};




class ChatClient : public TestBase {
public:
	TxString email;
	TxString password;

	TxData * lastMessage;
	TxString lastSenderID;
	VKMatch * match_;
	VKMatchmaker * matchmaker;

	ChatClient() {
		email = "";
		password = "";
		lastMessage = NULL;
		lastSenderID = "";
		match_ = NULL;
		matchmaker = VKMatchmaker::sharedMatchmaker();

	}

	bool sendMessageToAll(const char * msgBody);

	// REMIND: dummy function to reuse TestBase for non-GTest.
	void TestBody() {};
};

#endif /* TESTBASE_H_ */
