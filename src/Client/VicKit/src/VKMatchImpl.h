#ifndef __O_VK_MATCHIMPl_H__
#define __O_VK_MATCHIMPl_H__ (1)

#include "VicKit_types.h"

#include <VicKit/VKMatch.h>

class VKMatchImpl : public VKMatch
{
public :
	VKMatchImpl(const VicKit::ContextID & cid, const std::vector<VicKit::UID> & playerIDs) : VKMatch()
	{
		matchId_ = cid;
		playerIDs_ = playerIDs;
		expectedPlayerCount_ = playerIDs.size();
	}

	virtual ~VKMatchImpl() {};

	inline VicKit::ContextID getContextID() {
		return matchId_;
	}

	// Asynchronously send data to one or more players. Returns YES if delivery started, NO if unable to start sending and error will be set.
	virtual bool sendData( const TxData & data, const TxStringArray & playerIDs, VKMatchSendDataMode dataMode, VKError ** error);

	// Asynchronously broadcasts data to all players. Returns YES if delivery started, NO if unable to start sending and error will be set.
	virtual bool sendDataToAllPlayers( const TxData & data, VKMatchSendDataMode dataMode, VKError ** error);

	// Disconnect the match. This will show all other players in the match that the local player has disconnected. This should be called before releasing the match instance.
	virtual void disconnect();

	static VKMatchImpl * createMatch(VicKit::ContextID cid, const std::vector<std::string> & playerUIDs );
	static VKMatchImpl * getMatch(VicKit::ContextID cid );
private :
};

#endif /* __O_VK_MATCHIMPl_H__ */
