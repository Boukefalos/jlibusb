package ch.ntb.mcdp.system.memMap;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

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

public class MemoryMap {

	private TreeMap<String, Device> devices;

	// Elements
	private static final String E_MEMORY_MAP = "memorymap";

	private static final String E_DEVICE = "device";

	private static final String A_DEVICE_TYPE = "type";

	private static final String A_DEVICE_WIDTH = "width";

	private static final String E_ATTRIBUTES = "attributes";

	private static final String E_SIZE = "size";

	private static final String E_SEGMENT = "segment";

	private static final String E_INIT = "init";

	private static final String E_ASSIGNMENT = "assignment";

	private static final String A_NAME = "name";

	private static final String A_TYPE = "type";

	private static final String A_TYPE_VALUE = "value";

	private static final String A_TYPE_URI = "uri";

	private static final String A_MODE = "mode";

	private static final String V_MODE_REPLACE = "replace";

	private static final String A_READ = "read";

	private static final String A_WRITE = "write";

	private static final String A_CONST = "const";

	private static final String A_CODE = "code";

	private static final String A_VAR = "var";

	private static final String A_SYSCONST = "sysconst";

	private static final String A_HEAP = "heap";

	private static final String A_STACK = "stack";

	private static final String V_ADD = "add";

	private static final String V_REMOVE = "remove";

	private static final String E_BASE = "base";

	private static final String E_SUBSEGMENT = "subsegment";

	private static final String E_SUBSEGMENTLIST = "subsegmentList";

	private static final String A_START_ID = "startID";

	private static final String A_NUMBER_OF_ENTRIES = "numberOfEntries";

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

	public MemoryMap(String pathToMemFile) throws ParserConfigurationException,
			SAXException, IOException {
		devices = new TreeMap<String, Device>();
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setValidating(true);
		factory.setNamespaceAware(true);
		factory.setXIncludeAware(true);
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
		Document document = builder.parse(new File(pathToMemFile));
		NodeList list = document.getElementsByTagName(E_MEMORY_MAP);
		if (list == null) {
			throw new SAXParseException("<" + E_MEMORY_MAP
					+ "> element not found", null);
		}
		list = list.item(0).getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			// Device (REQUIRED+)
			System.out.println(list.item(i).getNodeName());
			if (list.item(i).getNodeName().equals(E_DEVICE)) {
				Device d = parseDevice(list.item(i));
				devices.put(d.getType(), d);
			}
		}
	}

	private Device parseDevice(Node node) {
		NamedNodeMap attributes = node.getAttributes();
		String type = null;
		int width = 0;
		for (int j = 0; j < attributes.getLength(); j++) {
			if (attributes.item(j).getNodeName().equals(A_DEVICE_TYPE)) {
				type = attributes.item(j).getNodeValue();
				System.out.println("\tType: " + type);
			} else if (attributes.item(j).getNodeName().equals(A_DEVICE_WIDTH)) {
				width = parseInt(attributes.item(j).getNodeValue());
				System.out.println("\tWidth: " + width);
			}
		}
		Device d = new Device(type, width);
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			System.out.println("\t\t" + children.item(i).getNodeName());
			// ELEMENT attributes (REQUIRED)
			// ELEMENT size (OPTIONAL?)
			// ELEMENT segment (REQUIRED+)
			// ELEMENT init (OPTIONAL)
			if (children.item(i).getNodeName().equals(E_ATTRIBUTES)) {
				parseAttributes(children.item(i), d.getAttributes());
			} else if (children.item(i).getNodeName().equals(E_SIZE)) {
				d.setSize(parseInt(children.item(i).getTextContent()));
			} else if (children.item(i).getNodeName().equals(E_SEGMENT)) {
				d.segments
						.add(parseSegment(children.item(i), d.getAttributes()));
			} else if (children.item(i).getNodeName().equals(E_INIT)) {
				parseInit(children.item(i));
			}
		}
		return d;
	}

	private void setAttributes(String nodeValue, MemAttributes attributes,
			int index) {
		if (nodeValue.equals(V_ADD)) {
			attributes.set(index);
			System.out.println("setAttribute (" + index + "): set: "
					+ attributes.isSet(index));
		} else if (nodeValue.equals(V_REMOVE)) {
			attributes.unset(index);
			System.out.println("setAttribute (" + index + "): unset: "
					+ attributes.isSet(index));
		}
	}

	private void parseAttributes(Node node, MemAttributes attributes) {
		NamedNodeMap attr = node.getAttributes();
		for (int i = 0; i < attr.getLength(); i++) {
			String nodeName = attr.item(i).getNodeName();
			String nodeValue = attr.item(i).getNodeValue();
			System.out.println(nodeName);
			if (nodeName.equals(A_MODE)) {
				if (nodeValue.equals(V_MODE_REPLACE)) {
					// clear all attributes
					attributes.reset();
				}
			} else if (nodeName.equals(A_READ)) {
				setAttributes(nodeValue, attributes, MemAttributes.read);
			} else if (nodeName.equals(A_WRITE)) {
				setAttributes(nodeValue, attributes, MemAttributes.write);
			} else if (nodeName.equals(A_CONST)) {
				setAttributes(nodeValue, attributes, MemAttributes.const_);
			} else if (nodeName.equals(A_CODE)) {
				setAttributes(nodeValue, attributes, MemAttributes.code);
			} else if (nodeName.equals(A_VAR)) {
				setAttributes(nodeValue, attributes, MemAttributes.var);
			} else if (nodeName.equals(A_SYSCONST)) {
				setAttributes(nodeValue, attributes, MemAttributes.sysconst);
			} else if (nodeName.equals(A_HEAP)) {
				setAttributes(nodeValue, attributes, MemAttributes.heap);
			} else if (nodeName.equals(A_STACK)) {
				setAttributes(nodeValue, attributes, MemAttributes.stack);
			}
		}
	}

	private int convertToNumber(String constant) {
		int result = -1;
		try {
			return parseInt(constant);
		} catch (NumberFormatException e) {
			// TODO: convert constant to number
			System.out.println("setBase: Kernel Constant: " + constant);
		}
		return result;
	}

	private Segment parseSegment(Node node, MemAttributes attributes) {
		// ATTRIBUTE name (REQUIRED) TODO
		// ELEMENT attributes (OPTIONAL?)
		// ELEMENT base (REQUIRED)
		// ELEMENT size (REQUIRED)
		// ELEMENT subsegment (OPTIONAL*)
		// ELEMENT subsegmentList (OPTIONAL?)
		NamedNodeMap attrMap = node.getAttributes();
		String name = "";
		for (int i = 0; i < attrMap.getLength(); i++) {
			if (attrMap.item(i).getNodeName().equals(A_NAME)) {
				name = attrMap.item(i).getNodeValue();
				// TODO remove
				System.out.println("-> Segment: name: " + name);
			}
		}
		MemAttributes attr = attributes.clone();
		Segment s = new Segment(name, attr);
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals(E_ATTRIBUTES)) {
				parseAttributes(children.item(i), attr);
			} else if (children.item(i).getNodeName().equals(E_BASE)) {
				s.setBase(convertToNumber(children.item(i).getTextContent()));
			} else if (children.item(i).getNodeName().equals(E_SIZE)) {
				s.setSize(convertToNumber(children.item(i).getTextContent()));
			} else if (children.item(i).getNodeName().equals(E_SUBSEGMENT)) {
				s.add(parseSegment(children.item(i), attr));
			} else if (children.item(i).getNodeName().equals(E_SUBSEGMENTLIST)) {
				parseSegmentList(s, children.item(i), attr);
			}
		}
		return s;
	}

	private void parseSegmentList(Segment rootSegment, Node segmentList,
			MemAttributes attributes) {
		String name = "";
		int nofEntries = 0, startID = 0, size = 0;
		MemAttributes attr = rootSegment.getMemAttributes().clone();
		NamedNodeMap attrMap = segmentList.getAttributes();
		for (int i = 0; i < attrMap.getLength(); i++) {
			if (attrMap.item(i).getNodeName().equals(A_NAME)) {
				name = attrMap.item(i).getNodeValue();
			} else if (attrMap.item(i).getNodeName()
					.equals(A_NUMBER_OF_ENTRIES)) {
				nofEntries = parseInt(attrMap.item(i).getNodeValue());
			} else if (attrMap.item(i).getNodeName().equals(A_START_ID)) {
				startID = parseInt(attrMap.item(i).getNodeValue());
			}
		}
		NodeList children = segmentList.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals(E_ATTRIBUTES)) {
				parseAttributes(children.item(i), attr);
			} else if (children.item(i).getNodeName().equals(E_SIZE)) {
				size = parseInt(children.item(i).getTextContent());
			}
		}
		for (int i = 0; i < nofEntries; i++) {
			Segment s = new Segment(new String(name
					+ Integer.toString(startID + i)), attr);
			s.setSize(size);
			rootSegment.add(s);
		}
	}

	private void parseInit(Node node) {
		NodeList children = node.getChildNodes();
		for (int i = 0; i < children.getLength(); i++) {
			if (children.item(i).getNodeName().equals(E_ASSIGNMENT)) {
				NamedNodeMap attr = children.item(i).getAttributes();
				String name = null, type = null, value = null;
				for (int j = 0; j < attr.getLength(); j++) {
					if (attr.item(j).getNodeName().equals(A_NAME)) {
						name = attr.item(j).getNodeValue();
					} else if (attr.item(j).getNodeName().equals(A_TYPE)) {
						type = attr.item(j).getNodeValue();
					}
				}
				value = children.item(i).getTextContent();
				System.out.print("\t\t\t assignment: type = " + type
						+ ", name = " + name + ", value = ");
				if (type.equals(A_TYPE_VALUE)) {
					System.out.println("0x"
							+ Integer.toHexString(parseInt(value)));
				} else if (type.equals(A_TYPE_URI)) {
					System.out.println(value);
				}
			}
		}
	}

	public void printMemMap() {
		Collection coll = devices.values();
		System.out.println("*** *** Memory Map *** ***");
		for (Iterator iter = coll.iterator(); iter.hasNext();) {
			Device element = (Device) iter.next();
			printTab(0);
			System.out.println("Device: " + element.getType() + ", size: 0x"
					+ Integer.toHexString(element.getSize()) + ", width: "
					+ element.getWidth());
			for (Iterator segIter = element.segments.iterator(); segIter
					.hasNext();) {
				Segment seg = (Segment) segIter.next();
				printSegment(1, seg);
			}
		}
		System.out.println("*** *** *** *** *** *** *** ***");
	}

	private void printTab(int count) {
		for (int i = 0; i < count; i++) {
			System.out.print('\t');
		}
	}

	private void printSegment(int indent, Segment seg) {
		printTab(indent);
		System.out.println("Segment: " + seg.getName() + ", base: 0x"
				+ Integer.toHexString(seg.getBase()) + ", size: 0x"
				+ Integer.toHexString(seg.getSize()));
		printMemAttributes(indent + 1, seg.getMemAttributes());
		for (Iterator subSegIter = seg.getSegments().iterator(); subSegIter
				.hasNext();) {
			Segment subseg = (Segment) subSegIter.next();
			printSegment(2, subseg);
		}
	}

	private void printMemAttributes(int indent, MemAttributes attr) {
		printTab(indent);
		System.out.print("MemAttributes: ");
		for (int i = 0; i < MemAttributes.NOF_ATTRIBUTES; i++) {
			if (attr.isSet(i)) {
				if (i > 0) {
					System.out.print(", ");
				}
				String type = null;
				switch (i) {
				case 0:
					type = "read";
					break;
				case 1:
					type = "write";
					break;
				case 2:
					type = "const";
					break;
				case 3:
					type = "code";
					break;
				case 4:
					type = "var";
					break;
				case 5:
					type = "sysconst";
					break;
				case 6:
					type = "heap";
					break;
				case 7:
					type = "stack";
					break;
				}
				System.out.print(type);
			}
		}
		System.out.println();
	}
}
