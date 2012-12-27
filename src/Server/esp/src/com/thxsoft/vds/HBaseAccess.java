package com.thxsoft.vds;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseAccess {
	private static Configuration config_ = HBaseConfiguration.create();

	
	// The maximum number of messages to pull per pullMessages request.
    public static int MAX_PULL_MESSAGE_COUNT = 200;

	// The maximum message Id
	public static long MAX_MESSAGE_ID = Long.MAX_VALUE;
	// The max value of signed int (64 bit).
	public static long MESSAGE_ID_FOR_META_CONTEXT = 0;
	
	private static byte[] concat(byte[] a, byte[] b)
	{
		byte[] result = new byte[a.length + b.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		return result;
	}

	private static byte[] concat(byte[] a, byte[] b, byte[] c)
	{
		byte[] result = new byte[a.length + b.length + c.length];
		System.arraycopy(a, 0, result, 0, a.length);
		System.arraycopy(b, 0, result, a.length, b.length);
		System.arraycopy(c, 0, result, a.length + b.length, c.length);
		return result;
	}

	////////////////////////////////////////////////////////////////////////////////////////////
	public static String TN_IDENTIFIERS = "vds_identifiers";
	private static HTable HT_IDENTIFIERS = null;
	public static HTable T_IDENTIFIERS() {
		try {
			if (HT_IDENTIFIERS == null)
				HT_IDENTIFIERS = new HTable(config_, TN_IDENTIFIERS);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return HT_IDENTIFIERS;
	}
	////////////////////////////////////////////////////////////////////////////////////////////
	// Row Key - only one row with key "1"
	public static byte[] rkIdentifiers()
	{
		return Bytes.toBytes("1");
	}
	// Column Families
	public static byte[] CF_IDENTIFIERS = Bytes.toBytes("i");

	// In vds_identifier_singleton
	public static byte[] CQ_I_NEXT_TENANT_ID     = Bytes.toBytes("NextTenantId");
	public static byte[] CQ_I_NEXT_SERVICE_ID    = Bytes.toBytes("NextServiceId");
	public static byte[] CQ_I_NEXT_USER_ID       = Bytes.toBytes("NextUserId");
	public static byte[] CQ_I_NEXT_GUEST_USER_ID = Bytes.toBytes("NextGuestUserId");


	////////////////////////////////////////////////////////////////////////////////////////////
	public static String TN_TENANTS              = "vds_tenants";
	private static HTable HT_TENANTS = null;
	public static HTable T_TENANTS() {
		try {
			if (HT_TENANTS == null)
				HT_TENANTS = new HTable(config_, TN_TENANTS);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return HT_TENANTS;
	}
	////////////////////////////////////////////////////////////////////////////////////////////
	// Row Key
	public static byte[] rkTenants(long tenantId)
	{
		return Bytes.toBytes(tenantId);
	}
	// Column Families
	public static byte[] CF_TENANTS_PROFILE = Bytes.toBytes("p");
	
	////////////////////////////////////////////////////////////////////////////////////////////
	public static String TN_TENANT_SERVICES  = "vds_tenant_services";
	////////////////////////////////////////////////////////////////////////////////////////////
	// Row Keys
	public static byte[] rkTenantServices(long tenantId, long serviceId)
	{
	
		return concat(Bytes.toBytes(tenantId), Bytes.toBytes(serviceId));
	}
	// Column Families 
	public static byte[] CF_TENANT_SERVICES_PROFILE = Bytes.toBytes("p");
	
	////////////////////////////////////////////////////////////////////////////////////////////
	public static String TN_SERVICE_USERS  = "vds_service_users";
	////////////////////////////////////////////////////////////////////////////////////////////
	// Row Keys
	public static byte[] rkServiceUsers(long serviceId, long userId)
	{
		return concat(Bytes.toBytes(serviceId), Bytes.toBytes(userId));
	}
	// Column Families 
	public static byte[] CF_SERVICE_USERS_STATISTICS = Bytes.toBytes("s");
	
	// Column Qualifiers
	public static byte[] CQ_SU_USAGE       = Bytes.toBytes("u");
	public static byte[] CQ_SU_STAR_POINTS = Bytes.toBytes("s");
	public static byte[] CQ_SU_COMMENT     = Bytes.toBytes("c");
	
	////////////////////////////////////////////////////////////////////////////////////////////
	public static String TN_USERS  = "vds_users";
	private static HTable HT_USERS = null;
	public static HTable T_USERS() {
		try {
			if (HT_USERS == null)
				HT_USERS = new HTable(config_, TN_USERS);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return HT_USERS;
	}	
	////////////////////////////////////////////////////////////////////////////////////////////
	// Row Keys
	public static byte[] rkUsers(long userId)
	{
		return Bytes.toBytes(userId);
	}
	// Column Families 
	public static byte[] CF_USERS_PROFILE = Bytes.toBytes("p");
	// Column Qualifiers
	public static byte[] CQ_U_PROFILE     = Bytes.toBytes("p");
	// Column Families 
	public static byte[] CF_USERS_FRIENDS = Bytes.toBytes("f");
	// Column Qualifiers
	public static byte[] cqUsersFriends(long friendUserId)
	{
		return Bytes.toBytes(friendUserId);
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	public static String TN_USERID_BY_EMAIL  = "vds_userid_by_email";
	private static HTable HT_USERID_BY_EMAIL = null;
	public static HTable T_USERID_BY_EMAIL() {
		try {
			if (HT_USERID_BY_EMAIL == null)
				HT_USERID_BY_EMAIL = new HTable(config_, TN_USERID_BY_EMAIL);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return HT_USERID_BY_EMAIL;
	}
	
	////////////////////////////////////////////////////////////////////////////////////////////
	// Row Keys
	public byte[] rkUserIdByEmail(String email)
	{
		return Bytes.toBytes(email);
	}
	// Column Families 
	public static byte[] CF_UBE_USER    = Bytes.toBytes("u");
	
	// Column Qualifiers
	public static byte[] CQ_UBE_USER_ID = Bytes.toBytes("u");
	
	////////////////////////////////////////////////////////////////////////////////////////////
	public static String TN_SERVICE_CONTEXT_MESSAGES  = "vds_service_context_messages";
	private static HTable HT_SERVICE_CONTEXT_MESSAGES = null;
	public static HTable T_SERVICE_CONTEXT_MESSAGES() {
		try {
			if (HT_SERVICE_CONTEXT_MESSAGES == null)
				HT_SERVICE_CONTEXT_MESSAGES = new HTable(config_, TN_SERVICE_CONTEXT_MESSAGES);
		} catch (IOException e) {
			e.printStackTrace();
		}
    	return HT_SERVICE_CONTEXT_MESSAGES;
	}
		
	////////////////////////////////////////////////////////////////////////////////////////////
	// Row Key for ServiceContextMessages table.
	// Format : serviceId(string)-serviceContextId(string)-messageId(signed int - 64bit)
	// Why only messageId has signed int type? We need to do range scan with the key.
	// For serviceId and serviceContextId, we don't need to do range scan, so we use string to store them in compact size.
	// "1" in string format takes 1 bytes but 1 in 'signed int - 64bit' format takes 8 bytes.
	public static byte[] rkServiceContextMessages(long serviceId, long serviceContextId, long messageId)
	{
		// Indicate that the row is the meta row for a service.
		// Why don't just use 0 here? 
		// We need to keep the meta row of a service adjacent to the context row recently created.
		// This is to try to make getting the next context Id from the meta row of a service to happen in the same region server with the newly created context row.
		if ( serviceContextId <= 0)
			serviceContextId = Long.MAX_VALUE;
		
		return concat(Bytes.toBytes(serviceId), Bytes.toBytes(serviceContextId), Bytes.toBytes(messageId));
	}
	// Row Key of the meta row of the service context.
	public static byte[] rkServiceContext(long serviceId, long serviceContextId)
	{
		return rkServiceContextMessages(serviceId, serviceContextId, MESSAGE_ID_FOR_META_CONTEXT);
	}
	// Row Key of the meta row of the service.
	public static byte[] rkService(long serviceId)
	{
		return rkServiceContextMessages(serviceId, 0, 0);
	}
	
	public static long getMessageId(byte[] rowKeyOfServiceContextMessages)
	{
		// convert the last 8 bytes to a long value within total 24 bytes of byte stream.
		long messageId = Bytes.toLong(rowKeyOfServiceContextMessages, 16, 8);
		return messageId;
	}
	
	// Column Families 
	public static byte[] CF_SCM_SERVICE         = Bytes.toBytes("s");
	
	// Column Qualifiers
	public static byte[] CQ_SCM_NEXT_CONTEXT_ID = Bytes.toBytes("c");

	// Column Families 
	public static byte[] CF_SCM_CONTEXT         = Bytes.toBytes("c");

	// TODO : Make sure the value of NextMessageId is adjacent to values of CF_SCM_MESSAGE even though there are many recipients in this context.
	// Column Qualifiers
	public static long   SCM_META_CONTEXT_ROW_RECIPIENT_USER_ID = 0L;
	public static byte[] CQ_SCM_NEXT_MESSAGE_ID = Bytes.toBytes(SCM_META_CONTEXT_ROW_RECIPIENT_USER_ID);
	public static byte[] cqServiceContextRecipient(long uid)
	{
		return Bytes.toBytes(uid);
	}
	public static long getServiceContextRecipientId(byte[] rawUserId)
	{
		return Bytes.toLong(rawUserId);
	}
	
	// Column Families 
	public static byte[] CF_SCM_MESSAGE         = Bytes.toBytes("m");
	
	// Column Qualifiers
	public static byte[] CQ_SCM_MESSAGE         = Bytes.toBytes("m");
	public static byte[] CQ_SCM_LIKE_COUNT      = Bytes.toBytes("l");
	public static byte[] CQ_SCM_DISLIKE_COUNT   = Bytes.toBytes("d");
	public static byte[] CQ_SCM_COMMENT_COUNT   = Bytes.toBytes("c");

	
	// Column Families 
	public static byte[] CF_SCM_LIKERS          = Bytes.toBytes("l");
	
	// Column Qualifiers
	public static byte[] cqMessageLikers(long likerUserId)
	{
		return Bytes.toBytes(likerUserId);
	}
	
	// Column Families 
	public static byte[] CF_SCM_DISLIKERS       = Bytes.toBytes("d");
	
	// Column Qualifiers
	public static byte[] cqMessageDislikers(long dislikerUserId)
	{
		return Bytes.toBytes(dislikerUserId);
	}

	
	// Column Families
	public static byte[] CF_SCM_REPLY           = Bytes.toBytes("r");
	
	// Column Qualifiers
	public static byte[] cqServiceContextMessageReply(long timestamp)
	{
		return Bytes.toBytes(timestamp);
	}


	// Column Families
	public static byte[] CF_SCM_PROPAGATED_MESSAGES = Bytes.toBytes("p");
	
	// Column Qualifiers
	public static byte[] cqServiceContextMessagePropagatedMessage(long userId)
	{
		return Bytes.toBytes(userId);
	}
	////////////////////////////////////////////////////////////////////////////////////////////
    public long getIncreasedLong(HTable counterTable, byte[] rowKey, byte[] columnFamily, byte[] columnQualifier, long amount) 
    		throws org.apache.thrift.TException 
    {
    	try {
        	long nextGuestId = counterTable.incrementColumnValue(
        							rowKey, 
        							columnFamily,
        							columnQualifier, 
        							amount);

        	return nextGuestId;
    	} catch (IOException e) {
    		throw new org.apache.thrift.TException("IO Error while incrementing a column value. Table : " + counterTable.getTableName() + ", RowKey : " + Bytes.toString(rowKey) + ", Column Familiy : " + Bytes.toString(columnFamily) + ", Column Qualifier : " + Bytes.toString(columnQualifier) + ", Amount : "+amount);
    	}
    }

	
	public long getNextId(byte[] identifier) throws org.apache.thrift.TException
	{
    	try {
    		HTable counterTable = T_IDENTIFIERS();

        	long nextId = counterTable.incrementColumnValue(
        			                rkIdentifiers(), 
        							CF_IDENTIFIERS,
        							identifier, 
        							1);
        	return nextId;
    	} catch (IOException e) {
    		// BUGBUG : remove stack trace.
    		e.printStackTrace();
    		throw new org.apache.thrift.TApplicationException("IOException in getNextId with "+ identifier); 
    	}
	}

	public void put(HTable table, byte[] rowKey, byte[] columnFamily, byte[] columnQualifier, byte[] value)  throws org.apache.thrift.TException
	{
		try {
			Put put = new Put(rowKey);

			put.add( columnFamily, columnQualifier, value);
		
			table.put(put);
		} catch (IOException e) {
    		throw new org.apache.thrift.TApplicationException("IOException while executing put. "+
		                                                      "Table:"+table.getTableName()+
		                                                      ", ColumnFamily:" + Bytes.toString(columnFamily)+
		                                                      ", columnQualifier:" + Bytes.toString(columnQualifier) ); 
		}
	}

	// In Scala : class PutRequest(byte[] columnFamily, byte[] columnQualifier, byte[] value);
	
	class PutRequest
	{
		private byte[] columnFamily_;
		private byte[] columnQualifier_; 
		private byte[] value_;
		
		public PutRequest( byte[] columnFamily, byte[] columnQualifier, byte[] value)
		{
			columnFamily_ = columnFamily;
			columnQualifier_ = columnQualifier;
			value_ = value;
		}
		
		public byte[] columnFamily() {
			return columnFamily_;
		}
		public byte[] columnQualifier() {
			return columnQualifier_;
		}
		public byte[] value() {
			return value_;
		}
	}
	public void put(HTable table, byte[] rowKey, List<PutRequest> putRequests)  throws org.apache.thrift.TException
	{
		try {
			Put put = new Put(rowKey);
			for ( PutRequest r : putRequests)
			{
				put.add( r.columnFamily(), r.columnQualifier(), r.value() );
			}
		
			table.put(put);
		} catch (IOException e) {
    		throw new org.apache.thrift.TApplicationException("IOException while executing multiple put requests. "+
		                                                      "Table:"+table.getTableName() ); 
		}
	}

	
	public void delete(HTable table, byte[] rowKey, byte[] columnFamily, byte[] columnQualifier)  throws org.apache.thrift.TException
	{
		try {

			Delete delete = new Delete(rowKey);

			delete.deleteColumns(columnFamily, columnQualifier);

			table.delete(delete);
		} catch (IOException e) {
    		throw new org.apache.thrift.TApplicationException("IOException while executing delete. "+
		                                                      "Table:"+table.getTableName()+
		                                                      ", ColumnFamily:" + Bytes.toString(columnFamily) +
		                                                      ", columnQualifier:" + Bytes.toString(columnQualifier) ); 
		}
	}
	
	// return a value for a given key.
	// return null if no row with the given rowKey is found.
	public byte[] get(HTable table, byte[] rowKey, byte[] columnFamily, byte[] columnQualifier)  throws org.apache.thrift.TException
	{
		try {
    		Get get = new Get(rowKey);
    		get.addColumn(columnFamily, columnQualifier);
    		
    		Result result = table.get(get);
    		
    		if (result.isEmpty())
    		{
    			return null;
    		}
    		
    		byte[] value = result.getValue(columnFamily, columnQualifier);
    		return value;
		} catch (IOException e) {
    		throw new org.apache.thrift.TApplicationException("IOException while executing get. "+
		                                                      "Table:"+table.getTableName()+
		                                                      ", ColumnFamily:"+Bytes.toString(columnFamily)+
		                                                      ", columnQualifier:"+Bytes.toString(columnQualifier) ); 
		}
	}

	// return list of values for list of row keys.
	// return an empty List if no row with the given rowKeys are found.
	public List<byte[]> get(HTable table, Collection<byte[]> rowKeys, byte[] columnFamily, byte[] columnQualifier)  throws org.apache.thrift.TException
	{
		List<byte[]> results = new ArrayList<byte[]>();
		try {
			
			List<Get> gets = new ArrayList<Get>();

			for (byte[] rowKey : rowKeys)
			{
	    		Get get = new Get(rowKey);
	    		get.addColumn(columnFamily, columnQualifier);
	    		gets.add(get);
			}
    		
    		Result[] hbaseResults = table.get(gets);
    		for (Result r : hbaseResults ) {
	    		if (! r.isEmpty())
	    		{
	    			byte[] value;
		    		value = r.getValue(columnFamily, columnQualifier);
	    			results.add( value );
	    		}
    		}
		} catch (IOException e) {
    		throw new org.apache.thrift.TApplicationException("IOException while executing get. "+
		                                                      "Table:"+table.getTableName()+
		                                                      ", ColumnFamily:"+Bytes.toString(columnFamily)+
		                                                      ", columnQualifier:"+Bytes.toString(columnQualifier)); 
		}
		
		return results;
	}
}
