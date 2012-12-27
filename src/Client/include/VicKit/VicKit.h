/*
 *
 *  VicKit.h
 *  VicKit
 *
 */
#ifndef __O_VICKIT_H__
#define __O_VICKIT_H__ (1)

// VicKit headers
#include <VicKit/VKDefines.h>
#include <VicKit/VKError.h>
#include <VicKit/VKMatch.h>
#include <VicKit/VKMatchmaker.h>
#include <VicKit/VKNotificationBanner.h>
#include <VicKit/VKPlayer.h>
#include <VicKit/VKLocalPlayer.h>

// Achievement/Leaderboard
#include <VicKit/VKScore.h>
#include <VicKit/VKLeaderboard.h>
#include <VicKit/VKAchievement.h>
#include <VicKit/VKAchievementDescription.h>
#include <VicKit/VKVicDataViewController.h>

class VicKitSystem {
public :
	static void initialize(const TxString & instanceName = "default");
	static void destroy();
};

// Not supported yet.
/*
#include <VicKit/VKPublicConstants.h>
#include <VicKit/VKPublicProtocols.h>
#include <VicKit/VKSession.h>
#include <VicKit/VKSessionError.h>
#include <VicKit/VKTurnBasedMatchmakerViewController.h>
#include <VicKit/VKTurnBasedMatch.h>
#include <VicKit/VKFriendRequestComposeViewController.h>
#include <VicKit/VKLeaderboardViewController.h>
#include <VicKit/VKPeerPickerController.h>
*/

#endif /* __O_VICKIT_H__ */
