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
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.datatree.dom.Priority;

/**
 * <b>JACKSON JSON ADAPTER</b><br>
 * <br>
 * Description: Standard JSON library for Java (or JVM platform in general), or,
 * as the "best JSON parser for Java".<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.fasterxml.jackson.core/jackson-
 * databind<br>
 * compile group: 'com.fasterxml.jackson.core', name: 'jackson-databind',
 * version: '2.9.5'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one JSON implementation (Boon, Bson, Gson, etc.) on
 * classpath, the preferred implementation is adjustable with the following
 * System Properties. If there is only one (eg. only the "jackson-databind")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this JSON API automatically.<br>
 * <br>
 * -Ddatatree.json.reader=io.datatree.dom.adapters.JsonJackson<br>
 * -Ddatatree.json.writer=io.datatree.dom.adapters.JsonJackson<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonJackson jsonJackson = new JsonJackson();<br>
 * TreeReaderRegistry.setReader("json", jsonJackson);<br>
 * TreeWriterRegistry.setWriter("json", jsonJackson);<br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "JsonJackson");<br>
 * String outputString = node.toString("JsonJackson");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(180)
public class JsonJackson extends AbstractJacksonTextAdapter {

	// --- CONSTRUCTOR ---

	public JsonJackson() {
		super(new ObjectMapper(new MappingJsonFactory()));

		// Install MongoDB / BSON serializers
		tryToAddSerializers("io.datatree.dom.adapters.JsonJacksonBsonSerializers", mapper, prettyMapper);
	}

	// --- IMPLEMENTED PARSER METHODS ---

	public Object parse(byte[] source) throws Exception {
		return parse(new String(source, StandardCharsets.UTF_8));
	}

	@Override
	public Object parse(String source) throws Exception {
		char c = source.charAt(0);
		if (c == '{') {
			return mapper.readValue(source, LinkedHashMap.class);
		}
		if (c == '[') {
			return mapper.readValue(source, LinkedList.class);
		}
		throw new IllegalArgumentException("Malformed JSON: " + source);
	}

	// --- ADD CUSTOM SERIALIZER ---

	public static final <T> void addSerializer(SimpleModule module, Class<T> type,
			CheckedBiConsumer<T, JsonGenerator> consumer) {
		module.addSerializer(type, new JsonSerializer<T>() {

			@Override
			public final void serialize(T value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
				consumer.accept(value, gen);
			}

		});
	}

}