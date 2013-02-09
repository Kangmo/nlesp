#ifndef __O_VK_INTERNAL_H__
#define __O_VK_INTERNAL_H__ (1)

#include "VicKit_types.h"
#include "VKAssert.h"
#include <VicKit/Basement.h>
#include <string>
#include <boost/thread.hpp>

inline std::string ConvertToThrift(const TxData & data)
{
	std::string s;

	s.assign( (char*) data.bytes(), data.length() );

	return s;
}
inline TxData ConvertFromThrift(const std::string & data)
{
	TxData dataUsingString(data);

	return dataUsingString;
}

class VKInternal{
public :
	VKInternal();
	~VKInternal();

	static inline void setInstanceName(const std::string & instanceName)
	{
		instanceName_ = instanceName;
	}

	static inline const std::string & instanceName()
	{
		return instanceName_;
	}

	static inline void setAuthSignature(const VicKit::AuthSignature & authSignature) {
		authSignature_ = authSignature;
	};

	static inline const VicKit::AuthSignature & authSignature() {
		return authSignature_;
	};

	static inline const VicKit::UID & myUID() {
		return authSignature_.uid;
	};

	// Start the thread that periodically calls pullData function to request data to server.
	static void StartPullMessagesThread();

	// Stop the PullMessagesThread
	static void StopPullMessagesThread();

	// Join the PullMessagesThread
	static void JoinPullMessagesThread();

	// [ThankyouSoft only] In all contexts, get all data whose timestamp is after the given one.
	static const VicKit::MessageID pullMessages(VicKit::MessageID startMessageId, VicKit::MessageID stopMessageId);

private:

	static void PullMessagesThreadFunc();

	static VicKit::AuthSignature authSignature_;
	static std::string instanceName_;
	static bool stopThread_;

	static boost::thread * poolMessageThread_;
};

// send pullData request to the server every 1 second
#define PULL_THREAD_SLEEP_MS (1000)

// When the user is not authenticated yet,sleep this amount of ms to recheck if the user was authenticated.
#define PULL_THREAD_CHECK_AUTHENTICATION_SLEEP_MS (1000)

#endif /* __O_VK_INTERNAL_H__ */
