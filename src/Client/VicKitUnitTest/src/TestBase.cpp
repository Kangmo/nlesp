#include "TestBase.h"
#include <string.h>

bool ChatClient::sendMessageToAll(const char * msgBody) {
	TxData message( (void*)msgBody, strlen(msgBody));
//	printf("[DEBUG]ChatClient::sendMessageToAll => %s\n", (const char *) message.bytes());
	VKError* error = NULL;
	bool result = match_->sendDataToAllPlayers(message, VKMatchSendDataReliable,
			&error);
	if ( error != NULL )
	{
		// Error happened.
		std::cout << "[ChatClient::sendMessageToAll] error code : " << error->errorCode()
				  <<                               " error message : " << error->errorMessage() ;
		delete error;
	}
	return result;
}
