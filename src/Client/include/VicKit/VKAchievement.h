#ifndef __O_VK_ACHIEVEMENT_H__
#define __O_VK_ACHIEVEMENT_H__ (1)

#include <VicKit/Basement.h>
#include <VicKit/VKDefines.h>


class VKAchievement {
public :
	typedef std::vector<VKAchievement> VKAchievementArray ;
private :
	TxString identifier_;
	double percentComplete_;
	bool completed_;
	bool hidden_;
	VKTimestamp lastReportedDate_;
	bool showsCompletionBanner_;
public :
    // getters
	// Achievement identifier
	const TxString & identifier() const { return identifier_; };
	// Required, Percentage of achievement complete.
	const double percentComplete() const { return percentComplete_; };
	// Set to NO until percentComplete = 100.
	const bool completed() const { return completed_; };
	// Set to NO when a report for that achievement is made. Note: to only unhide an achievement, report it with percentComplete = 0
	const bool isHidden() const { return hidden_; };
	// Date the achievement was last reported. ReadOnly. Created at initialization
	const VKTimestamp & lastReportedDate() const { return lastReportedDate_; };
	// A banner will be momentarily displayed after reporting a completed achievement
	const bool showsCompletionBanner() const { return showsCompletionBanner_; };

	// setters
	void identifier(const TxString & arg) { identifier_ = arg; };
	void percentComplete(const double arg) { percentComplete_ = arg; };
	void lastReportedDate(const VKTimestamp & arg) { lastReportedDate_ = arg; };
	void showsCompletionBanner(const bool arg) { showsCompletionBanner_ = arg; };

public :
	VKAchievement() {
		identifier_ = "";
		percentComplete_ = 0;
		completed_ = false;
		hidden_ = false;
		showsCompletionBanner_ = false;
	};

	VKAchievement(const TxString & identifier) {
		identifier_ = identifier;
		percentComplete_ = 0;
		completed_ = false;
		hidden_ = false;
		showsCompletionBanner_ = false;
	};

	// Asynchronously load all achievements for the local player
	class LoadAchievementsHandler {
    public :
		virtual ~LoadAchievementsHandler() {};
        virtual void onLoadAchievements( const VKAchievementArray & achievements, VKError * error ) = 0;
    };
	static void loadAchievements(LoadAchievementsHandler * handler);

	// Reset the achievements progress for the local player. All the entries for the local player are removed from the server. Error will be nil on success.
	//Possible reasons for error:
	// 1. Local player not authenticated
	// 2. Communications failure

	class ResetAchievementsHandler {
    public :
		virtual ~ResetAchievementsHandler() {};
        virtual void onResetAchievements( VKError * error ) = 0;
    };
	static void resetAchievements(ResetAchievementsHandler * handler);



	// Report an array of achievements to the server. Percent complete is required. Points, completed state are set based on percentComplete. isHidden is set to NO anytime this method is invoqued. Date is optional. Error will be nil on success.
	// Possible reasons for error:
	// 1. Local player not authenticated
	// 2. Communications failure
	// 3. Reported Achievement does not exist

	class ReportAchievementsHandler {
    public :
		virtual ~ReportAchievementsHandler() {};
        virtual void onReportAchievements( VKError * error ) = 0;
    };
	static void reportAchievements(const VKAchievementArray & achievements, ReportAchievementsHandler * handler);


	// Report this achievement to the server. Percent complete is required. Points, completed state are set based on percentComplete. isHidden is set to NO anytime this method is invoqued. Date is optional. Error will be nil on success.
	// Possible reasons for error:
	// 1. Local player not authenticated
	// 2. Communications failure
	// 3. Reported Achievement does not exist
	class ReportAchievementHandler {
    public :
		virtual ~ReportAchievementHandler() {};
        virtual void onReportAchievement( VKError * error ) = 0;
    };
	void reportAchievement(ReportAchievementHandler * handler);
};


#endif /* __O_VK_ACHIEVEMENT_H__ */
