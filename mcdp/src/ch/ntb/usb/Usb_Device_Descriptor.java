package ch.ntb.usb;

public class Usb_Device_Descriptor {
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
		sb.append("\tbcdUSB: " + bcdUSB + "\n");
		sb.append("\tbDeviceClass: " + bDeviceClass + "\n");
		sb.append("\tbDeviceSubClass: " + bDeviceSubClass + "\n");
		sb.append("\tbDeviceProtocol: " + bDeviceProtocol + "\n");
		sb.append("\tbMaxPacketSize0: " + bMaxPacketSize0 + "\n");
		sb.append("\tidVendor: " + idVendor + "\n");
		sb.append("\tidProduct: " + idProduct + "\n");
		sb.append("\tbcdDevice: " + bcdDevice + "\n");
		sb.append("\tiManufacturer: " + iManufacturer + "\n");
		sb.append("\tiProduct: " + iProduct + "\n");
		sb.append("\tiSerialNumber: " + iSerialNumber + "\n");
		sb.append("\tbNumConfigurations: " + bNumConfigurations + "\n");
		return sb.toString();
	}

};