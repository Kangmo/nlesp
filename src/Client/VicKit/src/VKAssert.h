#ifndef _O_VK_ASSERT_H_
#define _O_VK_ASSERT_H_ (1)

#ifdef __APPLE__
    #include "TargetConditionals.h"
#endif

// Windows
#if defined(_MSC_VER)

#  include <crtdefs.h>

#  ifdef  __cplusplus
extern "C" {
#  endif

_CRTIMP void __cdecl _wassert(_In_z_ const wchar_t * _Message, _In_z_ const wchar_t *_File, _In_ unsigned _Line);

#  ifdef  __cplusplus
}
#  endif

#  define tx_assert(_Expression) (void)( (!!(_Expression)) || (_wassert(_CRT_WIDE(#_Expression), _CRT_WIDE(__FILE__), __LINE__), 0) )

// iOS
#elif defined(__IPHONE_OS_VERSION_MIN_REQUIRED)
// iOS-always turn on assertion
#  undef NDEBUG
#  include <assert.h>
#  define VK_ASSERT assert
#  define NDEBUG (1)

#elif TARGET_OS_MAC

#  include <assert.h>
#  define VK_ASSERT assert

// Linux
#else

#ifdef	_ASSERT_H

# undef	_ASSERT_H
# undef	assert
# undef __ASSERT_VOID_CAST

# ifdef	__USE_GNU
#  undef assert_perror
# endif

#endif /* assert.h	*/

#define	_ASSERT_H	1
#include <features.h>

#if defined __cplusplus && __GNUC_PREREQ (2,95)
# define __ASSERT_VOID_CAST static_cast<void>
#else
# define __ASSERT_VOID_CAST (void)
#endif

#ifndef _ASSERT_H_DECLS
#define _ASSERT_H_DECLS
__BEGIN_DECLS

/* This prints an "Assertion failed" message and aborts.  */
extern void __assert_fail (__const char *__assertion, __const char *__file,
			   unsigned int __line, __const char *__function)
     __THROW __attribute__ ((__noreturn__));

/* Likewise, but prints the error text for ERRNUM.  */
extern void __assert_perror_fail (int __errnum, __const char *__file,
				  unsigned int __line,
				  __const char *__function)
     __THROW __attribute__ ((__noreturn__));


/* The following is not at all used here but needed for standard
   compliance.  */
extern void __assert (const char *__assertion, const char *__file, int __line)
     __THROW __attribute__ ((__noreturn__));


__END_DECLS
#endif /* Not _ASSERT_H_DECLS */


# define VK_ASSERT(expr)				\
  ((expr)								\
   ? __ASSERT_VOID_CAST (0)						\
   : __assert_fail (__STRING(expr), __FILE__, __LINE__, __ASSERT_FUNCTION))

# ifdef	__USE_GNU
#  define assert_perror(errnum)						\
  (!(errnum)								\
   ? __ASSERT_VOID_CAST (0)						\
   : __assert_perror_fail ((errnum), __FILE__, __LINE__, __ASSERT_FUNCTION))
# endif

/* Version 2.4 and later of GCC define a magical variable `__PRETTY_FUNCTION__'
   which contains the name of the function currently being defined.
   This is broken in G++ before version 2.6.
   C9x has a similar variable called __func__, but prefer the GCC one since
   it demangles C++ function names.  */
# if defined __cplusplus ? __GNUC_PREREQ (2, 6) : __GNUC_PREREQ (2, 4)
#   define __ASSERT_FUNCTION	__PRETTY_FUNCTION__
# else
#  if defined __STDC_VERSION__ && __STDC_VERSION__ >= 199901L
#   define __ASSERT_FUNCTION	__func__
#  else
#   define __ASSERT_FUNCTION	((__const char *) 0)
#  endif
# endif

#endif // _MSC_VER

#endif //_O_TX_ASSERT_H_

