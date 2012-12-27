/usr/local/hbase/bin/hbase shell <<-EOF

#
disable 'vds_identifiers'
drop 'vds_identifiers'
#
# Row Key : '1' - only one row.
#
# Contains a row having monotonously increasing key values used in other tables
#
create 'vds_identifiers', 'i'
incr 'vds_identifiers', '1', 'i:NextTenantId', 1
incr 'vds_identifiers', '1', 'i:NextServiceId', 1
incr 'vds_identifiers', '1', 'i:NextUserId', 1
incr 'vds_identifiers', '1', 'i:NextGuestUserId', 1

disable 'vds_tenants'
drop 'vds_tenants'
#
# Row Key : TenantId
#
# Contains a row for a tenant
#
# p(rofile): tenant "P"rofile
#
create 'vds_tenants', 'p'


disable 'vds_tenant_services'
drop 'vds_tenant_services'
#
# Row Key : TenantId-ServiceId
#
# Contains a row for a service
#
# p(rofile): service "P"rofile
#
create 'vds_tenant_services', 'p'


disable 'vds_service_users'
drop 'vds_service_users'
#
# Row Key : ServiceId-UserId
#
# Contains a row for usage statistics of a service by a user
#
# s(tatistics): column family for user statistics for the service
#    column qualifiers :
#    u(sage) : how many times the user "U"sed the service
#    s(star points): the star points evaluated by the user
#    c(omment) : comment on the service. Need to have a separate table that is sorted by time to show comments of a service sorted by time
create 'vds_service_users', 's'


disable 'vds_users'
drop 'vds_users'
#
# Row Key : UserId
#
# Contains a row for a user
#
# p(rofile): column family for user "P"rofile
#    column qualifier : p
#    value : User Profile
#
# f(riends): column family for user friends
#    column qualifier : UserId of the user friend
#    value: The time/date that the user requested friendship. The friendship is not mutual, it's more like one-way following on Twitter.
#
create 'vds_users', 'p', 'f'

disable 'vds_service_context_messages'
drop 'vds_service_context_messages'
#
# row key : ServiceId-ServiceContextId-MessageId
#
# Contains a row for a message sent to a context of a service
# Contains a special row for each service with ServiceContextId=max(signed int 64)
# Contains a special row for each context with MessageId=max(signed int 64). It contains description of the context itself.
# - Why? When a new message is sent to a context, we first need to get the next MessageId for it, and then put a the context of the message with the MessageId.
# - We don't want to involve two ResgionServers whenver we send a message to a context.
# - To try our best to make these multiple operations happen in the same 
#   RegionServer of HBase,  We keep the descriptor of a context in 
#   vds_service_context_messages table by using special MessageId, 
#   +9,223,372,036,854,775,807 which is the max(signed int 64). 
# - Why use MessageId=max(signed int 64) for the context descriptor? 
#   We need to keep the descriptor record adjacent to the latest message in the
#   table, which has the maximum MessageId in it. By that way we have higher 
#   probability to have the context descriptor and the last appended record 
#   in the same in-memory buffer in a RegionServer. 
#
#   We can reduce the overhead of getting 
#   the next MessageId from the context descriptor whenever we append a 
#   new message into a context.
#
# - The ServiceId monotonously increases from 1.
# - For each ServiceId, ServiceContextId monotonously increases from 1.
# - For each ServiceId-ServiceContextId pair, MessageId monotonously increases from 1. 
#
#
# s(service): column family for the description of the "S"ervice
#    Only the row with ServiceContextId=max(signed int 64) has the description of the service. It has MessageId=0.
#    column qualifier : c(ontextId)
#    value : The next contextId to use in the service
#
# c(ontext): column family for the description of the "C"ontext
#    Only the row with MessageId=max(signed int 64) has the description of the context.
#    column qualifier : UserId(>0) ( one column for each participant in the context )
#    value : The list of description to the active endpoints that receives data from this context.
#
#    column qualifier : 0
#    value : The next messageId to use in the context 
#     
# m(essage): column family for the content of the "M"essage
#    column qualifier : m(essage)
#    value : the content of the "M"essage 
#    column qualifier : l(ike)
#    value : the number of votes that users "L"iked  
#    column qualifier : d(islike)
#    value : the number of votes that users "D"isliked  
#    column qualifier : c(comment count)
#    value : the number of comments to the message
#
# l(ikers) : column family for the list of users who liked a message
#    column qualifier : UserId of the user who liked the message
#    value : the long timestamp(System.currentTimeMillis() in server) that the user liked it.
#
# d(islikers) : column family for the list of users who disliked a message
#    column qualifier : UserId of the user who disliked the message
#    value : the long timestamp(System.currentTimeMillis() in server) that the user disliked it.
#
#    Why separate likers and dislikers into two column families?
#       - We need to get the list of likers quickly to show them in the UI. 
#       - If we mix UIDs of likers and dislikers in one column, we need to do full scan get separate list of likers or dislikers. 
#
# r(reply): column family for list of "R"eplies to the message. (Used by SNS case only)
#    column qualifier : long timestamp(System.currentTimeMillis() in server) - the time when the reply was received by the Thrift server.
#    value : the content of the reply message
#   
#    Replies are stored only in the original context. The content of the reply is not propagated to corresponding messages in "InBox" contexts of recipients of the origianl context.
#    However, the count of replies are propagated to corresponding messages in "InBox" contexts of recipients of the original context.
#    Why? By design, SNS client will show the number of replies, not the content of the replies in the list of news feeds for a user.
#    To see replies, the user has to open up a separate dialog, which in turn request replies stored in the original context.
#
#    Open Issue : In case a user has a huge number of followers, we have two issues
#      1) The number of replies should be propagated to a massive number of messages in the "InBox" contexts of all followers. 
#      2) To see the actual reply text, huge number of followers will have to access the same HBase Region Server which has the message in the original context.
#
# p(ropagated messages) : column family for keeping the list of pair of propagated contextID and message ID.
#    column qualifier : UserId(>0) ( one column for each participant in the context )
#    value : The message ID of the propagated message in the "InBox" of the user.
#    Why is this required?
#       When a user posts a reply on the message in the original context, the count of replies should be propagated to every corresponding message for each participants of the original context.
#
create 'vds_service_context_messages', 's', 'c', 'm', 'l', 'd', 'r', 'p'


disable 'vds_userid_by_email'
drop 'vds_userid_by_email'
#
# Row Key : email
#
# Contains a row for each user mapping from email to UserId 
#
# u: column family for user id
#    column qualifier : u
#    value : UserId
#   
create 'vds_userid_by_email', 'u' 
EOF
