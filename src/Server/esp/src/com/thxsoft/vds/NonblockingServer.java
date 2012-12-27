
package com.thxsoft.vds;
import com.thxsoft.vds.thrift.*;

import org.apache.thrift.transport.*;
import org.apache.thrift.server.*;


public class NonblockingServer implements Runnable{
    public void run() {
        try {
        	int port = 9090;
            TNonblockingServerTransport serverTransport = new TNonblockingServerSocket(port);
            VicDataService.Processor<VicDataServiceImpl> processor = new VicDataService.Processor<VicDataServiceImpl>(new VicDataServiceImpl());
 
            TServer server = new TNonblockingServer(new TNonblockingServer.Args(serverTransport).
                    processor(processor));
            System.out.println("Starting server on port " + port + " ...");
            
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }
 
    public static void startServerThread() {
        NonblockingServer srv = new NonblockingServer();
        new Thread(srv).start();
    }
}
