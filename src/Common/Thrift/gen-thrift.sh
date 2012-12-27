/usr/local/bin/thrift --gen cpp:cob_style VicKit.thrift
#rm gen-cpp/ClientService_async_server.skeleton.cpp
#rm gen-cpp/ClientService_server.skeleton.cpp
rm gen-cpp/VicDataService_async_server.skeleton.cpp


for f in VicDataService.h 
do
    sed 's/#include <tr1\/functional>/#include <boost\/functional.hpp>/g' gen-cpp/$f | sed 's/::apache::thrift::TAsyncProcessor/::apache::thrift::async::TAsyncProcessor/g' > gen-cpp/${f}.new
    mv gen-cpp/${f}.new gen-cpp/$f
done


/usr/local/bin/thrift --gen java VicKit.thrift
