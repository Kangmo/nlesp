#ifndef __O_VK_MATCHMAKER_H__
#define __O_VK_MATCHMAKER_H__ (1)

#include <VicKit/Basement.h>

class VKPlayer;
class VKMatch;

class VKMatchRequest {
private:
    unsigned long long matchId_;
    unsigned int minPlayers_;
    unsigned int maxPlayers_;
    unsigned int playerGroup_;
    unsigned int playerAttributes_;
    TxStringArray playersToInvite_;
public:
    VKMatchRequest()
    {
    	// TODO : make sure the "InBox" context ID of a user does not start with 0
    	matchId_ = 0;
    	minPlayers_ = 0;
    	maxPlayers_ = 0;
    	playerGroup_ = 0;
    	playerAttributes_ = 0;
    }
    unsigned long long matchId() const { return matchId_; };
    unsigned int minPlayers() const { return minPlayers_; };            // Minimum number of players for the match
    unsigned int maxPlayers() const { return maxPlayers_; };            // Maximum number of players for the match
    unsigned int playerGroup() const { return playerGroup_; };          // The player group identifier. Matchmaking will only take place between players in the same group.
    unsigned int playerAttributes() const { return playerAttributes_; };// optional flags such that when all player flags are OR'ed together in a match they evaluate to 0xFFFFFFFF
    const TxStringArray & playersToInvite() const { return playersToInvite_; };        // Array of player IDs to invite, or nil if none

    void matchId(unsigned long long matchId) { matchId_ = matchId ; };
    void minPlayers(unsigned int arg) { minPlayers_ = arg; };
    void maxPlayers(unsigned int arg) { maxPlayers_ = arg; };
    void playerGroup(unsigned int arg) { playerGroup_ = arg; };
    void playerAttributes(unsigned int arg) { playerAttributes_ = arg; };
    void playersToInvite(const TxStringArray & arg) { playersToInvite_ = arg; };
};


// VKInvite represents an accepted game invite, it is used to create a VKMatchmakerViewController
class VKInvite  {
private :
    TxString inviter_;
    bool hosted_;
	unsigned long long matchId_;

	// players in the invited match
    TxStringArray playersToInvite_;
public :
    // BUGBUG : try to hide the constructor from VicKit API.
    VKInvite(unsigned long long matchId, TxString inviter, const TxStringArray & playersToInvite)
    {
    	matchId_ = matchId;
    	inviter_ = inviter;
    	hosted_ = false;
    	playersToInvite_ = playersToInvite;
    }
    const unsigned long long matchId() const { return matchId_; };
    const TxString & inviter() const { return inviter_; };
    bool isHosted() const { return hosted_; };
    const TxStringArray & playersToInvite() const { return playersToInvite_; };        // Array of player IDs to invite, or nil if none
};

// VKMatchmaker is a singleton object to manage match creation from invites and auto-matching.
class VKMatchmaker {
public :
// The shared matchmaker
    static VKMatchmaker * sharedMatchmaker();

// An inviteHandler must be set in order to receive game invites or respond to external requests to initiate an invite. The inviteHandler will be called when an invite or request is received. It may be called immediately if there is a pending invite or request when the application is launched. The inviteHandler may be called multiple times.
// Either acceptedInvite or playersToInvite will be present, but never both.

    class MatchForInviteHandler {
    public :
        virtual void onMatchForInvite(VKMatch * match, VKError * error) = 0;
    };
    void matchForInvite(VKInvite* invite, MatchForInviteHandler * handler);

/*
    class MatchesAfterMatchHandler {
    public :
        virtual void onMatches(const std::vector<VKMatch*> & matches, TxError * error) = 0;
    };
    void matchesAfterMatch(unsigned long long lastMatchId, MatchesAfterMatchHandler * handler);
*/
    class InviteHandler {
    public :
        virtual void onInvite(VKInvite * acceptedInvite, TxStringArray * playersToInvite) = 0;
    };
private : 
    InviteHandler * inviteHandler_;
public :
    InviteHandler * inviteHandler() { return inviteHandler_; };
    void inviteHandler(InviteHandler * arg) { inviteHandler_ = arg; };

// Auto-matching to find a peer-to-peer match for the specified request. Error will be nil on success:
// Possible reasons for error:
// 1. Communications failure
// 2. Unauthenticated player
// 3. Timeout

    class FindMatchHandler {
    public :
        virtual void onFindMatch(VKMatch * match, VKError * error) = 0;
    };
    void findMatch(const VKMatchRequest & request, FindMatchHandler * handler);

// Matchmaking for host-client match request. This returns a list of player identifiers to be included in the match. Determination and communication with the host is not part of this API.
// Possible reasons for error:
// 1. Communications failure
// 2. Unauthenticated player
// 3. Timeout
    class FindPlayersHandler {
    public :
        virtual void onFindPlayers(const TxStringArray & playerIDs, VKError * error) = 0;
    };
    void findPlayersForHostedMatchRequest(const VKMatchRequest & request, FindPlayersHandler * handler);

// Auto-matching to add additional players to a peer-to-peer match for the specified request. Error will be nil on success:
// Possible reasons for error:
// 1. Communications failure
// 2. Timeout
    class AddPlayersHandler {
    public :
        virtual void onAddPlayers(VKError * error) = 0;
    };
    void addPlayers(VKMatch * match, const VKMatchRequest & matchRequest, AddPlayersHandler * handler);

// Cancel matchmaking
    void cancel();

// Query the server for recent activity in the specified player group. A larger value indicates that a given group has seen more recent activity. Error will be nil on success.
// Possible reasons for error:
// 1. Communications failure
    class QueryPlayerGroupActivityHandler {
    public :
        virtual void onQueryPlayerGroupActivity(int activity, VKError * error) = 0;
    };
    void queryPlayerGroupActivity(unsigned int playerGroup, QueryPlayerGroupActivityHandler * handler);

// Query the server for recent activity for all the player groups of that game. Error will be nil on success.
// Possible reasons for error:
// 1. Communications failure
    class QueryActivityHandler {
    public :
        virtual void onCompleteQueryActivity(int activity, VKError * error) = 0;
    };
    void queryActivity(QueryActivityHandler * handler);
};

#endif /*__O_VK_MATCHMAKER_H__*/
