#ifndef __O_VK_NOTIFICATIONBANNER_H__
#define __O_VK_NOTIFICATIONBANNER_H__ (1)

#include <VicKit/Basement.h>

// Asynchronously shows a notification banner like the one used for Game Center Welcome Back message.
// If a banner is already being displayed, additional banners will be shown in sequence. Use this to notify the user of game events, high scores, completed achievements, etc.

class VKNotificationBanner {
    class ShowBannderCompletionHandler {
    public :
        virtual void onCompleteShowBanner() = 0;
    };
	static void showBanner(const TxString & title, const TxString & message, ShowBannderCompletionHandler * handler);
};

#endif /* __O_VK_NOTIFICATIONBANNER_H__ */
