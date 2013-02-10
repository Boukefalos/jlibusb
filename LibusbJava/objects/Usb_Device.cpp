/*!	\file
 *
 * 	C++ Stub for the java class of a java class ch.ntb.inf.libusbJava.Usb_Device.
 *
 *	\author Ueli Niederer <ueli.niederer @ ntb.ch>
 */
#include "Usb_Device.h"
#include <string.h>

#ifdef DO_UNIT_TEST
#	include <test/CuTest.h>

#	define TEST_CASE(name)			static void name(CuTest *tc)
#endif

typedef struct JField {
	const char *type;
	const char *name;
	jfieldID id;
}tJField;

typedef struct Usb_Device {
	int loaded;

	jclass clazz;

	jmethodID constructor;

	jfieldID next;
	jfieldID prev;
	jfieldID filename;
	jfieldID bus;
	jfieldID descriptor;
	jfieldID config;
	jfieldID devnum;
	jfieldID num_children;
	jfieldID children;
	jfieldID devStructAddr;
}tUsb_Device;

#define LOAD_FIELD(env, me, type, name)	me->name = env->GetFieldID(me->clazz, #name, (type))
#if 0
#define SANITY_CHECK()	{ if (me->loaded == 0) Usb_Device_connect(env); }
#else
#define SANITY_CHECK()
#endif

static tUsb_Device  instance = { 0 };
static tUsb_Device *me = &instance;

int Usb_Device_connect(JNIEnv *env)
{
	jobject theClass = env->FindClass("ch/ntb/inf/libusbJava/Usb_Device");
	if (theClass == NULL) goto no_class;

	me->clazz = (jclass) env->NewGlobalRef(theClass); 	// make it global to avoid class unloading and therefore
													// invalidating the references obtained.
	if (me->clazz == NULL) goto no_memory;

	me->constructor = env->GetMethodID(me->clazz, "<init>", "()V");
	if (me->constructor == NULL) goto no_constructor;

	LOAD_FIELD(env, me, "Lch/ntb/inf/libusbJava/Usb_Device;", next);
	LOAD_FIELD(env, me, "Lch/ntb/inf/libusbJava/Usb_Device;", prev);
	LOAD_FIELD(env, me, "Ljava/lang/String;", filename);
	LOAD_FIELD(env, me, "Lch/ntb/inf/libusbJava/Usb_Bus;", bus);
	LOAD_FIELD(env, me, "Lch/ntb/inf/libusbJava/Usb_Device_Descriptor;", descriptor);
	LOAD_FIELD(env, me, "[Lch/ntb/inf/libusbJava/Usb_Config_Descriptor;", config);
	LOAD_FIELD(env, me, "B", devnum);
	LOAD_FIELD(env, me, "B", num_children);
	LOAD_FIELD(env, me, "Lch/ntb/inf/libusbJava/Usb_Device;", children);
	LOAD_FIELD(env, me, "J", devStructAddr);

	me->loaded = 1;
	return 0;

no_constructor:
	env->DeleteGlobalRef(me->clazz);
no_memory:
no_class:
	return -1;
}

void Usb_Device_disconnect(JNIEnv *env)
{
	if (me->loaded == 0) return;

	env->DeleteGlobalRef(me->clazz);
	memset(me, 0, sizeof(tUsb_Device));
}

jobject Usb_Device_create(JNIEnv *env, libusb_device *handle, uint8_t address){
	if (me->loaded == 0) return NULL;

	jobject result = env->NewObject(me->clazz, me->constructor);

	if (result != NULL) {
		char	filename[4];

		snprintf(filename, 4, "%03d", address);

		env->SetLongField(result, me->devStructAddr, (jlong)handle);
		env->SetByteField(result, me->devnum, address);
		env->SetObjectField(result, me->filename, env->NewStringUTF(filename));

		env->SetByteField(result, me->num_children, 0);		/*Hardcoded to 0, like the compatibility Layer*/
		env->SetObjectField(result, me->children, NULL);	/*Hardcoded to NULL, like the compatibility Layer*/
	}

	return result;
}

libusb_device *	Usb_Device_HandleGet(JNIEnv *env, jobject dev)
{
	if (me->loaded == 0) return NULL;

	return (libusb_device*)env->GetLongField(dev, me->devStructAddr);
}

void Usb_Device_HandleSet(JNIEnv *env, jobject dev, libusb_device *handle)
{
	env->SetLongField(dev, me->devStructAddr, (jlong)handle);
}

void Usb_Device_ConfigSet(JNIEnv *env, jobject dev, jobjectArray config)
{
	env->SetObjectField(dev, me->config, config);
}

void Usb_Device_DescriptorSet(JNIEnv *env, jobject dev, jobject desc)
{
	env->SetObjectField(dev, me->descriptor, desc);
}

void Usb_Device_NextSet(JNIEnv *env, jobject dev, jobject next) {
	env->SetObjectField(dev, me->next, next);
}

void Usb_Device_PrevSet(JNIEnv *env, jobject dev, jobject prev) {
	env->SetObjectField(dev, me->prev, prev);
}
