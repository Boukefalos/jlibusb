/** 
 * Java libusb1.0 wrapper
 * Roger Millischer <roger.millischer at ntb.ch>
 *
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.inf.libusbJava;

import ch.ntb.inf.libusbJava.exceptions.LibusbError;

/**
 * This class represents the Java Native Interface to the shared library which
 * is (with some exceptions) a one-to-one representation of the libusb1.0 API.<br>
 * <br>
 * <h1>Project Description</h1> Java libusb1.0 is a Java wrapper for the
 * libusb1.0 USB library.
 * 
 * <a href="http://www.libusb.org/">libusb</a> aim is to create a library for
 * use by user level applications to access USB devices regardless of OS.<br>
 * <a href="http://www.libusb.org/wiki/windows_backend">Libusb-Windows
 * backend</a> aim of this project is to bring a Windows backend to the mainline
 * libusb 1.0 branch, so that libusb 1.0 can easily be used on Windows
 * platforms. The library allows user space applications to access any USB
 * device on Windows in a generic way without writing any line of kernel driver
 * code.<br>
 * <br>
 * The API description of this class has been copied from the <a
 * href="http://libusb.sourceforge.net/api-1.0/index.html">libusb-1.0 API
 * Reference</a> and adapted where neccessary.<br>
 * 
 */
public class LibusbJava1 {
	/**
	 * Set message verbosity.
	 * <ul>
	 * <li>Level 0: no messages ever printed by the library (default)</li>
	 * <li>Level 1: error messages are printed to stderr</li>
	 * <li>Level 2: warning and error messages are printed to stderr</li>
	 * <li>Level 3: informational messages are printed to stdout, warning and
	 * error messages are printed to stderr</li>
	 * </ul>
	 * 
	 * The default level is 0, which means no messages are ever printed. If you
	 * choose to increase the message verbosity level, ensure that your
	 * application does not close the stdout/stderr file descriptors.<br>
	 * <br>
	 * You are advised to set level 3. libusb is conservative with its message
	 * logging and most of the time, will only log messages that explain error
	 * conditions and other oddities. This will help you debug your software.<br>
	 * <br>
	 * If the LIBUSB_DEBUG environment variable was set when libusb was
	 * initialized, this function does nothing: the message verbosity is fixed
	 * to the value in the environment variable.<br>
	 * <br>
	 * If libusb was compiled without any message logging, this function does
	 * nothing: you'll never get any messages.<br>
	 * <br>
	 * If libusb was compiled with verbose debug message logging, this function
	 * does nothing: you'll always get messages from all levels.<br>
	 * <br>
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 * @param level
	 *            debug level to set
	 */

	public static native void libusb_set_debug(long ctx, int level);

	/**
	 * Initialize libusb.<br>
	 * <br>
	 * This function must be called before calling any other libusb function.<br>
	 * <br>
	 * 
	 * @return a context to operate on
	 * @throws LibusbError
	 *             if an error occured
	 */
	public static native long libusb_init() throws LibusbError;

	/**
	 * Deinitialize libusb.<br>
	 * <br>
	 * Should be called after closing all open devices and before your
	 * application terminates.<br>
	 * <br>
	 * 
	 * @param ctx
	 *            the context to deinitialize, or 0 if the default context has
	 *            been used.
	 */
	public static native void libusb_exit(long ctx);

	/**
	 * Returns a list of USB devices currently attached to the system.<br>
	 * <br>
	 * This is your entry point into finding a USB device to operate.<br>
	 * <br>
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 * 
	 * @return Usb_Devices currently attached to the system<br>
	 *         if no device is attached the devnum of the returned device is set
	 *         to -1<br>
	 *         or NULL if an error occurs
	 */
	public static native Usb_Device libusb_get_device_list(long ctx);

	/**
	 * Get the number of the bus that a device is connected to.
	 * 
	 * @param dev
	 *            a device
	 * @return the bus number
	 */
	public static native short libusb_get_bus_number(Usb_Device dev);

	/**
	 * Calculate the maximum packet size which a specific endpoint is capable is
	 * sending or receiving in the duration of 1 microframe.<br>
	 * <br>
	 * Only the active configution is examined. The calculation is based on the
	 * wMaxPacketSize field in the endpoint descriptor as described in section
	 * 9.6.6 in the USB 2.0 specifications.<br>
	 * <br>
	 * If acting on an isochronous or interrupt endpoint, this function will
	 * multiply the value found in bits 0:10 by the number of transactions per
	 * microframe (determined by bits 11:12). Otherwise, this function just
	 * returns the numeric value found in bits 0:10.<br>
	 * <br>
	 * This function is useful for setting up isochronous transfers, for example
	 * you might pass the return value from this function to
	 * <code>libusb_set_iso_packet_lengths()</code> in order to set the length
	 * field of every isochronous packet in a transfer.<br>
	 * <br>
	 * Since libusb v1.0.3.<br>
	 * <br>
	 * This functionality is not available on Windows-Systems<br>
	 * <br>
	 * 
	 * @param dev
	 *            a device
	 * @param endpoint
	 *            address of the endpoint in question
	 * @return the maximum packet size which can be sent/received on this
	 *         endpoint<br>
	 *         LIBUSB_ERROR_NOT_FOUND if the endpoint does not exist<br>
	 *         LIBUSB_ERROR_OTHER on other failure<br>
	 *         TODO wenn funktion impl. richtig verlinken @see
	 *         libusb_set_iso_packet_lengths()
	 */

	public static native int libusb_get_max_iso_packet_size(Usb_Device dev,
			short endpoint);

	/**
	 * Increment the reference count of a device.
	 * 
	 * @param dev
	 *            the device to reference
	 * @return the same device
	 */
	public static native Usb_Device libusb_ref_device(Usb_Device dev);

	/**
	 * Decrement the reference count of a device.<br>
	 * <br>
	 * If the decrement operation causes the reference count to reach zero, the
	 * device shall be destroyed.<br>
	 * <br>
	 * 
	 * @param dev
	 *            the device to unreference
	 */

	public static native void libusb_unref_device(Usb_Device dev);

	/**
	 * Open a device and obtain a device handle.<br>
	 * <br>
	 * A handle allows you to perform I/O on the device in question.<br>
	 * <br>
	 * Internally, this function adds a reference to the device and makes it
	 * available to you through <code>libusb_get_device()</code>. This reference
	 * is removed during <code>libusb_close()</code>.<br>
	 * <br>
	 * This is a non-blocking function; no requests are sent over the bus.<br>
	 * <br>
	 * 
	 * @param dev
	 *            the device to open
	 * 
	 * @return a device handle >= 0
	 * 
	 * @throws LibusbError
	 *             in case of an error<br>
	 *             Possible causes are:<br>
	 *             - ERROR_NO_MEM on memory allocation failure<br>
	 *             - ERROR_ACCESS if the user has insufficient permissions<br>
	 *             - ERROR_NO_DEVICE if the device has been disconnected<br>
	 *             - another ERROR code on other failure
	 * 
	 * @see #libusb_get_device(long)
	 */
	public static native long libusb_open(Usb_Device dev) throws LibusbError;

	/**
	 * Convenience function for finding a device with a particular
	 * idVendor/idProduct combination.<br>
	 * <br>
	 * This function is intended for those scenarios where you are using libusb
	 * to knock up a quick test application - it allows you to avoid calling
	 * <code>libusb_get_device_list()</code> and worrying about
	 * traversing/freeing the list.<br>
	 * <br>
	 * This function has limitations and is hence not intended for use in real
	 * applications: if multiple devices have the same IDs it will only give you
	 * the first one, etc.<br>
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 * @param vendor_id
	 *            the idVendor value to search for
	 * @param product_id
	 *            the idProduct value to search for
	 * @return a handle for the first found device, or NULL on error or if the
	 *         device could not be found.
	 * @see #libusb_get_device_list(long)
	 */
	public static native long libusb_open_device_with_vid_pid(long ctx,
			int vendor_id, int product_id);

	/**
	 * Close a device handle.<br>
	 * <br>
	 * Should be called on all open handles before your application exits.<br>
	 * <br>
	 * Internally, this function destroys the reference that was added by
	 * <code>libusb_open()</code> on the given device.<br>
	 * <br>
	 * This is a non-blocking function; no requests are sent over the bus.<br>
	 * <br>
	 * 
	 * @param dev_handle
	 *            the handle to close
	 */
	public static native void libusb_close(long dev_handle);

	/**
	 * Get the underlying device for a handle.<br>
	 * <br>
	 * This function does not modify the reference count of the returned device,
	 * so do not feel compelled to unreference it when you are done.<br>
	 * <br>
	 * 
	 * @param dev_handle
	 *            a device handle
	 * @return the underlying {@link Usb_Device}
	 */
	public static native Usb_Device libusb_get_device(long dev_handle);

	/**
	 * Determine the bConfigurationValue of the currently active configuration.<br>
	 * <br>
	 * You could formulate your own control request to obtain this information,
	 * but this function has the advantage that it may be able to retrieve the
	 * information from operating system caches (no I/O involved).<br>
	 * <br>
	 * If the OS does not cache this information, then this function will block
	 * while a control transfer is submitted to retrieve the information.<br>
	 * <br>
	 * This function will return a value of 0 if the device is in unconfigured
	 * state.<br>
	 * <br>
	 * 
	 * @param dev_handle
	 *            a device handle
	 * @return bConfigurationValue of the currently active configuration
	 * @throws LibusbError
	 *             in case of an error<br>
	 *             Possible error causes are:<br>
	 *             - ERROR_NO_DEVICE if the device has been disconnected -
	 *             another ERROR code on other failure
	 */
	public static native int libusb_get_configuration(long dev_handle)
			throws LibusbError;

	/**
	 * Set the active configuration for a device.<br>
	 * <br>
	 * The operating system may or may not have already set an active
	 * configuration on the device. It is up to your application to ensure the
	 * correct configuration is selected before you attempt to claim interfaces
	 * and perform other operations.<br>
	 * <br>
	 * If you call this function on a device already configured with the
	 * selected configuration, then this function will act as a lightweight
	 * device reset: it will issue a SET_CONFIGURATION request using the current
	 * configuration, causing most USB-related device state to be reset
	 * (altsetting reset to zero, endpoint halts cleared, toggles reset).<br>
	 * <br>
	 * You cannot change/reset configuration if your application has claimed
	 * interfaces - you should free them with
	 * {@link #libusb_release_interface(long, int)} first. You cannot
	 * change/reset configuration if other applications or drivers have claimed
	 * interfaces.<br>
	 * <br>
	 * A configuration value of -1 will put the device in unconfigured state.
	 * The USB specifications state that a configuration value of 0 does this,
	 * however buggy devices exist which actually have a configuration 0.<br>
	 * <br>
	 * You should always use this function rather than formulating your own
	 * SET_CONFIGURATION control request. This is because the underlying
	 * operating system needs to know when such changes happen.<br>
	 * <br>
	 * This is a blocking function.<br>
	 * <br>
	 * 
	 * @param dev_handle
	 *            a device handle
	 * @param configuration
	 *            the bConfigurationValue of the configuration you wish to
	 *            activate, or -1 if you wish to put the device in unconfigured
	 *            state
	 * @throws LibusbError
	 *             in case of an error<br>
	 *             Possible error causes are:<br>
	 *             - ERROR_NOT_FOUND if the requested configuration does not
	 *             exist<br>
	 *             - ERROR_BUSY if interfaces are currently claimed<br>
	 *             - ERROR_NO_DEVICE if the device has been disconnected<br>
	 *             - another LIBUSB_ERROR code on other failure<br>
	 */
	public static native void libusb_set_configuration(long dev_handle,
			int configuration) throws LibusbError;

	/**
	 * Claim an interface on a given device handle.<br>
	 * <br>
	 * You must claim the interface you wish to use before you can perform I/O
	 * on any of its endpoints.<br>
	 * <br>
	 * It is legal to attempt to claim an already-claimed interface, in which
	 * case libusb just returns 0 without doing anything.<br>
	 * <br>
	 * Claiming of interfaces is a purely logical operation, it does not cause
	 * any requests to be sent over the bus. Interface claiming is used to
	 * instruct the underlying operating system that your application wishes to
	 * take ownership of the interface.<br>
	 * <br>
	 * This is a non-blocking function.<br>
	 * <br>
	 * 
	 * @param dev_handle
	 *            a device handle
	 * @param interface_number
	 *            the bInterfaceNumber of the interface you wish to claim
	 * @throws LibusbError
	 *             in case of an error<br>
	 *             Possible causes for errors are:<br>
	 *             - ERROR_NOT_FOUND if the interface was not claimed<br>
	 *             - ERROR_NO_DEVICE if the device has been disconnected<br>
	 *             - ERROR_BUSY if another program or driver has claimed the
	 *             interface<br>
	 *             - another LIBUSB_ERROR code on other failure<br>
	 */
	public static native void libusb_claim_interface(long dev_handle,
			int interface_number) throws LibusbError;

	/**
	 * Release an interface previously claimed with
	 * {@link #libusb_claim_interface(long, int)}.<br>
	 * <br>
	 * You should release all claimed interfaces before closing a device handle.<br>
	 * <br>
	 * This is a blocking function. A SET_INTERFACE control request will be sent
	 * to the device, resetting interface state to the first alternate setting.<br>
	 * <br>
	 * 
	 * @param dev_handle
	 *            a device handle
	 * @param interface_number
	 *            the bInterfaceNumber of the previously-claimed interface
	 * @throws LibusbError
	 *             in case of an error<br>
	 *             Possible causes for errors are:<br>
	 *             - ERROR_NOT_FOUND if the interface was not claimed<br>
	 *             - ERROR_NO_DEVICE if the device has been disconnected<br>
	 *             - another ERROR code on other failure<br>
	 */
	public static native void libusb_release_interface(long dev_handle,
			int interface_number) throws LibusbError;

	/**
	 * Activate an alternate setting for an interface.<br>
	 * <br>
	 * The interface must have been previously claimed with
	 * {@link #libusb_claim_interface(long, int)}.<br>
	 * <br>
	 * You should always use this function rather than formulating your own
	 * SET_INTERFACE control request. This is because the underlying operating
	 * system needs to know when such changes happen.<br>
	 * <br>
	 * This is a blocking function.<br>
	 * <br>
	 * 
	 * @param dev_handle
	 *            a device handle
	 * @param interface_number
	 *            the bInterfaceNumber of the previously-claimed interface
	 * @param alternate_setting
	 *            the bAlternateSetting of the alternate setting to activate
	 * @throws LibusbError
	 *             in case of an error<br>
	 *             Possible causes for errors are:<br>
	 *             - ERROR_NOT_FOUND if the interface was not claimed, or the
	 *             requested alternate setting does not exist<br>
	 *             - ERROR_NO_DEVICE if the device has been disconnected<br>
	 *             - another LIBUSB_ERROR code on other failure<br>
	 */
	public static native void libusb_set_interface_alt_setting(long dev_handle,
			int interface_number, int alternate_setting) throws LibusbError;

	/**
	 * Clear the halt/stall condition for an endpoint.<br>
	 * <br>
	 * Endpoints with halt status are unable to receive or transmit data until
	 * the halt condition is stalled.<br>
	 * <br>
	 * You should cancel all pending transfers before attempting to clear the
	 * halt condition.<br>
	 * <br>
	 * This is a blocking function.<br>
	 * <br>
	 * 
	 * @param dev_handle
	 *            a device handle
	 * @param endpoint
	 *            the endpoint to clear halt status
	 * @throws LibusbError
	 *             in case of an error<br>
	 *             Possible causes for errors are:<br>
	 *             - ERROR_NOT_FOUND if the endpoint does not exist<br>
	 *             - ERROR_NO_DEVICE if the device has been disconnected<br>
	 *             - another LIBUSB_ERROR code on other failure<br>
	 */
	public static native void libusb_clear_halt(long dev_handle, short endpoint)
			throws LibusbError;

	/**
	 * Perform a USB port reset to reinitialize a device.<br>
	 * <br>
	 * The system will attempt to restore the previous configuration and
	 * alternate settings after the reset has completed.<br>
	 * <br>
	 * If the reset fails, the descriptors change, or the previous state cannot
	 * be restored, the device will appear to be disconnected and reconnected.
	 * This means that the device handle is no longer valid (you should close
	 * it) and rediscover the device. A return code of LIBUSB_ERROR_NOT_FOUND
	 * indicates when this is the case.<br>
	 * <br>
	 * This is a blocking function which usually incurs a noticeable delay.<br>
	 * <br>
	 * 
	 * @param dev_handle
	 *            a handle of the device to reset
	 * @throws LibusbError
	 *             in case of an error<br>
	 *             Possible causes for errors are:<br>
	 *             - ERROR_NOT_FOUND if re-enumeration is required, or if the
	 *             device has been disconnected<br>
	 *             - another LIBUSB_ERROR code on other failure<br>
	 */
	public static native void libusb_reset_device(long dev_handle)
			throws LibusbError;

	/**
	 * Determine if a kernel driver is active on an interface.<br>
	 * <br>
	 * If a kernel driver is active, you cannot claim the interface, and libusb
	 * will be unable to perform I/O.<br>
	 * <br>
	 * 
	 * @param dev_handle
	 *            a device handle
	 * @param interface_number
	 *            the interface to check
	 * @return 0 if no kernel driver is active<br>
	 *         1 if a kernel driver is active<br>
	 * @throws LibusbError
	 *             in case of an error<br>
	 *             Possible causes for errors are:<br>
	 *             - ERROR_NO_DEVICE if the device has been disconnected<br>
	 *             - another LIBUSB_ERROR code on other failure<br>
	 * @see #libusb_detach_kernel_driver(long, int)
	 */
	public static native int libusb_kernel_driver_active(long dev_handle,
			int interface_number) throws LibusbError;

	/**
	 * Detach a kernel driver from an interface.<br>
	 * <br>
	 * If successful, you will then be able to claim the interface and perform
	 * I/O.<br>
	 * <br>
	 * 
	 * @param dev_handle
	 *            a device handle
	 * @param interface_number
	 *            the interface to detach the driver from
	 * @throws LibusbError
	 *             in case of an error<br>
	 *             Possible causes for errors are:<br>
	 *             - ERROR_NOT_FOUND if no kernel driver was active<br>
	 *             - ERROR_INVALID_PARAM if the interface does not exist<br>
	 *             - ERROR_NO_DEVICE if the device has been disconnected<br>
	 *             - another ERROR code on other failure<br>
	 * @see #libusb_kernel_driver_active(long, int)
	 */
	public static native void libusb_detach_kernel_driver(long dev_handle,
			int interface_number) throws LibusbError;

	/**
	 * Re-attach an interface's kernel driver, which was previously detached
	 * using {@link #libusb_detach_kernel_driver(long, int)}.<br>
	 * 
	 * @param dev_handle
	 *            a device handle
	 * @param interface_number
	 *            the interface to attach the driver from
	 * @throws LibusbError
	 *             in case of an error<br>
	 *             Possible causes for errors are:<br>
	 *             - ERROR_NOT_FOUND if no kernel driver was active<br>
	 *             - ERROR_INVALID_PARAM if the interface does not exist<br>
	 *             - ERROR_NO_DEVICE if the device has been disconnected<br>
	 *             - ERROR_BUSY if the driver cannot be attached because the
	 *             interface is claimed by a program or driver<br>
	 *             - another ERROR code on other failure<br>
	 * @see #libusb_kernel_driver_active(long, int)
	 */
	public static native void libusb_attach_kernel_driver(long dev_handle,
			int interface_number) throws LibusbError;

	/**
	 * Get the USB configuration descriptor for the currently active
	 * configuration.<br>
	 * <br>
	 * This is a non-blocking function which does not involve any requests being
	 * sent to the device.<br>
	 * <br>
	 * 
	 * @param dev
	 *            a device
	 * @return {@link Usb_Config_Descriptor}
	 */
	public static native Usb_Config_Descriptor libusb_get_active_config_descriptor(
			Usb_Device dev);

	/**
	 * Retrieve a string descriptor in C style ASCII.<br>
	 * <br>
	 * Wrapper around
	 * {@link #libusb_get_string_descriptor(long, short, int, int)}. Uses the
	 * first language supported by the device.
	 * 
	 * @param dev_handle
	 *            a device handle
	 * @param desc_index
	 *            the index of the descriptor to retrieve
	 * @param size
	 *            number of charactes which will be retrieved (the length of the
	 *            resulting String)
	 * @return a string which contains the string descriptor
	 * @throws LibusbError
	 *             in case of an error<br>
	 */
	public static native String libusb_get_string_descriptor_ascii(
			long dev_handle, short desc_index, int size) throws LibusbError;

	/**
	 * Retrieve a descriptor from the default control pipe.<br>
	 * <br>
	 * This is a convenience function which formulates the appropriate control
	 * message to retrieve the descriptor.
	 * 
	 * @param dev_handle
	 *            a device handle
	 * @param desc_type
	 *            the descriptor type
	 * @param desc_index
	 *            the index of the descriptor to retrieve
	 * @param size
	 *            number of bytes which will be retrieved (the length of the
	 *            resulting byte[])
	 * @return a byte[] which contains the descriptor or null on failure
	 * @throws LibusbError
	 *             in case of an error<br>
	 * 
	 */
	public static native byte[] libusb_get_descriptor(long dev_handle,
			int desc_type, short desc_index, int size) throws LibusbError;

	/**
	 * Retrieve a descriptor from a device.<br>
	 * <br>
	 * This is a convenience function which formulates the appropriate control
	 * message to retrieve the descriptor. The string returned is Unicode, as
	 * detailed in the USB specifications.
	 * 
	 * @param dev_handle
	 *            a device handle
	 * @param desc_index
	 *            the index of the descriptor to retrieve
	 * @param langid
	 *            the language ID for the string descriptor
	 * @param size
	 *            number of charactes which will be retrieved (the length of the
	 *            resulting String)
	 * @return a string which contains the string descriptor
	 * @throws LibusbError
	 *             in case of an error<br>
	 * @see #libusb_get_string_descriptor_ascii(long, short, int)
	 */
	public static native String libusb_get_string_descriptor(long dev_handle,
			short desc_index, int langid, int size) throws LibusbError;

	/**
	 * Allocate a libusb transfer with a specified number of isochronous packet
	 * descriptors.<br>
	 * 
	 * The returned transfer is pre-initialized for you. When the new transfer
	 * is no longer needed, it should be freed with
	 * <code>libusb_free_transfer()</code>.<br>
	 * <br>
	 * Transfers intended for non-isochronous endpoints (e.g. control, bulk,
	 * interrupt) should specify an iso_packets count of zero.<br>
	 * <br>
	 * For transfers intended for isochronous endpoints, specify an appropriate
	 * number of packet descriptors to be allocated as part of the transfer. The
	 * returned transfer is not specially initialized for isochronous I/O you
	 * are still required to set the <b>num_iso_packets</b> and <b>type</b>
	 * fields accordingly.<br>
	 * <br>
	 * It is safe to allocate a transfer with some isochronous packets and then
	 * use it on a non-isochronous endpoint. If you do this, ensure that at time
	 * of submission, num_iso_packets is 0 and that type is set appropriately.<br>
	 * 
	 * @param iso_packets
	 *            number of isochronous packet descriptors to allocate
	 * @return a newly allocated transfernumber, or NULL on error
	 */
	public static native long libusb_alloc_transfer(int iso_packets);

	/**
	 * Free a transfer.
	 * 
	 * This should be called for all transfers allocated with
	 * <code>libusb_alloc_transfer()</code>.<br>
	 * <br>
	 * It is legal to call this function with a NULL transfer. In this case, the
	 * function will simply return safely.<br>
	 * <br>
	 * It is not legal to free an active transfer (one which has been submitted
	 * and has not yet completed).<br>
	 * <br>
	 * 
	 * @param transfernumber
	 *            the transfer to free
	 */
	public static native void libusb_free_transfer(long transfernumber);

	/**
	 * Submit a transfer.<br>
	 * <br>
	 * This function will fire off the USB transfer and then return immediately.<br>
	 * <br>
	 * 
	 * @param transfernumber
	 *            the transfer to submit
	 * @throws LibusbError
	 *             in case of an error<br>
	 *             Possible causes for errors are:<br>
	 *             - ERROR_NO_DEVICE if the device has been disconnected<br>
	 *             - ERROR_BUSY if the transfer has already been submitted. <br>
	 *             - another LIBUSB_ERROR code on other failure <br>
	 */
	public static native void libusb_submit_transfer(long transfernumber)
			throws LibusbError;

	/**
	 * Asynchronously cancel a previously submitted transfer.<br>
	 * <br>
	 * This function returns immediately, but this does not indicate
	 * cancellation is complete. Libusb_event will be invoked at some later time
	 * to notify you.
	 * 
	 * @param transfernumber
	 *            a transfer
	 * @throws LibusbError
	 *             in case of an error<br>
	 *             Possible causes for errors are:<br>
	 *             - ERROR_NOT_FOUND if the transfer is already complete or
	 *             cancelled. <br>
	 *             - a LIBUSB_ERROR code on failure <br>
	 */
	public static native void libusb_cancel_transfer(long transfernumber)
			throws LibusbError;

	/**
	 * Get the data section of a control transfer.<br>
	 * <br>
	 * This convenience function is here to remind you that the data does not
	 * start until 8 bytes into the actual buffer, as the setup packet comes
	 * first.<br>
	 * <br>
	 * Calling this function only makes sense from a transfer event handler, or
	 * situations where you have already allocated a suitably sized buffer.
	 * 
	 * @param transfernumber
	 *            a transfer
	 * @return the data section from the transfer, null if the transfer number
	 *         was invalid.
	 */
	public static native byte[] libusb_control_transfer_get_data(
			long transfernumber);

	/**
	 * Get the control setup packet of a control transfer.<br>
	 * <br>
	 * This convenience function is here to remind you that the control setup
	 * occupies the first 8 bytes of the transfer data buffer.<br>
	 * <br>
	 * Calling this function only makes sense from a transfer event handler, or
	 * situations where you have already allocated a suitably sized buffer at
	 * transfer->buffer.
	 * 
	 * @param transfernumber
	 *            a transfer
	 * @return the setup packet from the transfer, null if the transfer number
	 *         was invalid.
	 */
	public static native byte[] libusb_control_transfer_get_setup(
			long transfernumber);

	/**
	 * Helper function to populate the setup packet (first 8 bytes of the data
	 * buffer) for a control transfer.<br>
	 * 
	 * @param bmRequestType
	 * @param bRequest
	 * @param wValue
	 * @param wIndex
	 * @param wLength
	 * @return the setup packet
	 */
	public static native byte[] libusb_fill_control_setup(int bmRequestType,
			int bRequest, int wValue, int wIndex, int wLength);

	/**
	 * Helper function to populate the required libusb_transfer fields for a
	 * control transfer.<br>
	 * <br>
	 * If you pass a transfer buffer to this function, the first 8 bytes will be
	 * interpreted as a control setup packet, and the wLength field will be used
	 * to automatically populate the length field of the transfer. Therefore the
	 * recommended approach is:<br>
	 * <br>
	 * 1. Call <code>libusb_fill_control_setup()</code><br>
	 * 2. Allocate a suitably sized data buffer (including space for control
	 * setup)<br>
	 * 3. Put the retrieved setup packet into the firste 8 byte of the data
	 * buffer.<br>
	 * 4. If this is a host-to-device transfer with a data stage, put the data
	 * in place after the setup packet<br>
	 * 5. Call this function<br>
	 * 6. Call <code>libusb_submit_transfer()</code><br>
	 * <br>
	 * It is also legal to pass a NULL buffer to this function, in which case
	 * this function will not attempt to populate the length field. Remember
	 * that you must then populate the buffer and length fields later.
	 * 
	 * @param transfernumber
	 *            transfer to populate
	 * @param dev_handle
	 *            handle of the device that
	 * @param buffer
	 *            data buffer. If provided, this function will interpret the
	 *            first 8 bytes as a setup packet and infer the transfer length
	 *            from that.
	 * @param timeout
	 *            timeout for the transfer in milliseconds
	 */
	public static native void libusb_fill_control_transfer(long transfernumber,
			long dev_handle, byte[] buffer, int timeout);

	/**
	 * Helper function to populate the required libusb_transfer fields for a
	 * bulk transfer.
	 * 
	 * @param transfernumber
	 *            the transfer to populate
	 * @param dev_handle
	 *            handle of the device that will handle the transfer
	 * @param endpoint
	 *            address of the endpoint where this transfer will be sent
	 * @param buffer
	 *            data buffer
	 * @param length
	 *            length of data buffer
	 * @param timeout
	 *            timeout for the transfer in milliseconds
	 */
	public static native void libusb_fill_bulk_transfer(long transfernumber,
			long dev_handle, byte endpoint, byte[] buffer, int length,
			int timeout);

	/**
	 * Helper function to populate the required libusb_transfer fields for an
	 * interrupt transfer.
	 * 
	 * @param transfernumber
	 *            the transfer to populate
	 * @param dev_handle
	 *            handle of the device that will handle the transfer
	 * @param endpoint
	 *            address of the endpoint where this transfer will be sent
	 * @param buffer
	 *            data buffer
	 * @param length
	 *            length of data buffer
	 * @param timeout
	 *            timeout for the transfer in milliseconds
	 */
	public static native void libusb_fill_interrupt_transfer(
			long transfernumber, long dev_handle, byte endpoint, byte[] buffer,
			int length, int timeout);

	/**
	 * Helper function to populate the required libusb_transfer fields for an
	 * isochronous transfer.
	 * 
	 * @param transfernumber
	 *            the transfer to populate
	 * @param dev_handle
	 *            handle of the device that will handle the transfer
	 * @param endpoint
	 *            address of the endpoint where this transfer will be sent
	 * @param buffer
	 *            data buffer
	 * @param length
	 *            length of data buffer
	 * @param num_iso_packets
	 *            the number of isochronous packets
	 * @param timeout
	 *            timeout for the transfer in milliseconds
	 */
	public static native void libusb_fill_iso_transfer(long transfernumber,
			long dev_handle, byte endpoint, byte[] buffer, int length,
			int num_iso_packets, int timeout);

	/**
	 * Convenience function to set the length of all packets in an isochronous
	 * transfer, based on the num_iso_packets field in the transfer structure.
	 * 
	 * @param transfer
	 *            a transfer
	 * @param length
	 *            the length to set in each isochronous packet descriptor
	 */
	public static native void libusb_set_iso_packet_lengths(long transfer,
			int length);

	/**
	 * Convenience function to locate the position of an isochronous packet
	 * within the buffer of an isochronous transfer.<br>
	 * <br>
	 * This is a thorough function which loops through all preceding packets,
	 * accumulating their lengths to find the position of the specified packet.
	 * Typically you will assign equal lengths to each packet in the transfer,
	 * and hence the above method is sub-optimal. You may wish to use
	 * <code>libusb_get_iso_packet_buffer_simple()</code> instead.<br>
	 * <br>
	 * 
	 * @param transfer
	 *            a transfer
	 * @param packet
	 *            the packet to return
	 * @return the packet buffer inside the transfer buffer, or NULL if the
	 *         packet does not exist
	 */
	public static native byte[] libusb_get_iso_packet_buffer(long transfer,
			int packet);

	/**
	 * Convenience function to locate the position of an isochronous packet
	 * within the buffer of an isochronous transfer, for transfers where each
	 * packet is of identical size.<br>
	 * <br>
	 * This function relies on the assumption that every packet within the
	 * transfer is of identical size to the first packet. Calculating the
	 * location of the packet buffer is then just a simple calculation: buffer +
	 * (packet_size * packet)<br>
	 * <br>
	 * Do not use this function on transfers other than those that have
	 * identical packet lengths for each packet.
	 * 
	 * @param transfernumber
	 *            a transfer
	 * @param packet
	 *            he packet to return
	 * @return the packet buffer inside the transfer buffer, or NULL if the
	 *         packet does not exist.
	 */
	public static native byte[] libusb_get_iso_packet_buffer_simple(
			long transfernumber, int packet);

	/**
	 * Attempt to acquire the event handling lock.<br>
	 * <br>
	 * This lock is used to ensure that only one thread is monitoring libusb
	 * event sources at any one time.<br>
	 * <br>
	 * You only need to use this lock if you are developing an application which
	 * calls poll() or select() on libusb's file descriptors directly. If you
	 * stick to libusb's event handling loop functions (e.g.
	 * <code>libusb_handle_events()</code>) then you do not need to be concerned
	 * with this locking.<br>
	 * <br>
	 * While holding this lock, you are trusted to actually be handling events.
	 * If you are no longer handling events, you must call
	 * <code>libusb_unlock_events()</code> as soon as possible.
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 * @return 0 if the lock was obtained successfully <br>
	 *         1 if the lock was not obtained (i.e. another thread holds the
	 *         lock)<br>
	 */
	public static native int libusb_try_lock_events(long ctx);

	/**
	 * Acquire the event handling lock, blocking until successful acquisition if
	 * it is contended.<br>
	 * <br>
	 * This lock is used to ensure that only one thread is monitoring libusb
	 * event sources at any one time.<br>
	 * <br>
	 * You only need to use this lock if you are developing an application which
	 * calls poll() or select() on libusb's file descriptors directly. If you
	 * stick to libusb's event handling loop functions (e.g.
	 * <code>libusb_handle_events()</code>) then you do not need to be concerned
	 * with this locking.<br>
	 * <br>
	 * While holding this lock, you are trusted to actually be handling events.
	 * If you are no longer handling events, you must call
	 * <code>libusb_unlock_events()</code> as soon as possible.
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 */
	public static native void libusb_lock_events(long ctx);

	/**
	 * Release the lock previously acquired with
	 * <code>libusb_try_lock_events()</code> or
	 * <code>libusb_lock_events()</code>.<br>
	 * <br>
	 * Releasing this lock will wake up any threads blocked on
	 * <code>libusb_wait_for_event()</code>.
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 */
	public static native void libusb_unlock_events(long ctx);

	/**
	 * Determine if it is still OK for this thread to be doing event handling.<br>
	 * <br>
	 * Sometimes, libusb needs to temporarily pause all event handlers, and this
	 * is the function you should use before polling file descriptors to see if
	 * this is the case.<br>
	 * <br>
	 * If this function instructs your thread to give up the events lock, you
	 * should just continue the usual logic. On the next iteration, your thread
	 * will fail to obtain the events lock, and will hence become an event
	 * waiter.<br>
	 * <br>
	 * This function should be called while the events lock is held: you don't
	 * need to worry about the results of this function if your thread is not
	 * the current event handler.
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 * @return 1 if event handling can start or continue<br>
	 *         0 if this thread must give up the events lock <br>
	 */
	public static native int libusb_event_handling_ok(long ctx);

	/**
	 * Determine if an active thread is handling events (i.e. if anyone is
	 * holding the event handling lock).
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 * @return 1 if a thread is handling events <br>
	 *         0 if there are no threads currently handling events<br>
	 */
	public static native int libusb_event_handler_active(long ctx);

	/**
	 * Acquire the event waiters lock.<br>
	 * <br>
	 * This lock is designed to be obtained under the situation where you want
	 * to be aware when events are completed, but some other thread is event
	 * handling so calling <code>libusb_handle_events()</code> is not allowed.<br>
	 * <br>
	 * You then obtain this lock, re-check that another thread is still handling
	 * events, then call <code>libusb_wait_for_event()</code>.<br>
	 * <br>
	 * You only need to use this lock if you are developing an application which
	 * calls poll() or select() on libusb's file descriptors directly, and may
	 * potentially be handling events from 2 threads simultaenously. If you
	 * stick to libusb's event handling loop functions (e.g.
	 * <code>libusb_handle_events()</code>) then you do not need to be concerned
	 * with this locking.
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 */
	public static native void libusb_lock_event_waiters(long ctx);

	/**
	 * Release the event waiters lock.
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 */
	public static native void libusb_unlock_event_waiters(long ctx);

	/**
	 * Wait for another thread to signal completion of an event.<br>
	 * <br>
	 * Must be called with the event waiters lock held, see
	 * <code>libusb_lock_event_waiters()</code>.<br>
	 * <br>
	 * This function will block until any of the following conditions are met:<br>
	 * 
	 * 1. The timeout expires <br>
	 * 2. A transfer completes <br>
	 * 3. A thread releases the event handling lock through
	 * <code>libusb_unlock_events()</code><br>
	 * <br>
	 * Condition 1 is obvious. Condition 2 unblocks your thread after the
	 * callback for the transfer has completed. Condition 3 is important because
	 * it means that the thread that was previously handling events is no longer
	 * doing so, so if any events are to complete, another thread needs to step
	 * up and start event handling.<br>
	 * <br>
	 * This function releases the event waiters lock before putting your thread
	 * to sleep, and reacquires the lock as it is being woken up.
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 * @param timeval
	 *            maximum timeout in seconds for this blocking function. 0
	 *            seconds indicates unlimited timeout
	 * @return 0 after a transfer completes or another thread stops event
	 *         handling<br>
	 *         1 if the timeout expired <br>
	 */
	public static native int libusb_wait_for_event(long ctx, long timeval);

	/**
	 * Handle any pending events.<br>
	 * <br>
	 * libusb determines "pending events" by checking if any timeouts have
	 * expired and by checking the set of file descriptors for activity.<br>
	 * <br>
	 * If a zero timeval is passed, this function will handle any
	 * already-pending events and then immediately return in non-blocking style.<br>
	 * <br>
	 * If a non-zero timeval is passed and no events are currently pending, this
	 * function will block waiting for events to handle up until the specified
	 * timeout. If an event arrives or a signal is raised, this function will
	 * return early.
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 * @param timeval
	 *            the maximum time to block waiting for events, or zero for
	 *            non-blocking mode
	 * @throws LibusbError
	 *             in case of an error<br>
	 */
	public static native void libusb_handle_events_timeout(long ctx,
			long timeval) throws LibusbError;

	/**
	 * Handle any pending events in blocking mode with a sensible timeout.<br>
	 * <br>
	 * This timeout is currently hardcoded at 2 seconds but we may change this
	 * if we decide other values are more sensible. For finer control over
	 * whether this function is blocking or non-blocking, or the maximum
	 * timeout, use <code>libusb_handle_events_timeout()</code> instead.
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 * @throws LibusbError
	 *             in case of an error<br>
	 */
	public static native void libusb_handle_events(long ctx) throws LibusbError;

	/**
	 * Handle any pending events by polling file descriptors, without checking
	 * if any other threads are already doing so.<br>
	 * <br>
	 * Must be called with the event lock held, see
	 * <code>libusb_lock_events()</code>.<br>
	 * <br>
	 * This function is designed to be called under the situation where you have
	 * taken the event lock and are calling poll()/select() directly on libusb's
	 * file descriptors (as opposed to using <code>libusb_handle_events()</code>
	 * or similar). You detect events on libusb's descriptors, so you then call
	 * this function with a zero timeout value (while still holding the event
	 * lock).
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 * @param timeval
	 *            the maximum time in seconds to block waiting for events, or
	 *            zero for non-blocking mode
	 * @throws LibusbError
	 *             in case of an error<br>
	 */
	public static native void libusb_handle_events_locked(long ctx, long timeval)
			throws LibusbError;

	/**
	 * Determines whether your application must apply special timing
	 * considerations when monitoring libusb's file descriptors.<br>
	 * <br>
	 * This function is only useful for applications which retrieve and poll
	 * libusb's file descriptors in their own main loop (The more advanced
	 * option).<br>
	 * <br>
	 * Ordinarily, libusb's event handler needs to be called into at specific
	 * moments in time (in addition to times when there is activity on the file
	 * descriptor set). The usual approach is to use
	 * <code>libusb_get_next_timeout()</code> to learn about when the next
	 * timeout occurs, and to adjust your poll()/select() timeout accordingly so
	 * that you can make a call into the library at that time.<br>
	 * <br>
	 * Some platforms supported by libusb do not come with this baggage - any
	 * events relevant to timing will be represented by activity on the file
	 * descriptor set, and <code>libusb_get_next_timeout()</code> will always
	 * return 0. This function allows you to detect whether you are running on
	 * such a platform.<br>
	 * <br>
	 * Since v1.0.5.
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 * @return 0 if you must call into libusb at times determined by
	 *         <code>libusb_get_next_timeout()</code>, or 1 if all timeout
	 *         events are handled internally or through regular activity on the
	 *         file descriptors.
	 */
	public static native int libusb_pollfds_handle_timeouts(long ctx);

	/**
	 * Determine the next internal timeout that libusb needs to handle.<br>
	 * <br>
	 * You only need to use this function if you are calling poll() or select()
	 * or similar on libusb's file descriptors yourself - you do not need to use
	 * it if you are calling libusb_handle_events() or a variant directly.<br>
	 * <br>
	 * You should call this function in your main loop in order to determine how
	 * long to wait for select() or poll() to return results. libusb needs to be
	 * called into at this timeout, so you should use it as an upper bound on
	 * your select() or poll() call.<br>
	 * <br>
	 * When the timeout has expired, call into libusb_handle_events_timeout()
	 * (perhaps in non-blocking mode) so that libusb can handle the timeout.<br>
	 * <br>
	 * This function may return an zero timevalue. If this is the case, it
	 * indicates that libusb has a timeout that has already expired so you
	 * should call libusb_handle_events_timeout() or similar immediately. A
	 * return code of -1 indicates that there are no pending timeouts.<br>
	 * <br>
	 * On some platforms, this function will always returns -1 (no pending
	 * timeouts).
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 * @return time to next timeout<br>
	 *         -1 if there are no pending timeouts<br>
	 * @throws LibusbError
	 *             If an error is occured in libusb
	 */
	public static native int libusb_get_next_timeout(long ctx)
			throws LibusbError;

	/**
	 * Register notification functions for file descriptor additions/removals.<br>
	 * <br>
	 * These functions will be invoked for every new or removed file descriptor
	 * that libusb uses as an event source.<br>
	 * <br>
	 * To remove notifiers, set remove TRUE.<br>
	 * <br>
	 * Note that file descriptors may have been added even before you register
	 * these notifiers (e.g. at <code>libusb_init()</code> time).<br>
	 * <br>
	 * Additionally, note that the removal notifier may be called during
	 * <code>libusb_exit()</code> (e.g. when it is closing file descriptors that
	 * were opened and added to the poll set at <code>libusb_init()</code>
	 * time).<br>
	 * If you don't want this, remove the notifiers immediately before calling
	 * <code>libusb_exit()</code>.
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 * @param remove
	 *            select add or remove notifiers
	 */
	public static native void libusb_set_pollfd_notifiers(long ctx,
			boolean remove);

	/**
	 * Retrieve a array of file descriptors that should be polled by your main
	 * loop as libusb event sources.<br>
	 * <br>
	 * 
	 * The actual list contents must not be touched.
	 * 
	 * @param ctx
	 *            the context to operate on, or NULL for the default context
	 * @return array of Libusb_pollfd or NULL on error
	 */
	public static native Libusb_pollfd[] libusb_get_pollfds(long ctx);

	/**
	 * Perform a USB control transfer.<br>
	 * <br>
	 * The direction of the transfer is inferred from the bmRequestType field of
	 * the setup packet.<br>
	 * <br>
	 * The wValue, wIndex and wLength fields values should be given in
	 * host-endian byte order.
	 * 
	 * @param dev_handle
	 *            a handle for the device to communicate with
	 * @param bmRequestType
	 *            the request type field for the setup packet
	 * @param bRequest
	 *            the request field for the setup packet
	 * @param wValue
	 *            the value field for the setup packet
	 * @param wIndex
	 *            the index field for the setup packet
	 * @param data
	 *            a suitably-sized data buffer for either input or output
	 *            (depending on direction bits within bmRequestType)
	 * @param wLength
	 *            the length field for the setup packet. The data buffer should
	 *            be at least this size.
	 * @param timeout
	 *            timeout (in millseconds) that this function should wait before
	 *            giving up due to no response being received. For an unlimited
	 *            timeout, use value 0.
	 * @return on success, the number of bytes actually transferred <br>
	 *         LIBUSB_ERROR_TIMEOUT if the transfer timed out <br>
	 *         LIBUSB_ERROR_PIPE if the control request was not supported by the
	 *         device <br>
	 *         LIBUSB_ERROR_NO_DEVICE if the device has been disconnected <br>
	 *         another LIBUSB_ERROR code on other failures
	 */
	public static native int libusb_control_transfer(long dev_handle,
			int bmRequestType, int bRequest, int wValue, int wIndex,
			byte[] data, int wLength, int timeout);

	/**
	 * Perform a USB bulk transfer.<br>
	 * <br>
	 * The direction of the transfer is inferred from the direction bits of the
	 * endpoint address.<br>
	 * <br>
	 * For bulk reads, the length field indicates the maximum length of data you
	 * are expecting to receive. If less data arrives than expected, this
	 * function will return that data, so be sure to check the returned value.<br>
	 * <br>
	 * You should also check this value for bulk writes. Not all of the data may
	 * have been written.
	 * 
	 * @param dev_handle
	 *            a handle for the device to communicate with
	 * @param endpoint
	 *            the address of a valid endpoint to communicate with
	 * @param data
	 *            a suitably-sized data buffer for either input or output
	 *            (depending on endpoint)
	 * @param length
	 *            for bulk writes, the number of bytes from data to be sent. for
	 *            bulk reads, the maximum number of bytes to receive into the
	 *            data buffer.
	 * @param timeout
	 *            timeout (in millseconds) that this function should wait before
	 *            giving up due to no response being received. For an unlimited
	 *            timeout, use value 0.
	 * @return The number of bytes actually transferred
	 * @throws LibusbError
	 *             in case an error occured<br>
	 *             Possible causes are:<br>
	 *             - ERROR_TIMEOUT if the transfer timed out <br>
	 *             - ERROR_PIPE if the control request was not supported by the
	 *             device <br>
	 *             - ERROR_OVERFLOW if the device offered more data<br>
	 *             - ERROR_NO_DEVICE if the device has been disconnected <br>
	 *             - another code on other failures.
	 */
	public static native int libusb_bulk_transfer(long dev_handle,
			byte endpoint, byte[] data, int length, int timeout)
			throws LibusbError;

	/**
	 * Perform a USB interrupt transfer.<br>
	 * <br>
	 * The direction of the transfer is inferred from the direction bits of the
	 * endpoint address.<br>
	 * <br>
	 * For bulk reads, the length field indicates the maximum length of data you
	 * are expecting to receive. If less data arrives than expected, this
	 * function will return that data, so be sure to check the returned value.<br>
	 * <br>
	 * You should also check this value for bulk writes. Not all of the data may
	 * have been written.<br>
	 * <br>
	 * The default endpoint bInterval value is used as the polling interval.
	 * 
	 * @param dev_handle
	 *            a handle for the device to communicate with
	 * @param endpoint
	 *            the address of a valid endpoint to communicate with
	 * @param data
	 *            a suitably-sized data buffer for either input or output
	 *            (depending on endpoint)
	 * @param length
	 *            for bulk writes, the number of bytes from data to be sent. for
	 *            bulk reads, the maximum number of bytes to receive into the
	 *            data buffer.
	 * @param timeout
	 *            timeout (in millseconds) that this function should wait before
	 *            giving up due to no response being received. For an unlimited
	 *            timeout, use value 0.
	 * @return The number of bytes actually transferred <br>
	 * @throws LibusbError
	 *             in case an error occured<br>
	 *             Possible causes are:<br>
	 *             - ERROR_TIMEOUT if the transfer timed out <br>
	 *             - ERROR_PIPE if the control request was not supported by the
	 *             device <br>
	 *             - ERROR_OVERFLOW if the device offered more data<br>
	 *             - ERROR_NO_DEVICE if the device has been disconnected <br>
	 *             - another ERROR code on other failures
	 */
	public static native int libusb_interrupt_transfer(long dev_handle,
			byte endpoint, byte[] data, int length, int timeout)
			throws LibusbError;

	/**
	 * Returns the error string after an error occured.
	 * 
	 * @return the last error sring.
	 */
	public static native String libusb_strerror();

	static {
		String os = System.getProperty("os.name");
		if (os.contains("Windows")) {
			System.loadLibrary("LibusbJava-1_0");
		} else {
			System.loadLibrary("usbJava-1.0");
		}

		/*
		 * After loading the library, we register a "shutdown hook" to be called
		 * for cleaning up stuff in the library when exiting.
		 */
		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				try {
					LibusbJava1.teardown();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		/*
		 * After setting up the cleanup callback, it's now safe to call the code
		 * that initializes stuff in the native library
		 */
		int setup_result = setup();

		if (setup_result < 0) {
			throw new UnsatisfiedLinkError("Could not setup the library. ("
					+ setup_result + ")");
		}
	}

	/**
	 * This method is used to setup stuff in the native library. After calling
	 * this function, {@link #teardown()} has to be called if the native library
	 * is not used anymore.
	 * 
	 * @return - 0 if successful - <0 if an error occured
	 */
	private static native int setup();

	/**
	 * This method cleans up stuff initialized using {@link #setup()}.
	 */
	private static native void teardown();
}
