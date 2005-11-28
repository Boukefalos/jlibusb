package ch.ntb.mcdp.system.memMap;

import java.util.LinkedList;

public class Device {

	private String type;

	private int width;

	private int size = -1;

	private MemAttributes attr;

	LinkedList<Segment> segments;

	Device(String type, int width) {
		this.type = type;
		this.width = width;
		this.attr = new MemAttributes();
		this.size = -1;
		segments = new LinkedList<Segment>();
	}

	public String getType() {
		return type;
	}

	public int getWidth() {
		return width;
	}

	public MemAttributes getAttributes() {
		return attr;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

}
