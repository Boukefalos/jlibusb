package ch.ntb.inf.libusbJava.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import ch.ntb.inf.libusbJava.LibusbJava1;
import ch.ntb.inf.libusbJava.Usb_Device;
import ch.ntb.inf.libusbJava.exceptions.LibusbError;

public class LibusbJava1Test {
	/**
	 * This method does not need to be tested. This test only exists to document
	 * the fact that this function has not been forgotten.
	 */
	@Test
	public void testLibusb_set_debug() {
	}

	/**
	 * This test is used to check if the library constructs and throws the
	 * correct exceptions if requested to do so.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testLibusb_exception() {
		/*
		 * We go through every currently possible error just to check a several
		 * different error codes.
		 */
		for (int i = -1; i > LibusbError.ERROR_OTHER; i--) {
			boolean exception_occured = false;

			try {
				LibusbJava1.libusb_exceptionTest(i);
			} catch (LibusbError e) {
				assertEquals("Exception code correct", e.getErrorCode(), i);
				exception_occured = true;
			}

			assertTrue("Exception occured", exception_occured);
		}
	}
	
	/**
	 * Tests a helper function in the DLL that creates a byte array object from
	 * a piece of memory given.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testLibusb_to_byteArrayWithContent() {
		final String str = "SimpleTest";
		final int testLen = 5;
		final byte[] reference = str.substring(0, testLen).getBytes();
		
		byte[] result = LibusbJava1.to_byteArrayTest(str, testLen);
		
		assertNotNull("Got a byte array", result);
		assertEquals("Byte array has correct length", testLen, result.length);
		
		for (int i = 0;i < result.length;i++)
		{
			assertEquals("Array content is correct", reference[i], result[i]);
		}
	}

	/**
	 * Tests a helper function in the DLL that creates a byte array object from
	 * a piece of memory given.
	 */
	@SuppressWarnings("deprecation")
	@Test
	public void testLibusb_to_byteArrayLength0() {
		final String str = "SimpleTest";
		
		byte[] result = LibusbJava1.to_byteArrayTest(str, 0);
		
		assertNotNull("Got a byte array", result);
		assertEquals("Byte array has correct length", 0, result.length);
	}

	@Test
	public void testLibusb_init() {
		try {
			long handle = LibusbJava1.libusb_init();
			LibusbJava1.libusb_exit(handle);
		} catch (LibusbError e) {
			fail("Initialization failed, Code: " + e.getErrorString());
		}
	}

	/**
	 * This method can not be tested (yet). This test only exists to document
	 * the fact that this function has not been forgotten.
	 */
	@Test
	public void testLibusb_exit() {
	}

	@Test
	public void testLibusb_get_device_list() throws LibusbError {
		long handle = LibusbJava1.libusb_init();
		Usb_Device devices = LibusbJava1.libusb_get_device_list(handle);
		assertNotNull("Got devices", devices);
		System.out.println(devices.toString());
		assertNull("Bus is null", devices.getBus());
		LibusbJava1.libusb_exit(handle);
	}

	@Test
	public void testLibusb_get_bus_number() throws LibusbError {
		long handle = LibusbJava1.libusb_init();
		Usb_Device devices = LibusbJava1.libusb_get_device_list(handle);
		assertNotNull("Got devices", devices);
		System.out.println(devices.getBus());
		LibusbJava1.libusb_exit(handle);
	}

	@Test
	public void testLibusb_get_max_iso_packet_size() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_ref_device() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_unref_device() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_open() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_open_device_with_vid_pid() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_close() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_get_device() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_get_configuration() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_set_configuration() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_claim_interface() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_release_interface() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_set_interface_alt_setting() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_clear_halt() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_reset_device() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_kernel_driver_active() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_detach_kernel_driver() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_attach_kernel_driver() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_get_active_config_descriptor() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_get_string_descriptor_ascii() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_get_descriptor() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_get_string_descriptor() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_alloc_transfer() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_free_transfer() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_submit_transfer() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_cancel_transfer() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_control_transfer_get_data() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_control_transfer_get_setup() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_fill_control_setup() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_fill_control_transfer() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_fill_bulk_transfer() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_fill_interrupt_transfer() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_fill_iso_transfer() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_set_iso_packet_lengths() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_get_iso_packet_buffer() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_get_iso_packet_buffer_simple() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_try_lock_events() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_lock_events() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_unlock_events() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_event_handling_ok() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_event_handler_active() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_lock_event_waiters() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_unlock_event_waiters() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_wait_for_event() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_handle_events_timeout() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_handle_events() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_handle_events_locked() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_pollfds_handle_timeouts() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_get_next_timeout() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_set_pollfd_notifiers() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_get_pollfds() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_control_transfer() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_bulk_transfer() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_interrupt_transfer() {
		fail("Not yet implemented");
	}

	@Test
	public void testLibusb_strerror() {
		fail("Not yet implemented");
	}

	@Test
	public void testIsHandleValid() {
		fail("Not yet implemented");
	}

}
