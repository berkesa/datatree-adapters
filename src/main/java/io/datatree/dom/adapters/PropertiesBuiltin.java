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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import io.datatree.Tree;
import io.datatree.dom.Config;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;

/**
 * <b>BUILT-IN JAVA PROPERTIES ADAPTER</b><br>
 * <br>
 * Description: Built-in API without any dependencies.<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * -Ddatatree.properties.reader=io.datatree.dom.adapters.PropertiesBuiltin<br>
 * -Ddatatree.properties.writer=io.datatree.dom.adapters.PropertiesBuiltin<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * If there is more than one "properties" implementation on classpath, the
 * preferred implementation is adjustable with the following System Properties.
 * If there is only one (this) implementation on the classpath, this step is NOT
 * necessary, the DataTree API will use this implementation automatically.<br>
 * <br>
 * PropertiesBuiltin properties = new PropertiesBuiltin();<br>
 * TreeReaderRegistry.setReader("properties", properties);<br>
 * TreeWriterRegistry.setWriter("properties", properties);<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputString, "properties");<br>
 * String outputString = node.toString("properties");<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "PropertiesBuiltin");<br>
 * String outputString = node.toString("PropertiesBuiltin");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(20)
public class PropertiesBuiltin extends AbstractTextAdapter {

	// --- CONSTANTS ---

	public static final String FIRST_INDEX = "firstIndex";

	protected static final String LINE_SEPARATOR = System.getProperty("line.separator", "\r\n");
	protected static final char[] HEX = "0123456789ABCDEF".toCharArray();
	protected static final int UNICODE_ESCAPE = -1;
	protected static final int[] VALUE_ESCAPES;
	protected static final int[] KEY_ESCAPES;
	
	// --- NAME OF THE FORMAT ---

	@Override
	public String getFormat() {
		return "properties";
	}

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		if (value == null) {
			return "";
		}
		StringBuilder builder = new StringBuilder(512);
		Tree node = new ParsedTree(value, meta);
		Tree zeroIndexes = node.get(Config.META + '.' + FIRST_INDEX);
		write(builder, node, zeroIndexes);
		if (insertMeta && meta != null) {
			write(builder, node.getMeta(), zeroIndexes);
		}
		return builder.toString();
	}

	// --- RECURSIVE WRITE ---

	protected static final void write(StringBuilder builder, Tree node, Tree zeroIndexes) {
		int startIndex = zeroIndexes == null ? 1 : Integer.MIN_VALUE;
		for (Tree child : node) {
			if (child.isStructure()) {
				write(builder, child, zeroIndexes);
			} else {
				if (startIndex == Integer.MIN_VALUE) {
					startIndex = zeroIndexes.get(node.getName(), 1);
				}
				append(builder, child.getPath(startIndex), String.valueOf(child.asString()));
			}
		}
	}

	// --- CHARACTER CONVERTERS ---

	protected static final void append(StringBuilder builder, String key, String value) {

		// Convert key
		String formattedKey = key.replace('[', '.').replace("]", "");

		// Append key
		char c;
		boolean replace = false;
		char[] chars = formattedKey.toCharArray();
		for (int i = 0; i < chars.length; i++) {
			c = chars[i];
			if ((c > 0xFF) || KEY_ESCAPES[c] != 0) {
				replace = true;
				break;
			}
		}
		if (replace) {
			appendWithEscapes(builder, formattedKey, KEY_ESCAPES);
		} else {
			builder.append(formattedKey);
		}
		builder.append('=');

		// Append value
		if (value != null && !value.isEmpty()) {
			replace = false;
			chars = value.toCharArray();
			for (int i = 0; i < chars.length; i++) {
				c = chars[i];
				if ((c > 0xFF) || VALUE_ESCAPES[c] != 0) {
					replace = true;
					break;
				}
			}
			if (replace) {
				appendWithEscapes(builder, value, VALUE_ESCAPES);
			} else {
				builder.append(value);
			}
		}

		// Line break
		builder.append(LINE_SEPARATOR);
	}

	protected static final void appendWithEscapes(StringBuilder builder, String txt, int[] esc) {
		final int end = txt.length();
		int i = 0;
		do {
			char c = txt.charAt(i);
			int type = (c > 0xFF) ? UNICODE_ESCAPE : esc[c];
			if (type == 0) {
				builder.append(c);
				continue;
			}
			if (type == UNICODE_ESCAPE) {
				builder.append('\\');
				builder.append('u');
				builder.append(HEX[c >>> 12]);
				builder.append(HEX[(c >> 8) & 0xF]);
				builder.append(HEX[(c >> 4) & 0xF]);
				builder.append(HEX[c & 0xF]);
			} else {
				builder.append('\\');
				builder.append((char) type);
			}
		} while (++i < end);
	}

	// --- INIT ESCAPE ARRAYS ---

	static {
		int[] table = new int[256];
		for (int i = 0; i < 32; ++i) {
			table[i] = UNICODE_ESCAPE;
			table[128 + i] = UNICODE_ESCAPE;
		}
		table[0x7F] = UNICODE_ESCAPE;
		table['\t'] = 't';
		table['\r'] = 'r';
		table['\n'] = 'n';
		table['\\'] = '\\';
		VALUE_ESCAPES = table;

		table = Arrays.copyOf(VALUE_ESCAPES, 256);
		table['#'] = '#';
		table['!'] = '!';
		table['='] = '=';
		table[':'] = ':';
		table[' '] = ' ';
		KEY_ESCAPES = table;
	}

	// --- IMPLEMENTED PARSER METHOD ---

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Object parse(String source) throws Exception {

		// Parse source
		Tree node = new Tree();
		LinkedProperties properties = new LinkedProperties();
		properties.load(new StringReader(source));
		LinkedHashMap<Object, Object> map = properties.map;

		final HashMap<String, Boolean> zeroIndexes = new HashMap<>();
		final HashMap<String, Boolean> arrays = new HashMap<>();

		// Convert Map to Node
		final StringBuilder path = new StringBuilder();
		for (Map.Entry<Object, Object> entry : map.entrySet()) {
			String key = (String) entry.getKey();
			String value = (String) entry.getValue();

			final StringTokenizer st = new StringTokenizer(key, ". ");
			path.setLength(0);
			while (st.hasMoreTokens()) {
				String token = st.nextToken();

				String prefix = path.toString();
				Boolean array = arrays.get(prefix);

				boolean number;
				if (array != null && !array) {
					number = false;
				} else {
					number = isNumeric(token);
				}

				if (number && array == null) {

					// Analyze array
					array = true;
					final ArrayList<Integer> indexes = new ArrayList<>();
					for (Object o : map.keySet()) {
						String t = (String) o;
						if (t.startsWith(prefix)) {
							int l = prefix.length() + 1;
							int i = t.indexOf('.', l);
							if (i > -1) {
								t = t.substring(l, i);
							} else {
								t = t.substring(l);
							}
							if (isNumeric(t)) {
								int idx = Integer.parseInt(t);
								if (indexes.contains(idx) && i == -1) {

									// Double index!
									array = false;
									break;
								}
								indexes.add(idx);
							} else {

								// Not numeric!
								array = false;
								break;
							}
						}
					}

					// Check index sequence (1,2,3,etc.)
					Collections.sort(indexes);
					int prev = Integer.MIN_VALUE;
					for (int idx : indexes) {
						if (prev == Integer.MIN_VALUE) {

							// First element is not "0" or "1"
							if (idx != 0 && idx != 1) {
								array = false;
								break;
							}

						} else if (prev - idx > 1) {

							// Not sequential!
							array = false;
							break;
						}
						prev = idx;
					}

					arrays.put(prefix, array);
					if (array) {
						String test = prefix + ".0";
						boolean zeroIndex = false;
						for (Object o : map.keySet()) {
							if (((String) o).startsWith(test)) {
								zeroIndex = true;
								break;
							}
						}
						zeroIndexes.put(prefix, zeroIndex);
					} else {
						number = false;
					}
				}

				if (number) {
					path.append('[');
					int idx = Integer.parseInt(token);
					if (!zeroIndexes.get(prefix)) {
						idx--;
					}
					path.append(idx);
					path.append(']');
				} else {
					if (path.length() > 0) {
						path.append('.');
					}
					path.append(token);
				}

			}
			node.put(path.toString(), (String) value);
		}

		// Store first indexes in metadata block
		Object result = node.asObject();
		if (!zeroIndexes.isEmpty()) {
			Tree meta = node.getMeta();
			Tree indexMap = meta.putMap(FIRST_INDEX);
			for (Map.Entry<String, Boolean> entry : zeroIndexes.entrySet()) {
				indexMap.put(entry.getKey(), entry.getValue() ? 0 : 1);
			}

			// Pushback metadata into the value map
			if (result instanceof Map) {
				((Map) result).put(Config.META, meta.asObject());
			}
		}
		return result;
	}

	protected static final boolean isNumeric(String txt) {
		if (txt.isEmpty()) {
			return false;
		}
		for (char c : txt.toCharArray()) {
			if (c < '0' || c > '9') {
				return false;
			}
		}
		return true;
	}

	protected static final class LinkedProperties extends Properties {

		private static final long serialVersionUID = 1L;

		protected LinkedHashMap<Object, Object> map = new LinkedHashMap<>(64);

		@Override
		public Object put(Object key, Object value) {
			return map.put(key, value);
		}

	}

}
