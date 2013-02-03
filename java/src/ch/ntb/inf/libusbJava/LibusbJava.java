/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 * 
 * Changes:
 * 12.04.2012 NTB / Ueli Niederer implemented exception handling
 * 18.10.2010 NTB / Roger Millischer change from native interface to compatibility layer
 * 
 */
package ch.ntb.inf.libusbJava;

import ch.ntb.inf.libusbJava.exceptions.LibusbError;

/**
 * This class is used as compatibility layer for libusb 0.1 projects. 
 * 
 * @deprecated This class will not be subject to test anymore. Be aware that possible 
 *             modifications could always break the functionality. For new
 * 			   projects only use {@link LibusbJava1}. 
 */
public class LibusbJava {

	private static Usb_Bus busses = null;
	private static long defaultCTX = 0;
	/**
	 * System error codes.<br>
	 * This list is not complete! For more error codes see the file 'errorno.h'
	 * on your system.
	 */
	public static final int ERROR_SUCCESS = LibusbError.ERROR_NONE;
	public static final int ERROR_BUSY = LibusbError.ERROR_BUSY;
	public static final int ERROR_INVALID_PARAMETER = LibusbError.ERROR_INVALID_PARAM;
	public static final int ERROR_TIMEDOUT = LibusbError.ERROR_TIMEOUT;
	public static final int ERROR_IO_ERROR = LibusbError.ERROR_IO;
	public static final int ERROR_NOT_ENOUGH_MEMORY = LibusbError.ERROR_NO_MEM;

	/**
	 * Sets the debugging level of libusb.<br>
	 * 
	 * The range is from 0 to 255, where 0 disables debug output and 255 enables
	 * all output. On application start, debugging is disabled (0).
	 * 
	 * @param level
	 *            0 to 255
	 */
	public static void usb_set_debug(int level) {
		LibusbJava1.libusb_set_debug(0, level);
	}

	// Core
	/**
	 * Just like the name implies, <code>usb_init</code> sets up some internal
	 * structures. <code>usb_init</code> must be called before any other libusb
	 * functions.
	 */
	public static void usb_init() {
		if (defaultCTX != 0) {
			return;
		}
		try {
			defaultCTX = LibusbJava1.libusb_init();
		} catch (LibusbError e) {
			System.err.println("LibusbJava-1.0 init failed with errorcode: "
					+ e.getMessage());
			e.printStackTrace();
			defaultCTX = 0;
		}
		LibusbJava1.libusb_set_debug(0, 0);
	}

	/**
	 * <code>usb_find_busses</code> will find all of the busses on the system.
	 * 
	 * @return the number of changes since previous call to this function (total
	 *         of new busses and busses removed).
	 */
	public static int usb_find_busses() {
		int changes = 0;
		long tmpBusNumber;
		Usb_Bus nbusses = null;
		Usb_Bus bus = nbusses, tmp;
		boolean found = false;
		Usb_Device devices = null;
		devices = LibusbJava1.libusb_get_device_list(0);

		// no busses
		if (devices.getDevnum() == -1) {
			while (busses != null) {
				changes++;
				busses = busses.getNext();
			}
			return changes;
		}
		// find busses
		while (devices != null) {
			tmpBusNumber = (long) LibusbJava1.libusb_get_bus_number(devices);
			while (bus != null) {
				if (bus.getLocation() == tmpBusNumber) {
					found = true;
					break;
				}
				bus = bus.getNext();
			}
			if (found) {
				devices = devices.getNext();
				found = false;
				continue;
			}
			if (nbusses == null) {
				nbusses = new Usb_Bus(Long.toString(tmpBusNumber), tmpBusNumber);
			} else {
				tmp = new Usb_Bus(Long.toString(tmpBusNumber), tmpBusNumber);
				bus = nbusses;
				// append
				while (bus.getNext() != null) {
					bus = bus.getNext();
				}
				tmp.setPrev(bus);
				bus.setNext(tmp);
			}
			bus = nbusses;
			devices = devices.getNext();
		}
		// compare to existing bus-list
		bus = busses;
		tmp = nbusses;
		found = false;
		while (bus != null) {
			tmpBusNumber = bus.getLocation();
			while (tmp != null) {
				if (tmpBusNumber == tmp.getLocation()) {
					found = true;
					// remove from nbusses list
					if (tmp.getPrev() != null) {
						tmp.getPrev().setNext(tmp.getNext());
					} else {
						nbusses = tmp.getNext();
					}
					if (tmp.getNext() != null) {
						tmp.getNext().setPrev(tmp.getPrev());
					}
					break;
				}
				tmp = tmp.getNext();
			}
			if (!found) {
				// remove from busses an increment changes
				if (bus.getPrev() != null) {
					bus.getPrev().setNext(bus.getNext());
				} else {
					busses = bus.getNext();
				}
				if (bus.getNext() != null) {
					bus.getNext().setPrev(bus.getPrev());
				}
				changes++;
			}
			bus = bus.getNext();
			tmp = nbusses;
			found = false;
		}
		// add new busses
		bus = busses;
		while (tmp != null) {
			tmp = tmp.getNext();
			changes++;
		}
		if (busses != null) {
			while (bus.getNext() != null) {
				bus = bus.getNext();
			}
			bus.setNext(nbusses);
		} else {
			busses = nbusses;
		}
		return changes;
	}

	/**
	 * <code>usb_find_devices</code> will find all of the devices on each bus.
	 * This should be called after <code>usb_find_busses</code>.
	 * 
	 * @return the number of changes since the previous call to this function
	 *         (total of new device and devices removed).
	 */
	public static int usb_find_devices() {
		int changes = 0;
		byte tmpDevnum;
		long tmpBusNumber;
		Usb_Bus bus = busses;
		boolean found = false;
		Usb_Device devices, dev, ndev;

		devices = LibusbJava1.libusb_get_device_list(0);
		// Compare to existing bus-structure and remove removed devices
		while (bus != null) {
			dev = bus.getDevices();
			while (dev != null) {
				tmpDevnum = dev.getDevnum();
				ndev = devices;
				while (ndev != null) {
					// if dev already exist remove from ndev list
					if (tmpDevnum == ndev.getDevnum()) {
						found = true;
						if (ndev.getPrev() != null) {
							ndev.getPrev().setNext(ndev.getNext());
						} else {
							devices = ndev.getNext();
						}
						if (ndev.getNext() != null) {
							ndev.getNext().setPrev(ndev.getPrev());
						}
						break;
					}
					ndev = ndev.getNext();
				}
				if (!found) {
					// remove device from bus an increment changes
					if (dev.getPrev() != null) {
						dev.getPrev().setNext(dev.getNext());
					} else {
						bus.setDevices(dev.getNext());
					}
					if (dev.getNext() != null) {
						dev.getNext().setPrev(dev.getPrev());
					}
					changes++;
				} else {
					found = false;
				}
				dev = dev.getNext();
			}
			bus = bus.getNext();
		}
		// Add new Devices
		bus = busses;
		while (devices != null) {
			ndev = devices.getNext();
			// find correct bus
			tmpBusNumber = (long) LibusbJava1.libusb_get_bus_number(devices);
			while (bus != null) {
				if (bus.getLocation() == tmpBusNumber) {
					break;
				}
				bus = bus.getNext();
			}
			// insert device
			if (bus != null) {
				if (bus.getDevices() == null) {
					devices.setNext(null);
				} else {
					devices.setNext(bus.getDevices());
					devices.getNext().setPrev(devices);
				}
				devices.setPrev(null);
				bus.setDevices(devices);
				devices.setBus(bus);
				changes++;
			}
			devices = ndev;
			bus = busses;
		}
		return changes;
	}

	/**
	 * <code>usb_get_busses</code> returns a tree of descriptor objects.<br>
	 * The tree represents the bus structure with devices, configurations,
	 * interfaces and endpoints. Note that this is only a copy. To refresh the
	 * information, <code>usb_get_busses()</code> must be called again.<br>
	 * The name of the objects contained in the tree is starting with
	 * <code>Usb_</code>.
	 * 
	 * @return the structure of all busses and devices. <code>Note:</code> The
	 *         java objects are copies of the C structs.
	 */
	public static Usb_Bus usb_get_busses() {
		return busses;
	}

	// Device Operations
	/**
	 * <code>usb_open</code> is to be used to open up a device for use.
	 * <code>usb_open</code> must be called before attempting to perform any
	 * operations to the device.
	 * 
	 * @param dev
	 *            The device to open.
	 * @return a handle used in future communication with the device. 0 if an
	 *         error has occurred.
	 */
	public static long usb_open(Usb_Device dev) {
		long handle = 0;
		
		try {
			handle = LibusbJava1.libusb_open(dev);
		}
		catch (LibusbError e) {
			System.err.println("LibusbJava-1.0 failed with errorcode: "
					+ e.getMessage());
			e.printStackTrace();
			handle = 0;
		}
		
		return handle;
	}

	/**
	 * <code>usb_close</code> closes a device opened with <code>usb_open</code>.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @return 0
	 */
	public static int usb_close(long dev_handle) {
		LibusbJava1.libusb_close(dev_handle);
		return 0;
	}

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
	public static int usb_set_configuration(long dev_handle, int configuration) {
		int result = 0;
		
		try {
			LibusbJava1.libusb_set_configuration(dev_handle, configuration);
		} catch (LibusbError e) {
			result = -1;
		}
		
		return result;
	}

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
	public static int usb_set_altinterface(long dev_handle, int alternate) {
		Usb_Device dev = LibusbJava1.libusb_get_device(dev_handle);
		int nofInterfaces = LibusbJava1
				.libusb_get_active_config_descriptor(dev).getBNumInterfaces();
		int interface_number, success = 0;
		for (interface_number = 0; interface_number < nofInterfaces; interface_number++) {
			try
			{
				LibusbJava1.libusb_release_interface(dev_handle, interface_number);
				
				try
				{
					LibusbJava1.libusb_claim_interface(dev_handle, interface_number);
				}
				catch (LibusbError e)
				{
					return e.getErrorCode();
				}
				break;
			}
			catch (LibusbError e)
			{
				/* Move ahead. */
			}
		}
		
		try {
			LibusbJava1.libusb_set_interface_alt_setting(dev_handle, interface_number, alternate);
			success = 0;
		} 
		catch (LibusbError e) {
			success = -1;
		}
		
		return success;
	}

	/**
	 * Clears any halt status on an endpoint.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param ep
	 *            The value specified in the descriptor field bEndpointAddress.
	 * @return 0 on success or < 0 on error.
	 */
	public static int usb_clear_halt(long dev_handle, int ep) {
		int result = 0;
		
		try {
			LibusbJava1.libusb_clear_halt(dev_handle, (short) ep);
		} catch (LibusbError e) {
			result = e.getErrorCode();
		}
		
		return result;
	}

	/**
	 * Resets a device by sending a RESET down the port it is connected to.<br>
	 * <br>
	 * <b>Causes re-enumeration:</b> After calling <code>usb_reset</code>, the
	 * device will need to re-enumerate and thusly, requires you to find the new
	 * device and open a new handle. The handle used to call
	 * <code>usb_reset</code> will no longer work.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @return 0 on success or < 0 on error.
	 */
	public static int usb_reset(long dev_handle) {
		int res = 0;
		
		try {
			LibusbJava1.libusb_claim_interface(dev_handle, 0);
			try {
				LibusbJava1.libusb_reset_device(dev_handle);
			}
			catch (LibusbError e) {
				res = e.getErrorCode();
			}
			LibusbJava1.libusb_release_interface(dev_handle, 0);
			LibusbJava1.libusb_close(dev_handle);
		} catch (LibusbError e) {
			/* Ignore all errors of these calls */
		}
		
		return res;

	}

	/**
	 * Claim an interface of a device.<br>
	 * <br>
	 * <b>Must be called!:</b> <code>usb_claim_interface</code> must be called
	 * before you perform any operations related to this interface (like
	 * <code>usb_set_altinterface, usb_bulk_write</code>, etc).
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param interface_
	 *            The value as specified in the descriptor field
	 *            bInterfaceNumber.
	 * @return 0 on success or < 0 on error.
	 */
	public static int usb_claim_interface(long dev_handle, int interface_) {
		int result = 0;
		
		try {
			LibusbJava1.libusb_claim_interface(dev_handle, interface_);
		} catch (LibusbError e) {
			result = e.getErrorCode();
		}
		
		return result;
	}

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
	public static int usb_release_interface(long dev_handle, int interface_) {
		int result = 0;
		
		try {
			LibusbJava1.libusb_release_interface(dev_handle, interface_);
		} catch (LibusbError e) {
			result = e.getErrorCode();
		}
		
		return result;
	}

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
	public static int usb_control_msg(long dev_handle, int requesttype,
			int request, int value, int index, byte[] bytes, int size,
			int timeout) {
		return LibusbJava1.libusb_control_transfer(dev_handle, requesttype,
				request, value, index, bytes, size, timeout);
	}

	/**
	 * Retrieves the string descriptor specified by index and langid from a
	 * device.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param index
	 * @param langid
	 * @return the descriptor String or null
	 */
	public static String usb_get_string(long dev_handle, int index, int langid) {
		String result;
		
		try {
			result = LibusbJava1.libusb_get_string_descriptor(dev_handle, (short) index, langid, 255);
		} catch (LibusbError e) {
			result = null;
		}
		
		return result;
	}

	/**
	 * <code>usb_get_string_simple</code> is a wrapper around
	 * <code>usb_get_string</code> that retrieves the string description
	 * specified by index in the first language for the descriptor.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param index
	 * @return the descriptor String or null
	 */
	public static String usb_get_string_simple(long dev_handle, int index) {
		String result = null;
		
		try {
			result = LibusbJava1.libusb_get_string_descriptor_ascii(dev_handle,(short) index, 255);
		} catch (LibusbError e) {
			result = null;
		}
		
		return result;
	}

	/**
	 * Retrieves a descriptor from the device identified by the type and index
	 * of the descriptor from the default control pipe.<br>
	 * <br>
	 * See {@link #usb_get_descriptor_by_endpoint(long, int, byte, byte, int)}
	 * for a function that allows the control endpoint to be specified.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param type
	 * @param index
	 * @param size
	 *            number of charactes which will be retrieved (the length of the
	 *            resulting String)
	 * @return the descriptor String or null
	 */
	public static byte[] usb_get_descriptor(long dev_handle, byte type,
			byte index, int size) {
		byte[] result = null;
		
		try {
			result = LibusbJava1.libusb_get_descriptor(dev_handle, type, index, size);
		} catch (LibusbError e) {
			e.printStackTrace();
		}
		
		return result;
	}

	/**
	 * Retrieves a descriptor from the device identified by the type and index
	 * of the descriptor from the control pipe identified by ep.
	 * 
	 * @param dev_handle
	 *            The handle to the device.
	 * @param ep
	 * @param type
	 * @param index
	 * @param size
	 *            number of charactes which will be retrieved (the length of the
	 *            resulting String)
	 * @return the descriptor String or null
	 */
	public static String usb_get_descriptor_by_endpoint(long dev_handle,
			int ep, byte type, byte index, int size) {

		// We just send a control message directly;
		byte data[] = new byte[size];
		char string[] = new char[size];
		int transfered;
		transfered = LibusbJava1.libusb_control_transfer(dev_handle,
				0x80 | (ep & 0xFF), 0x07, (type << 8) | index, 0, data, size,
				1000);
		if (transfered > 0) {
			for (int i = 0; i < transfered; i++) {
				string[i] = (char) data[i];
			}
			return String.valueOf(string);
		}
		return null;
	}

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
	public static int usb_bulk_write(long dev_handle, int ep, byte[] bytes,
			int size, int timeout) {
		int result = LibusbError.ERROR_OTHER;

		try {
			result = LibusbJava1.libusb_bulk_transfer(dev_handle, (byte) ep,
					bytes, size, timeout);
		} catch (LibusbError e) {
			result = e.getErrorCode();
		}

		return result;
	}

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
	public static int usb_bulk_read(long dev_handle, int ep, byte[] bytes,
			int size, int timeout) {
		int result = LibusbError.ERROR_OTHER;

		try {
			result = LibusbJava1.libusb_bulk_transfer(dev_handle, (byte) ep,
					bytes, size, timeout);
		} catch (LibusbError e) {
			result = e.getErrorCode();
		}

		return result;
	}

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
	public static int usb_interrupt_write(long dev_handle, int ep,
			byte[] bytes, int size, int timeout) {
		int result = LibusbError.ERROR_OTHER;

		try {
			result = LibusbJava1.libusb_interrupt_transfer(dev_handle, (byte) ep, 
					bytes, size, timeout);
		} catch (LibusbError e) {
			result = e.getErrorCode();
		}

		return result;
	}

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
	public static int usb_interrupt_read(long dev_handle, int ep, byte[] bytes,
			int size, int timeout) {
		int result = LibusbError.ERROR_OTHER;

		try {
			result = LibusbJava1.libusb_interrupt_transfer(dev_handle, (byte) ep, 
					bytes, size, timeout);
		} catch (LibusbError e) {
			result = e.getErrorCode();
		}

		return result;
	}

	/**
	 * Returns the error string after an error occured.
	 * 
	 * @return the last error sring.
	 */
	public static String usb_strerror() {
		return LibusbJava1.libusb_strerror();
	}

}