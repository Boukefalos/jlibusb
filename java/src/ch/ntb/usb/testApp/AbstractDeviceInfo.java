/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <spandi at users.sourceforge.net>
 *
 * http://libusbjava.sourceforge.net
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.testApp;

public abstract class AbstractDeviceInfo {

	private short idVendor;

	private short idProduct;

	private int timeout;

	private int configuration;

	private int interface_;

	private int altinterface;

	private int outEPBulk;

	private int inEPBulk;

	private int outEPInt;

	private int inEPInt;

	private int sleepTimeout;

	private int maxDataSize;

	private TransferMode mode;

	private boolean compareData = true;

	public static enum TransferMode {
		Bulk, Interrupt
	}

	public AbstractDeviceInfo() {
		initValues();
	}

	abstract public void initValues();

	public int getAltinterface() {
		return altinterface;
	}

	public int getConfiguration() {
		return configuration;
	}

	public short getIdProduct() {
		return idProduct;
	}

	public short getIdVendor() {
		return idVendor;
	}

	public int getInEPBulk() {
		return inEPBulk;
	}

	public int getInEPInt() {
		return inEPInt;
	}

	public int getInterface() {
		return interface_;
	}

	public int getMaxDataSize() {
		return maxDataSize;
	}

	public int getOutEPBulk() {
		return outEPBulk;
	}

	public int getOutEPInt() {
		return outEPInt;
	}

	public int getSleepTimeout() {
		return sleepTimeout;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setAltinterface(int altinterface) {
		this.altinterface = altinterface;
	}

	public void setConfiguration(int configuration) {
		this.configuration = configuration;
	}

	public void setIdProduct(short idProduct) {
		this.idProduct = idProduct;
	}

	public void setIdVendor(short idVendor) {
		this.idVendor = idVendor;
	}

	public void setInEPBulk(int in_ep_bulk) {
		this.inEPBulk = in_ep_bulk;
	}

	public void setInEPInt(int in_ep_int) {
		this.inEPInt = in_ep_int;
	}

	public void setInterface(int interface_) {
		this.interface_ = interface_;
	}

	public void setMaxDataSize(int maxDataSize) {
		this.maxDataSize = maxDataSize;
	}

	public void setOutEPBulk(int out_ep_bulk) {
		this.outEPBulk = out_ep_bulk;
	}

	public void setOutEPInt(int out_ep_int) {
		this.outEPInt = out_ep_int;
	}

	public void setSleepTimeout(int sleepTimeout) {
		this.sleepTimeout = sleepTimeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	public TransferMode getMode() {
		return mode;
	}

	public void setMode(TransferMode mode) {
		this.mode = mode;
	}

	public boolean doCompareData() {
		return compareData;
	}

	public void setDoCompareData(boolean compareData) {
		this.compareData = compareData;
	}
}
