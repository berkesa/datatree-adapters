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

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Date;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiConsumer;

import org.datatree.dom.BASE64;
import org.datatree.dom.Config;
import org.datatree.dom.Priority;
import org.datatree.dom.builtin.AbstractTextAdapter;
import org.datatree.dom.builtin.JsonBuiltin;
import org.datatree.dom.converters.DataConverterRegistry;

import jodd.json.JsonContext;
import jodd.json.JsonParser;
import jodd.json.JsonSerializer;
import jodd.json.TypeJsonSerializer;
import jodd.util.StringPool;

/**
 * <b>JODD JSON ADAPTER</b><br>
 * <br>
 * Description: Jodd Json is lightweight library for (de)serializing Java
 * objects into and from JSON.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/org.jodd/jodd-json<br>
 * compile group: 'org.jodd', name: 'jodd-json', version: '3.8.5'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * -Ddatatree.json.reader=org.datatree.dom.adapters.JsonJodd<br>
 * -Ddatatree.json.writer=org.datatree.dom.adapters.JsonJodd<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonJodd jsonJodd = new JsonJodd();<br>
 * TreeReaderRegistry.setReader("json", jsonJodd);<br>
 * TreeWriterRegistry.setWriter("json", jsonJodd);<br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(140)
public class JsonJodd extends AbstractTextAdapter {

	// --- OBJECT MAPPER INSTANCE ---

	public JsonSerializer mapper = new JsonSerializer().deep(true);

	// --- CONSTRUCTOR ---

	public JsonJodd() {

		// Install MongoDB / BSON serializers
		tryToAddSerializers("org.datatree.dom.adapters.JsonJoddBsonSerializers", mapper);

		// InetAddress
		addSerializer(mapper, InetAddress.class, (value, ctx) -> {
			ctx.writeString(value.getCanonicalHostName());
		});
		addSerializer(mapper, Inet4Address.class, (value, ctx) -> {
			ctx.writeString(value.getCanonicalHostName());
		});
		addSerializer(mapper, Inet6Address.class, (value, ctx) -> {
			ctx.writeString(value.getCanonicalHostName());
		});

		// UUID
		addSerializer(mapper, UUID.class, (value, ctx) -> {
			ctx.write(StringPool.QUOTE);
			ctx.write(value.toString());
			ctx.write(StringPool.QUOTE);
		});

		// Date
		if (Config.USE_TIMESTAMPS) {
			addSerializer(mapper, Date.class, (value, ctx) -> {
				ctx.write(StringPool.QUOTE);
				ctx.write(DataConverterRegistry.convert(String.class, value));
				ctx.write(StringPool.QUOTE);
			});
		} else {
			addSerializer(mapper, Date.class, (value, ctx) -> {
				ctx.writeNumber(value.getTime());
			});
		}

		// Byte array
		addSerializer(mapper, byte[].class, (value, ctx) -> {
			ctx.write(StringPool.QUOTE);
			ctx.write(BASE64.encode(value));
			ctx.write(StringPool.QUOTE);
		});
	}

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			String json = mapper.serialize(input);
			if (pretty) {
				return JsonBuiltin.format(json);
			}
			return json;
		});
	}

	// --- READER CACHE ---

	public Queue<JsonParser> parsers = new ConcurrentLinkedQueue<>();

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		JsonParser parser = parsers.poll();
		if (parser == null) {
			parser = new JsonParser().looseMode(true);
		}
		final Object result = parser.parse(source);
		if (parsers.size() > Config.POOL_SIZE) {
			return result;
		}
		parsers.add(parser);
		return result;
	}

	// --- ADD CUSTOM SERIALIZER ---

	public static final <T> void addSerializer(JsonSerializer mapper, Class<T> type,
			BiConsumer<T, JsonContext> consumer) {
		mapper.withSerializer(type, new TypeJsonSerializer<T>() {

			@Override
			public final boolean serialize(JsonContext ctx, T value) {
				consumer.accept(value, ctx);
				return true;
			}

		});
	}

}