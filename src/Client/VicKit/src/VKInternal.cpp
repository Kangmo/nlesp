#include <iostream>
#include <fstream>

#include <VicKit/Basement.h>
#include <VicKit/VKMatchmaker.h>

#include "VKInternal.h"

#include "VKMatchImpl.h"

#include "VKDefines.h"
#include "VicDataService.h"

#include <concurrency/ThreadManager.h>
#include <protocol/TBinaryProtocol.h>
#include <transport/TBufferTransports.h>
#include <concurrency/BoostThreadFactory.h>
#include <transport/TSocket.h>
#include <transport/TBufferTransports.h>
using namespace apache::thrift::transport;
using namespace apache::thrift::concurrency;
using namespace apache::thrift::transport;
using namespace apache::thrift::protocol;

//------------------------------------------------------------------------------------
//                                 static members
//------------------------------------------------------------------------------------

// The authentication signature, which we get from server as a response of a successful authentication.
// For all requestes except authenticate, we need to pass the authSignature as the first argument to help servers check that we are authenticated and authorized.
VicKit::AuthSignature VKInternal::authSignature_;

// The instance name of the process. This is for testing purpose.
// We need to have separate data files within a folder in case multiple processes run in a folder simulating multiple users.
// The instance name is used for the file name on the same folder to have different data files for different processes.
std::string VKInternal::instanceName_ = "";


// This flag is turned on to stop the pull data thread when the system is destroyed.
bool VKInternal::stopThread_ = false;

// The thread object for the pull message thread.
boost::thread * VKInternal::poolMessageThread_ = NULL;

//------------------------------------------------------------------------------------
//                                 pullData
//------------------------------------------------------------------------------------

static inline TxData ConvertFromThrift( VicKit::ContextMessage contextMessage )
{
	TxData data = ConvertFromThrift(contextMessage.messageContent.message);
	data.setTimestamp(contextMessage.messageContent.sentTime);

	return data;
}


// Received an invitation to a match.
// call  VKMatchmaker::sharedMatchMaker().inviteHandler().onInvite( ... )
static void handleContextInvitation( VicKit::ContextMessage & contextMessage )
{
	VKMatchImpl * matchImpl = VKMatchImpl::getMatch(contextMessage.messageContent.senderContextId);
	if ( matchImpl == NULL ) // Normal case : The match is not found
	{
		// call inviteHandler().onInvite in VKMatchmaker
		VKMatchmaker * matchMaker = VKMatchmaker::sharedMatchmaker();
		VKMatchmaker::InviteHandler * inviteHandler = matchMaker->inviteHandler();
		if ( inviteHandler == NULL )
		{
			// BUGBUG : Show an error message saying "You need to set an invite handler
			VK_ASSERT(0);
		}

		// deserialize ContextInvitationData from ContextMessage.data
		VicKit::ContextInvitationMessage contextInvitationMessage;
		{
			TxData data = ConvertFromThrift(contextMessage);

			boost::shared_ptr<TMemoryBuffer> strBuffer(new TMemoryBuffer());
			boost::shared_ptr<TBinaryProtocol> binaryProtcol(new TBinaryProtocol(strBuffer));

			strBuffer->resetBuffer((unsigned char*) data.bytes(), data.length() );
			contextInvitationMessage.read(binaryProtcol.get());
		}

		VKInvite invite(contextMessage.messageContent.senderContextId, contextMessage.messageContent.senderUID, contextInvitationMessage.playersToInvite);

		// The implementation of onInvite should call VKMatchmaker::matchForInvite to get VKMatch from the VKInvite object.
		// Then matchForInvite creates a new VKMatchImpl, maps it to theMatches[VKInvite.cid].
		inviteHandler->onInvite(&invite, NULL /* playersToInvite */);
	}
	else // The match is found
	{
		// The context invitation was already accepted and a new VKMatchImpl was created on the client side.
		// Do nothing.
	}
}

// Data arrived to a specific context.
// Find the match(context) object, call onReceive callback passing the received data.
static void handleContextMessage( VicKit::ContextMessage & contextMessage )
{
	VKMatchImpl * matchImpl = VKMatchImpl::getMatch(contextMessage.messageContent.senderContextId);
	if ( matchImpl == NULL ) // The match is not found
	{
		// VKMatchmakr::inviteHandler().onInvite did not create a match by calling VKMatchmaker::matchForInvite.
		// We assume that the client programmer using VicKit API does not want to accept the invitation in this case.
		// Ignore the data we received from the match of the invitation.
		// BUGBUG : Optimize not to receive data from a match whose invitation was not accepted by sending "Not accepted" message to the server.
	}
	else // The match is found
	{
		VKMatchDelegate * matchDelegate = matchImpl->delegate();
		if ( ! matchDelegate )
		{
			// BUGBUG : Show an error message saying "set VKMatchDelegate on a Match object to receive data"
			VK_ASSERT(0);
		}

		TxData data = ConvertFromThrift(contextMessage);

		matchDelegate->onReceiveData(matchImpl, data, contextMessage.messageContent.senderUID);
	}
}

// Request all data whose timestamp is after the given one.
// "All data" includes match invitations, personal messages, friend request, and any data sent to any match.
// Return the maximum MessageID of all pulled messages.
// Return -1 if no message found.
const VicKit::MessageID VKInternal::pullMessages(VicKit::MessageID startMessageId, VicKit::MessageID stopMessageId)
{
	VicKit::ReqPullMessages request;
	request.startMessageID = startMessageId;
	request.stopMessageID = stopMessageId;


	// BUGBUG : Optimize to use a connection that is already established, instead of opening a new connection for each pullData request.
    boost::shared_ptr<TSocket> socket(new TSocket(ESP_SERVER_IP, ESP_SERVER_PORT));
    boost::shared_ptr<TTransport> transport(new TFramedTransport(socket));
    boost::shared_ptr<TProtocol> protocol(new TBinaryProtocol(transport));

    transport->open();

    VicKit::VicDataServiceClient client(protocol);

    VicKit::ResPullMessages response;

    client.pullMessages(response, VKInternal::authSignature(), request);

    transport->close();

	if (response.messageList.size() > 0 )
	{
		// The messages in the messageList is in descending order of the sent time.
	    // We need to traverse them in ascending order of the sent time.
		for (int i=response.messageList.size()-1; i>=0; i--) {
			VicKit::ContextMessage & contextMessage = response.messageList[i];

			VicKit::MessageType::type messageType = contextMessage.messageContent.messageType;
			printf("[DEBUG] Received Message Id : %ld, Type : %d\n", contextMessage.messageID, (int)messageType );

			switch( messageType )
			{
				case VicKit::MessageType::MT_CONTEXT_MESSAGE:
				{
					handleContextMessage( contextMessage );
				}
				break;

				case VicKit::MessageType::MT_CONTEXT_INVITATION:
				{
					handleContextInvitation( contextMessage );
				}
				break;
				default :
					printf("Unknown Message Type : %d\n", (int)messageType );
				break;
			}
		}

		printf("[DEBUG] Done." );

		return response.maxMessageID;
	}
	else
	{
		return -1;
	}
}

static inline const char * internalDataFileName()
{
	return (VKInternal::instanceName() + ".dat").c_str();
}

// return -1 if the file is not found.
static VicKit::MessageID readMaxMessageId()
{
	VicKit::MessageID maxMessageId = -1;
    std::ifstream ifs(internalDataFileName(), std::ios::binary );
    if (ifs.eof())
    	return -1;

    ifs >> maxMessageId;

    if (ifs.fail() || ifs.bad() )
    	maxMessageId = -1;

    return maxMessageId;
}

static void writeMaxMessageId(VicKit::MessageID messageId)
{
    std::ofstream ofs(internalDataFileName(), std::ios::binary );
    ofs << messageId;
}

void VKInternal::PullMessagesThreadFunc() {

	// The maximum message id of the latest message that we pulled from the server.
	VicKit::MessageID messageId = readMaxMessageId();
	// If MaxMessageId was not written before,
	while( ! stopThread_ )
	{
	    const VicKit::AuthSignature & authSignature = VKInternal::authSignature();
	    // BUGBUG : When the authentication is invalidated, we need to set an empty string to authSignature.uid.
	    if ( authSignature.uid == "" )
	    {
			boost::this_thread::sleep(boost::posix_time::milliseconds(PULL_THREAD_CHECK_AUTHENTICATION_SLEEP_MS));
	    	continue;
	    }

		// -1 on the stopMessageId means that it needs to pull all messages after the the messageId
		VicKit::MessageID newMessageId = VKInternal::pullMessages(messageId, -1);

		if ( newMessageId < 0 ) // no message found.
		{
			// sleep only if we didn't get any new message from server.
			// don't sleep if we are receiving any new message from server.
			boost::this_thread::sleep(boost::posix_time::milliseconds(PULL_THREAD_SLEEP_MS));
		}
		else
		{
			// store the new timestamp persistently.
			// BUGBUG : Optimize : This may make the battery to run out quickly. Consider storing the timestamp less frequently.
			messageId = newMessageId + 1;
			writeMaxMessageId(messageId);
		}
	}
}

// Start the thread that sends pullMessages request to the server.
void VKInternal::StartPullMessagesThread()
{
	VK_ASSERT( poolMessageThread_ == NULL );
	poolMessageThread_ = new boost::thread(PullMessagesThreadFunc);
}

void VKInternal::StopPullMessagesThread()
{
	VK_ASSERT( poolMessageThread_ != NULL );
	stopThread_ = true;
}
void VKInternal::JoinPullMessagesThread()
{
	VK_ASSERT( poolMessageThread_ != NULL );
	poolMessageThread_->join();
	delete poolMessageThread_;
}


