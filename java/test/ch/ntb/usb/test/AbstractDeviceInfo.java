/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schläpfer <libusb@drip.ch>
 *
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.test;

public abstract class AbstractDeviceInfo {

	private short IdVendor;

	private short IdProduct;

	private int Timeout;

	private int Configuration;

	private int Interface;

	private int Altinterface;

	private int OUT_EP_BULK;

	private int IN_EP_BULK;

	private int OUT_EP_INT;

	private int IN_EP_INT;

	private int SleepTimeout;

	private int MaxDataSize;

	private WriteMode mode;

	public static enum WriteMode {
		Bulk, Interrupt
	}

	public AbstractDeviceInfo() {
		initValues();
	}

	abstract public void initValues();

	public int getAltinterface() {
		return Altinterface;
	}

	public int getConfiguration() {
		return Configuration;
	}

	public short getIdProduct() {
		return IdProduct;
	}

	public short getIdVendor() {
		return IdVendor;
	}

	public int getIN_EP_BULK() {
		return IN_EP_BULK;
	}

	public int getIN_EP_INT() {
		return IN_EP_INT;
	}

	public int getInterface() {
		return Interface;
	}

	public int getMaxDataSize() {
		return MaxDataSize;
	}

	public int getOUT_EP_BULK() {
		return OUT_EP_BULK;
	}

	public int getOUT_EP_INT() {
		return OUT_EP_INT;
	}

	public int getSleepTimeout() {
		return SleepTimeout;
	}

	public int getTimeout() {
		return Timeout;
	}

	public void setAltinterface(int altinterface) {
		Altinterface = altinterface;
	}

	public void setConfiguration(int configuration) {
		Configuration = configuration;
	}

	public void setIdProduct(short idProduct) {
		IdProduct = idProduct;
	}

	public void setIdVendor(short idVendor) {
		IdVendor = idVendor;
	}

	public void setIN_EP_BULK(int in_ep_bulk) {
		IN_EP_BULK = in_ep_bulk;
	}

	public void setIN_EP_INT(int in_ep_int) {
		IN_EP_INT = in_ep_int;
	}

	public void setInterface(int interface1) {
		Interface = interface1;
	}

	public void setMaxDataSize(int maxDataSize) {
		MaxDataSize = maxDataSize;
	}

	public void setOUT_EP_BULK(int out_ep_bulk) {
		OUT_EP_BULK = out_ep_bulk;
	}

	public void setOUT_EP_INT(int out_ep_int) {
		OUT_EP_INT = out_ep_int;
	}

	public void setSleepTimeout(int sleepTimeout) {
		SleepTimeout = sleepTimeout;
	}

	public void setTimeout(int timeout) {
		Timeout = timeout;
	}

	public WriteMode getMode() {
		return mode;
	}

	public void setMode(WriteMode mode) {
		this.mode = mode;
	}

}
