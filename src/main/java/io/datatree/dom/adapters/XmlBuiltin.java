/**
 * This software is licensed under the Apache 2 license, quoted below.<br>
 * <br>
 * Copyright 2017 Andras Berkes [andras.berkes@programmer.net]<br>
 * <br>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at<br>
 * <br>
 * http://www.apache.org/licenses/LICENSE-2.0<br>
 * <br>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.datatree.dom.adapters;

import java.io.StringReader;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import io.datatree.Tree;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;

/**
 * <b>BUILT-IN XML ADAPTER</b><br>
 * <br>
 * Description: Built-in API without any dependencies.<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputString, "xml");<br>
 * String outputString = node.toString("xml");<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "XmlBuiltin");<br>
 * String outputString = node.toString("XmlBuiltin");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(20)
public class XmlBuiltin extends AbstractTextAdapter {

	// --- CONSTANTS ---

	protected static final char[] XML_HEADER = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n".toCharArray();
	protected static final char[] EQUALS = "=\"".toCharArray();
	protected static final char[] CR_LF = "\r\n".toCharArray();
	protected static final char[] BEGIN_END_TAG = "</".toCharArray();
	protected static final char[] LT = "&lt;".toCharArray();
	protected static final char[] GT = "&gt;".toCharArray();
	protected static final char[] AMP = "&amp;".toCharArray();
	protected static final char[] QUOT = "&quot;".toCharArray();
	protected static final char[] X27 = "&#x27;".toCharArray();
	protected static final char[] ITEM_BEGIN = "<item>".toCharArray();
	protected static final char[] ITEM_END = "<item>".toCharArray();

	// --- COMMON XML BUILDER FACTORY ---

	public DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();

	// --- NAME OF THE FORMAT ---
	
	@Override
	public String getFormat() {
		return "xml";
	}
	
	// --- CONSTRUCTOR ---

	public XmlBuiltin() {
		builderFactory.setValidating(false);
	}
	
	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		if (value == null) {
			return "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n<xml/>";
		}
		StringBuilder out = new StringBuilder(512);
		out.append(XML_HEADER);
		Tree node = new ParsedTree(value, meta);
		toXML(out, pretty ? 0 : -1, insertMeta && node.isMeta() ? false : insertMeta, node);
		return out.toString();
	}

	// --- protected UTILITIES ---

	protected static final void toXML(StringBuilder out, int indent, boolean insertMeta, Tree node) {
		String name = node.get("_name", node.isPrimitive() ? "item" : node.getName());

		// Start XML element
		addSpaces(out, indent);
		out.append('<');
		String elementName;
		if (name == null || name.isEmpty()) {
			elementName = node.get("_name", node.getParent() == null ? "xml" : "item");
		} else {
			elementName = name;
		}
		out.append(elementName);

		// Write attributes
		for (Tree attr : node) {
			String attrName = attr.getName();
			if (!attrName.startsWith("@")) {
				continue;
			}
			out.append(' ');
			out.append(attrName.substring(1));
			out.append(EQUALS);
			writeAttribute(out, attr.asString());
			out.append('\"');
		}

		// End of XML element
		out.append('>');
		if (indent > -1) {
			out.append(CR_LF);
		}

		// Insert metadata
		if (insertMeta) {
			Tree meta = node.getMeta(false);
			if (meta != null && !meta.isEmpty()) {
				toXML(out, indent > -1 ? 2 : -1, false, meta);
			}
		}

		// Properties
		int len = out.length();
		String text = null;
		for (Tree child : node) {
			String childName = child.getName();
			if (childName.isEmpty() || node.isEnumeration()) {
				childName = "item";
			} else {
				if (childName.startsWith("@") || "_name".equals(childName)) {
					continue;
				}
				if ("_text".equals(childName)) {
					text = child.asString();
					continue;
				}
			}
			
			// BASE64
			if (!child.isPrimitive()) {
				continue;
			}
			if (indent > -1) {
				addSpaces(out, indent + 2);
			}
			out.append('<');
			out.append(childName);
			out.append('>');
			writeXMLContent(out, String.valueOf(child.asString()));
			out.append(BEGIN_END_TAG);
			out.append(childName);
			out.append('>');
			if (indent > -1) {
				out.append(CR_LF);
			}
		}
		if (text == null && node.isPrimitive()) {
			text = node.asString();
		}
		if (text == null) {

			// Write items of array
			Tree items = node.get("_items");

			if (items != null) {
				addChildren(out, indent, items, false);
			} else {
				addChildren(out, indent, node, true);
			}

			// Formatting
			if (indent > -1) {
				if (out.length() == len) {
					out.setLength(len - 2);
				} else {
					addSpaces(out, indent);
				}
			}
		} else {

			// Formatting
			if (indent > -1) {
				out.setLength(len - 2);
			}

			// Text content
			writeXMLContent(out, text.trim());
		}

		// Close element
		out.append(BEGIN_END_TAG);
		out.append(elementName);
		out.append('>');
		if (indent > 0) {
			out.append(CR_LF);
		}
	}

	protected static final void addChildren(StringBuilder out, int indent, Tree node, boolean skipPrimitive) {
		int newIndent = indent > -1 ? indent + 2 : 0;
		for (Tree child : node) {
			if (child.isPrimitive()) {
				if (skipPrimitive) {
					continue;
				}
				addSpaces(out, newIndent);
				out.append(ITEM_BEGIN);
				out.append(child.asString());
				out.append(ITEM_END);
				if (indent > 0) {
					out.append(CR_LF);
				}
				continue;
			}
			toXML(out, indent > -1 ? newIndent : -1, false, child);
		}
	}

	protected static final void addSpaces(StringBuilder out, int count) {
		if (count < 1) {
			return;
		}
		for (int i = 0; i < count; i++) {
			out.append(' ');
		}
	}

	protected static final void writeAttribute(StringBuilder builder, String fromString) {
		if (fromString == null) {
			return;
		}
		char[] chars = fromString.toCharArray();
		if (chars.length != 0) {
			char c;
			for (int n = 0; n < chars.length; n++) {
				c = chars[n];
				if (c == '"') {
					builder.append(QUOT);
				} else {
					builder.append(c);
				}
			}
		}
	}

	protected static final void writeXMLContent(StringBuilder builder, String fromString) {
		if (fromString == null) {
			return;
		}
		char[] chars = fromString.toCharArray();
		if (chars.length != 0) {
			char c;
			for (int n = 0; n < chars.length; n++) {
				c = chars[n];
				switch (c) {
				case '<':
					builder.append(LT);
					break;
				case '>':
					builder.append(GT);
					break;
				case '&':
					builder.append(AMP);
					break;
				case '"':
					builder.append(QUOT);
					break;
				case '\'':
					builder.append(X27);
					break;
				default:
					builder.append(c);
					break;
				}
			}
		}
	}
	
	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		DocumentBuilder builder = builderFactory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(source));
		Node node = builder.parse(is).getFirstChild();
		LinkedHashMap<String, Object> map = new LinkedHashMap<>();
		return copyChildren("xml", node, map);
	}

	// --- RECURSIVE STRUCTURE BUILDER ---

	protected static final Object copyChildren(String parentName, Node source, LinkedHashMap<String, Object> map) {

		// Node name
		LinkedList<Object> array = null;
		boolean isArray = isArray(source);
		String name = source.getNodeName();
		if (name != null && !parentName.equals(name)) {
			map.put("_name", name);
		}

		// Copy attributes
		NamedNodeMap attributes = source.getAttributes();
		if (attributes != null) {
			int max = attributes.getLength();
			for (int i = 0; i < max; i++) {
				Node attribute = attributes.item(i);
				name = attribute.getNodeName();
				String value = attribute.getNodeValue();
				if (name != null && value != null) {
					map.put('@' + name, value);
				}
			}
		}

		// JSON array?
		if (isArray) {
			array = new LinkedList<>();
			if (hasProperties(source)) {
				map.put("_items", array);
			}
		}

		// Loop on children
		NodeList children = source.getChildNodes();
		if (children != null) {
			int max = children.getLength();
			for (int i = 0; i < max; i++) {
				Node child = children.item(i);
				if (child.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				String value = getTextValue(child);
				if (value != null && !hasProperties(child)) {

					// Property
					name = child.getNodeName();
					if (name != null) {
						if (isArray) {
							putWithType(null, array, name, value);
						} else {
							putWithType(map, null, name, value);
						}
					}

				} else {

					// Substructure
					LinkedHashMap<String, Object> subTree = new LinkedHashMap<>();
					if (isArray) {
						name = "";
						array.add(subTree);
					} else {
						name = child.getNodeName();
						map.put(name, subTree);
					}
					String text = getTextValue(child);
					if (text != null && !text.isEmpty()) {
						subTree.put("_text", text);
					}

					// Recursive copy
					Object result = copyChildren(name, child, subTree);
					if (result != subTree) {
						map.put(name, result);
					}
				}
			}
		}
		return map.isEmpty() && array != null ? array : map;
	}

	// --- XML DOM HELPERS ---

	protected static final boolean isArray(Node node) {
		NodeList children = node.getChildNodes();
		if (children != null) {
			HashSet<String> names = new HashSet<String>();
			int max = children.getLength();
			for (int i = 0; i < max; i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					String name = child.getNodeName();
					if (names.contains(name)) {
						return true;
					}
					names.add(name);
				}
			}
		}
		return false;
	}

	protected static final boolean hasProperties(Node node) {
		NamedNodeMap attributes = node.getAttributes();
		if (attributes != null && attributes.getLength() > 0) {
			return true;
		}
		NodeList children = node.getChildNodes();
		if (children != null) {
			int max = children.getLength();
			boolean textOnly = false;
			boolean allItem = true;
			for (int i = 0; i < max; i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.TEXT_NODE) {
					continue;
				}
				String value = getTextValue(child);
				if (value != null) {
					textOnly = true;
					if (allItem && !"item".equals(child.getNodeName())) {
						allItem = false;
					}
				} else {
					textOnly = false;
					break;
				}
			}
			if (textOnly && !(max > 1 && allItem)) {
				return true;
			}
		}
		return false;
	}

	protected static final String getTextValue(Node node) {
		if (node.getNodeType() == Node.ELEMENT_NODE) {
			NodeList subChildren = node.getChildNodes();
			if (subChildren != null) {
				int subMax = subChildren.getLength();
				StringBuilder tmp = new StringBuilder(32);
				for (int j = 0; j < subMax; j++) {
					Node subChild = subChildren.item(j);
					short type = subChild.getNodeType();
					if (type == Node.TEXT_NODE || type == Node.CDATA_SECTION_NODE) {
						tmp.append(subChild.getNodeValue());
					} else if (type == Node.ELEMENT_NODE) {
						return null;
					}
				}
				return tmp.toString();
			}
		}
		return null;
	}

	protected static final void putWithType(LinkedHashMap<String, Object> map, LinkedList<Object> list, String name,
			String value) {
		if (value == null || value.isEmpty()) {
			putOrAdd(map, list, name, value);
			return;
		}
		if ("true".equals(value)) {
			putOrAdd(map, list, name, true);
			return;
		}
		if ("false".equals(value)) {
			putOrAdd(map, list, name, false);
			return;
		}
		boolean isNumeric = true;
		boolean isInteger = true;
		boolean wasPoint = false;
		char[] chars = value.toCharArray();
		for (char c : chars) {
			if (c == '.') {
				if (wasPoint) {
					isNumeric = false;
					break;					
				}
				isInteger = false;
				wasPoint = true;
				continue;
			}
			if (c < '0' || c > '9') {
				isNumeric = false;
				break;
			}
		}
		if (isNumeric) {
			if (isInteger) {
				putOrAdd(map, list, name, Long.parseLong(value));
			} else {
				putOrAdd(map, list, name, Double.parseDouble(value));
			}
			return;
		}
		putOrAdd(map, list, name, value);
	}

	protected static final void putOrAdd(LinkedHashMap<String, Object> map, LinkedList<Object> list, String name,
			Object value) {
		if (map == null) {
			list.add(value);
		} else {
			map.put(name, value);
		}
	}

}