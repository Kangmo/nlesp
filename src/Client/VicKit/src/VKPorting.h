/*
 * VKPorting.h
 *
 *  Created on: Nov 25, 2012
 *      Author: ladmin
 */

#ifndef VKPORTING_H_
#define VKPORTING_H_

#ifdef _WIN64
#   define PLATFORM_WIN64 (1)
#elif _WIN32
#   define PLATFORM_WIN32 (1)
#elif __APPLE__
    #include "TargetConditionals.h"
    #if TARGET_OS_IPHONE
#     define PLATFORM_IOS (1)
#     define PLATFORM_IOS_IPHONE (1)
    #elif TARGET_IPHONE_SIMULATOR
#     define PLATFORM_IOS (1)
#     define PLATFORM_IOS_SIMULATOR (1)
    #elif TARGET_OS_MAC
#     define PLATFORM_OSX (1)
        // Other kinds of Mac OS
    #else
#error "Unsupported Apple Platform"
    #endif
#elif __unix // all unices not caught above
#     define PLATFORM_UNIX (1)
#elif __posix
#     define PLATFORM_POSIX (1)
#else
#     define PLATFORM_LINUX (1)
#endif


#endif /* VKPORTING_H_ */
