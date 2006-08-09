package ch.ntb.usb;

/**
 * Represents the descriptor of a USB device.<br>
 * A USB device can only have one device descriptor. It specifies some basic,
 * yet important information about the device.<br>
 * <br>
 * The length of the device descriptor is
 * {@link ch.ntb.usb.Usb_Descriptor#USB_DT_DEVICE_SIZE} and the type is
 * {@link ch.ntb.usb.Usb_Descriptor#USB_DT_DEVICE}.
 * 
 * @author schlaepfer
 * 
 */
public class Usb_Device_Descriptor extends Usb_Descriptor {
	/**
	 * Device and/or interface class codes.
	 */
	public static final int USB_CLASS_PER_INTERFACE = 0, USB_CLASS_AUDIO = 1,
			USB_CLASS_COMM = 2, USB_CLASS_HID = 3, USB_CLASS_PRINTER = 7,
			USB_CLASS_MASS_STORAGE = 8, USB_CLASS_HUB = 9, USB_CLASS_DATA = 10,
			USB_CLASS_VENDOR_SPEC = 0xff;

	/**
	 * USB Specification number to which the device complies to.<br>
	 * This field reports the highest version of USB the device supports. The
	 * value is in binary coded decimal with a format of 0xJJMN where JJ is the
	 * major version number, M is the minor version number and N is the sub
	 * minor version number.<br>
	 * Examples: USB 2.0 is reported as 0x0200, USB 1.1 as 0x0110 and USB 1.0 as
	 * 0x100
	 */
	public short bcdUSB;

	/**
	 * Class code (Assigned by <a href="http://www.usb.org">www.usb.org</a>)<br>
	 * If equal to zero, each interface specifies it's own class code. If equal
	 * to 0xFF, the class code is vendor specified. Otherwise the field is a
	 * valid class code.
	 */
	public byte bDeviceClass;

	/**
	 * Subclass code (Assigned by <a href="http://www.usb.org">www.usb.org</a>)<br>
	 */
	public byte bDeviceSubClass;

	/**
	 * Protocol code (Assigned by <a href="http://www.usb.org">www.usb.org</a>)<br>
	 */
	public byte bDeviceProtocol;

	/**
	 * Maximum packet size for endpoint zero. <br>
	 * Valid sizes are 8, 16, 32, 64.
	 */
	public byte bMaxPacketSize0;

	/**
	 * Vendor ID (Assigned by <a href="http://www.usb.org">www.usb.org</a>)<br>
	 */
	public short idVendor;

	/**
	 * Product ID (Assigned by <a href="http://www.usb.org">www.usb.org</a>)<br>
	 */
	public short idProduct;

	/**
	 * Device release number<br>
	 * Assigned by the manufacturer of the device.
	 */
	public short bcdDevice;

	/**
	 * Index of manufacturer string descriptor<br>
	 * If this value is 0, no string descriptor is used.
	 */
	public byte iManufacturer;

	/**
	 * Index of product string descriptor<br>
	 * If this value is 0, no string descriptor is used.
	 */
	public byte iProduct;

	/**
	 * Index of serial number string descriptor<br>
	 * If this value is 0, no string descriptor is used.
	 */
	public byte iSerialNumber;

	/**
	 * Number of possible configurations supported at its current speed
	 */
	public byte bNumConfigurations;

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Usb_Device_Descriptor idVendor: 0x"
				+ Integer.toHexString(idVendor & 0xFFFF) + ", idProduct: 0x"
				+ Integer.toHexString(idProduct & 0xFFFF));
		return sb.toString();
	}
}