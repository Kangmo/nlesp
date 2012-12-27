#ifndef __O_BASEMENT_H__ 
#define __O_BASEMENT_H__ (1)

#include <tr1/memory>
#include <vector>
#include <string>
#include <sstream>

#include <VicKit/VKError.h>

typedef std::string TxString;

typedef std::vector<TxString> TxStringArray ;

typedef unsigned long long VKUInt64;
typedef long long          VKInt64;
typedef unsigned int       VKUInt32;
typedef int                VKInt32;

class TxData {

private:
	// dataHolder_ holds data only if the object was initialized with TxData(std::string)
    std::string        dataHolder_;
    unsigned long long timestamp_;
    void *   data_;
    size_t   length_;
public :    
    TxData() {
        data_   = NULL;
        length_ = 0;
        timestamp_ = 0;
    };

    void initWithDataHolder(const std::string & dataHolder)
    {
        dataHolder_ = dataHolder;

        data_   = (void*)dataHolder_.data();
		length_ = dataHolder_.length() ;
    }

	TxData(const std::string & dataHolder)
    {
		initWithDataHolder(dataHolder);
        timestamp_ = 0;
    };

    TxData(const TxData & srcData ) {
    	std::string dataHolder((const char*)srcData.bytes(), srcData.length());
		initWithDataHolder(dataHolder);
        timestamp_ = srcData.timestamp();
    };

    TxData(void * data, unsigned int length)
    {
        data_   = data;
        length_ = length;
        timestamp_ = 0;
    };

    TxData(unsigned long long timestamp, void * data, unsigned int length)
    {
        data_   = data;
        length_ = length;
        timestamp_ = timestamp;
    };

    inline unsigned long long timestamp() const {
    	return timestamp_;
    };

    inline void setTimestamp(unsigned long long timestamp) {
        timestamp_ = timestamp;
    }

    inline void * bytes() const {
    	// BUGBUG throw error instead of assertion. assert macro does not do anything in release mode.
    	//assert(data_);
    	return data_;
    };
    inline size_t length() const {
    	// BUGBUG throw error instead of assertion. assert macro does not do anything in release mode.
    	//assert(length_ > 0);
    	return length_;
    };
};

typedef TxData TxImage;


class TxTimeInterval {
};

class VKTimestamp {
};

class VKRange {
};

class VKDialog {
};

#endif
