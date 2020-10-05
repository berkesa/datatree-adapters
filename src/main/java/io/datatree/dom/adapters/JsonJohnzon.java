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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonStructure;

import org.apache.johnzon.mapper.Adapter;
import org.apache.johnzon.mapper.Mapper;
import org.apache.johnzon.mapper.MapperBuilder;

import io.datatree.dom.Config;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;
import io.datatree.dom.converters.DataConverterRegistry;

/**
 * <b>APACHE JOHNZON JSON ADAPTER</b><br>
 * <br>
 * Description: Apache Johnzon is a project providing an implementation of
 * JsonProcessing (aka jsr-353) and a set of useful extension for this
 * specification like an Object mapper, some JAX-RS providers and a
 * WebSocket module provides a basic integration with Java WebSocket API.<br>
 * <br>
 * <b>Dependencies:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/org.apache.johnzon/johnzon-mapper
 * <br>
 * compile group: 'org.apache.johnzon', name: 'johnzon-mapper', version:
 * '1.2.8'<br>
 * <br>
 * https://mvnrepository.com/artifact/javax.json/javax.json-api<br>
 * compile group: 'javax.json', name: 'javax.json-api', version: '1.1.4'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one JSON implementation (Jackson, Bson, Gson, etc.) on
 * classpath, the preferred implementation is adjustable with the following
 * System Properties. If there is only one (eg. only the "johnzon-mapper")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this JSON API automatically.<br>
 * <br>
 * -Ddatatree.json.reader=io.datatree.dom.adapters.JsonJohnzon<br>
 * -Ddatatree.json.writer=io.datatree.dom.adapters.JsonJohnzon<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonJohnzon jsonJohnzon = new JsonJohnzon();<br>
 * TreeReaderRegistry.setReader("json", jsonJohnzon);<br>
 * TreeWriterRegistry.setWriter("json", jsonJohnzon);<br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "JsonJohnzon");<br>
 * String outputString = node.toString("JsonJohnzon");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(90)
public class JsonJohnzon extends AbstractTextAdapter {

	// --- OBJECT MAPPER INSTANCES ---

	public Mapper mapper = create(false);
	public Mapper prettyMapper = create(true);

	// --- WRITER CACHE ---

	public Queue<StringWriter> writers = new ConcurrentLinkedQueue<>();

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			StringWriter writer = writers.poll();
			if (writer == null) {
				writer = new StringWriter(512);
			} else {
				writer.getBuffer().setLength(0);
			}
			if (pretty) {
				writeToMapper(prettyMapper, input, writer);
			} else {
				writeToMapper(mapper, input, writer);
			}
			final String json = writer.toString();
			if (writers.size() <= Config.POOL_SIZE) {
				writers.add(writer);
			}
			return json;
		});
	}

	protected static final void writeToMapper(Mapper target, Object value, StringWriter writer) {
		if (value instanceof Collection) {
			target.writeArray((Collection<?>) value, writer);
		} else if (value.getClass().isArray()) {
			int len = Array.getLength(value);
			LinkedList<Object> list = new LinkedList<>();
			for (int i = 0; i < len; i++) {
				list.addLast(Array.get(value, i));
			}
			target.writeArray(list, writer);
		} else {
			target.writeObject(value, writer);
		}
	}

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		char c = source.charAt(0);
		if (c == '{') {
			return mapper.readObject(new StringReader(source), LinkedHashMap.class);
		}
		if (c == '[') {
			Map<String, Object> map = mapper.readObject(new StringReader("{\"a\":" + source + "}"), Map.class);
			return map.get("a");
		}
		throw new IllegalArgumentException("Malformed JSON: " + source);
	}

	// --- FACTORY ---

	public static final Mapper create(boolean pretty) {
		MapperBuilder builder = new MapperBuilder();

		// Install MongoDB / BSON serializers
		tryToAddSerializers("io.datatree.dom.adapters.JsonJohnzonBsonSerializers", builder);

		// Install serializers for Apache Cassandra
		builder.setTreatByteArrayAsBase64(true);
		builder.setSkipNull(false);
		builder.setSupportsComments(true);

		// InetAddress
		addSerializer(builder, InetAddress.class, String.class, (value) -> {
			return value.getCanonicalHostName();
		});
		addSerializer(builder, Inet4Address.class, String.class, (value) -> {
			return value.getCanonicalHostName();
		});
		addSerializer(builder, Inet6Address.class, String.class, (value) -> {
			return value.getCanonicalHostName();
		});

		// UUID
		addSerializer(builder, UUID.class, String.class, (value) -> {
			return value.toString();
		});

		// Date format
		if (Config.USE_TIMESTAMPS) {
			addSerializer(builder, Date.class, String.class, (value) -> {
				return DataConverterRegistry.convert(String.class, new Date(value.getTime()));
			});
		} else {
			addSerializer(builder, Date.class, Long.class, (value) -> {
				return value.getTime();
			});
		}

		// Pretty printing
		builder.setPretty(pretty);

		builder.setReaderFactory(new JsonReaderFactory() {

			@Override
			public final Map<String, ?> getConfigInUse() {
				return Collections.emptyMap();
			}

			@Override
			public final JsonReader createReader(InputStream in, Charset charset) {
				return Json.createReader(new InputStreamReader(in, charset));
			}

			@Override
			public final JsonReader createReader(InputStream in) {
				return Json.createReader(in);
			}

			@Override
			public final JsonReader createReader(Reader reader) {
				JsonReader jsonReader = Json.createReader(reader);
				return new JsonReader() {

					@Override
					public final JsonObject readValue() {
						return jsonReader.readObject();
					}

					@Override
					public final JsonObject readObject() {
						return jsonReader.readObject();
					}

					@Override
					public final JsonArray readArray() {
						return jsonReader.readArray();
					}

					@Override
					public final JsonStructure read() {
						return jsonReader.read();
					}

					@Override
					public final void close() {
						jsonReader.close();
					}

				};
			}

		});

		return builder.build();
	}

	// --- ADD CUSTOM SERIALIZER ---

	public static final <FROM, TO> void addSerializer(MapperBuilder builder, Class<FROM> from, Class<TO> to,
			Function<FROM, TO> function) {
		builder.addAdapter(from, String.class, new Adapter<FROM, TO>() {

			@Override
			public FROM to(TO to) {
				return null;
			}

			@Override
			public TO from(FROM a) {
				return function.apply(a);
			}

		});
	}

}