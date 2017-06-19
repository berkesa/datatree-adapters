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
import java.util.Date;
import java.util.UUID;

import flexjson.JSONContext;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.TransformerUtil;
import flexjson.transformer.AbstractTransformer;
import flexjson.transformer.TypeTransformerMap;
import io.datatree.dom.BASE64;
import io.datatree.dom.Config;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;
import io.datatree.dom.converters.DataConverterRegistry;

/**
 * <b>FLEXJSON JSON ADAPTER</b><br>
 * <br>
 * Description: Flexjson is a lightweight library for serializing and
 * deserializing Java objects into and from JSON.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/net.sf.flexjson/flexjson<br>
 * compile group: 'net.sf.flexjson', name: 'flexjson', version: '3.3'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * -Ddatatree.json.reader=io.datatree.dom.adapters.JsonFlex<br>
 * -Ddatatree.json.writer=io.datatree.dom.adapters.JsonFlex<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonFlex jsonFlex = new JsonFlex();<br>
 * TreeReaderRegistry.setReader("json", jsonFlex);<br>
 * TreeWriterRegistry.setWriter("json", jsonFlex);<br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(30)
public class JsonFlex extends AbstractTextAdapter {

	// --- OBJECT MAPPER INSTANCES ---

	public JSONSerializer normalMapper;
	public JSONSerializer prettyMapper;

	// --- CONSTRUCTOR ---

	public JsonFlex() {
		TypeTransformerMap map = new TypeTransformerMap(TransformerUtil.getDefaultTypeTransformers());

		// Install MongoDB / BSON serializers
		tryToAddSerializers("io.datatree.dom.adapters.JsonFlexBsonSerializers", map);

		// Install serializers for Apache Cassandra

		// InetAddress
		addSerializer(map, InetAddress.class, (value, ctx) -> {
			ctx.writeQuoted(value.getCanonicalHostName());
		});
		addSerializer(map, Inet4Address.class, (value, ctx) -> {
			ctx.writeQuoted(value.getCanonicalHostName());
		});
		addSerializer(map, Inet6Address.class, (value, ctx) -> {
			ctx.writeQuoted(value.getCanonicalHostName());
		});

		// UUID
		addSerializer(map, UUID.class, (value, ctx) -> {
			ctx.write("\"");
			ctx.write(value.toString());
			ctx.write("\"");
		});

		// BASE64
		addSerializer(map, byte[].class, (value, ctx) -> {
			ctx.write("\"");
			ctx.write(BASE64.encode(value));
			ctx.write("\"");
		});

		// Date
		addSerializer(map, Date.class, (value, ctx) -> {
			if (Config.USE_TIMESTAMPS) {
				ctx.writeQuoted(DataConverterRegistry.convert(String.class, value));
			} else {
				ctx.write(Long.toString(value.getTime()));
			}
		});

		normalMapper = new JSONSerializer(map);
		prettyMapper = new JSONSerializer(map);
		prettyMapper.prettyPrint(true);
	}

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			if (pretty) {
				return prettyMapper.deepSerialize(input);
			}
			return normalMapper.deepSerialize(input);
		});
	}

	// --- OBJECT MAPPER INSTANCE ---

	public JSONDeserializer<Object> mapper = new JSONDeserializer<>();

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		return mapper.deserialize(source);
	}

	// --- ADD CUSTOM SERIALIZER ---

	public static final <T> void addSerializer(TypeTransformerMap map, Class<T> type,
			CheckedBiConsumer<T, JSONContext> consumer) {
		map.put(type, new AbstractTransformer() {

			@SuppressWarnings("unchecked")
			@Override
			public final void transform(Object object) {
				try {
					consumer.accept((T) object, getContext());
				} catch (IOException cause) {
					throw new IllegalArgumentException(cause);
				}
			}

		});
	}

}