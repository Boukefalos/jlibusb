package ch.ntb.inf.libusbJava.exceptions;

public class LibusbError extends Exception {
	private static final long serialVersionUID = 9096323614080207236L;
	
	/**
	 * libusb error codes
	 */
	public static final int ERROR_NONE = 0;
	public static final int ERROR_IO = -1;
	public static final int ERROR_INVALID_PARAM = -2;
	public static final int ERROR_ACCESS = -3;
	public static final int ERROR_NO_DEVICE = -4;
	public static final int ERROR_NOT_FOUND = -5;
	public static final int ERROR_BUSY = -6;
	public static final int ERROR_TIMEOUT = -7;
	public static final int ERROR_OVERFLOW = -8;
	public static final int ERROR_PIPE = -9;
	public static final int ERROR_INTERRUPTED = -10;
	public static final int ERROR_NO_MEM = -11;
	public static final int ERROR_NOT_SUPPORTED = -12;
	public static final int ERROR_OTHER = -99;
	public static final int ERROR_JAVA_REFERENCES_NOT_LOADED = -100;
	public static final int ERROR_JAVA_WRONG_ENVIRONMENT = -101;
	public static final int ERROR_JAVA_ILEGAL_DEVICE_HANDLE = -102;
	
	private int code = ERROR_NONE;

	public LibusbError(int code)
	{
		super("libusb result: " + getStringFromCode(code));
		this.code = code;
	}
	
	public int getErrorCode()
	{
		return code;
	}
	
	public static String getStringFromCode(int code)
	{
		String result;
		
		switch (code)
		{
			case ERROR_IO:
			{
				result = "ERROR_IO";
				break;
			}
			case ERROR_INVALID_PARAM:
			{
				result = "ERROR_INVALID_PARAM";
				break;
			}
			case ERROR_ACCESS:
			{
				result = "ERROR_ACCESS";
				break;
			}
			case ERROR_NO_DEVICE:
			{
				result = "ERROR_NO_DEVICE";
				break;
			}
			case ERROR_NOT_FOUND:
			{
				result = "ERROR_NOT_FOUND";
				break;
			}
			case ERROR_BUSY:
			{
				result = "ERROR_BUSY";
				break;
			}

			case ERROR_TIMEOUT:
			{
				result = "ERROR_TIMEOUT";
				break;
			}

			case ERROR_OVERFLOW:
			{
				result = "ERROR_OVERFLOW";
				break;
			}

			case ERROR_PIPE:
			{
				result = "ERROR_PIPE";
				break;
			}

			case ERROR_INTERRUPTED:
			{
				result = "ERROR_INTERRUPTED";
				break;
			}

			case ERROR_NO_MEM:
			{
				result = "ERROR_NO_MEM";
				break;
			}

			case ERROR_NOT_SUPPORTED:
			{
				result = "ERROR_NOT_SUPPORTED";
				break;
			}
			
			case ERROR_OTHER:
			{
				result = "ERROR_OTHER";
				break;
			}
			
			case ERROR_JAVA_REFERENCES_NOT_LOADED:
			{
				result = "ERROR_JAVA_REFERENCES_NOT_LOADED";
				break;
			}
			
			case ERROR_JAVA_WRONG_ENVIRONMENT:
			{
				result = "ERROR_JAVA_WRONG_ENVIRONMENT";
				break;
			}
			
			case ERROR_JAVA_ILEGAL_DEVICE_HANDLE:
			{
				result = "ERROR_JAVA_ILEGAL_DEVICE_HANDLE";
				break;
			}
			
			default:
			{
				result = "ERROR_UNKNWON (" + code + ")";
				break;
			}
		}
		
		return result;
	}

	public String getErrorString() {
		return getStringFromCode(getErrorCode());
	}
}
