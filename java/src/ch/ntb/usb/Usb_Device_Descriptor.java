package ch.ntb.usb;

public class Usb_Device_Descriptor {
	/*
	 * Device and/or Interface Class codes
	 */
	public static final int USB_CLASS_PER_INTERFACE = 0;
	public static final int USB_CLASS_AUDIO = 1;
	public static final int USB_CLASS_COMM = 2;
	public static final int USB_CLASS_HID = 3;
	public static final int USB_CLASS_PRINTER = 7;
	public static final int USB_CLASS_MASS_STORAGE = 8;
	public static final int USB_CLASS_HUB = 9;
	public static final int USB_CLASS_DATA = 10;
	public static final int USB_CLASS_VENDOR_SPEC = 0xff;

	/*
	 * Descriptor types
	 */
	public static final int USB_DT_DEVICE = 0x01;
	public static final int USB_DT_CONFIG = 0x02;
	public static final int USB_DT_STRING = 0x03;
	public static final int USB_DT_INTERFACE = 0x04;
	public static final int USB_DT_ENDPOINT = 0x05;

	public static final int USB_DT_HID = 0x21;
	public static final int USB_DT_REPORT = 0x22;
	public static final int USB_DT_PHYSICAL = 0x23;
	public static final int USB_DT_HUB = 0x29;

	/*
	 * Descriptor sizes per descriptor type
	 */
	public static final int USB_DT_DEVICE_SIZE = 18;
	public static final int USB_DT_CONFIG_SIZE = 9;
	public static final int USB_DT_INTERFACE_SIZE = 9;
	public static final int USB_DT_ENDPOINT_SIZE = 7;
	public static final int USB_DT_ENDPOINT_AUDIO_SIZE = 9; /* Audio extension */
	public static final int USB_DT_HUB_NONVAR_SIZE = 7;

	public byte bLength;

	public byte bDescriptorType;

	public short bcdUSB;

	public byte bDeviceClass;

	public byte bDeviceSubClass;

	public byte bDeviceProtocol;

	public byte bMaxPacketSize0;

	public short idVendor;

	public short idProduct;

	public short bcdDevice;

	public byte iManufacturer;

	public byte iProduct;

	public byte iSerialNumber;

	public byte bNumConfigurations;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("** Usb_Device_Descriptor **\n");
		sb.append("\tblenght: " + bLength + "\n");
		sb.append("\tbDescriptorType: " + bDescriptorType + "\n");
		sb.append("\tbcdUSB: 0x" + Integer.toHexString(bcdUSB) + "\n");
		sb.append("\tbDeviceClass: " + bDeviceClass + "\n");
		sb.append("\tbDeviceSubClass: " + bDeviceSubClass + "\n");
		sb.append("\tbDeviceProtocol: " + bDeviceProtocol + "\n");
		sb.append("\tbMaxPacketSize0: " + bMaxPacketSize0 + "\n");
		sb.append("\tidVendor: 0x" + Integer.toHexString(idVendor & 0xFFFF)
				+ "\n");
		sb.append("\tidProduct: 0x" + Integer.toHexString(idProduct & 0xFFFF)
				+ "\n");
		sb.append("\tbcdDevice: " + bcdDevice + "\n");
		sb.append("\tiManufacturer: " + iManufacturer + "\n");
		sb.append("\tiProduct: " + iProduct + "\n");
		sb.append("\tiSerialNumber: " + iSerialNumber + "\n");
		sb.append("\tbNumConfigurations: " + bNumConfigurations + "\n");
		return sb.toString();
	}

};