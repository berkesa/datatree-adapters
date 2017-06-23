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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

import io.datatree.dom.Config;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;
import software.amazon.ion.IonReader;
import software.amazon.ion.IonSystem;
import software.amazon.ion.IonType;
import software.amazon.ion.IonWriter;
import software.amazon.ion.SymbolToken;
import software.amazon.ion.Timestamp;
import software.amazon.ion.system.IonSystemBuilder;
import software.amazon.ion.system.IonTextWriterBuilder;

/**
 * <b>AMAZON ION JSON ADAPTER</b><br>
 * <br>
 * Description: Amazon Ion is a richly-typed, self-describing, hierarchical data
 * serialization format offering interchangeable binary and text
 * representations. The text format (a superset of JSON) is easy to read and
 * author, supporting rapid prototyping. The rich type system provides
 * unambiguous semantics for long-term preservation of business data which can
 * survive multiple generations of software evolution. Ion was built to solve
 * the rapid development, decoupling, and efficiency challenges faced every day
 * while engineering large-scale, service-oriented architectures.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/software.amazon.ion/ion-java<br>
 * compile group: 'software.amazon.ion', name: 'ion-java', version: '1.0.2'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one JSON implementation (Jackson, Bson, Gson, etc.) on
 * classpath, the preferred implementation is adjustable with the following
 * System Properties. If there is only one (eg. only the "ion-java")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this JSON API automatically.<br>
 * <br>
 * -Ddatatree.ion.reader=io.datatree.dom.adapters.IonIon<br>
 * -Ddatatree.ion.writer=io.datatree.dom.adapters.IonIon<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonIon jsonIon = new JsonIon();<br>
 * TreeReaderRegistry.setReader("json", jsonIon);<br>
 * TreeWriterRegistry.setWriter("json", jsonIon);<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "JsonIon");<br>
 * String outputString = node.toString("JsonIon");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(80)
public class JsonIon extends AbstractTextAdapter {

	// --- CONSTRUCTOR ---

	public JsonIon() {

		// Install Java / Apache Cassandra serializers
		addDefaultSerializers();

		// Install MongoDB / BSON serializers
		tryToAddSerializers("io.datatree.dom.adapters.IonIonBsonSerializers", converters);
	}

	public void addDefaultSerializers() {

		// InetAddress
		addSerializer(converters, InetAddress.class, (value) -> {
			return value.getCanonicalHostName();
		});
		addSerializer(converters, Inet4Address.class, (value) -> {
			return value.getCanonicalHostName();
		});
		addSerializer(converters, Inet6Address.class, (value) -> {
			return value.getCanonicalHostName();
		});

		// UUID
		addSerializer(converters, UUID.class, (value) -> {
			return value.toString();
		});
	}

	// --- CONVERTERS ---

	public HashMap<Class<?>, Function<Object, Object>> converters = new HashMap<>();

	// --- WRITER FACTORY ---

	public CachedWriter createWriter(boolean pretty) throws IOException {
		CachedWriter writer = new CachedWriter();
		writer.buffer = new ByteArrayOutputStream(512);
		if (pretty) {
			writer.writer = IonTextWriterBuilder.minimal().withPrettyPrinting().build(writer.buffer);
		} else {
			writer.writer = IonTextWriterBuilder.minimal().build(writer.buffer);
		}
		return writer;
	}

	// --- ION WRITER CACHES ---

	public Queue<CachedWriter> writers = new ConcurrentLinkedQueue<>();
	public Queue<CachedWriter> prettyWriters = new ConcurrentLinkedQueue<>();

	public final class CachedWriter {
		public ByteArrayOutputStream buffer;
		public IonWriter writer;
	}

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {

			// Get ION writer from cache
			CachedWriter writer;
			if (pretty) {
				writer = prettyWriters.poll();
			} else {
				writer = writers.poll();
			}
			if (writer == null) {
				writer = createWriter(pretty);
			} else {
				writer.buffer.reset();
			}

			// Serialize data
			write(writer.writer, null, input);
			writer.writer.flush();
			String json = new String(writer.buffer.toByteArray(), StandardCharsets.UTF_8);
			if (writers.size() > Config.POOL_SIZE) {

				// Writer pool is full
				return json;
			}

			// Recycle ION writer instance
			if (pretty) {
				prettyWriters.add(writer);
			} else {
				writers.add(writer);
			}
			return json;
		});
	}

	@SuppressWarnings("unchecked")
	public void write(IonWriter writer, Object name, Object value) throws Exception {

		// Field name
		if (name != null) {
			writer.setFieldName(String.valueOf(name));
		}

		// Null value
		if (value == null) {
			writer.writeNull();
			return;
		}

		// Map
		if (value instanceof Map) {
			writer.stepIn(IonType.STRUCT);
			for (Map.Entry<Object, Object> entry : ((Map<Object, Object>) value).entrySet()) {
				write(writer, entry.getKey(), entry.getValue());
			}
			writer.stepOut();
			return;
		}

		// List or Set
		if (value instanceof Collection) {
			writer.stepIn(IonType.LIST);
			for (Object object : (Collection<Object>) value) {
				write(writer, null, object);
			}
			writer.stepOut();
			return;
		}

		// Convert value
		Function<Object, Object> converter = converters.get(value.getClass());
		if (converter != null) {
			value = converter.apply(value);
			if (value == null) {
				writer.writeNull();
				return;
			}
		}

		// Byte array
		if (value instanceof byte[]) {
			writer.writeBlob((byte[]) value);
			return;
		}

		// Object arrays
		if (value.getClass().isArray()) {
			writer.stepIn(IonType.LIST);
			int len = Array.getLength(value);
			for (int i = 0; i < len; i++) {
				write(writer, null, Array.get(value, i));
			}
			writer.stepOut();
			return;
		}

		// BOOL
		if (value instanceof Boolean) {
			writer.writeBool((Boolean) value);
			return;
		}

		// INT (BigInteger)
		if (value instanceof BigInteger) {
			writer.writeInt((BigInteger) value);
			return;
		}

		// Long
		if (value instanceof Long) {
			writer.writeInt(((Number) value).longValue());
			return;
		}

		// Integer
		if (value instanceof Integer || value instanceof Short || value instanceof Byte) {
			writer.writeInt(((Number) value).intValue());
			return;
		}

		// FLOAT (Double or Float)
		if (value instanceof Double || value instanceof Float) {
			writer.writeFloat(((Number) value).doubleValue());
			return;
		}

		// DECIMAL (BigDecimal)
		if (value instanceof BigDecimal) {
			writer.writeDecimal((BigDecimal) value);
			return;
		}

		// STRING
		if (value instanceof String) {
			writer.writeString((String) value);
			return;
		}

		// TIMESTAMP (Date)
		if (value instanceof Date) {
			writer.writeTimestamp(Timestamp.forDateZ((Date) value));
			return;
		}

		// Other (unknown) types
		writer.writeString(String.valueOf(value));
	}

	// --- COMMON PARSER INSTANCE ---

	public IonSystem parser = IonSystemBuilder.standard().build();

	// --- IMPLEMENTED PARSER METHODS ---

	@Override
	public Object parse(String source) throws Exception {
		return getSingletonItem(parse(null, null, parser.newReader(source)));
	}

	@Override
	public Object parse(byte[] source) throws Exception {
		return getSingletonItem(parse(null, null, parser.newReader(source)));
	}

	public Object getSingletonItem(Object result) {
		if (result != null && result instanceof List) {
			List<?> list = (List<?>) result;
			if (list.size() == 1) {
				return list.get(0);
			}
		}
		return result;
	}

	public Object parse(LinkedHashMap<String, Object> map, LinkedList<Object> list, IonReader reader) throws Exception {
		IonType type;
		String name;
		while ((type = reader.next()) != null) {

			// Field name
			name = reader.getFieldName();

			// Init container
			if (name == null) {
				if (list == null) {
					list = new LinkedList<>();
				}
			} else {
				if (map == null) {
					map = new LinkedHashMap<>();
				}
			}

			// Field value
			switch (type) {
			case NULL:

				// Null
				add(map, list, name, null);
				continue;

			case STRUCT:
			case DATAGRAM:

				// Map
				LinkedHashMap<String, Object> subMap = new LinkedHashMap<>();
				add(map, list, name, subMap);
				reader.stepIn();
				parse(subMap, null, reader);
				reader.stepOut();
				continue;

			case LIST:
			case SEXP:

				// List
				LinkedList<Object> subList = new LinkedList<>();
				add(map, list, name, subList);
				reader.stepIn();
				parse(null, subList, reader);
				reader.stepOut();
				continue;

			case BOOL:

				// Boolean
				add(map, list, name, reader.booleanValue());
				continue;

			case INT:

				// BigInteger (or Long or Integer)
				switch (reader.getIntegerSize()) {
				case INT:
					add(map, list, name, reader.intValue());
					continue;
				case LONG:
					add(map, list, name, reader.longValue());
					continue;
				default:
					add(map, list, name, reader.bigIntegerValue());
				}
				continue;

			case FLOAT:

				// Double
				add(map, list, name, reader.doubleValue());
				continue;

			case DECIMAL:

				// BigDecimal
				add(map, list, name, reader.bigDecimalValue());
				continue;

			case TIMESTAMP:

				// Date
				add(map, list, name, reader.dateValue());
				continue;

			case STRING:

				// String
				add(map, list, name, reader.stringValue());
				continue;

			case SYMBOL:

				// Symbol
				SymbolToken symbol = reader.symbolValue();
				subMap = new LinkedHashMap<>();
				subMap.put("text", symbol.getText());
				subMap.put("sid", symbol.getSid());
				add(map, list, name, subMap);
				continue;

			case BLOB:
			case CLOB:

				// Byte array
				add(map, list, name, reader.newBytes());
				continue;

			default:

				// Ignored
			}
		}
		if (map == null) {
			return list;
		}
		return map;
	}

	public void add(LinkedHashMap<String, Object> map, LinkedList<Object> list, String name, Object value) {
		if (name == null) {
			if (list != null) {
				list.add(value);
			}
		} else {
			if (map != null) {
				map.put(name, value);
			}
		}
	}

	// --- ADD CUSTOM SERIALIZER ---

	@SuppressWarnings("unchecked")
	public static final <T> void addSerializer(HashMap<Class<?>, Function<Object, Object>> converters, Class<T> type,
			Function<T, Object> function) {
		converters.put(type, (Function<Object, Object>) function);
	}

}
