/* 
 * Java libusb wrapper
 * Copyright (c) 2005-2006 Andreas Schl�pfer <libusb@drip.ch>
 *
 * This library is covered by the LGPL, read LGPL.txt for details.
 */
package ch.ntb.usb.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.ntb.usb.Device;
import ch.ntb.usb.LibusbJava;
import ch.ntb.usb.USB;
import ch.ntb.usb.USBException;
import ch.ntb.usb.Usb_Config_Descriptor;
import ch.ntb.usb.Usb_Device_Descriptor;
import ch.ntb.usb.testApp.AbstractDeviceInfo;
import ch.ntb.usb.testApp.AbstractDeviceInfo.TransferMode;

public class DeviceTest {

	private static final String testdevicePropertiesFile = "testdevice.properties";

	private static final String deviceInfoKey = "testdeviceInfo";

	private static AbstractDeviceInfo devinfo;

	private static byte[] testData;

	private static byte[] readData;

	private static Device dev;

	@SuppressWarnings("unchecked")
	@BeforeClass
	public static void setUp() throws Exception {
		// load the device info class with the key
		// from 'testdevice.properties'
		InputStream propInputStream = new FileInputStream(
				testdevicePropertiesFile);
		Properties devInfoProp = new Properties();
		devInfoProp.load(propInputStream);
		String devInfoClazzName = devInfoProp.getProperty(deviceInfoKey);
		if (devInfoClazzName == null) {
			throw new Exception("property " + deviceInfoKey
					+ " not found in file " + testdevicePropertiesFile);
		}
		Class devInfoClazz = Class.forName(devInfoClazzName);
		devinfo = (AbstractDeviceInfo) devInfoClazz.newInstance();
		// devinfo = new CY7C68013A();
		// setup test data
		testData = new byte[devinfo.getMaxDataSize()];
		readData = new byte[testData.length];
		// initialise the device
		LibusbJava.usb_set_debug(255);
		dev = USB.getDevice(devinfo.getIdVendor(), devinfo.getIdProduct());
	}

	@SuppressWarnings("null")
	@Test
	public void getDescriptors() throws Exception {
		dev.updateDescriptors();
		Usb_Device_Descriptor devDescriptor = dev.getDeviceDescriptor();
		assertTrue(devDescriptor != null);
		assertEquals(devinfo.getIdProduct(), devDescriptor.getIdProduct());
		assertEquals(devinfo.getIdVendor(), devDescriptor.getIdVendor());
		Usb_Config_Descriptor confDescriptors[] = dev.getConfigDescriptors();
		assertTrue(confDescriptors != null);
		assertTrue(confDescriptors[0].getInterface().length > 0);
	}

	@Test
	public void initalReset() throws Exception {
		doOpen();
		dev.reset();
		timeout();
	}

	@Test(expected = USBException.class)
	public void testClose() throws Exception {
		try {
			// this calls must not throw an exception
			doOpen();
			doClose();
		} catch (USBException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		// this call must throw an exception, because the device is closed
		dev.writeBulk(devinfo.getOutEPBulk(), testData, testData.length,
				devinfo.getTimeout(), false);
	}

	@Test(expected = USBException.class)
	public void testReset1() throws Exception {
		try {
			// this calls must not throw an exception
			doOpen();
			dev.reset();
			timeout();
		} catch (USBException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		// this call must throw an exception, because the device is closed
		dev.writeBulk(devinfo.getOutEPBulk(), testData, testData.length,
				devinfo.getTimeout(), false);
	}

	@Test(expected = USBException.class)
	public void testReset2() throws Exception {
		try {
			// this calls must not throw an exception
			doOpen();
			dev.reset();
			timeout();
		} catch (USBException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
		// this call must throw an exception, because the device can't be closed
		doClose();
	}

	@Test
	public void bulkWriteRead() throws Exception {
		devinfo.setMode(TransferMode.Bulk);
		doOpenWriteReadClose();
	}

	@Test
	public void interruptWriteRead() throws Exception {
		devinfo.setMode(TransferMode.Interrupt);
		doOpenWriteReadClose();
	}

	@Test
	public void bulkWriteReadMultiple() throws Exception {
		final int NumberOfIterations = 100;

		devinfo.setMode(TransferMode.Bulk);
		doOpen();
		for (int i = 0; i < NumberOfIterations; i++) {
			initTestData();
			doWriteRead();
			compare(testData, readData);
		}
		doClose();
	}

	@Test
	public void multipleOpenCloseWithBulkWrite() throws Exception {
		devinfo.setMode(TransferMode.Bulk);
		for (int i = 0; i < 5; i++) {
			doOpen();
			doClose();
		}
		doOpenWriteReadClose();
		for (int i = 0; i < 10; i++) {
			doOpen();
			doWriteRead();
			doClose();
		}
		doOpenWriteReadClose();
		for (int i = 0; i < 5; i++) {
			doOpen();
			doClose();
		}
	}

	@Test
	public void bulkAndInterrupt() throws Exception {
		doOpen();
		// BULK
		devinfo.setMode(TransferMode.Bulk);
		doWriteRead();
		// INTERRUPT
		devinfo.setMode(TransferMode.Interrupt);
		doWriteRead();
		doClose();
	}

	@Test
	public void bulkAndInterruptMultiple() throws Exception {
		for (int i = 0; i < 20; i++) {
			devinfo.setMode(TransferMode.Bulk);
			doOpenWriteReadClose();
			devinfo.setMode(TransferMode.Interrupt);
			doOpenWriteReadClose();
		}
	}

	@Test
	public void invalidConfig() throws Exception {
		try {
			dev.open(devinfo.getConfiguration() + 5, devinfo.getInterface(),
					devinfo.getAltinterface());
			fail("USBException expected");
		} catch (USBException e) {
			System.err.println("INFO: " + getClass()
					+ ": error expected: could not set config "
					+ (devinfo.getConfiguration() + 5));
		}
		doOpenWriteReadClose();
	}

	@Test
	public void invalidInterface() throws Exception {
		try {
			dev.open(devinfo.getConfiguration(), devinfo.getInterface() + 5,
					devinfo.getAltinterface());
			fail("USBException expected");
		} catch (USBException e) {
			System.err.println("INFO: " + getClass()
					+ ": error expected: could not claim interface "
					+ (devinfo.getInterface() + 5));
		}
		doOpenWriteReadClose();
	}

	@Test
	public void invalidAltinterface() throws Exception {
		try {
			dev.open(devinfo.getConfiguration(), devinfo.getInterface(),
					devinfo.getAltinterface() + 5);
			fail("USBException expected");
		} catch (USBException e) {
			System.err.println("INFO: " + getClass()
					+ ": error expected: could not set alt interface "
					+ (devinfo.getAltinterface() + 5));
		}
		doOpenWriteReadClose();
	}

	@Test
	public void testGetIdProduct() {
		Assert.assertEquals(dev.getIdProduct(), devinfo.getIdProduct());
	}

	@Test
	public void testGetIdVendor() {
		Assert.assertEquals(dev.getIdVendor(), devinfo.getIdVendor());
	}

	@Test
	public void testGetAltinterface() {
		Assert.assertEquals(dev.getAltinterface(), devinfo.getAltinterface());
	}

	@Test
	public void testGetConfiguration() {
		Assert.assertEquals(dev.getConfiguration(), devinfo.getConfiguration());
	}

	@Test
	public void testGetInterface() {
		Assert.assertEquals(dev.getInterface(), devinfo.getInterface());
	}

	@Test
	public void testGetMaxPacketSize() throws USBException {
		doOpen();
		Assert.assertEquals(dev.getMaxPacketSize(), devinfo.getMaxDataSize());
		doClose();
	}

	@AfterClass
	public static void tearDown() throws Exception {
		if (dev != null && dev.isOpen()) {
			dev.close();
		}
	}

	private void doOpen() throws USBException {
		dev.open(devinfo.getConfiguration(), devinfo.getInterface(), devinfo
				.getAltinterface());
	}

	private void doClose() throws USBException {
		dev.close();
	}

	private void doOpenWriteReadClose() throws Exception {
		doOpen();
		doWriteRead();
		compare(testData, readData);
		doClose();
	}

	private void doWriteRead() throws Exception {
		initTestData();
		if (devinfo.getMode().equals(TransferMode.Bulk)) {
			dev.writeBulk(devinfo.getOutEPBulk(), testData, testData.length,
					devinfo.getTimeout(), false);
			dev.readBulk(devinfo.getInEPBulk(), readData, readData.length,
					devinfo.getTimeout(), false);
		} else if (devinfo.getMode().equals(TransferMode.Interrupt)) {
			dev.writeInterrupt(devinfo.getOutEPInt(), testData,
					testData.length, devinfo.getTimeout(), false);
			dev.readInterrupt(devinfo.getInEPInt(), readData, readData.length,
					devinfo.getTimeout(), false);
		}
		compare(testData, readData);
	}

	private static void compare(byte[] d1, byte[] d2) {
		int minLength = Math.min(d1.length, d2.length);
		for (int i = 0; i < minLength; i++) {
			assertEquals(d1[i], d2[i]);
		}
	}

	private static void timeout() {
		try {
			Thread.sleep(devinfo.getSleepTimeout());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private static void initTestData() {
		for (int i = 0; i < testData.length; i++) {
			testData[i] = (byte) (Math.random() * 0xff);
			readData[i] = 0;
		}
	}
}
