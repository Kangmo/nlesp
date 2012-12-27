/*
 * VicKit.cpp.h
 *
 *  Created on: Sep 8, 2012
 *      Author: kangmo
 */
#include <VicKit/VicKit.h>
#include "VKInternal.h"


// TODO : Need to document that we need to set VKMatchmaker::inviteHandler() before calling VicKit::initialize
void VicKitSystem::initialize(const TxString & instanceName) {
	VKInternal::setInstanceName(instanceName);
	VKInternal::StartPullMessagesThread();
}

void VicKitSystem::destroy() {
	VKInternal::StopPullMessagesThread();
	VKInternal::JoinPullMessagesThread();
}

