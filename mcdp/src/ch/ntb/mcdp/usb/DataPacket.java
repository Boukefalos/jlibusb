package ch.ntb.mcdp.usb;

public class DataPacket {

	/**
	 * minimal Length of a packet (no payload)
	 */
	public static final int PACKET_MIN_LENGTH = 6;

	public int subtype;

	public byte[] data;

	/**
	 * offset to the first byte of data
	 */
	public static final byte PACKET_DATA_OFFSET = 5;

	/**
	 * last byte of packet
	 */
	public static final byte PACKET_END = 0x1F;

	// Packet Constants
	/**
	 * first byte of header
	 */
	public static final byte PACKET_HEADER = 0x5B;

	DataPacket(int subtype, byte[] data) {
		this.subtype = subtype;
		this.data = data;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("Subtype: 0x"
				+ String.format("%1$02X", subtype) + "\t");
		sb.append("Data: ");
		for (int i = 0; i < data.length; i++) {
			sb.append("0x" + String.format("%1$02X", data[i]) + " ");
		}
		return sb.toString();
	}
}
