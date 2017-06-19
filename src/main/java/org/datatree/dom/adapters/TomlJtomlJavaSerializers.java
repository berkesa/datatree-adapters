/**
 * This software is licensed under the Apache 2 license, quoted below.<br>
 * <br>
 * Copyright 2017 Andras Berkes [andras.berkes@europe.com]<br>
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
package org.datatree.dom.adapters;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

import org.datatree.dom.BASE64;
import org.datatree.dom.Config;
import org.datatree.dom.converters.DataConverterRegistry;

import me.grison.jtoml.TomlSerializer;
import me.grison.jtoml.Util;

/**
 * <b>JTOML JAVA EXTENSIONS</b><br>
 * <br>
 * JTOML Java serializers.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class TomlJtomlJavaSerializers implements TomlSerializer {

	// Converters
	public Map<Class<?>, Function<Object, String>> converters = new HashMap<>();

	public TomlJtomlJavaSerializers() {

		// Numbers
		converters.put(Byte.class, (Object o) -> {
			return Byte.toString((Byte) o);
		});
		converters.put(Float.class, (Object o) -> {
			return Float.toString((Float) o);
		});
		converters.put(Short.class, (Object o) -> {
			return Short.toString((Short) o);
		});
		converters.put(BigDecimal.class, (Object o) -> {
			return ((BigDecimal) o).toPlainString();
		});
		converters.put(BigInteger.class, (Object o) -> {
			return ((BigInteger) o).toString();
		});
		
		// String
		converters.put(String.class, (Object o) -> {
			return quotedString((String) o);
		});

		// Calendar
		converters.put(Calendar.class, (Object o) -> {
			if (Config.USE_TIMESTAMPS) {
				return quotedString(DataConverterRegistry.convert(String.class, ((Calendar) o).getTime()));
			}
			return Long.toString(((Calendar) o).getTime().getTime());
		});

		// Date
		converters.put(Date.class, (Object o) -> {
			if (Config.USE_TIMESTAMPS) {
				return quotedString(DataConverterRegistry.convert(String.class, o));
			}
			return Long.toString(((Date) o).getTime());
		});

		// BASE64
		converters.put(byte[].class, (Object o) -> {
			return quotedString(BASE64.encode((byte[]) o));
		});

		// UUID
		converters.put(UUID.class, (Object o) -> {
			return quotedString(((UUID) o).toString());
		});

		// InetAddress
		converters.put(InetAddress.class, (Object o) -> {
			return quotedString(((InetAddress) o).getCanonicalHostName());
		});
		converters.put(Inet4Address.class, (Object o) -> {
			return quotedString(((Inet4Address) o).getCanonicalHostName());
		});
		converters.put(Inet6Address.class, (Object o) -> {
			return quotedString(((Inet6Address) o).getCanonicalHostName());
		});

	}
	
	public static final String quotedString(String txt) {
		return '\"' + Util.TomlString.escape(txt) + '\"';
	}

	@Override
	public String serialize(Object object) {
		return serialize(null, object);
	}

	@Override
	public String serialize(String rootKey, Object object) {
		StringBuilder buffer = new StringBuilder(512);
		serialize(buffer, null, object);
		return buffer.toString();
	}
	
	public void serializeCollection(StringBuilder buffer, Collection<?> list) {
		buffer.append('[');
		boolean first = true;
		for (Object item : (Collection<?>) list) {
			if (item == null) {
				if (first) {
					first = false;					
				} else {
					buffer.append(", ");	
				}
				buffer.append("null");
			} else if (item instanceof Collection) {
				if (first) {
					first = false;					
				} else {
					buffer.append(", ");	
				}
				serializeCollection(buffer, (Collection<?>) item);
			} else if (Util.Reflection.isTomlSupportedType(item.getClass())) {
				if (first) {
					first = false;					
				} else {
					buffer.append(", ");	
				}
				Function<Object, String> f = converters.get(item.getClass());
				if (f == null) {
					buffer.append(item);
				} else {
					buffer.append(f.apply(item));
				}
			}
		}
		buffer.append(']');
	}

	@SuppressWarnings("unchecked")
	public void serializeMap(StringBuilder buffer, String rootKey, Map<String, Object> map) {
		boolean writeRootKey = true;
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String name = entry.getKey();
			String nextRootKey = rootKey == null ? name : rootKey + "." + name;
			Object value = entry.getValue();
			if (value != null && value instanceof Map) {
				continue;
			}
			if (writeRootKey) {
				writeRootKey = false;
				if (rootKey != null) {
					if (buffer.length() > 0) {
						buffer.append('\n');
					}
					buffer.append('[').append(rootKey).append("]\n");
				}				
			}
			if (value == null) {
				buffer.append(name).append(" = null\n");
				continue;
			}
			Class<?> type = value.getClass();
			if (type.isArray() && type != byte[].class) {
				int size = Array.getLength(value);
				ArrayList<Object> array = new ArrayList<>(size);
				for (int i = 0; i < size; i++) {
					array.add(Array.get(value, i));
				}
				value = array;
			}
			if (value instanceof Collection) {
				buffer.append(name).append(" = ");
				serializeCollection(buffer, (Collection<?>) value);
				buffer.append('\n');
			} else if (converters.containsKey(type)) {
				buffer.append(name).append(" = ");
				buffer.append(converters.get(type).apply(value));
				buffer.append('\n');
			} else if (Util.Reflection.isTomlSupportedTypeExceptMap(type)) {
				if (converters.containsKey(type)) {
					value = converters.get(type).apply(value);
				}
				buffer.append(name).append(" = ").append(value).append('\n');
			} else {
				buffer.append('\n');
				serialize(buffer, nextRootKey, value);
			}
		}
		for (Map.Entry<String, Object> entry : map.entrySet()) {
			String name = entry.getKey();
			String nextRootKey = rootKey == null ? name : rootKey + "." + name;
			Object value = entry.getValue();
			if (value == null) {
				continue;
			}
			if (value instanceof Map) {
				serializeMap(buffer, nextRootKey, (Map<String, Object>) value);
			}
		}
	}

	@SuppressWarnings("unchecked")
	public void serialize(StringBuilder buffer, String rootKey, Object object) {
		try {
			if (object instanceof Map) {
				serializeMap(buffer, rootKey, (Map<String, Object>) object);
				return;
			}
			if (rootKey != null) {
				if (buffer.length() > 0) {
					buffer.append('\n');
				}
				buffer.append('[').append(rootKey).append("]\n");
			}
			final List<Field> fields = Arrays.asList(object.getClass().getDeclaredFields());
			Collections.sort(fields, Util.Reflection.newTomlFieldComparator(fields));
			for (Field f : fields) {
				Class<?> type = f.getType();
				Object value = Util.Reflection.getFieldValue(f, object);
				if (Collection.class.isAssignableFrom(type)) {
					buffer.append(f.getName()).append(" = ");
					serializeCollection(buffer, (Collection<?>) value);
					buffer.append('\n');
				} else if (converters.containsKey(type)) {
					buffer.append(f.getName()).append(" = ");
					buffer.append(converters.get(type).apply(value));
					buffer.append('\n');
				} else if (Util.Reflection.isTomlSupportedTypeExceptMap(type)) {
					if (converters.containsKey(type)) {
						value = converters.get(type).apply(value);
					}
					buffer.append(f.getName()).append(" = ").append(value).append('\n');
				} else {
					serialize(buffer, rootKey + "." + f.getName(), value);
				}
			}
		} catch (Throwable e) {
			throw new IllegalArgumentException("Could not serialize object with rootKey `" + rootKey + "`.", e);
		}
	}

}