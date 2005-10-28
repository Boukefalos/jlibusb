package ch.ntb.usb;

public class Usb_Interface_Descriptor {
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

	// TODO: Extra descriptors are not interpreted because of their unknown structure
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
		sb.append("\tbInterfaceClass: " + bInterfaceClass + "\n");
		sb.append("\tbInterfaceSubClass: " + bInterfaceSubClass + "\n");
		sb.append("\tbInterfaceProtocol: " + bInterfaceProtocol + "\n");
		sb.append("\tiInterface: " + iInterface + "\n");
		return sb.toString();
	}
}
