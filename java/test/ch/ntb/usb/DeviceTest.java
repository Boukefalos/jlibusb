package ch.ntb.usb;

import static org.junit.Assert.fail;
import junit.framework.Assert;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class DeviceTest {

	private static final short IdVendor = (short) 0x8235;

	private static final short IdProduct = 0x0222;

	private static final int Timeout = 20000;

	private static final int Configuration = 1;

	private static final int Interface = 0;

	private static final int Altinterface = 0;

	private static final int OUT_EP_BULK = 0x01;

	private static int IN_EP_BULK = 0x82;

	private static final int OUT_EP_INT = 0x03;

	private static final int IN_EP_INT = 0x84;

	private static final int Sleep_Timeout = 2000;

	private static byte[] testData;

	private static byte[] readData;

	private static enum WriteMode {
		Bulk, Interrupt
	}

	private static WriteMode mode;

	private static Device dev;

	@BeforeClass
	public static void setUp() throws Exception {
		// setup test data
		testData = new byte[USB.FULLSPEED_MAX_BULK_PACKET_SIZE];
		readData = new byte[testData.length];
		// initialise the device
		dev = USB.getDevice(IdVendor, IdProduct);
		dev.open(Configuration, Interface, Altinterface);
		dev.reset();
		timeout();
		mode = WriteMode.Bulk;
	}

	@Test(expected=USBException.class)
	public void testClose() throws Exception {
		dev.open(Configuration, Interface, Altinterface);
		dev.close();
		// this call must throw an exception, because the device is closed
		dev.writeBulk(OUT_EP_BULK, testData, testData.length, Timeout, false);
	}

	@Test
	public void testReset() throws Exception {
		dev.open(Configuration, Interface, Altinterface);
		dev.reset();
		try {
			// this call must throw an exception, because the device is closed
			dev.writeBulk(OUT_EP_BULK, testData, testData.length, Timeout,
					false);
			fail();
		} catch (Exception e) {
			// must throw an exception
		}
		try {
			// this call must throw an exception, because the device can't be
			// closed
			dev.close();
			fail();
		} catch (Exception e) {
			// must throw an exception
		}
	}

	@Test
	public void testBulk() throws Exception {
		mode = WriteMode.Bulk;
		timeout();
		testOpenWriteClose();
	}

	@Test
	public void testBulkMultiple() throws Exception {
		final int NumberOfIterations = 20;
		
		try {
			timeout();
			dev.open(Configuration, Interface, Altinterface);
			for (int i = 0; i < NumberOfIterations; i++) {
				initTestData();
				dev.writeBulk(OUT_EP_BULK, testData, testData.length, Timeout, true);
				dev.readBulk(IN_EP_BULK, readData, readData.length, Timeout, true);
				compare(testData, readData);
			}
			dev.close();
		} catch (USBException e) {
			dev.close();
			throw new USBException(e.getMessage());
		}
	}

	@Test
	public void testInterrupt() throws Exception {
		mode = WriteMode.Interrupt;
		testOpenWriteClose();
	}

	@Test
	public void testBulkAndInterrupt() throws Exception {
		timeout();
		dev.open(Configuration, Interface, Altinterface);
		// BULK
		initTestData();
		dev.writeBulk(OUT_EP_BULK, testData, testData.length, Timeout, false);
		dev.readBulk(IN_EP_BULK, readData, readData.length, Timeout, false);
		compare(testData, readData);
		// INTERRUPT
		initTestData();
		dev.writeInterrupt(OUT_EP_INT, testData, testData.length, Timeout,
				false);
		// TODO change to readInterrupt
		dev.readBulk(IN_EP_INT, readData, readData.length, Timeout, false);
		compare(testData, readData);
		dev.close();
	}

	@Test
	public void testBulkAndInterruptMultiple() {
		mode = WriteMode.Bulk;
		timeout();
	}

	@Test
	public void testGetIdProduct() {
		Assert.assertTrue(dev.getIdProduct() == IdProduct);
	}

	@Test
	public void testGetIdVendor() {
		Assert.assertTrue(dev.getIdVendor() == IdVendor);
	}

	@Test
	public void testGetAltinterface() {
		Assert.assertTrue(dev.getAltinterface() == Altinterface);
	}

	@Test
	public void testGetConfiguration() {
		Assert.assertTrue(dev.getConfiguration() == Configuration);
	}

	@Test
	public void testGetInterface() {
		Assert.assertTrue(dev.getInterface() == Interface);
	}

	@Test
	public void testGetMaxPacketSize() {
		Assert
				.assertTrue(dev.getMaxPacketSize() == USB.FULLSPEED_MAX_BULK_PACKET_SIZE);
	}

	@AfterClass
	public static void tearDown() {
		try {
			dev.close();
		} catch (USBException e) {
			// ignore Exceptions
		}
	}

	private void testOpenWriteClose() throws Exception {
		dev.open(Configuration, Interface, Altinterface);
		initTestData();
		if (mode == WriteMode.Bulk) {
			dev.writeBulk(OUT_EP_BULK, testData, testData.length, Timeout,
					false);
			dev.readBulk(IN_EP_BULK, readData, readData.length, Timeout, false);
		} else if (mode == WriteMode.Interrupt) {
			dev.writeInterrupt(OUT_EP_INT, testData, testData.length, Timeout,
					false);
			// TODO change to readInterrupt
			dev.readBulk(IN_EP_INT, readData, readData.length, Timeout, false);
		}
		compare(testData, readData);
		dev.close();
	}

	private static void compare(byte[] d1, byte[] d2) {
		int minLength = Math.min(d1.length, d2.length);
		for (int i = 0; i < minLength; i++) {
			if (d1[i] != d2[i])
				fail("received data not equal to sent data");
		}
	}

	private static void timeout() {
		try {
			Thread.sleep(Sleep_Timeout);
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
