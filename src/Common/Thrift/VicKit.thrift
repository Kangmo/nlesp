namespace cpp VicKit
namespace java com.thxsoft.vds.thrift

/* The User Id. starts from 1, increases monotonously for each user */
typedef string UID
typedef i64    ContextID
typedef i64    Timestamp
typedef i64    MessageID
typedef i64    VersionNumber
typedef i64    VoteCount

enum ErrorCode {
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
}

enum DetailedErrorCode {
   VKErrorDetailUserNotFound = 1001,
   VKErrorDetailInvalidUserIdentifierFormat = 1002,
   VKErrorDetailFriendIsRequester = 1003, 
   VKErrorDetailEmailNotFound = 1004,
   VKErrorDetailInvalidPassword = 1005, 
   VKErrorDetailEmptyEmailOnUserProfile = 1006, 
   VKErrorDetailEmptyPasswordOnUserProfile = 1007, 
   VKErrorDetailEmptyUserIdentifierOnUserProfile = 1008, 
   VKErrorDetailEmailAlreadyExists = 1009, 
   VKErrorDetailUnableToUpdateOtherUserProfile = 1010, 
   VKErrorDetailUnableToChangeEmail = 1011, 
   VKErrorDetailInvalidContextIdentifier = 1012, 
   VKErrorDetailContextNotFound = 1013, 
   VKErrorDetailExceedMaxUsers = 1014, 
   VKErrorDetailTooLowClientVersion = 1015
}

struct ErrorDetail
{
   1: DetailedErrorCode detailedCode,
   2: string            detailedMessageFormat,
   3: list<string>      detailedMessageArgs
}

/* An error descriptor which is embedded in all responses.
   Server code of the service fills in the descriptor if there is any error.
*/
struct ErrorDesc
{
   1: ErrorCode  code,
   2: string     message,
   3: ErrorDetail detail
}

struct UserProfile {
   1: UID    uid,
   2: string email,
   3: string encryptedPassword,
   4: string name,
   5: string statusMessage,
   6: binary photo
}

struct ReqAuthenticateUser {
   1: string email,
   2: string encryptedPassword,
   3: VersionNumber clientVersion
}

struct AuthSignature {
   1: UID    uid,
   2: string signature
}

struct ReqSendMessage {
   1: ContextID cid,
   2: binary    message 
}

/* Request messages whose message ID is from [startMessageID,endMessageID) 
   startMessageID is inclusive, endMessageID is exclusive.
   if startMessageID is -1, server decides the number of messages
   if endMessageID is -1, receive up to the most recent message.
*/ 
struct ReqPullMessages {
   /* Request context data whose timestamp is greater than the given time */ 
   1: MessageID startMessageID
   2: MessageID stopMessageID
}

enum MessageType {
   /* An invitation to a context such as asking to play multi-play game together */ 
   MT_CONTEXT_INVITATION = 1,
   /* A request to become a friend. */
   MT_FRIEND_REQUEST = 2,
   /* Personal message like messages in facebook. */
   MT_PERSONAL_MESSAGE = 3,
   /* Data sent via a context. */
   MT_CONTEXT_MESSAGE = 4
}

/* If ContextData.dataType is DT_CONTEXT_INVITATION,
   ContextData.message has serialized binary data of the following struct. */
struct ContextInvitationMessage {
   /* list of UIDs who are invited to the context */
   1: list<UID> playersToInvite
}

/* HBase will serialize each message with this thrift struct */
struct ContextMessageContent {
   1: ContextID    senderContextId,
   2: MessageID    senderMessageId,
   3: UID          senderUID,
   4: Timestamp    sentTime,
   5: MessageType  messageType,
   6: binary       message
}

struct ContextMessage {
   1: ContextMessageContent messageContent
   2: MessageID    messageID,
   3: VoteCount    commentCount,
   4: VoteCount    likeCount,
   5: VoteCount    dislikeCount
}

/* structs starting with "Res" means response to a request */
struct ResAuthenticateUser
{
   1: ErrorDesc     error,
   2: UserProfile   userProfile,
   3: AuthSignature authSignature
} 

struct ResCreateUserProfile
{
   1: ErrorDesc     error,
   2: UID           createdUserId 
}

struct ResUpdateUserProfile
{
   1: ErrorDesc     error
}

struct ResLoadUserProfiles
{
   1: ErrorDesc          error,
   2: list<UserProfile>  userProfiles 
}

struct ResLoadFriendProfiles
{
   1: ErrorDesc          error,
   2: list<UserProfile>  friendProfiles 
}

struct ResLoadFriendUIDs
{
   1: ErrorDesc     error,
   2: list<UID>     friendUIDs
}

struct ResRequestFriend
{
   1: ErrorDesc     error,
   2: UserProfile   friendProfile 
}

struct ResCancelFriend
{
   1: ErrorDesc     error,
   2: UserProfile   canceldFriendProfile 
}

struct ResSearchUsers
{
   1: ErrorDesc         error,
   2: list<UserProfile> userProfiles
}

struct ResCreateContext
{
   1: ErrorDesc     error,
   2: ContextID     createdContextId 
}

struct ResSendMessage
{
   1: ErrorDesc     error,
   2: MessageID     createdMessageId
}

struct ResPullMessages {
   1: ErrorDesc            error,
   /* The maximum message ID in the dataList. 
      Use this in the next pullData request by setting (maxMessageID+1) 
      to PullDataRequest.startMessageID 
   */
   2: MessageID            maxMessageID,
   3: list<ContextMessage> messageList
}


enum MessageEvaluationType {
   Like = 1,
   Dislike = 2,
   CancelLike = 3,
   CancelDislike = 4
}


/* A struct used to store as the value of reply. */ 
struct MessageReplyContent {
   /* No timestamp, because it is search key(column qualifier) in the vds_service_context_messages table. */
   1: i64        authorUserId, // The ID of the user who wrote the reply.
   2: binary     reply         // The reply message 
}


struct ReqEvalMessage {
   1: ContextID             contextID,
   2: MessageID             messageID,
   3: MessageEvaluationType type
}

struct ResEvalMessage {
   1: ErrorDesc error,
}

struct ReqCommentMessage {
   1: ContextID             contextID,
   2: MessageID             messageID,
   3: binary                comment
}

struct ResCommentMessage {
   1: ErrorDesc error
}


struct ReqLoadComments {
   1: ContextID senderContextID, // The message sender's "InBox" context ID
   2: MessageID senderMessageID  // The ID of the message in the sender's "InBox" context.
}

struct MessageComment {
   1: UID          commenterUID,
   2: Timestamp    commentTime,
   3: binary       comment
}

struct ResLoadComments {
   1: ErrorDesc error,
   2: ContextID senderContextID,   
   3: MessageID senderMessageID,  
   4: list<MessageComment> commentList
}

struct ResLoadFollowerUIDs
{
   1: ErrorDesc     error,
   2: list<UID>     followerUIDs
}

// The fields to store for each context when a context is serialized on the client side.
struct ClientContextData
{
   1: list<UID> playerUIDs
}

// The mapping from ContextID to ClientContextData to serialize on the client side.
struct ClientContextMap
{
   1: map<ContextID, ClientContextData> contextMap
}

service VicDataService {
   /*
      For all requests except authenticateUser, we need to pass the AuthSignature returned by a successful call of authenticateUser as the first argument. 
      For authenticateUser, just pass a dummy object(C++) or null(Java) to the sig parameter. 
   */
   ResAuthenticateUser    authenticateUser   ( 1: AuthSignature sig, 2: ReqAuthenticateUser authReq ),
   ResCreateUserProfile   createUserProfile  ( 1: AuthSignature sig, 2: UserProfile profile ), 
   ResUpdateUserProfile   updateUserProfile  ( 1: AuthSignature sig, 2: UserProfile profile ), 
   ResLoadUserProfiles    loadUserProfiles   ( 1: AuthSignature sig, 2: list<UID> uids ), 
   ResLoadFriendProfiles  loadFriendProfiles ( 1: AuthSignature sig, 2: UID uid ), 
   // List of followers
   ResLoadFriendUIDs      loadFriendUIDs     ( 1: AuthSignature sig, 2: UID uid ),
   ResRequestFriend       requestFriend      ( 1: AuthSignature sig, 2: UID uid ), 
   ResCancelFriend        cancelFriend       ( 1: AuthSignature sig, 2: UID uid ), 
   ResSearchUsers         searchUserByEmail  ( 1: AuthSignature sig, 2: string email ),
   ResCreateContext       createContext      ( 1: AuthSignature sig, 2: list<UID> uids ),
   ResSendMessage         sendMessage        ( 1: AuthSignature sig, 2: ReqSendMessage  req ),
   oneway void            sendOnewayMessage  ( 1: AuthSignature sig, 2: ReqSendMessage  req ),
   ResPullMessages        pullMessages       ( 1: AuthSignature sig, 2: ReqPullMessages req ),
   // SNS only : Like or dislike a message
   ResEvalMessage         evalMessage        ( 1: AuthSignature sig, 2: ReqEvalMessage  req ),
   // SNS only : Add a comment to a message
   ResCommentMessage      commentMessage     ( 1: AuthSignature sig, 2: ReqCommentMessage  req ),
   // SNS only : Get the list of comments for a message
   ResLoadComments         loadComments      ( 1: AuthSignature sig, 2: ReqLoadComments  req ),
   // SNS only : Get the list of UIDs of people who follow the person with the given uid.
   ResLoadFollowerUIDs    loadFollowerUIDs   ( 1: AuthSignature sig, 2: UID uid ) 
}

