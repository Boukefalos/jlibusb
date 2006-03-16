package ch.ntb.mcdp.dict;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public abstract class RegisterDict extends DefaultHandler {

	private static final long serialVersionUID = -582382284126896830L;

	private Class<? extends Register> regClass;
	private Method regClassGetTypesMethod;
	private static final String GetTypes_METHOD_NAME = "getTypes";

	private LinkedList registers;

	private String[] types;

	private static final String ELEMENT_REGISTER = "register";
	private static final String ELEMENT_DESCRIPTION = "description";

	private static final String ATTR_MNEMONIC = "mnemonic";
	private static final String ATTR_ALTMNEMONIC = "altmnemonic";
	private static final String ATTR_TYPE = "type";
	private static final String ATTR_VALUE = "value";
	private static final String ATTR_SIZE = "size";
	private static final String ATTR_ACCESSMODE = "accessmode";
	private static final String ATTR_ACCESSATTR = "accessattr";

	private Register reg;
	private StringBuffer cdata;

	/**
	 * Default constructor which takes the Class object from a
	 * <code>Register</code> subclass as argument. The registerDict will be of
	 * this Register-type.<br>
	 * An example:<br>
	 * MPC555Register extends Register -> use <code>MPC555Register.class</code>
	 * as parameter.
	 * 
	 * @param registerClass
	 *            subclass of Register
	 */
	protected RegisterDict(Class<? extends Register> registerClass) {
		this.regClass = registerClass;
		try {
			this.regClassGetTypesMethod = regClass.getMethod(
					GetTypes_METHOD_NAME, (Class[]) null);
			this.regClass.newInstance();
			this.types = (String[]) regClassGetTypesMethod.invoke(
					(Object[]) null, (Object[]) null);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		this.registers = new LinkedList();
	}

	private int convertType(String typeStr) throws SAXException {
		for (int index = 0; index < types.length; index++) {
			if (typeStr.equals(types[index])) {
				return index;
			}
		}
		throw new SAXException("invalid register definition: " + typeStr);
	}

	private int parseInt(String s) {
		if (s == "")
			return 0;
		if (s.indexOf('x') > 0) {
			// is hex number
			if (s.length() <= 2) { // exception for "0x"
				throw new NumberFormatException("string too short: " + s);
			}
			if ((s.length() > 10)) { // exception for e.g. 0x112345678
				throw new NumberFormatException("number too large: " + s);
			}
			// check if string too long (max
			return (int) Long.parseLong(s.substring(s.indexOf('x') + 1, s
					.length()), 16);
		} else {
			// is decimal number
			return Integer.parseInt(s);
		}
	}

	/**
	 * Adds the registers from the specified xml-file to the register
	 * dictionary. <br>
	 * The xml-file must be structured according to
	 * <code>registerDictionary.dtd</code>. The dtd-file must be adapted to
	 * the type values specific to this register. Include
	 * <code><!DOCTYPE registerDefinitions SYSTEM "registerDictionary.dtd"></code>
	 * in your xml file.
	 * 
	 * @param xmlPathname
	 *            path to the xml file
	 * @throws IOException
	 *             throws an IOException if the file is not found
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws ParserConfigurationException
	 *             throws an ParserConfigurationException if the SAX parser
	 *             can't be configured
	 * @throws SAXException
	 *             throws an SAXException if the file could not be successfully
	 *             parsed
	 */
	public void addRegistersFromFile(String xmlPathname) throws IOException,
			ParserConfigurationException, SAXException {
		// reset temporary register variable
		reg = null;
		// Use the default (non-validating) parser
		SAXParserFactory factory = SAXParserFactory.newInstance();
		factory.setValidating(true);
		// Parse the input
		SAXParser saxParser = factory.newSAXParser();
		saxParser.parse(new File(xmlPathname), this);
	}

	/**
	 * Print a list of Registers to standard out.
	 */
	public void printRegisters() {
		System.out
				.println("******************** register dictionary *********************");
		System.out
				.println("Mnemonic\tAltmnemonic\tType\tAddress\tSize\tDescription");
		System.out
				.println("**************************************************************");
		for (Iterator i = registers.iterator(); i.hasNext();) {
			Register r = (Register) i.next();
			System.out.println(r.toString());
		}
		System.out
				.println("**************************************************************");
	}

	/**
	 * Get a register by its name.
	 * 
	 * @param name
	 *            the register name
	 * @return register on null if no register is found
	 */
	public Register getRegister(String name) {
		for (Iterator i = registers.iterator(); i.hasNext();) {
			Register r = (Register) i.next();
			if (r.getMnemonic().equals(name) || r.getAltmnemonic().equals(name)) {
				return r;
			}
		}
		return null;
	}

	// ===========================================================
	// SAX DocumentHandler methods
	// ===========================================================

	public void startDocument() throws SAXException {
	}

	public void endDocument() throws SAXException {
	}

	public void startElement(String namespaceURI, String lName, // local name
			String qName, // qualified name
			Attributes attrs) throws SAXException {
		if (qName.equals(ELEMENT_REGISTER)) {
			if (attrs != null) {
				// instantiate new register
				try {
					reg = (Register) regClass.newInstance();
				} catch (Exception e) {
					throw new SAXException(e.getMessage());
				}
				for (int i = 0; i < attrs.getLength(); i++) {
					String attr_qName = attrs.getQName(i);
					String attr_value = attrs.getValue(i);
					if (attr_qName.equals(ATTR_MNEMONIC)) {
						reg.setMnemonic(attr_value);
					} else if (attr_qName.equals(ATTR_ALTMNEMONIC)) {
						reg.setAltmnemonic(attr_value);
					} else if (attr_qName.equals(ATTR_TYPE)) {
						reg.setType(convertType(attr_value));
					} else if (attr_qName.equals(ATTR_SIZE)) {
						reg.setSize(parseInt(attr_value));
					} else if (attr_qName.equals(ATTR_VALUE)) {
						reg.setValue(parseInt(attr_value));
					} else if (attr_qName.equals(ATTR_ACCESSMODE)) {
						reg.setAccessmode(Register.Accessmode
								.valueOf(attr_value));
					} else if (attr_qName.equals(ATTR_ACCESSATTR)) {
						reg.setAccessattr(Register.Accessattr
								.valueOf(attr_value));
					}
				}
			} else {
				throw new SAXException("attributes expected");
			}
		} else if (qName.equals(ELEMENT_DESCRIPTION)) {
			// reset the cdata for descriptions
			cdata = new StringBuffer();
		}
	}

	@SuppressWarnings("unchecked")
	public void endElement(String namespaceURI, String sName, // simple
			// name
			String qName // qualified name
	) throws SAXException {
		if (qName.equals(ELEMENT_DESCRIPTION)) {
			reg.setDescription(cdata.toString().trim());
		}
		if (reg != null) {
			registers.add(reg);
			reg = null;
		}
	}

	public void characters(char buf[], int offset, int len) throws SAXException {
		int startOffset = offset;
		while ((offset < startOffset + len) && (buf[offset] <= ' ')) {
			offset++;
		}
		len -= offset - startOffset;
		while ((len > 0) && (buf[offset + len - 1] <= ' ')) {
			len--;
		}
		cdata.append(buf, offset, len);
	}

	// ===========================================================
	// SAX ErrorHandler methods
	// ===========================================================

	// treat validation errors as fatal
	public void error(SAXParseException e) throws SAXParseException {
		throw e;
	}

	// dump warnings too
	public void warning(SAXParseException err) throws SAXParseException {
		System.out.println("** Warning" + ", line " + err.getLineNumber()
				+ ", uri " + err.getSystemId());
		System.out.println("   " + err.getMessage());
	}
}
