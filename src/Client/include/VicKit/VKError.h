/*
 *  VKError.h
 */

#ifndef __O_VK_ERROR_H__
#define __O_VK_ERROR_H__ (1)

#include <VicKit/Basement.h>
#include <VicKit/VKDefines.h>

// TODO : Investigate what this is.
VK_EXTERN_WEAK std::string VKErrorDomain;

typedef enum {
   VKSuccess = 0,
   VKErrorUnknown = 1,
   VKErrorCancelled = 2,
   VKErrorCommunicationsFailure = 3,
   VKErrorUserDenied = 4,
   VKErrorInvalidCredentials = 5,
   VKErrorNotAuthenticated = 6,
   VKErrorAuthenticationInProgress = 7,
   VKErrorInvalidPlayer = 8,
   VKErrorScoreNotSet = 9,
   VKErrorParentalControlsBlocked = 10,
   VKErrorPlayerStatusExceedsMaximumLength = 11,
   VKErrorPlayerStatusInvalid = 12,
   VKErrorMatchRequestInvalid = 13,
   VKErrorUnderage = 14,
   VKErrorGameUnrecognized = 15,
   VKErrorNotSupported = 16,
   VKErrorInvalidParameter = 17,
   VKErrorUnexpectedConnection = 18,
   VKErrorChallengeInvalid = 19,
   VKErrorTurnBasedMatchDataTooLarge = 20,
   VKErrorTurnBasedTooManySessions = 21,
   VKErrorTurnBasedInvalidParticipant = 22,
   VKErrorTurnBasedInvalidTurn = 23,
   VKErrorTurnBasedInvalidState = 24
} VKErrorCode;

class VKError {
private:
	VKErrorCode errorCode_;
	std::string errorMessage_;
public :
	VKError(const VKErrorCode errorCode, const std::string & errorMessage)
	{
		errorCode_ =  errorCode;
		errorMessage_ = errorMessage;
	}
    const std::string & errorMessage() const {
        return errorMessage_;
    }
    const VKErrorCode errorCode() const {
    	return errorCode_;
    }
};

#endif /* __O_VK_ERROR_H__ */
