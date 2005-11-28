package ch.ntb.mcdp.system.memMap;

import java.util.LinkedList;

public class Segment {

	private LinkedList<Segment> subsegments;

	private String name;

	private MemAttributes attr;

	private int base = 0;

	private int size = 0;

	Segment(String name) {
		this.name = name;
		subsegments = new LinkedList<Segment>();
		attr = new MemAttributes();
	}

	Segment(String name, MemAttributes attr) {
		this.name = name;
		subsegments = new LinkedList<Segment>();
		this.attr = attr;
	}

	public MemAttributes getMemAttributes() {
		return attr;
	}

	public void add(Segment s) {
		subsegments.add(s);
	}

	public LinkedList<Segment> getSegments() {
		return subsegments;
	}

	public int getBase() {
		return base;
	}

	public void setBase(int base) {
		this.base = base;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public String getName() {
		return name;
	}
}
