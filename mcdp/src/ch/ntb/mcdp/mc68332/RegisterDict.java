package ch.ntb.mcdp.mc68332;

import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class RegisterDict {

	private static LinkedList<Register> registers;

	private static final long serialVersionUID = -582382284126896830L;

	private static final String REGISTER_DEFINITIONS = "registerDefinitions";

	private static final String REGISTER_GROUP = "registerGroup";

	private static final String REG_GROUP_ATTR_BASEADDR = "baseAddress";

	private static final String REGISTER = "register";

	private static final String DESCRIPTON = "description";

	private static final String REG_ATTR_NAME = "name";

	private static final String REG_ATTR_TYPE = "type";

	private static final String REG_ATTR_OFFSET = "offset";

	private static final String REG_ATTR_SIZE = "size";

	private static final String REG_ATTR_TYPE_CTRLREG = "CtrlReg";

	private static final String REG_ATTR_TYPE_USERREG = "UserReg";

	private static final String REG_ATTR_TYPE_SYSREG = "SysReg";

	static {
		registers = new LinkedList<Register>();

		// data registers
		add("D0", Register.UserReg, 0x0, 4, "data register 0");
		add("D1", Register.UserReg, 0x1, 4, "data register 1");
		add("D2", Register.UserReg, 0x2, 4, "data register 2");
		add("D3", Register.UserReg, 0x3, 4, "data register 3");
		add("D4", Register.UserReg, 0x4, 4, "data register 4");
		add("D5", Register.UserReg, 0x5, 4, "data register 5");
		add("D6", Register.UserReg, 0x6, 4, "data register 6");
		add("D7", Register.UserReg, 0x7, 4, "data register 7");

		// address registers
		add("A0", Register.UserReg, 0x8, 4, "address register 0");
		add("A1", Register.UserReg, 0x9, 4, "address register 1");
		add("A2", Register.UserReg, 0xA, 4, "address register 2");
		add("A3", Register.UserReg, 0xB, 4, "address register 3");
		add("A4", Register.UserReg, 0xC, 4, "address register 4");
		add("A5", Register.UserReg, 0xD, 4, "address register 5");
		add("A6", Register.UserReg, 0xE, 4, "address register 6");
		add("A7", Register.UserReg, 0xF, 4, "address register 7");

		// system registers
		add("RPC", Register.SysReg, 0x0, 4, "return program counter");
		add("PCC", Register.SysReg, 0x1, 4,
				"current instruction program counter");
		add("SR", Register.SysReg, 0xB, 2, "status register");
		add("USP", Register.SysReg, 0xC, 4, "user stack pointer (A7)");
		add("SSP", Register.SysReg, 0xD, 4, "supervisor stack pointer");
		add("SFC", Register.SysReg, 0xE, 4, "source function code register");
		add("DFC", Register.SysReg, 0xF, 4,
				"destination function code register");
		add("ATEMP", Register.SysReg, 0x8, 4, "temporary register A");
		add("FAR", Register.SysReg, 0x9, 4, "fault address register");
		add("VBR", Register.SysReg, 0xA, 4, "vector base register");

		// TODO: remove
		try {
			addRegistersFromFile("resources/targets/mc68332/registerDictionary.xml");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static void add(String name, int type, int addr, int size,
			String description) {
		// remove before add for updates
		for (Iterator<Register> i = registers.iterator(); i.hasNext();) {
			if (i.next().name.equals(name)) {
				i.remove();
			}
		}
		registers.add(new Register(name, type, addr, size, description));
	}

	private static int parseInt(String s) {
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

	public static Register getRegister(String name) {
		for (Iterator<Register> i = registers.iterator(); i.hasNext();) {
			Register r = i.next();
			if (r.name.equals(name)) {
				return r;
			}
		}
		return null;
	}

	public static void printRegisters() {
		System.out
				.println("******************** register dictionary *********************");
		System.out.println("Name \t Type \t\t Address \t Size \t Description");
		System.out
				.println("**************************************************************");
		for (Iterator<Register> i = registers.iterator(); i.hasNext();) {
			Register r = i.next();
			String type;
			switch (r.type) {
			case Register.CtrReg:
				type = "CtrReg";
				break;
			case Register.SysReg:
				type = "SysReg";
				break;
			case Register.UserReg:
				type = "UserReg";
				break;
			default:
				type = Integer.toString(r.type);
			}
			System.out.println(r.name + "\t" + type + "\t\t0x"
					+ Integer.toHexString(r.addr) + "\t\t" + r.size + "\t"
					+ r.description);
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
	public static void addRegistersFromFile(String xmlPathname)
			throws IOException, ParserConfigurationException, SAXException {
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

			// treat warnings errors as fatal error
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
				}
			}
		}
	}

	private static void parseRegisterGroup(Node registerGroup, int baseAddr)
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
				int type;
				if (typeStr.equals(REG_ATTR_TYPE_CTRLREG)) {
					type = Register.CtrReg;
				} else if (typeStr.equals(REG_ATTR_TYPE_SYSREG)) {
					type = Register.SysReg;
				} else if (typeStr.equals(REG_ATTR_TYPE_USERREG)) {
					type = Register.UserReg;
				} else {
					throw new SAXException("invalid register definition: "
							+ list.item(i).getNodeName());
				}
				n = attributes.getNamedItem(REG_ATTR_OFFSET);
				int offset = parseInt(n.getNodeValue());
				n = attributes.getNamedItem(REG_ATTR_SIZE);
				int size = parseInt(n.getNodeValue());

				parseRegister(list.item(i), name, type, baseAddr + offset, size);
			}
		}
	}

	private static void parseRegister(Node register, String name, int type,
			int addr, int size) throws SAXException {
		NodeList list = register.getChildNodes();
		String description = "";
		for (int i = 0; i < list.getLength(); i++) {
			if (list.item(i).getNodeName().equals(DESCRIPTON)) {
				description = list.item(i).getTextContent();
			}
		}
		add(name, type, addr, size, description);
	}
}
