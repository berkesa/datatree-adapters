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
package org.datatree.dom.adapters;

import java.lang.reflect.Type;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.function.Function;

import org.datatree.dom.BASE64;
import org.datatree.dom.Config;
import org.datatree.dom.Priority;
import org.datatree.dom.builtin.AbstractTextAdapter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

/**
 * <b>GSON JSON ADAPTER</b><br>
 * <br>
 * Description: Gson is a Java library that can be used to convert Java
 * Objects into their JSON representation. It can also be used to convert a
 * JSON string to an equivalent Java object. Gson can work with arbitrary
 * Java objects including pre-existing objects that you do not have
 * source-code of.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.google.code.gson/gson<br>
 * compile group: 'com.google.code.gson', name: 'gson', version: '2.8.0'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * -Ddatatree.json.reader=org.datatree.dom.adapters.JsonGson<br>
 * -Ddatatree.json.writer=org.datatree.dom.adapters.JsonGson<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonGson jsonGson = new JsonGson();<br>
 * TreeReaderRegistry.setReader("json", jsonGson);<br>
 * TreeWriterRegistry.setWriter("json", jsonGson);<br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(110)
public class JsonGson extends AbstractTextAdapter {

	// --- OBJECT MAPPER INSTANCES ---

	public Gson mapper = create(false);
	public Gson prettyMapper = create(true);

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			if (pretty) {
				return prettyMapper.toJson(input);
			}
			return mapper.toJson(input);
		});
	}
	
	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		char c = source.charAt(0);
		if (c == '{') {
			return mapper.fromJson(source, LinkedHashMap.class);
		}
		if (c == '[') {
			return mapper.fromJson(source, LinkedList.class);
		}
		throw new IllegalArgumentException("Malformed JSON: " + source);
	}
	
	// --- FACTORY ---
	
	public static final Gson create(boolean pretty) {
		GsonBuilder builder = new GsonBuilder();
		builder.serializeNulls();
		builder.disableHtmlEscaping();

		// Install MongoDB / BSON serializers
		tryToAddSerializers("org.datatree.dom.adapters.JsonGsonBsonSerializers", builder);

		// Install serializers for Apache Cassandra
		addSerializer(builder, InetAddress.class, (value) -> {
			return new JsonPrimitive(value.getCanonicalHostName());
		});
		addSerializer(builder, Inet4Address.class, (value) -> {
			return new JsonPrimitive(value.getCanonicalHostName());
		});
		addSerializer(builder, Inet6Address.class, (value) -> {
			return new JsonPrimitive(value.getCanonicalHostName());
		});

		// Date serializer
		if (Config.USE_TIMESTAMPS) {
			builder.setDateFormat(Config.TIMESTAMP_FORMAT);
		} else {

			// Milliseconds since epoch Jan 1 , 1970 00:00:00 UTC
			addSerializer(builder, Date.class, (value) -> {
				return new JsonPrimitive(value.getTime());
			});
		}

		// BASE64 serializer
		addSerializer(builder, byte[].class, (value) -> {
			return new JsonPrimitive(BASE64.encode(value));
		});

		// Pretty printing
		if (pretty) {
			builder.setPrettyPrinting();
		}
		return builder.create();
	}

	// --- ADD CUSTOM SERIALIZER ---
	
	public static final <T> void addSerializer(GsonBuilder builder, Class<T> type, Function<T, JsonElement> function) {
		builder.registerTypeAdapter(type, new JsonSerializer<T>() {

			@Override
			public JsonElement serialize(T value, Type typeOfSrc, JsonSerializationContext context) {
				return function.apply(value);
			}
			
		});
	}
	
}