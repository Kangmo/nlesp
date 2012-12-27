
#include "VKInternal.h"

#include "AsyncRequester.h"

#include "VicKit_types.h"

#include "VKMatchImpl.h"

//------------------------------------------------------------------------------------
// Mapping from ContextID to VKMatchImpl
//------------------------------------------------------------------------------------

// The map from ContextID to VKMatch
static std::map<VicKit::ContextID, boost::shared_ptr<VKMatchImpl> > theMatches;

VKMatchImpl * VKMatchImpl::createMatch(VicKit::ContextID cid, const std::vector<std::string> & playerUIDs )
{
	boost::shared_ptr<VKMatchImpl> newMatch( new VKMatchImpl( cid, playerUIDs ) );

	theMatches[cid] = newMatch;

	return newMatch.get();
}

VKMatchImpl * VKMatchImpl::getMatch(VicKit::ContextID cid )
{
	std::map<VicKit::ContextID, boost::shared_ptr<VKMatchImpl> >::iterator it = theMatches.find(cid);
	if ( it == theMatches.end() ) // Normal case : The match is not found
		return NULL;

	boost::shared_ptr<VKMatchImpl> ref = it->second;
	return ref.get();
}

//------------------------------------------------------------------------------------
//                                 sendData
//------------------------------------------------------------------------------------

// Asynchronously send data to one or more players. Returns YES if delivery started, NO if unable to start sending and error will be set.
bool VKMatchImpl::sendData( const TxData & data, const TxStringArray & playerIDs, VKMatchSendDataMode dataMode, VKError ** error)
{
    *error = NULL;

    return true;
}

//------------------------------------------------------------------------------------
//                                 sendDataToAllPlayers
//------------------------------------------------------------------------------------


// Asynchronously broadcasts data to all players. Returns YES if delivery started, NO if unable to start sending and error will be set.
bool VKMatchImpl::sendDataToAllPlayers( const TxData & data, VKMatchSendDataMode dataMode, VKError ** error)
{

	// We don't receive any response.
	boost::shared_ptr<InternalCompletionHandler_ReturnVoid> nullHandler ( (InternalCompletionHandler_ReturnVoid*) NULL );

	VicKit::ReqSendMessage req;
	req.cid = getContextID();
	req.message = ConvertToThrift(data);

//	printf("[Client] data to send = %s\n",(const char*)data.bytes());
//	printf("[Client] message to send = %s\n",req.message.c_str());

    boost::shared_ptr<RequestingTask_ReturnVoid<VicKit::ReqSendMessage> > requestingTask (
        new RequestingTask_ReturnVoid<VicKit::ReqSendMessage>
        (  &VicKit::VicDataServiceClient::sendOnewayMessage,
       	   VKInternal::authSignature(),
           req,
           nullHandler)
    );

    // BUGBUG : sendData request is oneway, so no need to use asynchronous requester.
    theAsyncRequester.Request(requestingTask);

    *error = NULL;

    return true;
}


//------------------------------------------------------------------------------------
//                                 disconnect
//------------------------------------------------------------------------------------

// Disconnect the match. This will show all other players in the match that the local player has disconnected. This should be called before releasing the match instance.
void VKMatchImpl::disconnect()
{

}
