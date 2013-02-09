//
//  VKMatch.h
//  GameKit
//
//  Copyright 2010 Apple, Inc. All rights reserved.
//

#ifndef __O_VK_MATCH_H__
#define __O_VK_MATCH_H__ (1)

#include <VicKit/Basement.h>

class VKMatchDelegate;

typedef enum {
    VKMatchSendDataReliable,         // a.s.a.p. but requires fragmentation and reassembly for large messages, may stall if network congestion occurs
    VKMatchSendDataUnreliable        // Preferred method. Best effort and immediate, but no guarantees of delivery or order; will not stall.
} VKMatchSendDataMode;

typedef enum {
    VKPlayerStateUnknown,       // initial player state
    VKPlayerStateConnected,     // connected to the match
    VKPlayerStateDisconnected   // disconnected from the match
}VKPlayerConnectionState;

// VKMatch represents an active networking sessions between players. It handles network communications and can report player connection status. All matches are created by a VKMatchmaker.
class VKMatch {
public :
	VKMatch();
	virtual ~VKMatch();
protected:
	TxStringArray playerIDs_;
	VKMatchDelegate * delegate_;

	// The number of players that are not connected to the match yet.
	unsigned int expectedPlayerCount_;

	unsigned long long matchId_;
public :
	const TxStringArray & playerIDs() const { return playerIDs_; };  // NSStrings of player identifiers in the match
	VKMatchDelegate * delegate() { return delegate_; };
	void delegate( VKMatchDelegate * arg ) { delegate_ = arg; };
	unsigned int expectedPlayerCount() const { return expectedPlayerCount_; };
	unsigned long long matchId() const {
		return matchId_;
	}

// Asynchronously send data to one or more players. Returns YES if delivery started, NO if unable to start sending and error will be set.
	virtual bool sendData( const TxData & data, const TxStringArray & playerIDs, VKMatchSendDataMode dataMode, VKError ** error) = 0;

// Asynchronously broadcasts data to all players. Returns YES if delivery started, NO if unable to start sending and error will be set.
	virtual bool sendDataToAllPlayers( const TxData & data, VKMatchSendDataMode dataMode, VKError ** error) = 0;

// Disconnect the match. This will show all other players in the match that the local player has disconnected. This should be called before releasing the match instance.
	virtual void disconnect() = 0;
};

class VKMatchDelegate {
public :
	VKMatchDelegate() {};
	virtual ~VKMatchDelegate() {};

// The match received data sent from the player.
	virtual void onReceiveData( VKMatch * match, const TxData & data, const TxString & playerID ) = 0;

// The player state changed (eg. connected or disconnected)
	virtual void onChangeState( VKMatch * match, const TxString & playerID, VKPlayerConnectionState state ) {};

// The match was unable to connect with the player due to an error.
	virtual void onConnectionFailure( VKMatch * match, const TxString & playerID, VKError * error ) {};

// The match was unable to be established with any players due to an error.
	virtual void onFailure( VKMatch * match, VKError * error ) {};

// This method is called when the match is interrupted; if it returns YES, a new invite will be sent to attempt reconnection. This is supported only for 1v1 games
	virtual bool shouldReinvite(VKMatch * match, const TxString & playerID) {
		// TODO : check the default behavior.
		return true;
	};
};

#endif /* __O_VK_MATCH_H__ */
