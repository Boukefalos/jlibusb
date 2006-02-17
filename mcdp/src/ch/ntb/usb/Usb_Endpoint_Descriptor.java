package ch.ntb.usb;

public class Usb_Endpoint_Descriptor {
	public byte bLength;

	public byte bDescriptorType;

	public byte bEndpointAddress;

	public byte bmAttributes;

	public short wMaxPacketSize;

	public byte bInterval;

	public byte bRefresh;

	public byte bSynchAddress;

	// TODO: Extra descriptors are not interpreted because of their unknown
	// structure
	public Usb_Endpoint_Descriptor extra; /* Extra descriptors */

	public int extralen;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("**Usb_Endpoint_Descriptor**\n");
		sb.append("\tblenght: " + bLength + "\n");
		sb.append("\tbDescriptorType: " + bDescriptorType + "\n");
		sb.append("\tbEndpointAddress: 0x"
				+ Integer.toHexString(bEndpointAddress & 0xFF) + "\n");
		sb.append("\tbmAttributes: 0x"
				+ Integer.toHexString(bmAttributes & 0xFF) + "\n");
		sb.append("\twMaxPacketSize: " + wMaxPacketSize + "\n");
		sb.append("\tbInterval: " + bInterval + "\n");
		sb.append("\tbRefresh: " + bRefresh + "\n");
		sb.append("\tbSynchAddress: " + bSynchAddress + "\n");
		return sb.toString();
	}
}
