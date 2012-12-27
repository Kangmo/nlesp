#include <VicKit/VKMatchmaker.h>
#include "VKInternal.h"

#include "AsyncRequester.h"

#include "VicKit_types.h"

#include "VKMatchImpl.h"

#include <VicKit/VKMatch.h>

VKMatchmaker * VKMatchmaker::sharedMatchmaker()
{
    static VKMatchmaker * theMatchmaker = NULL;
    
    if ( theMatchmaker == NULL ) 
    {
        theMatchmaker = new VKMatchmaker();
    }
    return theMatchmaker;
}

//------------------------------------------------------------------------------------
//                                 findMatch
//------------------------------------------------------------------------------------

class __FindMatchHandler : public InternalCompletionHandler<VicKit::ResCreateContext> {
public :
	__FindMatchHandler( VKMatchmaker::FindMatchHandler * hanlder, const std::vector<std::string> & playerUIDs ) {
		handler_    = hanlder;
		playerUIDs_ = playerUIDs;
	};

	virtual ~__FindMatchHandler() {};

	virtual void onComplete( const VicKit::ResCreateContext & response) {
		if (response.error.code != VicKit::ErrorCode::VKSuccess )
		{
			VKError error((VKErrorCode)response.error.code, response.error.message);

			handler_->onFindMatch( NULL, & error );
		}
		else
		{
			VKMatchImpl * matchImpl = VKMatchImpl::createMatch(response.createdContextId, playerUIDs_);

			handler_->onFindMatch( matchImpl, NULL );
		}
	};
private :
	std::vector<std::string> playerUIDs_;
	VKMatchmaker::FindMatchHandler * handler_;
};


void VKMatchmaker::findMatch(const VKMatchRequest & request, FindMatchHandler * handler)
{
	boost::shared_ptr<__FindMatchHandler> internalHandler ( new __FindMatchHandler(handler, request.playersToInvite() ) );

	VicKit::UserProfile req;

    boost::shared_ptr<RequestingTask< std::vector<VicKit::UID> , VicKit::ResCreateContext> > requestingTask(
        new RequestingTask< std::vector<VicKit::UID>, VicKit::ResCreateContext>
        (  &VicKit::VicDataServiceClient::createContext,
      	   VKInternal::authSignature(),
      	   request.playersToInvite(),
           internalHandler)
    );

    theAsyncRequester.Request(requestingTask);
}

//------------------------------------------------------------------------------------
//                                 matchForInvite
//------------------------------------------------------------------------------------
void VKMatchmaker::matchForInvite(VKInvite* invite, MatchForInviteHandler * handler)
{
	VKMatch * match = VKMatchImpl::createMatch(invite->matchId(), invite->playersToInvite() );
	VK_ASSERT(match);

	// BUGBUG : Show an error instead of the assertion.
	VK_ASSERT(handler);

	handler->onMatchForInvite(match, NULL /*error*/);
}

//------------------------------------------------------------------------------------
//                                 findPlayersForHostedMatchRequest
//------------------------------------------------------------------------------------

void VKMatchmaker::findPlayersForHostedMatchRequest(const VKMatchRequest & request, FindPlayersHandler * handler)
{
    VK_ASSERT(handler);
}

//------------------------------------------------------------------------------------
//                                 addPlayers
//------------------------------------------------------------------------------------

void VKMatchmaker::addPlayers(VKMatch * match, const VKMatchRequest & matchRequest, AddPlayersHandler * handler)
{
    VK_ASSERT(handler);
}

//------------------------------------------------------------------------------------
//                                 cancel
//------------------------------------------------------------------------------------

void VKMatchmaker::cancel()
{
    
}

//------------------------------------------------------------------------------------
//                                 queryPlayerGroupActivity
//------------------------------------------------------------------------------------

void VKMatchmaker::queryPlayerGroupActivity(unsigned int playerGroup, QueryPlayerGroupActivityHandler * handler)
{
    VK_ASSERT(handler);
}

//------------------------------------------------------------------------------------
//                                 queryActivity
//------------------------------------------------------------------------------------

void VKMatchmaker::queryActivity(QueryActivityHandler * handler)
{
    VK_ASSERT(handler);
}
