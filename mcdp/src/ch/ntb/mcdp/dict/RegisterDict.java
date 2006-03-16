package ch.ntb.mcdp.dict;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Representation of a register dictionary. All registers are read from an
 * xml-file specified by <code>registerDictionary.dtd</code>. Note that the
 * type values are implementation specific. Therefore each <i>Register</i> has
 * its own <i>registerDictionary.dtd</i> definition. For an example see
 * <code>MPC555Register</code> and the corresponding
 * <code>resources/targets/mpc555/registerDictionary.dtd</code> file.
 * 
 * @author schlaepfer
 */
public abstract class RegisterDict {

	private LinkedList registers;

	private Class<? extends Register> regClass;

	private Method regClassGetTypesMethod;

	private static final String GetTypes_METHOD_NAME = "getTypes";

	private String[] types;

	private static final long serialVersionUID = -582382284126896830L;

	private static final String REGISTER_DEFINITIONS = "registerDefinitions";

	private static final String REGISTER_GROUP = "registerGroup";

	private static final String REG_GROUP_ATTR_BASEADDR = "baseAddress";

	private static final String REGISTER = "register";

	private static final String DESCRIPTION = "description";

	private static final String REG_ATTR_MNEMONIC = "mnemonic";

	private static final String REG_ATTR_TYPE = "type";

	private static final String REG_ATTR_VALUE = "value";

	private static final String REG_ATTR_SIZE = "size";

	/**
	 * Default constructor which takes the Class object from a
	 * <code>Register</code> subclass as argument. The registerDict will be of
	 * this Register-type.<br>
	 * An example:<br>
	 * MPC555Register extends Register -> use MPC555Register.class as parameter.
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

	/**
	 * Add a new register to the registerDict.
	 * 
	 * @param name
	 *            name of the register. Registers are identified by this value.
	 * @param type
	 *            register specific type
	 * @param value
	 *            address or a register specific value (e.g. BDI-identifier)
	 * @param size
	 *            size in bytes
	 * @param description
	 *            a string description of the register
	 */
	@SuppressWarnings("unchecked")
	public void add(String name, int type, int value, int size,
			String description) {
		// remove before add for updates
		for (Iterator i = registers.iterator(); i.hasNext();) {
			Register r = (Register) i.next();
			if (r.getMnemonic().equals(name) || r.getAltmnemonic().equals(name)) {
				i.remove();
			}
		}
		Register reg = null;
		try {
			reg = (Register) regClass.newInstance();
			reg.setMnemonic(name);
			reg.setType(type);
			reg.setValue(value);
			reg.setSize(size);
			reg.setDescription(description);
		} catch (Exception e) {
			e.printStackTrace();
			// TODO exception handling
			System.exit(1);
		}
		registers.add(reg);
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
	 * @throws ParserConfigurationException
	 *             throws an ParserConfigurationException if the SAX parser
	 *             can't be configured
	 * @throws SAXException
	 *             throws an SAXException if the file could not be successfully
	 *             parsed
	 */
	public void addRegistersFromFile(String xmlPathname) throws IOException,
			ParserConfigurationException, SAXException {
		Document document;
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		DocumentBuilder builder = factory.newDocumentBuilder();
		builder.setErrorHandler(new ErrorHandler() {
			// ignore fatal errors (an exception is guaranteed)
			public void fatalError(SAXParseException exception)
					throws SAXException {
			}

			// treat validation errors as fatal error
			public void error(SAXParseException e) throws SAXParseException {
				throw e;
			}

			// treat warnings as fatal error
			public void warning(SAXParseException e) throws SAXParseException {
				throw e;
			}
		});
		document = builder.parse(new File(xmlPathname));
		NodeList list = document.getElementsByTagName(REGISTER_DEFINITIONS);
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeName().equals(REGISTER_DEFINITIONS)) {
				list = list.item(i).getChildNodes();
			}
			for (int j = 0; j < list.getLength(); j++) {
				if (list.item(j).getNodeName().equals(REGISTER_GROUP)) {
					NamedNodeMap attributes = list.item(j).getAttributes();
					for (int k = 0; k < attributes.getLength(); k++) {
						if (attributes.item(k).getNodeName().equals(
								REG_GROUP_ATTR_BASEADDR)) {
							int baseAddr = parseInt(attributes.item(k)
									.getNodeValue());
							parseRegisterGroup(list.item(j), baseAddr);
						}
					}
				} else if (list.item(j).getNodeName().equals(REGISTER)) {
					NamedNodeMap attributes = list.item(j).getAttributes();
					// attributes: name, type, offset, size
					Node n = attributes.getNamedItem(REG_ATTR_MNEMONIC);
					String name = n.getNodeValue();
					n = attributes.getNamedItem(REG_ATTR_TYPE);
					String typeStr = n.getNodeValue();
					int type = convertType(typeStr);
					n = attributes.getNamedItem(REG_ATTR_VALUE);
					int value = parseInt(n.getNodeValue());
					n = attributes.getNamedItem(REG_ATTR_SIZE);
					int size = parseInt(n.getNodeValue());
					parseRegister(list.item(j), name, type, value, size);
				}
			}
		}
	}

	protected int convertType(String typeStr) throws SAXException {
		for (int index = 0; index < types.length; index++) {
			if (typeStr.equals(types[index])) {
				return index;
			}
		}
		throw new SAXException("invalid register definition: " + typeStr);
	}

	private void parseRegisterGroup(Node registerGroup, int baseAddr)
			throws SAXException {
		NodeList list = registerGroup.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeName().equals(REGISTER)) {
				NamedNodeMap attributes = list.item(i).getAttributes();
				// attributes: name, type, offset, size
				Node n = attributes.getNamedItem(REG_ATTR_MNEMONIC);
				String name = n.getNodeValue();
				n = attributes.getNamedItem(REG_ATTR_TYPE);
				String typeStr = n.getNodeValue();
				int type = convertType(typeStr);
				n = attributes.getNamedItem(REG_ATTR_VALUE);
				int offset = parseInt(n.getNodeValue());
				n = attributes.getNamedItem(REG_ATTR_SIZE);
				int size = parseInt(n.getNodeValue());

				parseRegister(list.item(i), name, type, baseAddr + offset, size);
			}
		}
	}

	private void parseRegister(Node register, String name, int type, int addr,
			int size) throws SAXException {
		NodeList list = register.getChildNodes();
		String description = "";
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeName().equals(DESCRIPTION)) {
				description = list.item(i).getTextContent();
			}
		}
		add(name, type, addr, size, description);
	}
}
