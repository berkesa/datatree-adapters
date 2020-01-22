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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.dslplatform.json.DslJson;
import com.dslplatform.json.DslJson.Settings;
import com.dslplatform.json.JsonReader;
import com.dslplatform.json.JsonWriter;
import com.dslplatform.json.JsonWriter.WriteObject;

import io.datatree.dom.Config;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;
import io.datatree.dom.builtin.JsonBuiltin;
import io.datatree.dom.converters.DataConverterRegistry;

import com.dslplatform.json.NumberConverter;
import com.dslplatform.json.ObjectConverter;
import com.dslplatform.json.StringConverter;

/**
 * <b>DSLJSON JSON ADAPTER</b><br>
 * <br>
 * Description: DSL Platform compatible JSON library (https://dsl-platform.com).
 * <br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.dslplatform/dsl-json<br>
 * compile group: 'com.dslplatform', name: 'dsl-json', version: '1.9.5'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one JSON implementation (Jackson, Bson, Gson, etc.) on
 * classpath, the preferred implementation is adjustable with the following
 * System Properties. If there is only one (eg. only the "dsl-json")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this JSON API automatically.<br>
 * <br>
 * -Ddatatree.json.reader=io.datatree.dom.adapters.JsonDSL<br>
 * -Ddatatree.json.writer=io.datatree.dom.adapters.JsonDSL<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonDSL jsonDSL = new JsonDSL();<br>
 * TreeReaderRegistry.setReader("json", jsonDSL);<br>
 * TreeWriterRegistry.setWriter("json", jsonDSL);<br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "JsonDSL");<br>
 * String outputString = node.toString("JsonDSL");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(100)
public class JsonDSL extends AbstractTextAdapter {

	// --- OBJECT MAPPER INSTANCE ---

	public DslJson<Object> mapper = create();

	// --- WRITER CACHE ---

	public Queue<JsonWriter> writers = new ConcurrentLinkedQueue<>();

	// --- IMPLEMENTED WRITER METHOD ---

	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			JsonWriter writer = writers.poll();
			if (writer == null) {
				writer = mapper.newWriter();
			} else {
				writer.reset();
			}
			mapper.serialize(writer, input);
			final String json = writer.toString();
			if (writers.size() <= Config.POOL_SIZE) {
				writers.add(writer);
			}
			if (pretty) {
				return JsonBuiltin.format(json);
			}
			return json;
		});
	}

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		byte[] bytes = source.getBytes(StandardCharsets.UTF_8);
		if (bytes[0] == '{') {
			return mapper.deserialize(LinkedHashMap.class, bytes, bytes.length);
		}
		if (bytes[0] == '[') {
			JsonReader<Object> reader = mapper.newReader(bytes, bytes.length);
			reader.getNextToken();
			return ObjectConverter.deserializeList(reader);
		}
		throw new IllegalArgumentException("Malformed JSON: " + source);
	}

	// --- FACTORY ---

	@SuppressWarnings("unchecked")
	public static final DslJson<Object> create() {
		DslJson<Object> mapper = new DslJson<Object>(new Settings<Object>());

		// Install BSON serializers
		tryToAddSerializers("io.datatree.dom.adapters.JsonDSLBsonSerializers", mapper);

		mapper.registerWriter(Byte.class, (writer, value) -> {
			NumberConverter.serializeNullable(value == null ? null : value.intValue(), writer);
		});

		mapper.registerWriter(Short.class, (writer, value) -> {
			NumberConverter.serializeNullable(value == null ? null : value.intValue(), writer);
		});

		mapper.registerWriter(BigInteger.class, (writer, value) -> {
			StringConverter.serializeNullable(DataConverterRegistry.convert(String.class, value), writer);
		});

		mapper.registerWriter(BigDecimal.class, (writer, value) -> {
			StringConverter.serializeNullable(DataConverterRegistry.convert(String.class, value), writer);
		});

		mapper.registerWriter(Date.class, (writer, value) -> {
			if (Config.USE_TIMESTAMPS) {
				StringConverter.serializeNullable(DataConverterRegistry.convert(String.class, value), writer);
			} else {
				NumberConverter.serialize(value.getTime(), writer);
			}
		});

		mapper.registerWriter(Collection.class, (writer, value) -> {
			Collection<Object> collection = (Collection<Object>) value;
			if (collection == null) {
				writer.writeNull();
				return;
			}
			writer.writeByte(JsonWriter.ARRAY_START);
			if (!collection.isEmpty()) {
				final Iterator<Object> it = collection.iterator();
				Object item = it.next();
				WriteObject<Object> elementWriter;
				if (item == null) {
					writer.writeNull();
				} else {
					elementWriter = (WriteObject<Object>) mapper.tryFindWriter(item.getClass());
					elementWriter.write(writer, item);
				}
				while (it.hasNext()) {
					writer.writeByte(JsonWriter.COMMA);
					item = it.next();
					if (item == null) {
						writer.writeNull();
					} else {
						elementWriter = (WriteObject<Object>) mapper.tryFindWriter(item.getClass());
						elementWriter.write(writer, item);
					}
				}
			}
			writer.writeByte(JsonWriter.ARRAY_END);
		});

		mapper.registerWriter(Object[].class, (writer, value) -> {
			Object[] array = (Object[]) value;
			if (array == null) {
				writer.writeNull();
				return;
			}
			writer.writeByte(JsonWriter.ARRAY_START);
			if (array.length > 0) {
				WriteObject<Object> elementWriter;
				boolean first = true;
				for (Object item : array) {
					if (first) {
						first = false;
					} else {
						writer.writeByte(JsonWriter.COMMA);
					}
					if (item == null) {
						writer.writeNull();
					} else {
						elementWriter = (WriteObject<Object>) mapper.tryFindWriter(item.getClass());
						elementWriter.write(writer, item);
					}
				}
			}
			writer.writeByte(JsonWriter.ARRAY_END);
		});

		return mapper;
	}

}