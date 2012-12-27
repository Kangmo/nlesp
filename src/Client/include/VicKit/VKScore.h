#ifndef __O_VK_SCORE_H__
#define __O_VK_SCORE_H__ (1)

#include <VicKit/Basement.h>
#include <VicKit/VKDefines.h>


class VKScore {
public :
typedef std::vector<VKScore> VKScoreArray;
private :
	VKInt64     value_;
	TxString    formattedValue_;
	TxString    category_;
	VKUInt64    context_;
	VKTimestamp date_;
	TxString    playerID_;
	VKInt32     rank_;
	bool        shouldSetDefaultLeaderboard_;

public :
	// getters
	// The score value as a 64bit integer.
	const VKInt64       value() const { return value_; };
	// The score formatted as a string, localized with a label
	const TxString &    formattedValue() const { return formattedValue_; };
	// leaderboard category (required)
	const TxString &    category() const { return category_; };
	// optional additional context that allows a game to store and retrieve additional data associated with the store.  Default value of zero is returned if no value is set.
	const VKUInt64      context() const { return context_; };
	// The date the score was recorded, defaults to current timestamp.
	const VKTimestamp & date() const { return date_; };
	// The identifier of the player that recorded the score.
	const TxString &    playerID() const { return playerID_; };
	// The rank of the player within the leaderboard, only valid when returned from GKLeaderboard
	const VKInt32       rank() const { return rank_; };
	// Convenience property to make the leaderboard associated with this GKScore, the default leaderboard for this player. Default value is false.
	// If true, reporting that score will make the category this score belongs to, the default leaderboard for this user
	const bool          shouldSetDefaultLeaderboard() const { return shouldSetDefaultLeaderboard_; };

	// Setters
	void value( const VKInt64 arg ) { value_ = arg; };
	void category( const TxString & arg ) { category_ = arg; };
	void context( const VKUInt64 arg ) { context_ = arg; };
	void shouldSetDefaultLeaderboard( const bool arg ) { shouldSetDefaultLeaderboard_ = arg; };


	VKScore()
	{
	}

	VKScore(const TxString & category)
	{
		category_ = category;
	}

	class ReportScoresHandler {
    public :
		virtual ~ReportScoresHandler() {};
        virtual void onReportScores(VKError * error ) = 0;
    };
	static void reportScores(const VKScoreArray & scores, ReportScoresHandler * handler);

	// Report this score to the server. The value must be set, and date may be changed.
	// Possible reasons for error:
	// 1. Value not set
	// 2. Local player not authenticated
	// 3. Communications problem
	class ReportScoreHandler {
    public :
		virtual ~ReportScoreHandler() {};
        virtual void onReportScore(VKError * error ) = 0;
    };
	void reportScore(ReportScoreHandler * handler);
};

#endif /* __O_VK_SCORE_H__ */
