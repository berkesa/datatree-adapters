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
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.boon.json.JsonFactory;
import org.boon.json.JsonParserFactory;
import org.boon.json.JsonSerializerFactory;
import org.boon.json.ObjectMapper;
import org.boon.json.implementation.JsonFastParser;
import org.boon.json.serializers.CustomObjectSerializer;
import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.primitive.CharBuf;

import io.datatree.dom.Config;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;
import io.datatree.dom.builtin.JsonBuiltin;

/**
 * <b>BOON JSON ADAPTER</b><br>
 * <br>
 * Description: Simple opinionated Java for the novice to expert level Java
 * Programmer. Low Ceremony. High Productivity. A real boon to Java to
 * developers!<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/io.fastjson/boon<br>
 * compile group: 'io.fastjson', name: 'boon', version: '0.34'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * -Ddatatree.json.reader=io.datatree.dom.adapters.JsonBoon<br>
 * -Ddatatree.json.writer=io.datatree.dom.adapters.JsonBoon<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonBoon jsonBoon = new JsonBoon();<br>
 * TreeReaderRegistry.setReader("json", jsonBoon);<br>
 * TreeWriterRegistry.setWriter("json", jsonBoon);<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(170)
public class JsonBoon extends AbstractTextAdapter {

	// --- OBJECT MAPPER INSTANCE ---

	public ObjectMapper mapper;

	// --- CONSTRUCTOR ---

	public JsonBoon() {
		JsonParserFactory jsonParserFactory = new JsonParserFactory();
		jsonParserFactory.lax();

		// Include nulls and empty fields
		JsonSerializerFactory serializerFactory = new JsonSerializerFactory();
		serializerFactory.setIncludeNulls(true);
		serializerFactory.setIncludeEmpty(true);

		// Install MongoDB / BSON serializers
		tryToAddSerializers("io.datatree.dom.adapters.JsonBoonBsonSerializers", serializerFactory);

		// Format dates
		serializerFactory.setJsonFormatForDates(Config.USE_TIMESTAMPS);

		mapper = JsonFactory.create(jsonParserFactory, serializerFactory);
	}

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			String json = mapper.toJson(input);
			if (pretty) {
				return JsonBuiltin.format(json);
			}
			return json;
		});
	}

	// --- READER CACHE ---

	public Queue<JsonFastParser> parsers = new ConcurrentLinkedQueue<>();

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		JsonFastParser parser = parsers.poll();
		if (parser == null) {
			parser = new JsonFastParser();
		}
		Object result = parser.parse(source);
		if (parsers.size() > Config.POOL_SIZE) {
			return result;
		}
		parsers.add(parser);
		return result;
	}

	// --- ADD CUSTOM SERIALIZER ---

	public static final <T> void addSerializer(JsonSerializerFactory factory, Class<T> type,
			CheckedBiConsumer<T, CharBuf> consumer) {
		factory.addTypeSerializer(type, new CustomObjectSerializer<T>() {

			@Override
			public Class<T> type() {
				return type;
			}

			@Override
			public void serializeObject(JsonSerializerInternal serializer, T instance, CharBuf builder) {
				try {
					consumer.accept(instance, builder);
				} catch (IOException cause) {
					throw new IllegalArgumentException(cause);
				}
			}

		});
	}

}