package ch.ntb.usb;

public class Usb_Interface_Descriptor {
	public static final int USB_MAXINTERFACES = 32;

	public byte bLength;

	public byte bDescriptorType;

	public byte bInterfaceNumber;

	public byte bAlternateSetting;

	public byte bNumEndpoints;

	public byte bInterfaceClass;

	public byte bInterfaceSubClass;

	public byte bInterfaceProtocol;

	public byte iInterface;

	public Usb_Endpoint_Descriptor[] endpoint;

	// TODO: Extra descriptors are not interpreted because of their unknown
	// structure
	public Usb_Interface_Descriptor extra; /* Extra descriptors */

	public int extralen;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("** Usb_Interface_Descriptor **\n");
		sb.append("\tblenght: " + bLength + "\n");
		sb.append("\tbDescriptorType: " + bDescriptorType + "\n");
		sb.append("\tbInterfaceNumber: " + bInterfaceNumber + "\n");
		sb.append("\tbAlternateSetting: " + bAlternateSetting + "\n");
		sb.append("\tbNumEndpoints: " + bNumEndpoints + "\n");
		sb.append("\tbInterfaceClass: 0x"
				+ Integer.toHexString(bInterfaceClass & 0xFF) + "\n");
		sb.append("\tbInterfaceSubClass: 0x"
				+ Integer.toHexString(bInterfaceSubClass & 0xFF) + "\n");
		sb.append("\tbInterfaceProtocol: 0x"
				+ Integer.toHexString(bInterfaceProtocol & 0xFF) + "\n");
		sb.append("\tiInterface: " + iInterface + "\n");
		return sb.toString();
	}
}
