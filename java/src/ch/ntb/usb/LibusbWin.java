package ch.ntb.usb;

/**
 * This class represents the Java Native Interface to the LibUsbWin.dll.<br>
 * <br>
 * <h1>Project Description</h1>
 * Java LibUsb-Win32 is a Java wrapper for the LibUsb-Win32 USB library. <a
 * href="http://libusb-win32.sourceforge.net/">LibUsb-Win32</a> is a port of
 * the USB library <a href="http://libusb.sourceforge.net/">libusb</a> to the
 * Windows operating systems. The library allows user space applications to
 * access any USB device on Windows in a generic way without writing any line of
 * kernel driver code.
 * 
 * @author schlaepfer
 * 
 */
public class LibusbWin {

	private LibusbWin() {
	}

	// Core
	/**
	 * Just like the name implies, <code>usb_init</code> sets up some internal
	 * structures. <code>usb_init</code> must be called before any other
	 * libusb functions.
	 */
	public static native void usb_init();

	/**
	 * <code>usb_find_busses</code> will find all of the busses on the system.
	 * 
	 * @return the number of changes since previous call to this function (total
	 *         of new busses and busses removed).
	 */
	public static native int usb_find_busses();

	/**
	 * <code>usb_find_devices</code> will find all of the devices on each bus.
	 * This should be called after <code>usb_find_busses</code>.
	 * 
	 * @return the number of changes since the previous call to this function
	 *         (total of new device and devices removed).
	 */
	public static native int usb_find_devices();

	/**
	 * <code>usb_get_busses</code> simply returns the value of the global
	 * variable usb_busses. This was implemented for those languages that
	 * support C calling convention and can use shared libraries, but don't
	 * support C global variables (like Delphi).
	 * 
	 * @return the structure of all busses and devices. <b>Note:</b> The java
	 *         objects are copies of the C structs.
	 */
	public static native Usb_Bus usb_get_busses();

	// Device Operations
	/**
	 * <code>usb_open</code> is to be used to open up a device for use.
	 * <code>usb_open</code> must be called before attempting to perform any
	 * operations to the device.
	 * 
	 * @param dev
	 *            The device to open.
	 * @return a handle used in future communication with the device.
	 */
	public static native int usb_open(Usb_Device dev);

	/**
	 * <code>usb_close</code> closes a device opened with
	 * <code>usb_open</code>.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @return 0 on success or < 0 on error.
	 */
	public static native int usb_close(int dev_handle);

	/**
	 * Sets the active configuration of a device
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param configuration
	 *            The value as specified in the descriptor field
	 *            bConfigurationValue.
	 * @return 0 on success or < 0 on error.
	 */
	public static native int usb_set_configuration(int dev_handle,
			int configuration);

	/**
	 * Sets the active alternate setting of the current interface
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param alternate
	 *            The value as specified in the descriptor field
	 *            bAlternateSetting.
	 * @return 0 on success or < 0 on error.
	 */
	public static native int usb_set_altinterface(int dev_handle, int alternate);

	/**
	 * Clears any halt status on an endpoint.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param ep
	 *            The value specified in the descriptor field bEndpointAddress.
	 * @return 0 on success or < 0 on error.
	 */
	public static native int usb_clear_halt(int dev_handle, int ep);

	/**
	 * Resets a device by sending a RESET down the port it is connected to.<br>
	 * <br>
	 * <b>Causes re-enumeration:</b> After calling <code>usb_reset</code>,
	 * the device will need to re-enumerate and thusly, requires you to find the
	 * new device and open a new handle. The handle used to call
	 * <code>usb_reset</code> will no longer work.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @return 0 on success or < 0 on error.
	 */
	public static native int usb_reset(int dev_handle);

	/**
	 * Claim an interface of a device.<br>
	 * <br>
	 * <b>Must be called!:</b> <code>usb_claim_interface</code> must be
	 * called before you perform any operations related to this interface (like
	 * <code>usb_set_altinterface, usb_bulk_write</code>, etc).
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param interface_
	 *            The value as specified in the descriptor field
	 *            bInterfaceNumber.
	 * @return 0 on success or < 0 on error.
	 */
	public static native int usb_claim_interface(int dev_handle, int interface_);

	/**
	 * Releases a previously claimed interface
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param interface_
	 *            The value as specified in the descriptor field
	 *            bInterfaceNumber.
	 * @return 0 on success or < 0 on error.
	 */
	public static native int usb_release_interface(int dev_handle,
			int interface_);

	// Control Transfers
	/**
	 * Performs a control request to the default control pipe on a device. The
	 * parameters mirror the types of the same name in the USB specification.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param requesttype
	 * @param request
	 * @param value
	 * @param index
	 * @param bytes
	 * @param size
	 * @param timeout
	 * @return the number of bytes written/read or < 0 on error.
	 */
	public static native int usb_control_msg(int dev_handle, int requesttype,
			int request, int value, int index, byte[] bytes, int size,
			int timeout);

	/**
	 * Retrieves the string descriptor specified by index and langid from a
	 * device.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param index
	 * @param langid
	 * @param buf
	 * @param buflen
	 * @return the number of bytes returned in buf or < 0 on error.
	 */
	public static native int usb_get_string(int dev_handle, int index,
			int langid, String buf, int buflen);

	/**
	 * <code>usb_get_string_simple</code> is a wrapper around
	 * <code>usb_get_string</code> that retrieves the string description
	 * specified by index in the first language for the descriptor.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param index
	 * @param buf
	 * @param buflen
	 * @return the number of bytes returned in buf or < 0 on error.
	 */
	public static native int usb_get_string_simple(int dev_handle, int index,
			String buf, int buflen);

	/**
	 * Retrieves a descriptor from the device identified by the type and index
	 * of the descriptor from the default control pipe.<br>
	 * <br>
	 * See <code>usb_get_descriptor_by_endpoint</code> for a function that
	 * allows the control endpoint to be specified.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param type
	 * @param index
	 * @param buf
	 * @param size
	 * @return the number of bytes read for the descriptor or < 0 on error.
	 */
	public static native int usb_get_descriptor(int dev_handle, byte type,
			byte index, String buf, int size);

	/**
	 * Retrieves a descriptor from the device identified by the type and index
	 * of the descriptor from the control pipe identified by ep.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param ep
	 * @param type
	 * @param index
	 * @param buf
	 * @param size
	 * @return the number of bytes read for the descriptor or < 0 on error.
	 */
	public static native int usb_get_descriptor_by_endpoint(int dev_handle,
			int ep, byte type, byte index, String buf, int size);

	// Bulk Transfers
	/**
	 * Performs a bulk write request to the endpoint specified by ep.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param ep
	 * @param bytes
	 * @param size
	 * @param timeout
	 * @return the number of bytes written on success or < 0 on error.
	 */
	public static native int usb_bulk_write(int dev_handle, int ep,
			byte[] bytes, int size, int timeout);

	/**
	 * Performs a bulk read request to the endpoint specified by ep.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param ep
	 * @param bytes
	 * @param size
	 * @param timeout
	 * @return the number of bytes read on success or < 0 on error.
	 */
	public static native int usb_bulk_read(int dev_handle, int ep,
			byte[] bytes, int size, int timeout);

	// Interrupt Transfers
	/**
	 * Performs an interrupt write request to the endpoint specified by ep.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param ep
	 * @param bytes
	 * @param size
	 * @param timeout
	 * @return the number of bytes written on success or < 0 on error.
	 */
	public static native int usb_interrupt_write(int dev_handle, int ep,
			byte[] bytes, int size, int timeout);

	/**
	 * Performs a interrupt read request to the endpoint specified by ep.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param ep
	 * @param bytes
	 * @param size
	 * @param timeout
	 * @return the number of bytes read on success or < 0 on error.
	 */
	public static native int usb_interrupt_read(int dev_handle, int ep,
			byte[] bytes, int size, int timeout);

	/**
	 * Returns the error string after an error occured.
	 * 
	 * @return the last error sring.
	 */
	public static native String usb_strerror();

	/** **************************************************************** */

	static {
		System.load(System.getenv("SystemRoot") + "/system32/LibusbWin.dll");
	}
}