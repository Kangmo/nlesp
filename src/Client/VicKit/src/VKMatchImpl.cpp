#include "VKPorting.h"

#include "VKInternal.h"

#include "AsyncRequester.h"

#include "VicKit_types.h"

#include "VKMatchImpl.h"

//------------------------------------------------------------------------------------
// Mapping from ContextID to VKMatchImpl
//------------------------------------------------------------------------------------

// The map from ContextID to VKMatch
static std::map<VicKit::ContextID, boost::shared_ptr<VKMatchImpl> > theMatches;

//----------------------------------------------------------------------------------------------------------
// Serialization and Deserialization of context mappings from context ID to context data on the client side.
//----------------------------------------------------------------------------------------------------------
#include <iostream>
#include <fcntl.h>
#include <stdio.h>
#include <unistd.h>
#include <sys/time.h>
#include <sys/stat.h>
#include <protocol/TBinaryProtocol.h>
#include <protocol/TDenseProtocol.h>
#include <protocol/TJSONProtocol.h>
#include <transport/TTransportUtils.h>
#include <transport/TFDTransport.h>

#include <boost/shared_ptr.hpp>

using namespace boost;
using namespace std;
using namespace apache::thrift;
using namespace apache::thrift::protocol;
using namespace apache::thrift::transport;

VicKit::ClientContextMap clientContextMap;

#define CONTEXT_MAP_DATA_FILE (internalDataFileName())

static inline const char * internalDataFileName()
{
    // iOS only
#if defined(PLATFORM_IOS)
    NSString *docsDir = [NSHomeDirectory() stringByAppendingPathComponent:@"Documents"];
    NSString *fileName = [NSString stringWithUTF8String:((VKInternal::instanceName() + "-cmap.dat").c_str())];
    NSString *path = [docsDir stringByAppendingPathComponent:fileName];
    NSLog(@"filePath: %@", path);
    return path.UTF8String;
#else
    
#endif
    // Linux
    return ((VKInternal::instanceName() + "-cmap.dat").c_str());
}

bool VKMatchImpl::serializeMatchMappings()
{
	  int fd = open(CONTEXT_MAP_DATA_FILE, O_CREAT | O_TRUNC | O_WRONLY, S_IRUSR | S_IWUSR | S_IXUSR);
	  if (-1 == fd)
	  {
          printf("VKMatchImpl::serializeMatchMappings: fd open fail\n");
		  return false;
	  }
    
    printf("VKMatchImpl::serializeMatchMappings: fd open success\n");
    
	  boost::shared_ptr<TFDTransport> innerTransport(new TFDTransport(fd));
	  boost::shared_ptr<TBufferedTransport> transport(new TBufferedTransport(innerTransport));

	  boost::shared_ptr<TBinaryProtocol> protocol(new TBinaryProtocol(transport));


	  transport->open();

	  uint32_t result = clientContextMap.write( protocol.get() );
    printf("VKMatchImpl::serializeMatchMappings: clientContextMap.write result = %d\n", result);

	  transport->close();

	  close(fd);
	  // BUGBUG : Make sure if we need to close fd.
	  return true;

}

bool VKMatchImpl::deserializeMatchMappings()
{
	  // check if the file exists
	  {
		  struct stat st;
		  if ( ::stat(CONTEXT_MAP_DATA_FILE, &st) == -1 && errno == ENOENT)
		  {
			  // file not found. For the first running, we don't have the serialized file at all.
              printf("VKMatchImpl::deserializeMatchMappings: file not found \n");
			  return true;
		  }
      }

	  int fd = open(CONTEXT_MAP_DATA_FILE,  O_RDONLY);
	  if (-1 == fd)
	  {
          printf("VKMatchImpl::deserializeMatchMappings: open failed \n");
		  return false;
	  }


	  boost::shared_ptr<TFDTransport> innerTransport(new TFDTransport(fd));
	  boost::shared_ptr<TBufferedTransport> transport(new TBufferedTransport(innerTransport));

	  boost::shared_ptr<TBinaryProtocol> protocol(new TBinaryProtocol(transport));

	  transport->open();

	  clientContextMap.contextMap.clear();
	  uint32_t result = clientContextMap.read( protocol.get() );
    printf("VKMatchImpl::deserializeMatchMappings: clientContextMap.read result = %d \n", result);

	  transport->close();

	  // For each mappings on clientContextMap, create match in theMatches map.
	  VKMatchImpl::clearAllMatches();
	  std::map<VicKit::ContextID, VicKit::ClientContextData>::iterator iter;

      for (iter = clientContextMap.contextMap.begin(); iter != clientContextMap.contextMap.end(); iter++) {
         VicKit::ContextID cid = iter->first;
         VicKit::ClientContextData contextData = iter->second;
          printf("VKMatchImpl::deserializeMatchMappings: cid = %lld \n", cid);
         VKMatchImpl * createdMatch = VKMatchImpl::createMatch(cid, contextData.playerUIDs);
   		 VK_ASSERT( createdMatch != NULL );
      }

	  close(fd);

	  return true;
}

void VKMatchImpl::clearAllMatches()
{
	  theMatches.clear();
}

VKMatchImpl * VKMatchImpl::createMatch(VicKit::ContextID cid, const std::vector<std::string> & playerUIDs )
{
	boost::shared_ptr<VKMatchImpl> newMatch( new VKMatchImpl( cid, playerUIDs ) );

	theMatches[cid] = newMatch;

	// Add to clientContextMap for serializing the mapping on the client side.
	VicKit::ClientContextData contextData;
	contextData.playerUIDs = playerUIDs;
	clientContextMap.contextMap[cid] = contextData;

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
	req.cid = matchId();
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
