package ch.ntb.mcdp.dict;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
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

public abstract class RegisterDict {

	private LinkedList registers;

	private Class regClass;

	private static final long serialVersionUID = -582382284126896830L;

	private static final String REGISTER_DEFINITIONS = "registerDefinitions";

	private static final String REGISTER_GROUP = "registerGroup";

	private static final String REG_GROUP_ATTR_BASEADDR = "baseAddress";

	private static final String REGISTER = "register";

	private static final String DESCRIPTION = "description";

	private static final String REG_ATTR_NAME = "name";

	private static final String REG_ATTR_TYPE = "type";

	private static final String REG_ATTR_VALUE = "value";

	private static final String REG_ATTR_SIZE = "size";

	protected RegisterDict(String registerClass) {
		try {
			this.regClass = Class.forName(registerClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.exit(1);
		}
		this.registers = new LinkedList();
	}

	@SuppressWarnings("unchecked")
	protected void add(String name, int type, int value, int size,
			String description) {
		// remove before add for updates
		for (Iterator i = registers.iterator(); i.hasNext();) {
			if (((Register)i.next()).name.equals(name)) {
				i.remove();
			}
		}
		Constructor[] regConstructors = regClass.getDeclaredConstructors();
		Register reg = null;
		try {
			reg = (Register) regConstructors[0].newInstance(name, type, value, size, description);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
		registers.add(reg);
	}

	protected int parseInt(String s) {
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

	public Register getRegister(String name) {
		for (Iterator i = registers.iterator(); i.hasNext();) {
			Register r = (Register) i.next();
			if (r.name.equals(name)) {
				return r;
			}
		}
		return null;
	}

	public void printRegisters() {
		System.out
				.println("******************** register dictionary *********************");
		System.out.println("Name\tType\tAddress\tSize\tDescription");
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
	 * <code>registerDictionary.dtd</code>. Include
	 * <code><!DOCTYPE registerDefinitions SYSTEM "registerDictionary.dtd"></code>
	 * in your xml file.
	 * 
	 * @param xmlPathname
	 * @throws IOException
	 * @throws ParserConfigurationException
	 * @throws SAXException
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
					Node n = attributes.getNamedItem(REG_ATTR_NAME);
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
		for (int index = 0; index < Register.types.length; index++) {
			if (typeStr.equals(Register.types[index])) {
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
				Node n = attributes.getNamedItem(REG_ATTR_NAME);
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
