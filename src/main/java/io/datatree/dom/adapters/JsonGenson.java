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

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.LinkedList;

import com.owlike.genson.Context;
import com.owlike.genson.Converter;
import com.owlike.genson.Genson;
import com.owlike.genson.GensonBuilder;
import com.owlike.genson.stream.ObjectReader;

import io.datatree.dom.Config;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;

/**
 * <b>GENSON JSON ADAPTER</b><br>
 * <br>
 * Description: Genson API is designed to be easy to use, it handles for you
 * all the databinding, streaming and much more.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.owlike/genson<br>
 * compile group: 'com.owlike', name: 'genson', version: '1.5'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one JSON implementation (Jackson, Bson, Gson, etc.) on
 * classpath, the preferred implementation is adjustable with the following
 * System Properties. If there is only one (eg. only the "genson") implementation
 * on the classpath, this step is NOT necessary, the DataTree API will use this
 * JSON API automatically.<br>
 * <br>
 * -Ddatatree.json.reader=io.datatree.dom.adapters.JsonGenson<br>
 * -Ddatatree.json.writer=io.datatree.dom.adapters.JsonGenson<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonGenson jsonGenson = new JsonGenson();<br>
 * TreeReaderRegistry.setReader("json", jsonGenson);<br>
 * TreeWriterRegistry.setWriter("json", jsonGenson);<br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "JsonGenson");<br>
 * String outputString = node.toString("JsonGenson");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(120)
public class JsonGenson extends AbstractTextAdapter {

	// --- OBJECT MAPPER INSTANCES ---

	public Genson mapper = create(false);
	public Genson prettyMapper = create(true);

	// --- IMPLEMENTED WRITER METHODS ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			if (pretty) {
				return prettyMapper.serialize(input);
			}
			return mapper.serialize(input);
		});
	}

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		char c = source.charAt(0);
		if (c == '{') {
			return mapper.deserialize(source, LinkedHashMap.class);
		}
		if (c == '[') {
			return mapper.deserialize(source, LinkedList.class);
		}
		throw new IllegalArgumentException("Malformed JSON: " + source);
	}

	// --- FACTORY ---

	public static final Genson create(boolean pretty) {
		GensonBuilder builder = new GensonBuilder();

		// Install MongoDB / BSON serializers
		tryToAddSerializers("io.datatree.dom.adapters.JsonGensonBsonSerializers", builder);

		// Install serializers for Apache Cassandra
		addSerializer(builder, InetAddress.class, (value, writer, ctx) -> {
			writer.writeString(value.getCanonicalHostName());
		});
		addSerializer(builder, Inet4Address.class, (value, writer, ctx) -> {
			writer.writeString(value.getCanonicalHostName());
		});
		addSerializer(builder, Inet6Address.class, (value, writer, ctx) -> {
			writer.writeString(value.getCanonicalHostName());
		});

		// Set Date format
		builder.useDateAsTimestamp(!Config.USE_TIMESTAMPS);
		if (Config.USE_TIMESTAMPS) {
			builder.useDateFormat(new SimpleDateFormat(Config.TIMESTAMP_FORMAT));
		}
		builder.useIndentation(pretty);
		return builder.create();
	}

	// --- ADD CUSTOM SERIALIZER ---

	public static final <T> void addSerializer(GensonBuilder builder, Class<T> type, OneWayConverter<T> converter) {
		builder.withConverter(converter, type);
	}

	@FunctionalInterface
	public interface OneWayConverter<T> extends Converter<T> {

		@Override
		public default T deserialize(ObjectReader reader, Context ctx) throws Exception {
			return null;
		}

	}

}