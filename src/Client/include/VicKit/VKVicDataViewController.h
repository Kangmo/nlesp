#ifndef __O_VK_VICDATA_VIEWCONTROLLER_H__
#define __O_VK_VICDATA_VIEWCONTROLLER_H__ (1)

#include <VicKit/Basement.h>
#include <VicKit/VKLeaderboard.h>
#include <VicKit/VKVicDataViewController.h>

typedef enum  {
    VKVicDataViewControllerStateDefault = -1,
    VKVicDataViewControllerStateLeaderboards ,
    VKVicDataViewControllerStateAchievements
// Not supported yet.
//    VKVicDataControllerStateChallenges,
} VKVicDataViewControllerState;


class VKVicDataViewController : public VKDialog {
private :
	VKVicDataViewController * vicDataDelegate_ ;
	VKVicDataViewControllerState viewState_;
	VKLeaderboardTimeScope leaderboardTimeScope_;
	TxString leaderboardCategory_;
public :
	// Getters
	const VKVicDataViewController * vicDataDelegate() const { return vicDataDelegate_ ; };
	const VKVicDataViewControllerState viewState() const { return viewState_; };
	const VKLeaderboardTimeScope leaderboardTimeScope() const { return leaderboardTimeScope_; };
	const TxString & leaderboardCategory() const { return leaderboardCategory_; };

	// Setters
	void vicDataDelegate( VKVicDataViewController * arg ) { vicDataDelegate_ = arg; };
	void viewState( const VKVicDataViewControllerState arg ) { viewState_ = arg; };
	void leaderboardTimeScope( const VKLeaderboardTimeScope arg ) { leaderboardTimeScope_ = arg; };
	void leaderboardCategory( const TxString & arg ) { leaderboardCategory_ = arg; };
};

class VKVicDataControllerDelegate {
	void vicDataViewControllerDidFinish(VKVicDataViewController *  vicDataViewController);
};

#endif /*__O_VK_VICDATA_VIEWCONTROLLER_H__*/
