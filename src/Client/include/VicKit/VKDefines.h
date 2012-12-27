/*
 *  VKDefines.h
 */
#ifndef __O_VK_DEFINES_H__
#define __O_VK_DEFINES_H__ (1)

#ifndef VK_EXTERN
#ifdef __cplusplus
#define VK_EXTERN   extern "C" 
#else
#define VK_EXTERN   extern 
#endif
#endif

#define	VK_EXTERN_CLASS	

#ifndef VK_EXTERN_WEAK
#define VK_EXTERN_WEAK  VK_EXTERN
#endif

#endif /* __O_VK_DEFINES_H__ */