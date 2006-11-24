/* 
 * Java LibUsb-Win32 wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <libusb@drip.ch>
 *
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb;

/**
 * Represents the descriptor of a USB configuration.<br>
 * A USB device can have several different configuration.<br>
 * <br>
 * The length of the configuration descriptor is
 * {@link ch.ntb.usb.Usb_Descriptor#USB_DT_CONFIG_SIZE} and the type is
 * {@link ch.ntb.usb.Usb_Descriptor#USB_DT_CONFIG}.
 * 
 * @author schlaepfer
 * 
 */
public class Usb_Config_Descriptor extends Usb_Descriptor {

	/**
	 * Maximum number of configuration per device
	 */
	public static final int USB_MAXCONFIG = 8;

	/**
	 * Total length in bytes of data returned.<br>
	 * When the configuration descriptor is read, it returns the entire
	 * configuration hierarchy which includes all related interface and endpoint
	 * descriptors. The <code>wTotalLength</code> field reflects the number of
	 * bytes in the hierarchy.
	 */
	public short wTotalLength;

	/**
	 * Number of interfaces
	 */
	public byte bNumInterfaces;

	/**
	 * Value to use as an argument to select this configuration ({@link LibusbJava#usb_set_configuration(int, int)}).
	 */
	public byte bConfigurationValue;

	/**
	 * Index of String descriptor describing this configuration
	 */
	public byte iConfiguration;

	/**
	 * Specifies power parameters for this configuration<br>
	 * <br>
	 * Bit 7: Reserved, set to 1 (USB 1.0 Bus Powered)<br>
	 * Bit 6: Self Powered<br>
	 * Bit 5: Remote Wakeup<br>
	 * Bit 4..0: Reserved, set to 0
	 */
	public byte bmAttributes;

	/**
	 * Maximum power consumption in 2mA units
	 */
	public byte MaxPower;

	/**
	 * USB interface descriptors
	 */
	public Usb_Interface[] interface_;

	/**
	 * Extra descriptors are currently not interpreted because of their unknown
	 * structure.
	 */
	public Usb_Config_Descriptor extra; /* Extra descriptors */
	// TODO

	public int extralen;

	@Override
	public String toString() {
		return "Usb_Config_Descriptor bNumInterfaces: 0x"
				+ Integer.toHexString(bNumInterfaces);
	}
}