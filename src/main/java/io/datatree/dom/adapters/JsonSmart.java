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

import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;

import io.datatree.dom.BASE64;
import io.datatree.dom.Config;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;
import io.datatree.dom.builtin.JsonBuiltin;
import io.datatree.dom.converters.DataConverterRegistry;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONAware;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONStyle;
import net.minidev.json.JSONValue;
import net.minidev.json.parser.JSONParser;
import net.minidev.json.reader.JsonWriter;
import net.minidev.json.reader.JsonWriterI;

/**
 * <b>JSON-SMART JSON ADAPTER</b><br>
 * <br>
 * Description: Json-smart is a performance focused, JSON processor lib. <br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/net.minidev/json-smart<br>
 * compile group: 'net.minidev', name: 'json-smart', version: '2.3'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one JSON implementation (Jackson, Bson, Gson, etc.) on
 * classpath, the preferred implementation is adjustable with the following
 * System Properties. If there is only one (eg. only the "json-smart")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this JSON API automatically.<br>
 * <br>
 * -Ddatatree.json.reader=io.datatree.dom.adapters.JsonSmart<br>
 * -Ddatatree.json.writer=io.datatree.dom.adapters.JsonSmart<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonSmart jsonSmart = new JsonSmart();<br>
 * TreeReaderRegistry.setReader("json", jsonSmart);<br>
 * TreeWriterRegistry.setWriter("json", jsonSmart);<br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "JsonSmart");<br>
 * String outputString = node.toString("JsonSmart");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(130)
public class JsonSmart extends AbstractTextAdapter {

	// --- PARSER CACHE ---

	public Queue<JSONParser> parsers = new ConcurrentLinkedQueue<>();

	// --- IMPLEMENTED WRITER METHOD ---

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			String json;
			if (input instanceof Map) {
				json = JSONObject.toJSONString((Map) input);
			} else if (input instanceof List) {
				json = JSONArray.toJSONString((List) input);
			} else if (input instanceof JSONAware) {
				json = ((JSONAware) input).toJSONString();
			} else if (input instanceof Set) {
				json = JSONArray.toJSONString(new ArrayList((Set) input));
			} else if (input.getClass().isArray()) {
				json = JSONArray.toJSONString(Arrays.asList(input));
			} else {
				throw new IllegalArgumentException("Unsupported data type (" + input + ")!");
			}
			if (pretty) {
				return JsonBuiltin.format(json);
			}
			return json;
		});
	}

	// --- REGISTER WRITERS ---

	static {

		// Install MongoDB / BSON serializers
		tryToAddSerializers("io.datatree.dom.adapters.JsonSmartBsonSerializers", JSONValue.defaultWriter);

		// BASE64
		addSerializer(JSONValue.defaultWriter, byte[].class, (value, out) -> {
			out.append('"');
			out.append(BASE64.encode(value));
			out.append('"');
		});

		// InetAddress
		addSerializer(JSONValue.defaultWriter, InetAddress.class, (value, out) -> {
			out.append('"');
			out.append(value.getCanonicalHostName());
			out.append('"');
		});
		addSerializer(JSONValue.defaultWriter, Inet4Address.class, (value, out) -> {
			out.append('"');
			out.append(value.getCanonicalHostName());
			out.append('"');
		});
		addSerializer(JSONValue.defaultWriter, Inet6Address.class, (value, out) -> {
			out.append('"');
			out.append(value.getCanonicalHostName());
			out.append('"');
		});

		// UUID
		addSerializer(JSONValue.defaultWriter, UUID.class, (value, out) -> {
			out.append('"');
			out.append(value.toString());
			out.append('"');
		});

		// Date
		addSerializer(JSONValue.defaultWriter, Date.class, (value, out) -> {
			if (Config.USE_TIMESTAMPS) {
				out.append('\"');
				out.append(DataConverterRegistry.convert(String.class, value));
				out.append('\"');
			} else {
				out.append(Long.toString(value.getTime()));
			}
		});
	}

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		JSONParser parser = parsers.poll();
		if (parser == null) {
			parser = new JSONParser(JSONParser.DEFAULT_PERMISSIVE_MODE);
		}
		final Object result = parser.parse(source);
		if (parsers.size() > Config.POOL_SIZE) {
			return result;
		}
		parsers.add(parser);
		return result;
	}

	// --- ADD CUSTOM SERIALIZER ---

	public static final <T> void addSerializer(JsonWriter writer, Class<T> type,
			CheckedBiConsumer<T, Appendable> consumer) {
		writer.registerWriter(new JsonWriterI<T>() {

			@Override
			public <E extends T> void writeJSONString(E value, Appendable out, JSONStyle compression)
					throws IOException {
				consumer.accept(value, out);
			}

		}, type);
	}

}
