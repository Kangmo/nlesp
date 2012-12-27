#ifndef __O_VK_LEADERBOARD_H__
#define __O_VK_LEADERBOARD_H__ (1)

#include <VicKit/Basement.h>
#include <VicKit/VKDefines.h>
#include <VicKit/VKScore.h>

typedef enum {
    VKLeaderboardTimeScopeToday = 0,
    VKLeaderboardTimeScopeWeek,
    VKLeaderboardTimeScopeAllTime
} VKLeaderboardTimeScope;

typedef enum {
    VKLeaderboardPlayerScopeGlobal = 0,
    VKLeaderboardPlayerScopeFriendsOnly
}VKLeaderboardPlayerScope;



class VKLeaderboard {
public :
	typedef std::vector<VKLeaderboard> VKLeaderboardArray;
private :
	VKLeaderboardTimeScope timeScope_;
	VKLeaderboardPlayerScope playerScope_;
	TxString category_;
	TxString title_;
	VKRange  range_;
	VKScore::VKScoreArray scores_;
	VKUInt32 maxRange_;
	VKScore  localPlayerScore_;
	bool loading_;
	TxString groupIdentifier_;

public :
	// Getters
	const VKLeaderboardTimeScope timeScope() const { return timeScope_; };
	// Filter on friends. Does not apply to leaderboard initialized with players.
	const VKLeaderboardPlayerScope playerScope() const { return playerScope_; };
	// leaderboard category.  If nil, then it will fetch the aggregate leaderboard
	const TxString & category() const { return category_; };
	// Localized category title. Defaults to nil until loaded.
	const TxString & title() const { return title_; };
	// Leaderboards start at index 1 and the length should be less than 100. Does not apply to leaderboards initialized with players.  Exception will be thrown if developer tries to set an invalid range
	const VKRange  & range() const { return range_; };
	// Scores are not valid until loadScores: has completed.
	const VKScore::VKScoreArray & scores() const { return scores_; };

	// The maxRange which represents the size of the leaderboard is not valid until loadScores: has completed.
	const VKUInt32 maxRange() const { return maxRange_; };
	// The local player's score
	const VKScore  & localPlayerScore() const { return localPlayerScore_; };
	// true if the leaderboard is currently loading
	const bool isLoading() const { return loading_; };
	// set when leaderboards have been designated a game group; set when loadLeaderboardsWithCompletionHandler has been called for leaderboards that support game groups
	const TxString & groupIdentifier() const { return groupIdentifier_; };

	// Setters
	void timeScope( const VKLeaderboardTimeScope arg ) { timeScope_= arg; };
	void playerScope( const VKLeaderboardPlayerScope arg ) { playerScope_ = arg; };
	void category( const TxString & arg ) { category_ = arg; };
	void range( const VKRange & arg ) { range_ = arg; };

	// Default is the range 1-10 with Global/AllTime scopes
	// if you want to change the scopes or range, set the properites before loading the scores.
	VKLeaderboard() {

	};

	// Specify an array of players ids, for example, the players who are in a match together
	// Defaults to AllTime score, if you want to change the timeScope, set the property before loading the scores. Range and playerScope are not applicable. playerIDs may not be nil.
	VKLeaderboard(const TxStringArray & playerIDs) {

	};

	// Load the scores for this leader board asynchronously.  Error will be nil on success.
	// Possible reasons for error:
	// 1. Communications problem
	// 2. Unauthenticated player
	class LoadScoresHandler {
    public :
		virtual ~LoadScoresHandler() {};
        virtual void onLoadScores( const VKScore::VKScoreArray & scores, VKError * error ) = 0;
    };
	void loadScores(LoadScoresHandler * handler);

	// Loads parallel arrays that maps categories to their title strings
	// Possible reasons for error:
	// 1. Communications problem
	// 2. Unauthenticated player
	// 3. Leaderboard not present
	class LoadCategoriesHandler {
    public :
		virtual ~LoadCategoriesHandler() {};
        virtual void onLoadCategories( const TxStringArray & categories, const TxStringArray & titles, VKError * error ) = 0;
    };
	static void loadCategories(LoadCategoriesHandler * handler);


	class LoadLeaderboardsHandler {
    public :
		virtual ~LoadLeaderboardsHandler() {};
        virtual void onLoadLeaderboard( const VKLeaderboardArray & leaderboards, VKError * error ) = 0;
    };
	static void loadLeaderboards(LoadLeaderboardsHandler * handler);


	// Set the default leaderboard for the local player per game
	// Possible reasons for error:
	// 1. Communications problem
	// 2. Unauthenticated player
	// 3. Leaderboard not present
	class SetDefaultLeaderboardHandler {
    public :
		virtual ~SetDefaultLeaderboardHandler() {};
        virtual void onSetDefaultLeaderboard( VKError * error ) = 0;
    };
	static void setDefaultLeaderboard(const TxString & categoryID, SetDefaultLeaderboardHandler * handler);
};

#endif /* __O_VK_LEADERBOARD_H__ */
