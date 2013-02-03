package ch.ntb.inf.libusbJava.test.exceptions;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import ch.ntb.inf.libusbJava.exceptions.LibusbError;

@RunWith(Parameterized.class)
public class LibusbErrorTest {
	@Parameters
	public static Collection<Object[]> codesToTest() {
		return Arrays.asList(new Object[][] {	{"ERROR_UNKNWON (0)", 0 }, 
											 	{"ERROR_IO", -1 },
												{"ERROR_INVALID_PARAM", -2},
												{"ERROR_ACCESS", -3},
												{"ERROR_NO_DEVICE", -4},
												{"ERROR_NOT_FOUND", -5},
												{"ERROR_BUSY", -6},
												{"ERROR_TIMEOUT", -7},
												{"ERROR_OVERFLOW", -8},
												{"ERROR_PIPE", -9},
												{"ERROR_INTERRUPTED", -10},
												{"ERROR_NO_MEM", -11},
												{"ERROR_NOT_SUPPORTED", -12},
												{"ERROR_UNKNWON (-13)", -13},
												{"ERROR_UNKNWON (-98)", -98},
												{"ERROR_JAVA_REFERENCES_NOT_LOADED", -100},
												{"ERROR_JAVA_WRONG_ENVIRONMENT", -101},
												{"ERROR_JAVA_ILEGAL_DEVICE_HANDLE", -102},
												{"ERROR_UNKNWON (-103)", -103},
												{"ERROR_OTHER", -99} });
	}
	
	private String exp_desc;
	private int    code;
	private LibusbError e;
	
	public LibusbErrorTest(String exp_desc, int code) {
		this.exp_desc = exp_desc;
		this.code = code;
		try {
			throw new LibusbError(code);
		} catch (LibusbError e) {
			this.e = e;
		}
	}
	
	@Test
	public void testGetErrorCode() {
		assertEquals("Error code is correct", e.getErrorCode(), code);
	}

	@Test
	public void testGetStringFromCode() {
		String gen_desc = LibusbError.getStringFromCode(code);
		assertEquals("Correct error description for " + code, exp_desc, gen_desc);
	}

	@Test
	public void testGetErrorString() {
		assertEquals("Correct error string for " + code, e.getErrorString(), exp_desc);
	}


	@Test
	public void testGetMessage() {
		assertEquals("Correct error string for " + code, e.getMessage(), "libusb result: " + exp_desc);
	}
}
