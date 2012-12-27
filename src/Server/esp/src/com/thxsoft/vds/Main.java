package com.thxsoft.vds;

import org.apache.thrift.transport.*;
import org.apache.thrift.async.*;
import org.apache.thrift.protocol.*;
import org.apache.thrift.*;


public class Main {
		
    private void invoke() {
/*
                TTransport transport;
                transport = new TSocket("192.168.0.7", 9091);
     
                TProtocol protocol = new TBinaryProtocol(transport);

                VicDataService.Client client = new VicDataService.Client(protocol);
                transport.open();
                RequestGuestUserId req = new RequestGuestUserId("guest100");
                RespondGuestUserId rsp = client.getGuestUserId(req);
                System.out.println("The added user id : " + rsp.uid);
                transport.close();
 */
        	
     	// Start Server Thread.
       	{
       		NonblockingServer.startServerThread();
       	}
        	
       	// Send Request - USe this code when you send any request from server to client.
       	// Ex> Send an invitation for a match to a player.
       	/*
        try {
        	{
                TNonblockingTransport transport = new TNonblockingSocket("127.0.0.1", 9091);
                TAsyncClientManager clientManager = new TAsyncClientManager();
                TProtocolFactory protocolFactory = new TBinaryProtocol.Factory();
                VicDataService.AsyncClient client = new VicDataService.AsyncClient(protocolFactory, clientManager, transport);

                
                org.apache.thrift.async.AsyncMethodCallback<VicDataService.AsyncClient.getGuestUserId_call> callback = 
                		new org.apache.thrift.async.AsyncMethodCallback<VicDataService.AsyncClient.getGuestUserId_call>()  {
              	  public void onComplete(VicDataService.AsyncClient.getGuestUserId_call call) {
              		  try {
                            System.out.println("onComplete");
                  		  RespondGuestUserId respond = call.getResult();
                            System.out.println("The added user id : " + respond.uid);
              		  } catch ( TException e ) {
                            e.printStackTrace();
              		  }
              	  }

              	  public void onError(Exception exception) {
              		  System.err.println("[onError] getGuestUserId :" + exception );
              		  exception.printStackTrace();
              	  }
                };
                
                new VicDataService.AsyncClient(protocolFactory, clientManager, transport).getGuestUserId(new RequestGuestUserId("guest101"), callback);

                System.out.println("Waiting...");
                while(true);
//                System.out.println("Waiting Done.");
            }
        } catch (TTransportException e) {
            e.printStackTrace();
        } catch (TException e) {
            e.printStackTrace();
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        */
            
    }
    
    public static void main(String[] args) {
    	Main c = new Main();
        c.invoke();
    }
}

