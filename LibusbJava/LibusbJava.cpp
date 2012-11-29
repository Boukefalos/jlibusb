/*!	\file
 * 	\brief Java libusb1.0 wrapper
 *
 * 	\copyright	2010-2012 NTB Interstate University of Applied Sciences of Technology Buchs
 * 				This libary is covered by the LGPL, read LGPL.txt for details
 *
 * 	\author 	Roger Millischer <roger.millischer at ntb.ch> (original author)
 * 	\author		Ueli Niederer <ueli.niederer at ntb.ch> (modifications and enhancements)
 *
 * 	\todo Currently all the pointers and handles passed to and received from libusb are coded
 * 		  in long values to get them in JVM.
 * 		  Clebert Suconic ( http://planet.jboss.org/post/pointers_in_jni_c ) suggests a
 * 		  possibly more elegant way to deal with this issue:
 *			Make use of the ByteBuffer-Class delivered by with the native IO package.
 *			(java.nio). As this class is made to store the start pointer to native buffers
 *			we could create a "ByteBuffer" of length 0 where the start address represents
 *			e.g. the handle.  This can  be done using the following JNI Call:
 *			  env->NewDirectByteBuffer(myPointer, 0); // size = 0, you don't want anyone to
 *			                                          // change the data you are pointing to...
 */

/********************************************************************************************
 *
 *		Includes
 *
 *******************************************************************************************/
#include <jni.h>
#include <cstddef>
#include <string>
#include <cstdlib>
#include <ctime>

#ifdef DO_UNIT_TEST
#	include <test/CuTest.h>

#	define TEST_CASE(name)			static void name(CuTest *tc)
#endif

#include <string.h>

#include "libusb.h"
#include "LibusbJava.h"

//#define DEBUGON

/********************************************************************************************
 *
 *		Globals
 *
 *******************************************************************************************/
/* global flag for loading all class, method and field ID references*/
int java_references_loaded = 0;

/* if > 0 an LibusbJava specific error string is set*/
char *libusbJavaError = NULL;
/********************************************************************************************
 *
 *		Macros
 *
 *******************************************************************************************/
/* to set and clear LibusbJava specific errors*/
#define setLibusbJavaError(error) libusbJavaError = error
#define clearLibusbJavaError() libusbJavaError = NULL

/********************************************************************************************
 *
 *		Prototyp and define for callback-function
 *
 *******************************************************************************************/
static void LIBUSB_CALL transfer_callback(struct libusb_transfer *transfer);

static void
		LIBUSB_CALL fd_added_callback(int fd, short events, void *user_data);

static void LIBUSB_CALL fd_removed_callback(int fd, void *user_data);

/********************************************************************************************
 *
 *      Local helper functions
 *
 *******************************************************************************************/
static __inline int						ReferencesLoad(JNIEnv *env);
static __inline void					ReferencesUnload(JNIEnv *env);
static __inline jbyteArray 	JNICALL 	to_byteArray(JNIEnv *env, const void *data, size_t len);
static __inline void 		JNICALL 	ThrowIfUnsuccessful(JNIEnv *env, int libusb_result);
static __inline void 		JNICALL		ThrowLibusbError(JNIEnv *env, jint code);

/********************************************************************************************
 *
 *		References
 *
 *******************************************************************************************/
/* class references*/
static jclass usb_devClazz, usb_devDescClazz, usb_confDescClazz, usb_intClazz,
		usb_intDescClazz, usb_epDescClazz, usb_cb_clazz, usb_pollfd_clazz;

/* method ID references*/
static jmethodID usb_devMid, usb_devDescMid, usb_confDescMid, usb_intMid,
		usb_intDescMid, usb_epDescMid, usb_transfer_cb_Mid,
		usb_fd_added_cb_Mid, usb_fd_removed_cb_Mid, usb_pollfd_Mid;

/* field ID references*/
/* usb_device*/
static jfieldID usb_devFID_next, usb_devFID_prev, usb_devFID_filename, usb_devFID_bus,
		usb_devFID_descriptor, usb_devFID_config, usb_devFID_devnum,
		usb_devFID_num_children, usb_devFID_children, usb_devFID_devStructAddr;
/* usb_deviceDescriptor */
static jfieldID usb_devDescFID_bLength, usb_devDescFID_bDescriptorType,
		usb_devDescFID_bcdUSB, usb_devDescFID_bDeviceClass,
		usb_devDescFID_bDeviceSubClass, usb_devDescFID_bDeviceProtocol,
		usb_devDescFID_bMaxPacketSize0, usb_devDescFID_idVendor,
		usb_devDescFID_idProduct, usb_devDescFID_bcdDevice,
		usb_devDescFID_iManufacturer, usb_devDescFID_iProduct,
		usb_devDescFID_iSerialNumber, usb_devDescFID_bNumConfigurations;
/* usb_configurationDescriptor*/
static jfieldID usb_confDescFID_bLength, usb_confDescFID_bDescriptorType,
		usb_confDescFID_wTotalLength, usb_confDescFID_bNumInterfaces,
		usb_confDescFID_bConfigurationValue, usb_confDescFID_iConfiguration,
		usb_confDescFID_bmAttributes, usb_confDescFID_MaxPower,
		usb_confDescFID_interface_, usb_confDescFID_extra,
		usb_confDescFID_extralen;
/* usb_interface*/
static jfieldID usb_intFID_altsetting, usb_intFID_num_altsetting;
/* usb_intDesc*/
static jfieldID usb_intDescFID_bLength, usb_intDescFID_bDescriptorType,
		usb_intDescFID_bInterfaceNumber, usb_intDescFID_bAlternateSetting,
		usb_intDescFID_bNumEndpoints, usb_intDescFID_bInterfaceClass,
		usb_intDescFID_bInterfaceSubClass, usb_intDescFID_bInterfaceProtocol,
		usb_intDescFID_iInterface, usb_intDescFID_endpoint,
		usb_intDescFID_extra, usb_intDescFID_extralen;
/* usb_endpointDescriptor*/
static jfieldID usb_epDescFID_bLength, usb_epDescFID_bDescriptorType,
		usb_epDescFID_bEndpointAddress, usb_epDescFID_bmAttributes,
		usb_epDescFID_wMaxPacketSize, usb_epDescFID_bInterval,
		usb_epDescFID_bRefresh, usb_epDescFID_bSynchAddress,
		usb_epDescFID_extra, usb_epDescFID_extralen;
/*Libusb_pollfd*/
static jfieldID usb_pollfdFID_fd, usb_pollfdFID_events;

#ifdef DO_UNIT_TEST
		static struct TestContext
		{
			JNIEnv *env;
		}test_context = { NULL };

	#	define	TEST_CONTEXT()	JNIEnv *env = test_context.env
#endif

/*!	\brief Structure holding all the global information needed. */
static struct {
	struct {
		int onLoadCalled;
		int refs_loaded;

		struct {
			struct {
				jclass usb_devClazz;
			}Usb_Device;

			struct {
				jclass usb_devDescClazz;
			}Usb_Device_Descriptor;

			struct {
				jclass usb_confDescClazz;
			}Usb_Config_Descriptor;
		}objs;
	}jni;
}info = { { 0 } };

/********************************************************************************************
 *
 *		Library Events
 *
 *******************************************************************************************/

/*!	\brief	The VM calls JNI_OnLoad when the native library is loaded (for example, through
 * 			System.loadLibrary).
 *
 * 	\see 	http://docs.oracle.com/javase/6/docs/technotes/guides/jni/spec/invocation.html#JNI_OnLoad
 *
 *	\return The JNI version needed by the native library (use constants as JNI_VERSION_X_Y).
 */
jint JNI_OnLoad(JavaVM *vm, void *reserved)
{
	JNIEnv* env = NULL;

	info.jni.onLoadCalled = -1;

	if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_1) != JNI_OK) {
		return -1;
	}

	ReferencesLoad(env);

	return JNI_VERSION_1_1;
}

#if defined(DO_UNIT_TEST)
	TEST_CASE(JNI_OnLoad_test)
	{
		TEST_CONTEXT();

		CuAssert(tc, "correct initialization", info.jni.onLoadCalled == 0);
		info.jni.onLoadCalled=10;

		/* Load the LibusbJava1 class to force the initialization of the library */
		jclass clazz = env->FindClass("ch/ntb/inf/libusbJava/LibusbJava1");
		CuAssert(tc, "ch/ntb/inf/libusbJava/LibusbJava1 loaded", clazz != NULL);

		CuAssert(tc, "JNI_OnLoad was executed", info.jni.onLoadCalled != 0);
		env->DeleteLocalRef(clazz);

		/* As garbage collection is not necessarily run after freeing a reference
		 * and there is no way to force the run of GC, we can't test this here. */
//		CuAssert(tc, "JNI_OnUnload was executed", info.jni.onLoadCalled == 0);
	}
#endif

/*!	\brief 	The VM calls JNI_OnUnload when the class loader containing the native library is
 *          garbage collected.
 *
 *			This function can be used to perform cleanup operations. Because this function is
 *			called in an unknown context (such as from a finalizer), the programmer should be
 *			conservative on using Java VM services, and refrain from arbitrary Java call-backs.
 *
 * 	\see 	http://docs.oracle.com/javase/6/docs/technotes/guides/jni/spec/invocation.html#JNI_OnUnload
 */
void JNI_OnUnload(JavaVM *vm, void *reserved)
{
	JNIEnv* env = NULL;

	if (vm->GetEnv(reinterpret_cast<void **>(&env), JNI_VERSION_1_1) == JNI_OK) {
		ReferencesUnload(env);
	}

	info.jni.onLoadCalled = 0;
}

/********************************************************************************************
 *
 *		Methods
 *
 *******************************************************************************************/

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_set_debug
 * Signature: (JI)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1set_1debug( JNIEnv *env, jclass obj, jlong ctx, jint level) {
	clearLibusbJavaError();
	libusb_context *context = NULL;
	if (ctx) {
		context = (libusb_context*) ((unsigned long) ctx);
	}
	libusb_set_debug(context, level);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_init
 * Signature: ()J
 ********************************************************************************************/
JNIEXPORT jlong JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1init( JNIEnv *env, jclass obj) {
	int res;
	libusb_context *context;
	clearLibusbJavaError();

	res = libusb_init(&context);

	#ifdef DEBUGON
		printf("res = %i \n", res);
		printf("env = %p\n", env);
		printf("context = %p\n", &context);
	#endif

	if (res != 0)
	{
		ThrowLibusbError(env, res);
		return 0;
	}
	else
	{
		return (jlong) context;
	}
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_exit
 * Signature: (J)V
 *******************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1exit(JNIEnv *env, jclass obj, jlong ctx) {
	libusb_context *context = NULL;
	clearLibusbJavaError();
	if (ctx) {
		context = (libusb_context*) ((unsigned long) ctx);
	}
	libusb_exit(context);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_get_device_list
 * Signature: (J)Lch/ntb/inf/libusbJava/Usb_Device;
 ********************************************************************************************/
JNIEXPORT jobject JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1get_1device_1list( JNIEnv *env, jclass obj, jlong ctx) {
	libusb_device **devs;
	libusb_context *context = (ctx != 0) ? (libusb_context*)ctx : NULL;
	ssize_t cnt;
	int res, a, i;
	uint8_t c, h, e;

	/* only load class references, method and field ID  once*/
	if (!java_references_loaded) {
		/* find classes and field ids*/
#ifdef DEBUGON
		printf("load references starts\n");
#endif

		/*usb_device*/
		jobject devClazz = env->FindClass("ch/ntb/inf/libusbJava/Usb_Device");//returns a local reference
		usb_devClazz = (jclass)env->NewGlobalRef(devClazz); // make it global to avoid class unloading and therefore
		                                                    // invalidating the references obtained.
		if (usb_devClazz == NULL) {
			return NULL; /* exception thrown */
		}
		usb_devMid = env->GetMethodID(usb_devClazz, "<init>", "()V");
		if (usb_devMid == NULL) {
			return NULL;
		}

		usb_devFID_next = env->GetFieldID(usb_devClazz, "next", "Lch/ntb/inf/libusbJava/Usb_Device;");
		usb_devFID_prev = env->GetFieldID(usb_devClazz, "prev", "Lch/ntb/inf/libusbJava/Usb_Device;");
		usb_devFID_filename = env->GetFieldID(usb_devClazz, "filename", "Ljava/lang/String;");
		usb_devFID_bus = env->GetFieldID(usb_devClazz, "bus", "Lch/ntb/inf/libusbJava/Usb_Bus;");
		usb_devFID_descriptor = env->GetFieldID(usb_devClazz, "descriptor", "Lch/ntb/inf/libusbJava/Usb_Device_Descriptor;");
		usb_devFID_config = env->GetFieldID(usb_devClazz, "config", "[Lch/ntb/inf/libusbJava/Usb_Config_Descriptor;");
		usb_devFID_devnum = env->GetFieldID(usb_devClazz, "devnum", "B");
		usb_devFID_num_children = env->GetFieldID(usb_devClazz, "num_children", "B");
		usb_devFID_children = env->GetFieldID(usb_devClazz, "children", "Lch/ntb/inf/libusbJava/Usb_Device;");
		usb_devFID_devStructAddr = env->GetFieldID(usb_devClazz, "devStructAddr", "J");

#ifdef DEBUGON
		printf("usb_device references loaded\n");
#endif
		/* usb_device_descriptor*/
		jobject devDescClazz = env->FindClass("ch/ntb/inf/libusbJava/Usb_Device_Descriptor");//returns a local reference
		usb_devDescClazz = (jclass)env->NewGlobalRef(devDescClazz);//make it global
		if (usb_devDescClazz == NULL) {
			return NULL; /* exception thrown */
		}
		usb_devDescMid = env->GetMethodID(usb_devDescClazz, "<init>", "()V");
		if (usb_devDescMid == NULL) {
			return NULL;
		}
		usb_devDescFID_bLength = env->GetFieldID(usb_devDescClazz, "bLength", "B");
		usb_devDescFID_bDescriptorType = env->GetFieldID(usb_devDescClazz, "bDescriptorType", "B");
		usb_devDescFID_bcdUSB = env->GetFieldID(usb_devDescClazz, "bcdUSB", "S");
		usb_devDescFID_bDeviceClass = env->GetFieldID(usb_devDescClazz, "bDeviceClass", "B");
		usb_devDescFID_bDeviceSubClass = env->GetFieldID(usb_devDescClazz, "bDeviceSubClass", "B");
		usb_devDescFID_bDeviceProtocol = env->GetFieldID(usb_devDescClazz, "bDeviceProtocol", "B");
		usb_devDescFID_bMaxPacketSize0 = env->GetFieldID(usb_devDescClazz, "bMaxPacketSize0", "B");
		usb_devDescFID_idVendor = env->GetFieldID(usb_devDescClazz, "idVendor", "S");
		usb_devDescFID_idProduct = env->GetFieldID(usb_devDescClazz, "idProduct", "S");
		usb_devDescFID_bcdDevice = env->GetFieldID(usb_devDescClazz, "bcdDevice", "S");
		usb_devDescFID_iManufacturer = env->GetFieldID(usb_devDescClazz, "iManufacturer", "B");
		usb_devDescFID_iProduct = env->GetFieldID(usb_devDescClazz, "iProduct", "B");
		usb_devDescFID_iSerialNumber = env->GetFieldID(usb_devDescClazz, "iSerialNumber", "B");
		usb_devDescFID_bNumConfigurations = env->GetFieldID(usb_devDescClazz, "bNumConfigurations", "B");


#ifdef DEBUGON
		printf("usb_device_descriptor references loaded\n");
#endif
		/* usb_configuration_descriptor*/
		jobject confDescClazz = env->FindClass("ch/ntb/inf/libusbJava/Usb_Config_Descriptor");//returns a local reference
		usb_confDescClazz = (jclass)env->NewGlobalRef(confDescClazz);//make it global
		if (usb_confDescClazz == NULL) {
			return NULL; /* exception thrown */
		}
		usb_confDescMid = env->GetMethodID(usb_confDescClazz, "<init>", "()V");
		if (usb_confDescMid == NULL) {
			return NULL;
		}

		usb_confDescFID_bLength = env->GetFieldID(usb_confDescClazz, "bLength", "B");
		usb_confDescFID_bDescriptorType = env->GetFieldID(usb_confDescClazz, "bDescriptorType", "B");
		usb_confDescFID_wTotalLength = env->GetFieldID(usb_confDescClazz, "wTotalLength", "S");
		usb_confDescFID_bNumInterfaces = env->GetFieldID(usb_confDescClazz, "bNumInterfaces", "B");
		usb_confDescFID_bConfigurationValue = env->GetFieldID(usb_confDescClazz, "bConfigurationValue", "B");
		usb_confDescFID_iConfiguration = env->GetFieldID(usb_confDescClazz, "iConfiguration", "B");
		usb_confDescFID_bmAttributes = env->GetFieldID(usb_confDescClazz, "bmAttributes", "B");
		usb_confDescFID_MaxPower = env->GetFieldID(usb_confDescClazz, "MaxPower", "B");
		usb_confDescFID_interface_ = env->GetFieldID(usb_confDescClazz, "interface_", "[Lch/ntb/inf/libusbJava/Usb_Interface;");
		usb_confDescFID_extra = env->GetFieldID(usb_confDescClazz, "extra", "[B");
		usb_confDescFID_extralen = env->GetFieldID(usb_confDescClazz, "extralen", "I");
#ifdef DEBUGON
		printf("usb_configuration_descriptor references loaded\n");
#endif
		/* usb_interface*/
		jobject intClazz = env->FindClass("ch/ntb/inf/libusbJava/Usb_Interface");//returns a local reference
		usb_intClazz = (jclass)env->NewGlobalRef(intClazz);//make it global
		if (usb_intClazz == NULL) {
			return NULL; /* exception thrown */
		}
		usb_intMid = env->GetMethodID(usb_intClazz, "<init>", "()V");
		if (usb_intMid == NULL) {
			return NULL;
		}
		usb_intFID_altsetting = env->GetFieldID(usb_intClazz, "altsetting", "[Lch/ntb/inf/libusbJava/Usb_Interface_Descriptor;");
		usb_intFID_num_altsetting = env->GetFieldID(usb_intClazz, "num_altsetting", "I");
#ifdef DEBUGON
		printf("usb_interface references loaded\n");
#endif
		/* usb_interface_descriptor*/
		jobject intDescClazz = env->FindClass( "ch/ntb/inf/libusbJava/Usb_Interface_Descriptor");//returns a local reference
		usb_intDescClazz = (jclass)env->NewGlobalRef(intDescClazz);//make it global
		if (usb_intDescClazz == NULL) {
			return NULL; /* exception thrown */
		}
		usb_intDescMid = env->GetMethodID(usb_intDescClazz, "<init>", "()V");
		if (usb_intDescMid == NULL) {
			return NULL;
		}
		usb_intDescFID_bLength = env->GetFieldID(usb_intDescClazz, "bLength", "B");
		usb_intDescFID_bDescriptorType = env->GetFieldID(usb_intDescClazz, "bDescriptorType", "B");
		usb_intDescFID_bInterfaceNumber = env->GetFieldID(usb_intDescClazz, "bInterfaceNumber", "B");
		usb_intDescFID_bAlternateSetting = env->GetFieldID(usb_intDescClazz, "bAlternateSetting", "B");
		usb_intDescFID_bNumEndpoints = env->GetFieldID(usb_intDescClazz, "bNumEndpoints", "B");
		usb_intDescFID_bInterfaceClass = env->GetFieldID(usb_intDescClazz, "bInterfaceClass", "B");
		usb_intDescFID_bInterfaceSubClass = env->GetFieldID(usb_intDescClazz, "bInterfaceSubClass", "B");
		usb_intDescFID_bInterfaceProtocol = env->GetFieldID(usb_intDescClazz, "bInterfaceProtocol", "B");
		usb_intDescFID_iInterface = env->GetFieldID(usb_intDescClazz, "iInterface", "B");
		usb_intDescFID_endpoint = env->GetFieldID(usb_intDescClazz, "endpoint", "[Lch/ntb/inf/libusbJava/Usb_Endpoint_Descriptor;");
		usb_intDescFID_extra = env->GetFieldID(usb_intDescClazz, "extra", "[B");
		usb_intDescFID_extralen = env->GetFieldID(usb_intDescClazz, "extralen", "I");
#ifdef DEBUGON
		printf("usb_interface_descriptor references loaded\n");
#endif
		/* usb_endpoint_descriptor*/
		jobject epDescClazz = env->FindClass("ch/ntb/inf/libusbJava/Usb_Endpoint_Descriptor");//returns a local reference
		usb_epDescClazz = (jclass)env->NewGlobalRef(epDescClazz);//make it global
		if (usb_epDescClazz == NULL) {
			return NULL; /* exception thrown */
		}
		usb_epDescMid = env->GetMethodID(usb_epDescClazz, "<init>", "()V");
		if (usb_epDescMid == NULL) {
			return NULL;
		}
		usb_epDescFID_bLength = env->GetFieldID(usb_epDescClazz, "bLength", "B");
		usb_epDescFID_bDescriptorType = env->GetFieldID(usb_epDescClazz, "bDescriptorType", "B");
		usb_epDescFID_bEndpointAddress = env->GetFieldID(usb_epDescClazz, "bEndpointAddress", "B");
		usb_epDescFID_bmAttributes = env->GetFieldID(usb_epDescClazz, "bmAttributes", "B");
		usb_epDescFID_wMaxPacketSize = env->GetFieldID(usb_epDescClazz, "wMaxPacketSize", "S");
		usb_epDescFID_bInterval = env->GetFieldID(usb_epDescClazz, "bInterval", "B");
		usb_epDescFID_bRefresh = env->GetFieldID(usb_epDescClazz, "bRefresh", "B");
		usb_epDescFID_bSynchAddress = env->GetFieldID(usb_epDescClazz, "bSynchAddress", "B");
		usb_epDescFID_extra = env->GetFieldID(usb_epDescClazz, "extra", "[B");
		usb_epDescFID_extralen = env->GetFieldID(usb_epDescClazz, "extralen", "I");
#ifdef DEBUGON
		printf("usb_endpoint_descriptor references loaded\n");
#endif
		/*libusb_event*/
		jobject cb_clazz = env->FindClass("ch/ntb/inf/libusbJava/Libusb_event");//returns a local reference
		usb_cb_clazz = (jclass)env->NewGlobalRef(cb_clazz);//make it global
		if (usb_cb_clazz == NULL) {
			printf("load Clazz failed\n");
			return NULL; /*exception thrown*/
		}
		usb_transfer_cb_Mid = env->GetMethodID(usb_cb_clazz, "transferCallback", "()V");
		if (usb_transfer_cb_Mid == NULL) {
			printf("load method transferCallback failed\n");
			return NULL; /* exception thrown */
		}
		usb_fd_added_cb_Mid = env->GetMethodID(usb_cb_clazz, "fdAddedCallback", "(I)V");
		if (usb_fd_added_cb_Mid == NULL) {
			printf("load method fdAddedCallback failed\n");
			return NULL;/* exception thrown */
		}
		usb_fd_removed_cb_Mid = env->GetMethodID(usb_cb_clazz, "fdRemovedCallback", "(I)V");
		if (usb_fd_removed_cb_Mid == NULL) {
			printf("load method fdRemovedCallback failed\n");
			return NULL;/* exception thrown */
		}
#ifdef DEBUGON
		printf("usb_event_descriptor references loaded\n");
#endif
		/*Libusb_pollfd*/
		jobject pollfd_clazz = env->FindClass("ch/ntb/inf/libusbJava/Libusb_pollfd");//returns a local reference
		usb_pollfd_clazz = (jclass)env->NewGlobalRef(pollfd_clazz);//make it global
		if (usb_pollfd_clazz == NULL) {
			return NULL; /*exception thrown*/
		}
		usb_pollfd_Mid = env->GetMethodID(usb_pollfd_clazz, "<init>", "()V");
		if (usb_devMid == NULL) {
			return NULL; /*exception thrown*/
		}
		usb_pollfdFID_fd = env->GetFieldID(usb_pollfd_clazz, "fd", "I");
		usb_pollfdFID_events = env->GetFieldID(usb_pollfd_clazz, "events", "B");
#ifdef DEBUGON
		printf("Libusb_pollfd_descriptor references loaded\n");
#endif
		java_references_loaded = 1;

#ifdef DEBUGON
		printf("libusb_init: Field initialization done \n");
#endif
	}

	/* objects*/
	jobject root_usb_devObj, usb_devObj, usb_devObj_next, usb_devObj_prev,
			usb_devDescObj, usb_confDescObj, usb_intObj, usb_intDescObj,
			usb_epDescObj;

	jobjectArray usb_confDescObjArray, usb_intObjArray, usb_intDescObjArray,
			usb_epDescObjArray;
	clearLibusbJavaError();

	libusb_config_descriptor *conf_desc;

	cnt = libusb_get_device_list(context, &devs);
	if (cnt < 0) {
		setLibusbJavaError("libusb_get_device_list: Error on Memory allocation");
		libusb_free_device_list(devs, 1);
		return NULL;
	}

	/*empty list*/
	if (!cnt) {
		libusb_free_device_list(devs, 1);
		usb_devObj = env->NewObject(usb_devClazz, usb_devMid);
		env->SetByteField(usb_devObj, usb_devFID_devnum, -1);
	}
	usb_devObj = NULL;
	usb_devObj_next = NULL;
	usb_devObj_prev = NULL;
	/* create a new object for every device*/
	for (i = 0; i < cnt; i++) {
#ifdef DEBUGON
		printf("libusb_get_device_list: dev %u \n", i);
#endif
		char filename[3];

		libusb_device_descriptor dev_desc;
		res = libusb_get_device_descriptor(devs[i], &dev_desc);

		if (res) {
			/* this shouldn't happen*/
			printf("\t libusb_get _device_list: dev %u coudn't read Devicedescriptor\n",i);
			continue;
		}
		if (dev_desc.bLength != 18) {
			printf("Corrupt Devicedescriptor dev %d\n", i);
			continue;
		}

		if (!usb_devObj) {
			usb_devObj = env->NewObject(usb_devClazz, usb_devMid);
			if (!usb_devObj) {
				setLibusbJavaError("shared library error: Error NewObject (usb_devObj)");
				return NULL;
			}
			root_usb_devObj = usb_devObj;
		}
		usb_devObj_next = NULL;
		if (i < cnt - 1) {
			usb_devObj_next = env->NewObject(usb_devClazz, usb_devMid);
			if (!usb_devObj_next) {
				setLibusbJavaError("shared library error: Error NewObject (usb_devObj_next)");
				return NULL;
			}
		}
		/*fill the fields of the object*/
		env->SetObjectField(usb_devObj, usb_devFID_next, usb_devObj_next);
		env->SetObjectField(usb_devObj, usb_devFID_prev, usb_devObj_prev);
		sprintf(filename, "%03d", libusb_get_device_address(devs[i]));
		env->SetObjectField(usb_devObj, usb_devFID_filename, env->NewStringUTF(filename));/*like the compatibility Layer*/
		env->SetByteField(usb_devObj, usb_devFID_devnum, libusb_get_device_address(devs[i]));
		env->SetByteField(usb_devObj, usb_devFID_num_children, 0);/*Hardcoded to 0, like the compatibility Layer*/
		env->SetObjectField(usb_devObj, usb_devFID_children, NULL);/*Hardcoded to NULL, like the compatibility Layer*/
		env->SetLongField(usb_devObj, usb_devFID_devStructAddr,	(jlong) devs[i]);

		/*device Descriptor*/
		usb_devDescObj = env->NewObject(usb_devDescClazz, usb_devDescMid);
		if (!usb_devDescObj) {
			setLibusbJavaError("shared library error: Error NewObject (usb_devDescObj)");
			return NULL;
		}
		env->SetByteField(usb_devDescObj, usb_devDescFID_bLength, dev_desc.bLength);
		env->SetByteField(usb_devDescObj, usb_devDescFID_bDescriptorType, dev_desc.bDescriptorType);
		env->SetShortField(usb_devDescObj, usb_devDescFID_bcdUSB, dev_desc.bcdUSB);
		env->SetByteField(usb_devDescObj, usb_devDescFID_bDeviceClass, dev_desc.bDeviceClass);
		env->SetByteField(usb_devDescObj, usb_devDescFID_bDeviceSubClass, dev_desc.bDeviceSubClass);
		env->SetByteField(usb_devDescObj, usb_devDescFID_bDeviceProtocol, dev_desc.bDeviceProtocol);
		env->SetByteField(usb_devDescObj, usb_devDescFID_bMaxPacketSize0, dev_desc.bMaxPacketSize0);
		env->SetShortField(usb_devDescObj, usb_devDescFID_idVendor,	dev_desc.idVendor);
		env->SetShortField(usb_devDescObj, usb_devDescFID_idProduct, dev_desc.idProduct);
		env->SetShortField(usb_devDescObj, usb_devDescFID_bcdDevice, dev_desc.bcdDevice);
		env->SetByteField(usb_devDescObj, usb_devDescFID_iManufacturer,	dev_desc.iManufacturer);
		env->SetByteField(usb_devDescObj, usb_devDescFID_iProduct, dev_desc.iProduct);
		env->SetByteField(usb_devDescObj, usb_devDescFID_iSerialNumber,	dev_desc.iSerialNumber);
		env->SetByteField(usb_devDescObj, usb_devDescFID_bNumConfigurations, dev_desc.bNumConfigurations);
		env->SetObjectField(usb_devDescObj, usb_devFID_descriptor, usb_devDescObj);
		env->SetObjectField(usb_devObj, usb_devFID_descriptor, usb_devDescObj);

		/*configuration descriptor*/
		/*Loop through all of the configurations*/
		usb_confDescObjArray = (jobjectArray) env->NewObjectArray(dev_desc.bNumConfigurations, usb_confDescClazz, NULL);
		if (!usb_confDescObjArray) {
			setLibusbJavaError("shared library error: Error NewOjectArray");
			return NULL;
		}
		for (c = 0; c < dev_desc.bNumConfigurations; c++) {
#ifdef DEBUGON
			printf("\t\tlibusb_get_device_list: configuration %u, Dev: %d \n", c, i);
#endif
			res = libusb_get_config_descriptor(devs[i], c, &conf_desc);
			if (res) {
				continue;
			}
			usb_confDescObj	= env->NewObject(usb_confDescClazz, usb_confDescMid);
			if (!usb_confDescObj) {
				setLibusbJavaError("shared library error: Error NewObject (usb_confDescObj)");
				return NULL;
			}
			env->SetObjectArrayElement(usb_confDescObjArray, c, usb_confDescObj);/*add it to the Array*/
			/*fill the fields*/
			env->SetByteField(usb_confDescObj, usb_confDescFID_bLength, conf_desc->bLength);
			env->SetByteField(usb_confDescObj, usb_confDescFID_bDescriptorType, conf_desc->bDescriptorType);
			env->SetShortField(usb_confDescObj, usb_confDescFID_wTotalLength, conf_desc->wTotalLength);
			env->SetByteField(usb_confDescObj, usb_confDescFID_bNumInterfaces, conf_desc->bNumInterfaces);
			env->SetByteField(usb_confDescObj, usb_confDescFID_bConfigurationValue, conf_desc->bConfigurationValue);
			env->SetByteField(usb_confDescObj, usb_confDescFID_iConfiguration, conf_desc->iConfiguration);
			env->SetByteField(usb_confDescObj, usb_confDescFID_bmAttributes, conf_desc->bmAttributes);
			env->SetByteField(usb_confDescObj, usb_confDescFID_MaxPower, conf_desc->MaxPower);
			env->SetIntField(usb_confDescObj, usb_confDescFID_extralen, conf_desc->extra_length);
			if (conf_desc->extra) {
				jbyteArray jbExtraDesc = to_byteArray(env, conf_desc->extra, conf_desc->extra_length);
				env->SetObjectField(usb_confDescObj, usb_confDescFID_extra, jbExtraDesc);
			} else {
				env->SetObjectField(usb_confDescObj, usb_confDescFID_extra,	NULL);
			}
			/*interface*/
			usb_intObjArray = (jobjectArray) env->NewObjectArray(conf_desc->bNumInterfaces, usb_intClazz, NULL);
			if (!usb_intObjArray) {
				setLibusbJavaError("shared library error: Error NewObjectArray (usb_intObjArray)");
				return NULL;
			}
			for (h = 0; h < conf_desc->bNumInterfaces; h++) {
#ifdef DEBUGON
				printf("\t\t\tlibusb_get_device_list: interface %u \n", h);
#endif

				if (conf_desc->interface == NULL) {
					/* this shouldn't happen*/
					printf("(*conf_desc)->usb_interface == NULL");
					continue;
				}
				usb_intObj = env->NewObject(usb_intClazz, usb_intMid);
				if (!usb_intObj) {
					setLibusbJavaError("shared library error: Error NewObject (usb_intObj)");
					return NULL;
				}
				env->SetObjectArrayElement(usb_intObjArray, h, usb_intObj);
				env->SetIntField(usb_intObj, usb_intFID_num_altsetting,	conf_desc->interface[h].num_altsetting);
				// interface descriptor
				usb_intDescObjArray = (jobjectArray) env->NewObjectArray( conf_desc->interface[h].num_altsetting, usb_intDescClazz, NULL);
				if (!usb_intDescObjArray) {
					setLibusbJavaError("shared library error: Error NewObjectArray (usb_intDescObjArray)");
					return NULL;
				}
				for (a = 0; a < conf_desc->interface[h].num_altsetting; a++) {
#ifdef DEBUGON
					printf("\t\t\t\tlibusb_get_device_list: interface descriptor %u \n", a);
#endif
					if (conf_desc->interface[h].altsetting == NULL) {
						// this shouldn't happen
						printf("LibusbJava: libusb_get_device_list: (*conf_desc)->interface->altsetting == NULL\n");
						continue;
					}
					usb_intDescObj = env->NewObject(usb_intDescClazz, usb_intDescMid);
					if (!usb_intDescObj) {
						setLibusbJavaError("shared library error: Error NewObject (usb_intDescObj)");
						return NULL;
					}
					env->SetObjectArrayElement(usb_intDescObjArray, a, usb_intDescObj);
					/*fill fields*/
					env->SetByteField(usb_intDescObj, usb_intDescFID_bLength, conf_desc->interface[h].altsetting[a].bLength);
					env->SetByteField(usb_intDescObj, usb_intDescFID_bDescriptorType, conf_desc->interface[h].altsetting[a].bDescriptorType);
					env->SetByteField(usb_intDescObj, usb_intDescFID_bInterfaceNumber, conf_desc->interface[h].altsetting[a].bInterfaceNumber);
					env->SetByteField(usb_intDescObj, usb_intDescFID_bAlternateSetting, conf_desc->interface[h].altsetting[a].bAlternateSetting);
					env->SetByteField(usb_intDescObj, usb_intDescFID_bNumEndpoints, conf_desc->interface[h].altsetting[a].bNumEndpoints);
					env->SetByteField(usb_intDescObj, usb_intDescFID_bInterfaceClass, conf_desc->interface[h].altsetting[a].bInterfaceClass);
					env->SetByteField(usb_intDescObj, usb_intDescFID_bInterfaceSubClass, conf_desc->interface[h].altsetting[a].bInterfaceSubClass);
					env->SetByteField(usb_intDescObj, usb_intDescFID_bInterfaceProtocol, conf_desc->interface[h].altsetting[a].bInterfaceProtocol);
					env->SetByteField(usb_intDescObj, usb_intDescFID_iInterface, conf_desc->interface[h].altsetting[a].iInterface);
					env->SetIntField(usb_intDescObj, usb_intDescFID_extralen, conf_desc->interface[h].altsetting[a].extra_length);
					if (conf_desc->interface[h].altsetting[a].extra) {
						jbyteArray jbExtraDesc = to_byteArray(env, conf_desc->interface[h].altsetting[a].extra, conf_desc->interface[h].altsetting[a].extra_length);
						env->SetObjectField(usb_intDescObj, usb_intDescFID_extra, jbExtraDesc);
					} else {
						env->SetObjectField(usb_intDescObj,	usb_intDescFID_extra, NULL);
					}
					/*endpoint descriptor*/
					usb_epDescObjArray = (jobjectArray) env->NewObjectArray( conf_desc->interface[h].altsetting[a].bNumEndpoints, usb_epDescClazz, NULL);
					if (!usb_epDescObjArray) {
						setLibusbJavaError("shared library error: Error NewObjectArray (usb_epDescObjArray)");
						return NULL;
					}
					for (e = 0; e < conf_desc->interface[h].altsetting[a].bNumEndpoints; e++) {
#ifdef DEBUGON
						printf("\t\t\t\t\tLibusb_get_device_list: endpoint descriptor %u \n",e);
#endif


						if (conf_desc->interface[h].altsetting[a].endpoint == NULL) {
							printf(	"LibusbJava: usb_get_busses: (*conf_desc)->interface->altsetting->endpoint == NULL\n");
							return root_usb_devObj;
						}
						usb_epDescObj = env->NewObject(usb_epDescClazz,	usb_epDescMid);
						if (!usb_epDescObj) {
							setLibusbJavaError("shared library error: Error NewObject (usb_epDescObj)");
							return NULL;
						}
						env->SetObjectArrayElement(usb_epDescObjArray, e, usb_epDescObj);
						env->SetByteField(usb_epDescObj, usb_epDescFID_bLength, conf_desc->interface[h].altsetting[a].endpoint[e].bLength);
						env->SetByteField(usb_epDescObj, usb_epDescFID_bDescriptorType, conf_desc->interface[h].altsetting[a].endpoint[e].bDescriptorType);
						env->SetByteField(usb_epDescObj, usb_epDescFID_bEndpointAddress, conf_desc->interface[h].altsetting[a].endpoint[e].bEndpointAddress);
						env->SetByteField(usb_epDescObj, usb_epDescFID_bmAttributes, conf_desc->interface[h].altsetting[a].endpoint[e].bmAttributes);
						env->SetShortField(usb_epDescObj, usb_epDescFID_wMaxPacketSize, conf_desc->interface[h].altsetting[a].endpoint[e].wMaxPacketSize);
						env->SetByteField(usb_epDescObj, usb_epDescFID_bInterval, conf_desc->interface[h].altsetting[a].endpoint[e].bInterval);
						env->SetByteField(usb_epDescObj, usb_epDescFID_bRefresh, conf_desc->interface[h].altsetting[a].endpoint[e].bRefresh);
						env->SetByteField(usb_epDescObj, usb_epDescFID_bSynchAddress, conf_desc->interface[h].altsetting[a].endpoint[e].bSynchAddress);
						env->SetIntField(usb_epDescObj, usb_epDescFID_extralen, conf_desc->interface[h].altsetting[a].endpoint[e].extra_length);
						if (conf_desc->interface[h].altsetting[a].endpoint[e].extra) {
							jbyteArray jbExtraDesc = to_byteArray(env,  conf_desc->interface[h].altsetting[a].endpoint[e].extra, conf_desc->interface[h].altsetting[a].endpoint[e].extra_length);
							env->SetObjectField(usb_epDescObj, usb_epDescFID_extra, jbExtraDesc);
						} else {
							env->SetObjectField(usb_epDescObj, usb_epDescFID_extra, NULL);
						}
					}
					env->SetObjectField(usb_intDescObj, usb_intDescFID_endpoint, usb_epDescObjArray);
				}
				env->SetObjectField(usb_intObj, usb_intFID_altsetting, usb_intDescObjArray);
			}
			env->SetObjectField(usb_confDescObj, usb_confDescFID_interface_, usb_intObjArray);
			libusb_free_config_descriptor(conf_desc);
		}
		if (res) {
			env->SetLongField(usb_devObj, usb_devFID_devStructAddr, (jlong) 0);
			continue;
		}
		env->SetObjectField(usb_devObj, usb_devFID_config, usb_confDescObjArray);
		usb_devObj_prev = usb_devObj;
		usb_devObj = usb_devObj_next;

	}
	//Eliminate empty last device
	env->SetObjectField(usb_devObj_prev, usb_devFID_next, NULL);

//	free(dev_desc);
	libusb_free_device_list(devs, 0);
#ifdef DEBUGON
	printf("libusb_get_device_list: done\n");
#endif
	return root_usb_devObj;
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_get_bus_number
 * Signature: (Lch/ntb/inf/libusbJava/Usb_Device;)S
 ********************************************************************************************/
JNIEXPORT jshort JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1get_1bus_1number( JNIEnv *env, jclass obj, jobject dev) {
	clearLibusbJavaError();
	libusb_device *libusb_dev = (libusb_device *) (long) env->GetLongField(dev,	usb_devFID_devStructAddr);
	return libusb_get_bus_number(libusb_dev);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_get_max_iso_packet_size
 * Signature: (Lch/ntb/inf/libusbJava/Usb_Device;S)I
 ********************************************************************************************/
JNIEXPORT jint
JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1get_1max_1iso_1packet_1size( JNIEnv *env, jclass obj, jobject dev, jshort epAddr) {
	clearLibusbJavaError();
	libusb_device *libusb_dev = (libusb_device *) (unsigned long) env->GetLongField(dev, usb_devFID_devStructAddr);
	return libusb_get_max_iso_packet_size(libusb_dev, epAddr);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_ref_device
 * Signature: (Lch/ntb/inf/libusbJava/Usb_Device;)Lch/ntb/inf/libusbJava/Usb_Device;
 ********************************************************************************************/
JNIEXPORT jobject JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1ref_1device(JNIEnv *env, jclass obj, jobject dev) {
	clearLibusbJavaError();
	libusb_device *libusb_dev =	(libusb_device *) (unsigned long) env->GetLongField(dev, usb_devFID_devStructAddr);
	libusb_ref_device(libusb_dev);
	return dev;
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_unref_device
 * Signature: (Lch/ntb/inf/libusbJava/Usb_Device;)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1unref_1device(JNIEnv *env, jclass obj, jobject dev) {
	clearLibusbJavaError();
	libusb_device *libusb_dev =	(libusb_device *) (unsigned long) env->GetLongField(dev,usb_devFID_devStructAddr);
	libusb_unref_device(libusb_dev);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_open
 * Signature: (Lch/ntb/inf/libusbJava/Usb_Device;)J
 ********************************************************************************************/
JNIEXPORT jlong JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1open( JNIEnv *env, jclass obj, jobject dev) {
	clearLibusbJavaError();
	libusb_device_handle *handle = NULL;
	libusb_device *libusb_dev =	(libusb_device *) (unsigned long) env->GetLongField(dev, usb_devFID_devStructAddr);
	int res = libusb_open(libusb_dev, &handle);

	if(res != 0)
	{
		ThrowLibusbError(env, res);
		handle = NULL;
	}

	return (jlong)handle;
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_open_device_with_vid_pid
 * Signature: (JII)J
 ********************************************************************************************/
JNIEXPORT jlong
JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1open_1device_1with_1vid_1pid(JNIEnv *env, jclass obj, jlong ctx, jint vendor_id, jint product_id) {
	clearLibusbJavaError();
	return (jlong) libusb_open_device_with_vid_pid((libusb_context*) (unsigned long) ctx, vendor_id, product_id);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_close
 * Signature: (J)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1close(JNIEnv *env, jclass obj, jlong dev_handle) {
	clearLibusbJavaError();
	libusb_close((libusb_device_handle*) (unsigned long) dev_handle);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_get_device
 * Signature: (J)Lch/ntb/inf/libusbJava/Usb_Device;
 ********************************************************************************************/
JNIEXPORT jobject JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1get_1device(JNIEnv *env, jclass obj, jlong handle) {
	int res, a;
	uint8_t c, h, e;
	char filename[3];
	clearLibusbJavaError();
	libusb_config_descriptor *conf_desc;
	struct libusb_device_descriptor dev_desc;
	libusb_device *lib_dev = libusb_get_device((libusb_device_handle*) (unsigned long) handle);

	/* objects*/
	jobject usb_devObj, usb_devDescObj, usb_confDescObj, usb_intObj, usb_intDescObj, usb_epDescObj;
	jobjectArray usb_confDescObjArray, usb_intObjArray, usb_intDescObjArray, usb_epDescObjArray;

	/* create a new object for device*/
	libusb_get_device_descriptor(lib_dev, &dev_desc);
	usb_devObj = env->NewObject(usb_devClazz, usb_devMid);
	if (!usb_devObj) {
		setLibusbJavaError("shared library error: Error NewObject (usb_devObj)");
		return NULL;
	}

	/*fill the fields of the object*/
	env->SetObjectField(usb_devObj, usb_devFID_next, NULL);
	env->SetObjectField(usb_devObj, usb_devFID_prev, NULL);

	sprintf(filename, "%03d", libusb_get_device_address(lib_dev));
	env->SetObjectField(usb_devObj, usb_devFID_filename, env->NewStringUTF(filename));/*like the compatibility Layer*/
	env->SetByteField(usb_devObj, usb_devFID_devnum, libusb_get_device_address(lib_dev));
	env->SetByteField(usb_devObj, usb_devFID_num_children, 0);/*Hardcoded to 0, like the compatibility Layer*/
	env->SetObjectField(usb_devObj, usb_devFID_children, NULL);/*Hardcoded to NULL, like the compatibility Layer*/
	env->SetLongField(usb_devObj, usb_devFID_devStructAddr,(jlong) lib_dev);

	/*device Descriptor*/
	usb_devDescObj = env->NewObject(usb_devDescClazz, usb_devDescMid);
	if (!usb_devDescObj) {
		setLibusbJavaError("shared library error: Error NewObject (usb_devDescObj)");
		return NULL;
	}
	env->SetByteField(usb_devDescObj, usb_devDescFID_bLength, dev_desc.bLength);
	env->SetByteField(usb_devDescObj, usb_devDescFID_bDescriptorType, dev_desc.bDescriptorType);
	env->SetShortField(usb_devDescObj, usb_devDescFID_bcdUSB, dev_desc.bcdUSB);
	env->SetByteField(usb_devDescObj, usb_devDescFID_bDeviceClass, dev_desc.bDeviceClass);
	env->SetByteField(usb_devDescObj, usb_devDescFID_bDeviceSubClass, dev_desc.bDeviceSubClass);
	env->SetByteField(usb_devDescObj, usb_devDescFID_bDeviceProtocol, dev_desc.bDeviceProtocol);
	env->SetByteField(usb_devDescObj, usb_devDescFID_bMaxPacketSize0, dev_desc.bMaxPacketSize0);
	env->SetShortField(usb_devDescObj, usb_devDescFID_idVendor, dev_desc.idVendor);
	env->SetShortField(usb_devDescObj, usb_devDescFID_idProduct, dev_desc.idProduct);
	env->SetShortField(usb_devDescObj, usb_devDescFID_bcdDevice, dev_desc.bcdDevice);
	env->SetByteField(usb_devDescObj, usb_devDescFID_iManufacturer, dev_desc.iManufacturer);
	env->SetByteField(usb_devDescObj, usb_devDescFID_iProduct, dev_desc.iProduct);
	env->SetByteField(usb_devDescObj, usb_devDescFID_iSerialNumber, dev_desc.iSerialNumber);
	env->SetByteField(usb_devDescObj, usb_devDescFID_bNumConfigurations, dev_desc.bNumConfigurations);
	env->SetObjectField(usb_devDescObj, usb_devFID_descriptor, usb_devDescObj);

	/*configuration descriptor*/
	/*Loop through all of the configurations*/
	usb_confDescObjArray = (jobjectArray) env->NewObjectArray(
			dev_desc.bNumConfigurations, usb_confDescClazz, NULL);
	if (!usb_confDescObjArray) {
		setLibusbJavaError("shared library error: Error NewOjectArray");
		return NULL;
	}
	for (c = 0; c < dev_desc.bNumConfigurations; c++) {
#ifdef DEBUGON
		printf("\t\tlibusb_get_device: configuration %u \n", c);
#endif
		res = libusb_get_config_descriptor(lib_dev, c, &conf_desc);
		if (res) {
			setLibusbJavaError("shared library error: read conf_desc failed");
			return usb_devObj;
		}

		usb_confDescObj = env->NewObject(usb_confDescClazz, usb_confDescMid);
		if (!usb_confDescObj) {
			setLibusbJavaError("shared library error: Error NewObject (usb_confDescObj)");
			return NULL;
		}
		env->SetObjectArrayElement(usb_confDescObjArray, c, usb_confDescObj);/*add it to the Array*/
		/*fill the fields*/
		env->SetByteField(usb_confDescObj, usb_confDescFID_bLength,	conf_desc->bLength);
		env->SetByteField(usb_confDescObj, usb_confDescFID_bDescriptorType, conf_desc->bDescriptorType);
		env->SetShortField(usb_confDescObj, usb_confDescFID_wTotalLength, conf_desc->wTotalLength);
		env->SetByteField(usb_confDescObj, usb_confDescFID_bNumInterfaces, conf_desc->bNumInterfaces);
		env->SetByteField(usb_confDescObj, usb_confDescFID_bConfigurationValue, conf_desc->bConfigurationValue);
		env->SetByteField(usb_confDescObj, usb_confDescFID_iConfiguration, conf_desc->iConfiguration);
		env->SetByteField(usb_confDescObj, usb_confDescFID_bmAttributes, conf_desc->bmAttributes);
		env->SetByteField(usb_confDescObj, usb_confDescFID_MaxPower, conf_desc->MaxPower);
		env->SetIntField(usb_confDescObj, usb_confDescFID_extralen, conf_desc->extra_length);
		if (conf_desc->extra) {
			jbyteArray jbExtraDesc = to_byteArray(env, conf_desc->extra, conf_desc->extra_length);
			env->SetObjectField(usb_confDescObj, usb_confDescFID_extra, jbExtraDesc);
		} else {
			env->SetObjectField(usb_confDescObj, usb_confDescFID_extra, NULL);
		}
		/*interface*/
		usb_intObjArray = (jobjectArray) env->NewObjectArray(conf_desc->bNumInterfaces, usb_intClazz, NULL);
		if (!usb_intObjArray) {
			setLibusbJavaError("shared library error: Error NewObjectArray (usb_intObjArray)");
			return NULL;
		}
		for (h = 0; h < conf_desc->bNumInterfaces; h++) {
#ifdef DEBUGON
			printf("\t\t\tlibusb_get_device: interface %u \n", h);
#endif

			if (conf_desc->interface == NULL) {
				// this shouldn't happen
				printf("(*conf_desc)->usb_interface == NULL");
				continue;
			}
			usb_intObj = env->NewObject(usb_intClazz, usb_intMid);
			if (!usb_intObj) {
				setLibusbJavaError("shared library error: Error NewObject (usb_intObj)");
				return NULL;
			}
			env->SetObjectArrayElement(usb_intObjArray, h, usb_intObj);
			env->SetIntField(usb_intObj, usb_intFID_num_altsetting,	conf_desc->interface[h].num_altsetting);
			// interface descriptor
			usb_intDescObjArray = (jobjectArray) env->NewObjectArray(conf_desc->interface[h].num_altsetting, usb_intDescClazz, NULL);
			if (!usb_intDescObjArray) {
				setLibusbJavaError("shared library error: Error NewObjectArray (usb_intDescObjArray)");
				return NULL;
			}
			for (a = 0; a < conf_desc->interface[h].num_altsetting; a++) {
#ifdef DEBUGON
				printf("\t\t\t\tlibusb_get_device: interface descriptor %u \n",	a);
#endif

				if (conf_desc->interface[h].altsetting == NULL) {
					// this shouldn't happen
					printf("LibusbJava: libusb_get_device_list: altsetting == NULL\n");
					continue;
				}
				usb_intDescObj = env->NewObject(usb_intDescClazz, usb_intDescMid);
				if (!usb_intDescObj) {
					setLibusbJavaError("shared library error: Error NewObject (usb_intDescObj)");
					return NULL;
				}
				env->SetObjectArrayElement(usb_intDescObjArray, a, usb_intDescObj);
				/*fill fields*/
				env->SetByteField(usb_intDescObj, usb_intDescFID_bLength, conf_desc->interface[h].altsetting[a].bLength);
				env->SetByteField(usb_intDescObj, usb_intDescFID_bDescriptorType, conf_desc->interface[h].altsetting[a].bDescriptorType);
				env->SetByteField(usb_intDescObj, usb_intDescFID_bInterfaceNumber, conf_desc->interface[h].altsetting[a].bInterfaceNumber);
				env->SetByteField(usb_intDescObj, usb_intDescFID_bAlternateSetting, conf_desc->interface[h].altsetting[a].bAlternateSetting);
				env->SetByteField(usb_intDescObj, usb_intDescFID_bNumEndpoints, conf_desc->interface[h].altsetting[a].bNumEndpoints);
				env->SetByteField(usb_intDescObj, usb_intDescFID_bInterfaceClass, conf_desc->interface[h].altsetting[a].bInterfaceClass);
				env->SetByteField(usb_intDescObj, usb_intDescFID_bInterfaceSubClass, conf_desc->interface[h].altsetting[a].bInterfaceSubClass);
				env->SetByteField(usb_intDescObj, usb_intDescFID_bInterfaceProtocol, conf_desc->interface[h].altsetting[a].bInterfaceProtocol);
				env->SetByteField(usb_intDescObj, usb_intDescFID_iInterface, conf_desc->interface[h].altsetting[a].iInterface);
				env->SetIntField(usb_intDescObj, usb_intDescFID_extralen, conf_desc->interface[h].altsetting[a].extra_length);
				if (conf_desc->interface[h].altsetting[a].extra) {
					jbyteArray jbExtraDesc = to_byteArray(env, conf_desc->interface[h].altsetting[a].extra, conf_desc->interface[h].altsetting[a].extra_length);
					env->SetObjectField(usb_intDescObj, usb_intDescFID_extra, jbExtraDesc);
				} else {
					env->SetObjectField(usb_intDescObj, usb_intDescFID_extra, NULL);
				}
				/*endpoint descriptor*/
				usb_epDescObjArray = (jobjectArray) env->NewObjectArray(conf_desc->interface[h].altsetting[a].bNumEndpoints, usb_epDescClazz, NULL);
				if (!usb_epDescObjArray) {
					setLibusbJavaError("shared library error: Error NewObjectArray (usb_epDescObjArray)");
					return NULL;
				}
				for (e = 0; e < conf_desc->interface[h].altsetting[a].bNumEndpoints; e++) {
#ifdef DEBUGON
					printf("\t\t\t\t\tLibusb_get_device: endpoint descriptor %u \n", e);
#endif

					if (conf_desc->interface[h].altsetting[a].endpoint == NULL) {
						printf("LibusbJava: usb_get_busses: endpoint == NULL\n");
						return usb_devObj;
					}
					usb_epDescObj = env->NewObject(usb_epDescClazz,	usb_epDescMid);
					if (!usb_epDescObj) {
						setLibusbJavaError("shared library error: Error NewObject (usb_epDescObj)");
						return NULL;
					}
					env->SetObjectArrayElement(usb_epDescObjArray, e, usb_epDescObj);
					env->SetByteField(usb_epDescObj, usb_epDescFID_bLength, conf_desc->interface[h].altsetting[a].endpoint[e].bLength);
					env->SetByteField(usb_epDescObj, usb_epDescFID_bDescriptorType, conf_desc->interface[h].altsetting[a].endpoint[e].bDescriptorType);
					env->SetByteField(usb_epDescObj, usb_epDescFID_bEndpointAddress, conf_desc->interface[h].altsetting[a].endpoint[e].bEndpointAddress);
					env->SetByteField(usb_epDescObj, usb_epDescFID_bmAttributes, conf_desc->interface[h].altsetting[a].endpoint[e].bmAttributes);
					env->SetShortField(usb_epDescObj, usb_epDescFID_wMaxPacketSize, conf_desc->interface[h].altsetting[a].endpoint[e].wMaxPacketSize);
					env->SetByteField(usb_epDescObj, usb_epDescFID_bInterval, conf_desc->interface[h].altsetting[a].endpoint[e].bInterval);
					env->SetByteField(usb_epDescObj, usb_epDescFID_bRefresh, conf_desc->interface[h].altsetting[a].endpoint[e].bRefresh);
					env->SetByteField(usb_epDescObj, usb_epDescFID_bSynchAddress, conf_desc->interface[h].altsetting[a].endpoint[e].bSynchAddress);
					env->SetIntField(usb_epDescObj, usb_epDescFID_extralen, conf_desc->interface[h].altsetting[a].endpoint[e].extra_length);
					if (conf_desc->interface[h].altsetting[a].endpoint[e].extra) {
						jbyteArray jbExtraDesc = to_byteArray(env, conf_desc->interface[h].altsetting[a].endpoint[e].extra, conf_desc->interface[h].altsetting[a].endpoint[e].extra_length);
						env->SetObjectField(usb_epDescObj, usb_epDescFID_extra, jbExtraDesc);
					} else {
						env->SetObjectField(usb_epDescObj, usb_epDescFID_extra, NULL);
					}
				}
				env->SetObjectField(usb_intDescObj, usb_intDescFID_endpoint, usb_epDescObjArray);
			}
			env->SetObjectField(usb_intObj, usb_intFID_altsetting, usb_intDescObjArray);
		}
		env->SetObjectField(usb_confDescObj, usb_confDescFID_interface_, usb_intObjArray);
		libusb_free_config_descriptor(conf_desc);
	}
	env->SetObjectField(usb_devObj, usb_devFID_config, usb_confDescObjArray);

#ifdef DEBUGON
	printf("libusb_get_device: done\n");
#endif
	return usb_devObj;
}
/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_get_configuration
 * Signature: (J)I
 ********************************************************************************************/
JNIEXPORT jint JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1get_1configuration(JNIEnv *env, jclass obj, jlong handle) {
	int config = 0;
	int libusb_result = 0;

	clearLibusbJavaError();
	libusb_result = libusb_get_configuration((libusb_device_handle*) (unsigned long) handle, &config);

	if (libusb_result != 0) {
		setLibusbJavaError("shared library error: get_configuration failed");
		ThrowLibusbError(env, libusb_result);
		config = 0;
	}

	return config;
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_set_configuration
 * Signature: (JI)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1set_1configuration(JNIEnv *env, jclass obj, jlong handle, jint config) {
	clearLibusbJavaError();
	ThrowIfUnsuccessful(env, libusb_set_configuration((libusb_device_handle*) (unsigned long) handle, config));
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_claim_interface
 * Signature: (JI)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1claim_1interface(JNIEnv *env, jclass obj, jlong handle, jint iNumber) {
	clearLibusbJavaError();
	ThrowIfUnsuccessful(env, libusb_claim_interface((libusb_device_handle*) (unsigned long) handle, iNumber));
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_release_interface
 * Signature: (JI)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1release_1interface(JNIEnv *env, jclass obj, jlong handle, jint iNumber) {
	clearLibusbJavaError();
	ThrowIfUnsuccessful(env, libusb_release_interface((libusb_device_handle*) (unsigned long) handle, iNumber));
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_set_interface_alt_setting
 * Signature: (JII)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1set_1interface_1alt_1setting(JNIEnv *env, jclass obj, jlong handle, jint iNumber, jint altSet) {
	clearLibusbJavaError();
	ThrowIfUnsuccessful(env, libusb_set_interface_alt_setting((libusb_device_handle*) (unsigned long) handle, iNumber, altSet));
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_clear_halt
 * Signature: (JS)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1clear_1halt(JNIEnv *env, jclass obj, jlong handle, jshort ep) {
	clearLibusbJavaError();
	ThrowIfUnsuccessful(env, libusb_clear_halt((libusb_device_handle*) (unsigned long) handle, ep));
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_reset_device
 * Signature: (J)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1reset_1device(JNIEnv *env, jclass obj, jlong handle) {
	clearLibusbJavaError();
	ThrowIfUnsuccessful(env, libusb_reset_device((libusb_device_handle*) (unsigned long) handle));
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_kernel_driver_active
 * Signature: (JI)I
 ********************************************************************************************/
JNIEXPORT jint JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1kernel_1driver_1active(JNIEnv *env, jclass obj, jlong handle, jint iNumber) {
	int libusb_result = 0;

	clearLibusbJavaError();
	libusb_result = libusb_kernel_driver_active((libusb_device_handle*) (unsigned long) handle, iNumber);

	if (libusb_result < 0)
	{
		ThrowLibusbError(env, libusb_result);
		libusb_result = 0;
	}

	return libusb_result;
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_detach_kernel_driver
 * Signature: (JI)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1detach_1kernel_1driver(JNIEnv *env, jclass obj, jlong handle, jint iNumber) {
	clearLibusbJavaError();
	ThrowIfUnsuccessful(env, libusb_detach_kernel_driver((libusb_device_handle*) (unsigned long) handle, iNumber));
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_attach_kernel_driver
 * Signature: (JI)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1attach_1kernel_1driver(JNIEnv *env, jclass obj, jlong handle, jint iNumber) {
	clearLibusbJavaError();
	ThrowIfUnsuccessful(env, libusb_attach_kernel_driver((libusb_device_handle*) (unsigned long) handle, iNumber));
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_get_active_config_descriptor
 * Signature: (Lch/ntb/inf/libusbJava/Usb_Device;)Lch/ntb/inf/libusbJava/Usb_Config_Descriptor;
 ********************************************************************************************/
JNIEXPORT jobject JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1get_1active_1config_1descriptor(JNIEnv *env, jclass obj, jobject dev) {
	uint8_t a, e;
	clearLibusbJavaError();
	libusb_config_descriptor *conf_desc;
	jobject usb_confDescObj, usb_intObj, usb_intDescObj, usb_epDescObj;
	jobjectArray usb_intObjArray, usb_intDescObjArray, usb_epDescObjArray;

	libusb_device *lib_dev = (libusb_device*) (unsigned long) env->GetLongField(dev, usb_devFID_devStructAddr);
	if (libusb_get_active_config_descriptor(lib_dev, &conf_desc)) {
		setLibusbJavaError("shared library error: get_configuration failed");
		return NULL;
	}
	usb_confDescObj = env->NewObject(usb_confDescClazz, usb_confDescMid);
	if (!usb_confDescObj) {
		setLibusbJavaError("shared library error: Error NewObject (usb_confDescObj)");
		return NULL;
	}
	/*fill the fields*/
	env->SetByteField(usb_confDescObj, usb_confDescFID_bLength,	conf_desc->bLength);
	env->SetByteField(usb_confDescObj, usb_confDescFID_bDescriptorType, conf_desc->bDescriptorType);
	env->SetShortField(usb_confDescObj, usb_confDescFID_wTotalLength, conf_desc->wTotalLength);
	env->SetByteField(usb_confDescObj, usb_confDescFID_bNumInterfaces, conf_desc->bNumInterfaces);
	env->SetByteField(usb_confDescObj, usb_confDescFID_bConfigurationValue, conf_desc->bConfigurationValue);
	env->SetByteField(usb_confDescObj, usb_confDescFID_iConfiguration, conf_desc->iConfiguration);
	env->SetByteField(usb_confDescObj, usb_confDescFID_bmAttributes, conf_desc->bmAttributes);
	env->SetByteField(usb_confDescObj, usb_confDescFID_MaxPower, conf_desc->MaxPower);
	env->SetIntField(usb_confDescObj, usb_confDescFID_extralen, conf_desc->extra_length);
	if (conf_desc->extra) {
		jbyteArray jbExtraDesc = to_byteArray(env, conf_desc->extra, conf_desc->extra_length);
		env->SetObjectField(usb_confDescObj, usb_confDescFID_extra, jbExtraDesc);
	} else {
		env->SetObjectField(usb_confDescObj, usb_confDescFID_extra, NULL);
	}
	/*interface*/
	usb_intObjArray = (jobjectArray) env->NewObjectArray(conf_desc->bNumInterfaces, usb_intClazz, NULL);
	if (!usb_intObjArray) {
		setLibusbJavaError("shared library error: Error NewObjectArray (usb_intObjArray)");
		return NULL;
	}
	for (int i = 0; i < conf_desc->bNumInterfaces; i++) {
#ifdef DEBUGON
		printf("\t\t\tlibusb_get_activ_config_descriptor: interface %u \n", i);
#endif
		if (conf_desc->interface == NULL) {
			// this shouldn't happen
			printf("conf_des->interface == NULL");
			continue;
		}
		usb_intObj = env->NewObject(usb_intClazz, usb_intMid);
		if (!usb_intObj) {
			setLibusbJavaError("shared library error: Error NewObject (usb_intObj)");
			return NULL;
		}
		env->SetObjectArrayElement(usb_intObjArray, i, usb_intObj);
		env->SetIntField(usb_intObj, usb_intFID_num_altsetting, conf_desc->interface[i].num_altsetting);
		// interface descriptor
		usb_intDescObjArray = (jobjectArray) env->NewObjectArray(conf_desc->interface[i].num_altsetting, usb_intDescClazz, NULL);
		if (!usb_intDescObjArray) {
			setLibusbJavaError("shared library error: Error NewObjectArray (usb_intDescObjArray)");
			return NULL;
		}
		for (a = 0; a < conf_desc->interface[i].num_altsetting; a++) {
#ifdef DEBUGON
			printf("\t\t\t\tlibusb_get__activ_config_descriptor: interface descriptor %u \n", a);
#endif

			if (conf_desc->interface[i].altsetting == NULL) {
				// this shouldn't happen
				printf("LibusbJava: libusb_get__activ_config_descriptor: altsetting == NULL\n");
				continue;
			}
			usb_intDescObj = env->NewObject(usb_intDescClazz, usb_intDescMid);
			if (!usb_intDescObj) {
				setLibusbJavaError("shared library error: Error NewObject (usb_intDescObj)");
				return NULL;
			}
			env->SetObjectArrayElement(usb_intDescObjArray, a, usb_intDescObj);
			/*fill fields*/
			env->SetByteField(usb_intDescObj, usb_intDescFID_bLength, conf_desc->interface[i].altsetting[a].bLength);
			env->SetByteField(usb_intDescObj, usb_intDescFID_bDescriptorType, conf_desc->interface[i].altsetting[a].bDescriptorType);
			env->SetByteField(usb_intDescObj, usb_intDescFID_bInterfaceNumber, conf_desc->interface[i].altsetting[a].bInterfaceNumber);
			env->SetByteField(usb_intDescObj, usb_intDescFID_bAlternateSetting, conf_desc->interface[i].altsetting[a].bAlternateSetting);
			env->SetByteField(usb_intDescObj, usb_intDescFID_bNumEndpoints, conf_desc->interface[i].altsetting[a].bNumEndpoints);
			env->SetByteField(usb_intDescObj, usb_intDescFID_bInterfaceClass, conf_desc->interface[i].altsetting[a].bInterfaceClass);
			env->SetByteField(usb_intDescObj, usb_intDescFID_bInterfaceSubClass, conf_desc->interface[i].altsetting[a].bInterfaceSubClass);
			env->SetByteField(usb_intDescObj, usb_intDescFID_bInterfaceProtocol, conf_desc->interface[i].altsetting[a].bInterfaceProtocol);
			env->SetByteField(usb_intDescObj, usb_intDescFID_iInterface, conf_desc->interface[i].altsetting[a].iInterface);
			env->SetIntField(usb_intDescObj, usb_intDescFID_extralen, conf_desc->interface[i].altsetting[a].extra_length);
			if (conf_desc->interface[i].altsetting[a].extra) {
				jbyteArray jbExtraDesc = to_byteArray(env, conf_desc->interface[i].altsetting[a].extra, conf_desc->interface[i].altsetting[a].extra_length);
				env->SetObjectField(usb_intDescObj, usb_intDescFID_extra, jbExtraDesc);
			} else {
				env->SetObjectField(usb_intDescObj, usb_intDescFID_extra, NULL);
			}
			/*endpoint descriptor*/
			usb_epDescObjArray = (jobjectArray) env->NewObjectArray(conf_desc->interface[i].altsetting[a].bNumEndpoints, usb_epDescClazz, NULL);
			if (!usb_epDescObjArray) {
				setLibusbJavaError("shared library error: Error NewObjectArray (usb_epDescObjArray)");
				return NULL;
			}
			for (e = 0; e < conf_desc->interface[i].altsetting[a].bNumEndpoints; e++) {
#ifdef DEBUGON
				printf("\t\t\t\t\tLibusb_get__activ_config_descriptor: endpoint descriptor %u \n",	e);
#endif
				if (conf_desc->interface[i].altsetting[a].endpoint == NULL) {
					printf("LibusbJava: usb_get__activ_config_descriptor: endpoint == NULL\n");
					return usb_confDescObj;
				}
				usb_epDescObj = env->NewObject(usb_epDescClazz, usb_epDescMid);
				if (!usb_epDescObj) {
					setLibusbJavaError("shared library error: Error NewObject (usb_epDescObj)");
					return NULL;
				}
				env->SetObjectArrayElement(usb_epDescObjArray, e, usb_epDescObj);
				env->SetByteField(usb_epDescObj, usb_epDescFID_bLength, conf_desc->interface[i].altsetting[a].endpoint[e].bLength);
				env->SetByteField(usb_epDescObj, usb_epDescFID_bDescriptorType, conf_desc->interface[i].altsetting[a].endpoint[e].bDescriptorType);
				env->SetByteField(usb_epDescObj, usb_epDescFID_bEndpointAddress, conf_desc->interface[i].altsetting[a].endpoint[e].bEndpointAddress);
				env->SetByteField(usb_epDescObj, usb_epDescFID_bmAttributes, conf_desc->interface[i].altsetting[a].endpoint[e].bmAttributes);
				env->SetShortField(usb_epDescObj, usb_epDescFID_wMaxPacketSize, conf_desc->interface[i].altsetting[a].endpoint[e].wMaxPacketSize);
				env->SetByteField(usb_epDescObj, usb_epDescFID_bInterval, conf_desc->interface[i].altsetting[a].endpoint[e].bInterval);
				env->SetByteField(usb_epDescObj, usb_epDescFID_bRefresh, conf_desc->interface[i].altsetting[a].endpoint[e].bRefresh);
				env->SetByteField(usb_epDescObj, usb_epDescFID_bSynchAddress, conf_desc->interface[i].altsetting[a].endpoint[e].bSynchAddress);
				env->SetIntField(usb_epDescObj, usb_epDescFID_extralen, conf_desc->interface[i].altsetting[a].endpoint[e].extra_length);
				if (conf_desc->interface[i].altsetting[a].endpoint[e].extra) {
					jbyteArray jbExtraDesc = to_byteArray(env, conf_desc->interface[i].altsetting[a].endpoint[e].extra, conf_desc->interface[i].altsetting[a].endpoint[e].extra_length);
					env->SetObjectField(usb_epDescObj, usb_epDescFID_extra, jbExtraDesc);
				} else {
					env->SetObjectField(usb_epDescObj, usb_epDescFID_extra, NULL);
				}
			}
			env->SetObjectField(usb_intDescObj, usb_intDescFID_endpoint, usb_epDescObjArray);
		}
		env->SetObjectField(usb_intObj, usb_intFID_altsetting, usb_intDescObjArray);
	}
	env->SetObjectField(usb_confDescObj, usb_confDescFID_interface_, usb_intObjArray);
	libusb_free_config_descriptor(conf_desc);

	return usb_confDescObj;
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_get_string_descriptor_ascii
 * Signature: (JSI)Ljava/lang/String;
 ********************************************************************************************/
JNIEXPORT jstring JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1get_1string_1descriptor_1ascii( JNIEnv *env, jclass obj, jlong handle, jshort desc_index, jint length) {
	jstring string;
	int res = 0;
	clearLibusbJavaError();
	unsigned char data[(int)length + 1];

	res = libusb_get_string_descriptor_ascii((libusb_device_handle*) (unsigned long) handle, desc_index, data, (int)length);

	if (res >= 0)
	{
		data[res] = '\0';
		string = env->NewStringUTF((const char*)data);
	}
	else
	{
		setLibusbJavaError("get_string_descriptor_ascii: retrieve String failed");
		string = NULL;
		ThrowLibusbError(env, res);
	}
	return string;
}
/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_get_descriptor
 * Signature: (JISI)[B
 ********************************************************************************************/

JNIEXPORT jbyteArray JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1get_1descriptor(
		JNIEnv *env, jclass obj, jlong handle, jint desc_type,
		jshort desc_index, jint size) {

	clearLibusbJavaError();
	int res;
	unsigned char data[255];

	res = libusb_get_descriptor((libusb_device_handle*) (unsigned long) handle,	desc_type, desc_index, data, size);
	if (res < 0) {
		setLibusbJavaError("libusb_get_descriptor: retrieve data failed");
		ThrowLibusbError(env, res);
		return NULL;
	}

	return to_byteArray(env, data, res);
}
/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_get_string_descriptor
 * Signature: (JSII)Ljava/lang/String;
 ********************************************************************************************/
JNIEXPORT jstring
JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1get_1string_1descriptor(JNIEnv *env, jclass obj, jlong handle, jshort desc_index, jint langid, jint size) {
	clearLibusbJavaError();
	int res;
	jstring string;
	unsigned char data[(int)size + 1];

	res = libusb_get_string_descriptor((libusb_device_handle*) (unsigned long) handle, desc_index, langid, data, size);

	if (res >= 0) {
		data[res] = '\0';
		string = env->NewStringUTF((const char*) data);
	} else {
		setLibusbJavaError("get_string_descriptor: retrieve String failed");
		string = NULL;
		ThrowLibusbError(env, res);
	}

	return string;

}
/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_alloc_transfer
 * Signature: (I)J
 ********************************************************************************************/
JNIEXPORT jlong JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1alloc_1transfer( JNIEnv *env, jclass obj, jint iso_packets) {
	clearLibusbJavaError();
	return (jlong) libusb_alloc_transfer(iso_packets);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_free_transfer
 * Signature: (J)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1free_1transfer( JNIEnv *env, jclass obj, jlong transfernumber) {
	clearLibusbJavaError();
	libusb_free_transfer((libusb_transfer*) (unsigned long) transfernumber);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_submit_transfer
 * Signature: (J)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1submit_1transfer(JNIEnv *env, jclass obj, jlong transfernumber) {
	clearLibusbJavaError();
	ThrowIfUnsuccessful(env, libusb_submit_transfer((libusb_transfer*) (unsigned long) transfernumber));
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_cancel_transfer
 * Signature: (J)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1cancel_1transfer(JNIEnv *env, jclass obj, jlong transfernumber) {
	clearLibusbJavaError();
	ThrowIfUnsuccessful(env, libusb_cancel_transfer((libusb_transfer*) (unsigned long) transfernumber));
}
/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_control_transfer_get_data
 * Signature: (J)[B
 ********************************************************************************************/
JNIEXPORT jbyteArray JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1control_1transfer_1get_1data(JNIEnv *env, jclass obj, jlong transfernumber) {
	clearLibusbJavaError();
	unsigned char *data;
	libusb_transfer* trans = (libusb_transfer*) (unsigned long) transfernumber;

	if (trans == NULL)
		return NULL;

	data = libusb_control_transfer_get_data(trans);
	return to_byteArray(env, data, trans->actual_length - LIBUSB_CONTROL_SETUP_SIZE );
}

/*********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_control_transfer_get_setup
 * Signature: (J)[B
 ********************************************************************************************/
JNIEXPORT jbyteArray JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1control_1transfer_1get_1setup(JNIEnv *env, jclass obj, jlong transfernumber) {
	clearLibusbJavaError();
	unsigned char *data;
	libusb_transfer* trans = (libusb_transfer*) (unsigned long) transfernumber;

	if (trans == NULL) {
		return NULL;
	}

	data = (unsigned char*)libusb_control_transfer_get_setup(trans);

	return to_byteArray(env, data, 8);
}
/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_fill_control_setup
 * Signature: (IIIII)[B
 ********************************************************************************************/
JNIEXPORT jbyteArray JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1fill_1control_1setup(JNIEnv *env, jclass obj, jint bmRequestType, jint bRequest, jint wValue, jint wIndex, jint wLength) {
	clearLibusbJavaError();
	unsigned char setup[8];

	libusb_fill_control_setup(setup, bmRequestType, bRequest, wValue, wIndex, wLength);

	return to_byteArray(env, setup, 8);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_fill_control_transfer
 * Signature: (JJ[BI)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1fill_1control_1transfer(JNIEnv *env, jclass obj, jlong transfernumber, jlong handle, jbyteArray buffer, jint timeout) {
	clearLibusbJavaError();
	jbyte *byteArrayElements = env->GetByteArrayElements(buffer, NULL);
	libusb_fill_control_transfer((libusb_transfer*) (unsigned long) transfernumber,(libusb_device_handle*) (unsigned long) handle,(unsigned char*) byteArrayElements, transfer_callback, env,(unsigned int) timeout);
	env->ReleaseByteArrayElements(buffer, byteArrayElements, 0);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_fill_bulk_transfer
 * Signature: (JJB[BII)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1fill_1bulk_1transfer(JNIEnv *env, jclass obj, jlong transfernumber, jlong handle,	jbyte endpoint, jbyteArray buffer, jint length, jint timeout) {
	clearLibusbJavaError();
	jbyte *byteArrayElements = env->GetByteArrayElements(buffer, NULL);
	libusb_fill_bulk_transfer((libusb_transfer*) (unsigned long) transfernumber,(libusb_device_handle*) (unsigned long) handle, endpoint,(unsigned char*) byteArrayElements, length, transfer_callback, env,(unsigned int) timeout);
	env->ReleaseByteArrayElements(buffer, byteArrayElements, 0);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_fill_interrupt_transfer
 * Signature: (JJB[BII)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1fill_1interrupt_1transfer(JNIEnv *env, jclass obj, jlong transfernumber, jlong handle,jbyte endpoint, jbyteArray buffer, jint length, jint timeout) {
	clearLibusbJavaError();
	jbyte *byteArrayElements = env->GetByteArrayElements(buffer, NULL);
	libusb_fill_interrupt_transfer((libusb_transfer*) (unsigned long) transfernumber,(libusb_device_handle*) (unsigned long) handle, endpoint,(unsigned char*) byteArrayElements, length, transfer_callback, env, timeout);
	env->ReleaseByteArrayElements(buffer, byteArrayElements, 0);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_fill_iso_transfer
 * Signature: (JJB[BIII)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1fill_1iso_1transfer(JNIEnv *env, jclass obj, jlong transfernumber, jlong handle, jbyte endpoint, jbyteArray buffer, jint length, jint num_iso_packets, jint timeout) {
	clearLibusbJavaError();
	jbyte *byteArrayElements = env->GetByteArrayElements(buffer, NULL);
	libusb_fill_iso_transfer((libusb_transfer*) (unsigned long) transfernumber,	(libusb_device_handle*) (unsigned long) handle, endpoint, (unsigned char*) byteArrayElements, length, num_iso_packets,	transfer_callback, env, timeout);
	env->ReleaseByteArrayElements(buffer, byteArrayElements, 0);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_set_iso_packet_lengths
 * Signature: (JI)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1set_1iso_1packet_1lengths(JNIEnv *env, jclass obj, jlong transfernumber, jint length) {
	clearLibusbJavaError();
	libusb_set_iso_packet_lengths((libusb_transfer*) (unsigned long) transfernumber, length);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_get_iso_packet_buffer
 * Signature: (JI)[B
 ********************************************************************************************/

JNIEXPORT jbyteArray JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1get_1iso_1packet_1buffer(JNIEnv *env, jclass obj, jlong transfernumber, jint packet) {
	clearLibusbJavaError();
	int size = 0;
	unsigned char *data = NULL;
	libusb_transfer* transfer =	(libusb_transfer*) (unsigned long) transfernumber;

	if (transfer == NULL) {
		setLibusbJavaError("libusb_get_iso_packet_buffer: ilegal transfernumber");
		return NULL;
	}

	if (packet < transfer->num_iso_packets) {
		size = transfer->iso_packet_desc[packet].actual_length;
	}

	data = libusb_get_iso_packet_buffer((libusb_transfer*) (unsigned long) transfernumber, packet);
	if (data == NULL) {
		setLibusbJavaError("libusb_get_iso_packet_buffer: packet does not exist");
		return NULL;
	}

	return to_byteArray(env, data, size);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_get_iso_packet_buffer_simple
 * Signature: (JI)[B
 ********************************************************************************************/
JNIEXPORT jbyteArray JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1get_1iso_1packet_1buffer_1simple(JNIEnv *env, jclass obj, jlong transfernumber, jint packet) {
	clearLibusbJavaError();
	int size = 0;
	unsigned char *data = NULL;
	libusb_transfer* transfer =	(libusb_transfer*) (unsigned long) transfernumber;

	if (transfer == NULL) {
		setLibusbJavaError("libusb_get_iso_packet_buffer_simple: ilegal transfernumber");
		return NULL;
	}

	if (packet < transfer->num_iso_packets) {
		size = transfer->iso_packet_desc[packet].actual_length;
	}

	data = libusb_get_iso_packet_buffer_simple((libusb_transfer*) (unsigned long) transfernumber, packet);

	if (data == NULL) {
		setLibusbJavaError("libusb_get_iso_packet_buffer_simple: packet does not exist");
		return NULL;
	}

	return to_byteArray(env, data, size);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_try_lock_events
 * Signature: (J)I
 ********************************************************************************************/
JNIEXPORT jint JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1try_1lock_1events(JNIEnv *env, jclass obj, jlong ctx) {
	clearLibusbJavaError();
	return libusb_try_lock_events((libusb_context*) (unsigned long) ctx);
//	return libusb_try_lock_events(NULL);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_lock_events
 * Signature: (J)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1lock_1events(JNIEnv *env, jclass obj, jlong ctx) {
	libusb_lock_events((libusb_context*) (unsigned long) ctx);
//	libusb_lock_events(NULL);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_unlock_events
 * Signature: (J)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1unlock_1events(JNIEnv *env, jclass obj, jlong ctx) {
	clearLibusbJavaError();
	libusb_unlock_events((libusb_context*) (unsigned long) ctx);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_event_handling_ok
 * Signature: (J)I
 ********************************************************************************************/
JNIEXPORT jint JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1event_1handling_1ok(JNIEnv *env, jclass obj, jlong ctx) {
	clearLibusbJavaError();
	return libusb_event_handling_ok((libusb_context*) (unsigned long) ctx);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_event_handler_active
 * Signature: (J)I
 ********************************************************************************************/
JNIEXPORT jint JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1event_1handler_1active(JNIEnv *env, jclass obj, jlong ctx) {
	clearLibusbJavaError();
	return libusb_event_handler_active((libusb_context*) (unsigned long) ctx);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_lock_event_waiters
 * Signature: (J)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1lock_1event_1waiters(JNIEnv *env, jclass obj, jlong ctx) {
	clearLibusbJavaError();
	libusb_lock_event_waiters((libusb_context*) (unsigned long) ctx);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_unlock_event_waiters
 * Signature: (J)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1unlock_1event_1waiters(JNIEnv *env, jclass obj, jlong ctx) {
	clearLibusbJavaError();
	libusb_unlock_event_waiters((libusb_context*) (unsigned long) ctx);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_wait_for_event
 * Signature: (JJ)I
 ********************************************************************************************/
JNIEXPORT jint JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1wait_1for_1event(JNIEnv *env, jclass obj, jlong ctx, jlong timevalue) {
	clearLibusbJavaError();
	if (timevalue) {
		timeval tv;
		tv.tv_sec = timevalue;
		tv.tv_usec = 0;
		return libusb_wait_for_event((libusb_context*) (unsigned long) ctx, &tv);
	}
	return libusb_wait_for_event((libusb_context*) (unsigned long) ctx, NULL);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_handle_events_timeout
 * Signature: (JJ)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1handle_1events_1timeout(JNIEnv *env, jclass obj, jlong ctx, jlong timevalue) {
	clearLibusbJavaError();
	if (timevalue != 0)
	{
		timeval tv;
		tv.tv_sec = timevalue;
		tv.tv_usec = 0;
		ThrowIfUnsuccessful(env, libusb_handle_events_timeout((libusb_context*) (unsigned long) ctx, &tv));
	}
	else
	{
		ThrowIfUnsuccessful(env, libusb_handle_events_timeout((libusb_context*) (unsigned long) ctx, NULL));
	}
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_handle_events
 * Signature: (J)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1handle_1events(JNIEnv *env, jclass obj, jlong ctx) {
	clearLibusbJavaError();
	ThrowIfUnsuccessful(env, libusb_handle_events((libusb_context*) (unsigned long) ctx));
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_handle_events_locked
 * Signature: (JJ)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1handle_1events_1locked(JNIEnv *env, jclass obj, jlong ctx, jlong timevalue) {
	clearLibusbJavaError();
	if (timevalue) {
		timeval tv;
		tv.tv_sec = timevalue;
		tv.tv_usec = 0;
		ThrowIfUnsuccessful(env, libusb_handle_events_locked((libusb_context*) (unsigned long) ctx, &tv));
	}
	else
	{
		ThrowIfUnsuccessful(env, libusb_handle_events_locked((libusb_context*) (unsigned long) ctx, NULL));
	}
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_pollfds_handle_timeouts
 * Signature: (J)I
 ********************************************************************************************/
JNIEXPORT jint JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1pollfds_1handle_1timeouts(JNIEnv *env, jclass obj, jlong ctx) {
	clearLibusbJavaError();
	return libusb_pollfds_handle_timeouts((libusb_context*) (unsigned long) ctx);
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_get_next_timeout
 * Signature: (J)I
 ********************************************************************************************/
JNIEXPORT jint JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1get_1next_1timeout(JNIEnv *env, jclass obj, jlong ctx) {
	clearLibusbJavaError();
	int res;
	timeval tv;

	/*!	\todo	Is this code working correctly if we use it in a 64-Bit environment? Actually
	 * 			it's unlikely to have a timeout of more than 2^(31)-1 seconds. But still it is a
	 * 			possible value. */
	res = libusb_get_next_timeout((libusb_context*) (unsigned long) ctx, &tv);

	if (res > 0)
	{
		res = tv.tv_sec;
	}
	else if (res == 0) {
		res = -1;
	}
	else
	{
		ThrowLibusbError(env, res);
		res = -2;
	}

	return res;
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_set_pollfd_notifiers
 * Signature: (JZ)V
 ********************************************************************************************/
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1set_1pollfd_1notifiers(JNIEnv *env, jclass obj, jlong ctx, jboolean remove) {
	clearLibusbJavaError();
	if (remove)
	{
		libusb_set_pollfd_notifiers((libusb_context*) (unsigned long) ctx, NULL, NULL, env);
	}
	else
	{
		libusb_set_pollfd_notifiers((libusb_context*) (unsigned long) ctx, fd_added_callback, fd_removed_callback, env);
	}
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_get_pollfds
 * Signature: (J)[Lch/ntb/inf/libusbJava/Libusb_pollfd;
 ********************************************************************************************/
JNIEXPORT jobjectArray JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1get_1pollfds(JNIEnv *env, jclass obj, jlong ctx) {
	clearLibusbJavaError();
	int count = 0;
	jobjectArray pollfdArray;
	jobject pollfdObj;
	const libusb_pollfd **fdList;

	fdList = libusb_get_pollfds((libusb_context*) (unsigned long) ctx);
	if (fdList == NULL) {
		return NULL;
	}
	/*determine the length of the list*/
	while (fdList[count]) {
		count++;
	}
	pollfdArray = (jobjectArray) env->NewObjectArray(count, usb_pollfd_clazz, NULL);
	if (!pollfdArray) {
		setLibusbJavaError("libusb_get_pollfds: error NewObjectArry (pollfdArray");
		return NULL;
	}

	for (int i = 0; i < count; i++) {
		pollfdObj = env->NewObject(usb_pollfd_clazz, usb_pollfd_Mid);
		if (!pollfdObj) {
			setLibusbJavaError("libusb_get_pollfds: Error NewObject (pollfdObj)");
			return NULL;
		}
		env->SetObjectArrayElement(pollfdArray, i, pollfdObj);
		env->SetIntField(pollfdObj, usb_pollfdFID_fd, fdList[i]->fd);
		env->SetShortField(pollfdObj, usb_pollfdFID_events, fdList[i]->events);
	}
	return pollfdArray;
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_control_transfer
 * Signature: (JIIII[BII)I
 ********************************************************************************************/
JNIEXPORT jint JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1control_1transfer(JNIEnv *env, jclass obj, jlong handle, jint bmRequestType, jint bRequest, jint wValue, jint wIndex, jbyteArray buffer, jint wLength, jint timeout) {
	clearLibusbJavaError();
	int res;
	unsigned char* data;
	data = (unsigned char*) env->GetByteArrayElements(buffer, NULL);
	res = libusb_control_transfer((libusb_device_handle*) (unsigned long) handle, bmRequestType, bRequest, wValue, wIndex, data, wLength, timeout);
	env->ReleaseByteArrayElements(buffer, (jbyte*) data, 0);

	return res;
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_bulk_transfer
 * Signature: (JB[BII)I
 ********************************************************************************************/
JNIEXPORT jint JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1bulk_1transfer(JNIEnv *env, jclass obj, jlong handle, jbyte endpoint, jbyteArray buffer, jint length, jint timeout) {
	clearLibusbJavaError();
	int libusb_result = 0;
	int bytes_transferred = 0;
	unsigned char* data = (unsigned char*) env->GetByteArrayElements(buffer, NULL);

	libusb_result = libusb_bulk_transfer((libusb_device_handle*) (unsigned long) handle,	endpoint, data, length, &bytes_transferred, timeout);
	env->ReleaseByteArrayElements(buffer, (jbyte*) data, 0);

	if (libusb_result != 0)
	{
		ThrowLibusbError(env, libusb_result);
		bytes_transferred = 0;
	}

	return bytes_transferred;
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_interrupt_transfer
 * Signature: (JB[BII)I
 ********************************************************************************************/
JNIEXPORT jint JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1interrupt_1transfer(JNIEnv *env, jclass obj, jlong handle, jbyte endpoint, jbyteArray buffer, jint length, jint timeout) {
	clearLibusbJavaError();
	int libusb_result;
	int bytes_transferred = 0;
	unsigned char* data = (unsigned char*) env->GetByteArrayElements(buffer, NULL);
	libusb_result = libusb_interrupt_transfer((libusb_device_handle*) (unsigned long) handle, endpoint, data,	length, &bytes_transferred, timeout);
	env->ReleaseByteArrayElements(buffer, (jbyte*) data, 0);

	if (libusb_result != 0)
	{
		ThrowLibusbError(env, libusb_result);
		bytes_transferred = 0;
	}

	return bytes_transferred;
}

/********************************************************************************************
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_strerror
 * Signature: ()Ljava/lang/String;
 ********************************************************************************************/
JNIEXPORT jstring JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1strerror(JNIEnv *env, jclass obj) {
	char *str = "Libusb-1.0 Error";

	/* check for LibusbJava specific errors first*/
	if (libusbJavaError != NULL) {
		str = libusbJavaError;
		clearLibusbJavaError();
	}

	return env->NewStringUTF(str);
}

/*
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    setup
 * Signature: ()I
 */
JNIEXPORT jint JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_setup(JNIEnv *env, jclass obj)
{
	return ReferencesLoad(env);
}

/*
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    teardown
 * Signature: ()V
 */
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_teardown(JNIEnv *env, jclass obj)
{
	ReferencesUnload(env);
}

/*
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    libusb_exceptionTest
 * Signature: (I)V
 */
JNIEXPORT void JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_libusb_1exceptionTest(JNIEnv *env, jclass obj, jint code)
{
	ThrowLibusbError(env, code);
}

/*
 * Class:     ch_ntb_inf_libusbJava_LibusbJava1
 * Method:    to_byteArrayTest
 * Signature: (Ljava/lang/String;I)[B
 */
JNIEXPORT jbyteArray JNICALL Java_ch_ntb_inf_libusbJava_LibusbJava1_to_1byteArrayTest(JNIEnv *env, jclass obj, jstring str, jint size)
{
	jbyteArray result = to_byteArray(env, env->GetStringUTFChars (str, NULL), size);
	env->ReleaseStringUTFChars(str, NULL);
	return result;
}

/********************************************************************************************
 * Class:     LibusbJava_1_0.cpp
 * Method:    transfer_callback
 *
 ********************************************************************************************/
static void LIBUSB_CALL transfer_callback(struct libusb_transfer *transfer) {

	((JNIEnv*) transfer->user_data)->CallVoidMethod(usb_cb_clazz,
			usb_transfer_cb_Mid);
}

/********************************************************************************************
 * Class:     LibusbJava_1_0.cpp
 * Method:    fd_added_callback
 *
 ********************************************************************************************/
static void LIBUSB_CALL fd_added_callback(int fd, short events, void *user_data) {

	((JNIEnv*) user_data)->CallVoidMethod(usb_cb_clazz, usb_fd_added_cb_Mid,
			(jint) fd);
}

/********************************************************************************************
 * Class:     LibusbJava_1_0.cpp
 * Method:    fd_removed_callback
 *
 ********************************************************************************************/
static void LIBUSB_CALL fd_removed_callback(int fd, void *user_data) {

	((JNIEnv*) user_data)->CallVoidMethod(usb_cb_clazz, usb_fd_removed_cb_Mid,
			(jint) fd);
}

/*!	\brief Turns a memory section into a java byte array that can be returned to the java
 *         environment.
 *
 *	\param	env		Environment to create the array for
 *	\param 	data	Pointer to the data to be put in the array
 *	\param	len		[bytes] Length of the data to be put into the array
 *
 *	\return	The pointer to the newly created byte array. NULL if an error occured
 *
 *	\note	If NULL is returned, #libusbJavaError is set to a matching error string.
 *
 * 	\test	Tested through the unit-test of the java part of this dll by passing a string
 * 			with length 5 and checking the result.
 * 	\test	Tested through the Unit-Test of the java part of this dll by passing a string
 * 			with length 0 and checking the result.
 */
static __inline jbyteArray JNICALL to_byteArray(JNIEnv *env, const void *data, size_t len)
{
	jbyteArray result = env->NewByteArray(len);

	if (result != NULL) {
		/* By using SetByteArrayRegion, we avoid that the JNI layer first copies the data of
		 * the array in our space just to overwrite them. As we just allocated the
		 * byte array with a length of "len", the set operation can never fail. The check for an
		 * exception can therefore be omitted. */
		env->SetByteArrayRegion(result, 0, len, (const signed char *)data);
#if 0 /* No need to check for exceptions here... */
		if (env->ExceptionOccurred()){
			setLibusbJavaError("to_byteArray: unable to copy data to array");
			env->DeleteLocalRef(result);
			result = NULL;
		}
#endif
	}
	else {
		setLibusbJavaError("to_byteArray: out of memory");
	}

	return result;
}

/*!	\brief	Convenience function that throws an exception in the callers environment if
 *         	the given result is not "success"
 *
 *			This function can be used to wrap calls to the libusb if no further reaction
 *			on a unsuccessful result is needed, than throwing an exception in the java
 *			environment.
 *
 *	\param	env				Java environment of the caller
 *	\param 	libusb_result	Result code of the libusb call. If the result is != 0, a
 *	       					LibusbError is thrown.
 *
 *	\test	Code-Review
 */
static __inline void JNICALL ThrowIfUnsuccessful(JNIEnv *env, int libusb_result)
{
	if (libusb_result != 0)
	{
		ThrowLibusbError(env, libusb_result);
	}
}

/*!	\brief Throws an exception of type LibusbError in the calling Java environment.
 *
 * 	\param	env		Environment to throw the exception in
 * 	\param	code	Error code that represents the cause of the exception
 *
 * 	\test	Tested through the unit-test of the java part of this dll
 */
static __inline void JNICALL ThrowLibusbError(JNIEnv *env, jint code)
{
    jmethodID constructor = NULL;
    jthrowable exception = NULL;

    jclass clazz = env->FindClass("ch/ntb/inf/libusbJava/exceptions/LibusbError");
    if (clazz == NULL)
    {
    	goto no_class;
    }

    constructor = env->GetMethodID(clazz, "<init>", "(I)V");
	if (constructor == NULL)
	{
		goto no_constructor;
	}

	exception = (jthrowable)env->NewObject(clazz, constructor, code);
	if (exception == NULL)
	{
		goto no_object;
	}

	if (env->Throw(exception) != 0)
	{
		goto throw_failed;
	}

	env->DeleteLocalRef(exception);
	env->DeleteLocalRef(clazz);

	return;

/* Error Handling. All errors covered here are caused by JNI callbacks and have 
 * therefore already thrown appropriate exceptions in the Java environment.
 * Therefore we only have to cleanup what we constructed. */
throw_failed:
	env->DeleteLocalRef(exception);

no_object:

no_constructor:
	env->DeleteLocalRef(clazz);

no_class:

	return;
}

/*!	\brief Loads all class References from the environment.
 *
 * 	\param	env	Pointer to an environment enabling access to the jvm
 *
 * 	\return
 * 			-  0 if the references could be loaded successfully
 * 			- <0 if an error occured
 */
static __inline int ReferencesLoad(JNIEnv *env)
{
	int result = -1;

	if (info.jni.refs_loaded != 0)
	{
		info.jni.refs_loaded = -1;
	}
	else
	{
		result = 0;
	}

	return result;

	return result;
}

static __inline void ReferencesUnload(JNIEnv *env)
{
	if (info.jni.refs_loaded == 0)
		return;
}

#ifdef DO_UNIT_TEST
	TEST_CASE(JVMTest)
	{
		TEST_CONTEXT();

		ThrowLibusbError(env, -1);
		jthrowable e = env->ExceptionOccurred();
		CuAssert(tc, "LibusbError-Exception occured", e != NULL);
		env->ExceptionClear();
	}
#endif

#ifdef DO_UNIT_TEST
	typedef CuSuite* (*tSuiteNew)(void);

	extern "C" JNIEXPORT CuSuite* GetLibusbJavaSuite(tSuiteNew SuiteNew, JNIEnv *env);

/*!	\brief Exports the test suite for the libraries helper functions
 *
 *	\param	SuiteNew	Pointer to an allocator function for a CuTest instance
 *	\param	env			JNI Environment for the test
 *
 *	\return	A fully setup test suite. */
JNIEXPORT CuSuite* GetLibusbJavaSuite(tSuiteNew SuiteNew, JNIEnv *env)
{
	CuSuite* suite = SuiteNew();

	SUITE_ADD_TEST(suite, JNI_OnLoad_test);
	SUITE_ADD_TEST(suite, JVMTest);

	test_context.env = env;
	return suite;
}
#endif
