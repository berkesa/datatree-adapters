/**
 * This software is licensed under the Apache 2 license, quoted below.<br>
 * <br>
 * Copyright 2020 Andras Berkes [andras.berkes@programmer.net]<br>
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

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.UUID;

import com.jsoniter.JsonIterator;
import com.jsoniter.extra.PreciseFloatSupport;
import com.jsoniter.output.EncodingMode;
import com.jsoniter.output.JsonStream;
import com.jsoniter.spi.DecodingMode;
import com.jsoniter.spi.JsoniterSpi;

import io.datatree.dom.Config;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;
import io.datatree.dom.builtin.JsonBuiltin;
import io.datatree.dom.converters.DataConverterRegistry;

/**
 * <b>JSON ITERATOR ADAPTER</b><br>
 * <br>
 * Description: jsoniter (json-iterator) is fast and flexible JSON parser available in Java and Go.<br>
 * <br>
 * <b>Dependencies:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.jsoniter/jsoniter<br>
 * compile group: 'com.jsoniter', name: 'jsoniter', version: '0.9.23'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * -Ddatatree.json.reader=io.datatree.dom.adapters.JsonJsoniter<br>
 * -Ddatatree.json.writer=io.datatree.dom.adapters.JsonJsoniter<br>
 * <br>
 * If there is more than one JSON implementation (Jackson, Bson, Gson, etc.) on
 * classpath, the preferred implementation is adjustable with the following
 * System Properties. If there is only one (eg. only the "Jsoniter")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this JSON API automatically.<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonJsoniter jsonJsoniter = new JsonJsoniter();<br>
 * TreeReaderRegistry.setReader("json", jsonJsoniter);<br>
 * TreeWriterRegistry.setWriter("json", jsonJsoniter);<br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "JsonJsoniter");<br>
 * String outputString = node.toString("JsonJsoniter");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(75)
public class JsonJsoniter extends AbstractTextAdapter {

	// --- STATIC CONSTRUCTOR ---

	static {
		JsonIterator.setMode(DecodingMode.DYNAMIC_MODE_AND_MATCH_FIELD_STRICTLY);
		JsonStream.setMode(EncodingMode.DYNAMIC_MODE);

		// Standard types
		addSerializer(UUID.class, (value, stream) -> {
			stream.writeVal(value.toString());
		});
		addSerializer(InetAddress.class, (value, stream) -> {
			stream.writeVal(value.getCanonicalHostName());
		});
		addSerializer(Inet4Address.class, (value, stream) -> {
			stream.writeVal(value.getCanonicalHostName());
		});
		addSerializer(Inet6Address.class, (value, stream) -> {
			stream.writeVal(value.getCanonicalHostName());
		});
		addSerializer(Date.class, (value, stream) -> {
			if (Config.USE_TIMESTAMPS) {
				stream.writeVal(DataConverterRegistry.convert(String.class, value));
			} else {
				stream.writeVal(value.getTime());
			}
		});
		
		PreciseFloatSupport.enable();
		
		// Install MongoDB / BSON serializers
		tryToAddSerializers("io.datatree.dom.adapters.JsonJsoniterBsonSerializers", new Object());
	}

	// --- PARSE BYTE ARRAY ---

	@Override
	public Object parse(byte[] source) throws Exception {
		byte b = source[0];
		if (b == '{') {
			return JsonIterator.deserialize(source, LinkedHashMap.class);
		}
		if (b == '[') {
			return JsonIterator.deserialize(source, LinkedList.class);
		}
		throw new IllegalArgumentException("Malformed JSON: " + source);
	}

	// --- PARSE STRING ---

	@Override
	public Object parse(String source) throws Exception {
		return parse(source.getBytes(StandardCharsets.UTF_8));
	}

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			String json = JsonStream.serialize(input);
			if (pretty) {
				return JsonBuiltin.format(json);
			}
			return json;
		});
	}

	// --- ADD CUSTOM SERIALIZER ---

	@SuppressWarnings("unchecked")
	public static final <T> void addSerializer(Class<T> type, CheckedBiConsumer<T, JsonStream> consumer) {
		JsoniterSpi.registerTypeEncoder(type, (obj, stream) -> {
			consumer.accept((T) obj, stream);
		});
	}
	
}