package ch.ntb.usb;

/**
 * Common USB descriptor values.<br>
 * 
 * @author schlaepfer
 * 
 */
public class Usb_Descriptor {

	/**
	 * Descriptor types ({@link #bDescriptorType}).
	 */
	public static final int USB_DT_DEVICE = 0x01, USB_DT_CONFIG = 0x02,
			USB_DT_STRING = 0x03, USB_DT_INTERFACE = 0x04,
			USB_DT_ENDPOINT = 0x05;

	/**
	 * Descriptor types ({@link #bDescriptorType}).
	 */
	public static final int USB_DT_HID = 0x21, USB_DT_REPORT = 0x22,
			USB_DT_PHYSICAL = 0x23, USB_DT_HUB = 0x29;

	/**
	 * Descriptor sizes per descriptor type ({@link #bLength}).
	 */
	public static final int USB_DT_DEVICE_SIZE = 18, USB_DT_CONFIG_SIZE = 9,
			USB_DT_INTERFACE_SIZE = 9, USB_DT_ENDPOINT_SIZE = 7,
			USB_DT_ENDPOINT_AUDIO_SIZE = 9 /* Audio extension */,
			USB_DT_HUB_NONVAR_SIZE = 7;

	/**
	 * Size of descriptor in bytes.
	 */
	public byte bLength;

	/**
	 * Type of descriptor.
	 */
	public byte bDescriptorType;

}