//
//  AsyncRequester.h
//  Tilemap
//
//  Created by Kangmo Kim on 12. 8. 9..
//
//

#ifndef __Tilemap__AsyncRequester__
#define __Tilemap__AsyncRequester__

#include <iostream>

#include "VKDefines.h"

//#include <tr1/functional>
//#include <memory>

#include <boost/function.hpp>
#include <boost/shared_ptr.hpp>

#include <thrift/concurrency/ThreadManager.h>
#include <thrift/protocol/TBinaryProtocol.h>
#include <thrift/transport/TBufferTransports.h>
#include <thrift/concurrency/PosixThreadFactory.h>
//#include <concurrency/BoostThreadFactory.h>
#include <thrift/transport/TSocket.h>

#include <VicKit/Basement.h>
#include "../Thrift/gen-cpp/VicDataService.h"

using namespace ::apache::thrift::protocol;
using namespace ::apache::thrift::transport;
using namespace ::apache::thrift::concurrency;

template <typename ResponseT> class InternalCompletionHandler  {
public :
	virtual ~InternalCompletionHandler() {}
	virtual void onComplete(const ResponseT & response) = 0;
};

template<typename RequestT, typename ResponseT>  class RequestingTask: public Runnable {
public :
    typedef boost::function<void(VicKit::VicDataServiceClient &, ResponseT &, const VicKit::AuthSignature &, const RequestT & )> request_func_t;
    typedef InternalCompletionHandler<ResponseT> completion_handler_t;

    // AuthSignature is the signature we get from server after a successful authentication.
    // After the authentication, we need to pass the AuthSignature for each request to the server.
    RequestingTask(request_func_t                requestFunc,
    		       const VicKit::AuthSignature & authSignature,
                   const RequestT              & request,
                   boost::shared_ptr<completion_handler_t> completionHandler ) {
        requestFunc_       = requestFunc;
    	authSignature_     = authSignature;
        request_           = request;
        completionHandler_ = completionHandler;
    };
    
    void run() {
        boost::shared_ptr<TSocket> socket(new TSocket(ESP_SERVER_IP, ESP_SERVER_PORT));
        boost::shared_ptr<TTransport> transport(new TFramedTransport(socket));
        boost::shared_ptr<TProtocol> protocol(new TBinaryProtocol(transport));

        transport->open();

        VicKit::VicDataServiceClient client(protocol);
        
        ResponseT response;
        
        requestFunc_(client, response, authSignature_, request_);
        
        transport->close();
        
        if (completionHandler_.get() != NULL)
        {
        	completionHandler_->onComplete(response);
        }
    };
    
private :
    request_func_t         requestFunc_;
    VicKit::AuthSignature  authSignature_;
    RequestT               request_;
    boost::shared_ptr<completion_handler_t>  completionHandler_;
};

//------------------------------------------------------------------------------
// The request task definition for service functions whose return type is void
//------------------------------------------------------------------------------

class InternalCompletionHandler_ReturnVoid  {
public :
	virtual ~InternalCompletionHandler_ReturnVoid() {}
	virtual void onComplete() = 0;
};

template<typename RequestT>  class RequestingTask_ReturnVoid: public Runnable {
public :
    typedef boost::function<void(VicKit::VicDataServiceClient &, const VicKit::AuthSignature &, const RequestT & )> request_func_t;
    typedef InternalCompletionHandler_ReturnVoid completion_handler_t;

    RequestingTask_ReturnVoid(
    		       request_func_t                requestFunc,
    		       const VicKit::AuthSignature & authSignature,
                   const RequestT              & request,
                   boost::shared_ptr<completion_handler_t> completionHandler ) {

        requestFunc_       = requestFunc;
    	authSignature_     = authSignature;
        request_           = request;
        completionHandler_ = completionHandler;
    };

    void run() {
        boost::shared_ptr<TSocket> socket(new TSocket(ESP_SERVER_IP, ESP_SERVER_PORT));
        boost::shared_ptr<TTransport> transport(new TFramedTransport(socket));
        boost::shared_ptr<TProtocol> protocol(new TBinaryProtocol(transport));

        transport->open();

        VicKit::VicDataServiceClient client(protocol);

        requestFunc_(client, authSignature_, request_);

        transport->close();

        if (completionHandler_.get() != NULL)
        {
            completionHandler_->onComplete();
        }
    };

private :
    request_func_t         requestFunc_;
    VicKit::AuthSignature  authSignature_;
    RequestT               request_;

    boost::shared_ptr<completion_handler_t>  completionHandler_;
};

/*
//------------------------------------------------------------------------------
// The request task definition for service functions whose return type is the Response object
//------------------------------------------------------------------------------

template<typename RequestT, typename ResponseT>  class RequestingTask_ReturnResponse: public Runnable {
public :
    typedef boost::function<ResponseT(VicKit::VicDataServiceClient &, const VicKit::AuthSignature &, const RequestT & )> request_func_t;
    typedef InternalCompletionHandler<ResponseT> completion_handler_t;

    RequestingTask_ReturnResponse(
    		       request_func_t                requestFunc,
    		       const VicKit::AuthSignature & authSignature,
                   const RequestT              & request,
                   boost::shared_ptr<completion_handler_t> completionHandler ) {

        requestFunc_ = requestFunc;
    	authSignature_ = authSignature;
        request_ = request;
        completionHandler_ = completionHandler;
    };

    void run() {
        boost::shared_ptr<TSocket> socket(new TSocket(ESP_SERVER_IP, ESP_SERVER_PORT));
        boost::shared_ptr<TTransport> transport(new TFramedTransport(socket));
        boost::shared_ptr<TProtocol> protocol(new TBinaryProtocol(transport));

        transport->open();

        VicKit::VicDataServiceClient client(protocol);

        ResponseT response = requestFunc_(client, authSignature_, request_);

        transport->close();

        // BUGBUG send TxError * if any error happens
        if (completionHandler_.get() != NULL)
        {
        	completionHandler_->onComplete(response, NULL);
        }
    };

private :
    request_func_t         requestFunc_;
    VicKit::AuthSignature  authSignature_;
    RequestT               request_;

    boost::shared_ptr<completion_handler_t>  completionHandler_;
};

*/

class AsyncRequester {
public :
    AsyncRequester()
    {
        // using thread pool with maximum 15 threads to handle incoming requests
        threadManager_ = ThreadManager::newSimpleThreadManager(15);
        boost::shared_ptr<PosixThreadFactory> threadFactory = boost::shared_ptr<PosixThreadFactory>(new PosixThreadFactory());
        threadManager_->threadFactory(threadFactory);
        threadManager_->start();
    }

    void Request( boost::shared_ptr<Runnable> requestingTask )
    {
        // BUGBUG : check timeout, expiretime.
        threadManager_->add(requestingTask, 0, 0);
    }
private :
    boost::shared_ptr<ThreadManager> threadManager_;

};

extern AsyncRequester theAsyncRequester;

#endif /* defined(__Tilemap__AsyncRequester__) */
