package ch.ntb.usb;

public class Usb_Config_Descriptor {
	public byte bLength;

	public byte bDescriptorType;

	public short wTotalLength;

	public byte bNumInterfaces;

	public byte bConfigurationValue;

	public byte iConfiguration;

	public byte bmAttributes;

	public byte MaxPower;

	public Usb_Interface []interface_;

	// TODO: Extra descriptors are not interpreted because of their unknown structure
	public Usb_Config_Descriptor extra; /* Extra descriptors */

	public int extralen;

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("** Usb_Config_Descriptor **\n");
		sb.append("\tblenght: " + bLength + "\n");
		sb.append("\tbDescriptorType: " + bDescriptorType + "\n");
		sb.append("\tbNumInterfaces: " + bNumInterfaces + "\n");
		sb.append("\tbConfigurationValue: " + bConfigurationValue + "\n");
		sb.append("\tiConfiguration: " + iConfiguration + "\n");
		sb.append("\tbmAttributes: " + bmAttributes + "\n");
		sb.append("\tMaxPower: " + MaxPower + "\n");
		return sb.toString();
	}

}