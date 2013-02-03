#ifndef USBDEVICE_H_
#define USBDEVICE_H_

	#include <jni.h>
	#include <libusb.h>

	int		Usb_Device_connect(JNIEnv *env);
	void	Usb_Device_disconnect(JNIEnv *env);

	jobject 		Usb_Device_create(JNIEnv *env, libusb_device *handle, uint8_t address);
	libusb_device *	Usb_Device_HandleGet(JNIEnv *env, jobject dev);
	void			Usb_Device_HandleSet(JNIEnv *env, jobject dev, libusb_device *handle);
	void			Usb_Device_ConfigSet(JNIEnv *env, jobject dev, jobjectArray config);
	void			Usb_Device_DescriptorSet(JNIEnv *env, jobject dev, jobject desc);
	void 			Usb_Device_NextSet(JNIEnv *env, jobject dev, jobject next);
	void 			Usb_Device_PrevSet(JNIEnv *env, jobject dev, jobject prev);

#endif /* USBDEVICE_H_ */
