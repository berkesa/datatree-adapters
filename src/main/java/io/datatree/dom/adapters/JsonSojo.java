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

import java.net.InetAddress;
import java.util.UUID;

import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;
import io.datatree.dom.builtin.JsonBuiltin;
import net.sf.sojo.core.Converter;
import net.sf.sojo.core.ConverterInterceptor;
import net.sf.sojo.interchange.json.JsonSerializer;

/**
 * <b>SOJO JSON ADAPTER</b><br>
 * <br>
 * Description: SOJO stands for Simplify your Old Java Objects or, in noun form,
 * Simplified Old Java Objects.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/net.sf.sojo/sojo<br>
 * compile group: 'net.sf.sojo', name: 'sojo', version: '1.0.8'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one JSON implementation (Jackson, Bson, Gson, etc.) on
 * classpath, the preferred implementation is adjustable with the following
 * System Properties. If there is only one (eg. only the "sojo")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this JSON API automatically.<br>
 * <br>
 * -Ddatatree.json.reader=io.datatree.dom.adapters.JsonSojo<br>
 * -Ddatatree.json.writer=io.datatree.dom.adapters.JsonSojo<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonSojo sojo = new JsonSojo();<br>
 * TreeReaderRegistry.setReader("json", sojo);<br>
 * TreeWriterRegistry.setWriter("json", sojo);<br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "JsonSojo");<br>
 * String outputString = node.toString("JsonSojo");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(10)
public class JsonSojo extends AbstractTextAdapter {

	// --- OBJECT MAPPER ---

	public JsonSerializer mapper = create();

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			String json = (String) mapper.serialize(input);
			if (pretty) {
				return JsonBuiltin.format(json);
			}
			return json;
		});
	}

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		return mapper.deserialize(source);
	}

	// --- FACTORY ---

	public static final JsonSerializer create() {
		JsonSerializer mapper = new JsonSerializer();
		Converter converter = mapper.getObjectUtil().getConverter();

		// Install MongoDB / BSON serializers
		boolean allInstalled = tryToAddSerializers("io.datatree.dom.adapters.JsonSojoBsonSerializers", converter);

		// Install serializers for Apache Cassandra
		if (!allInstalled) {
			converter.addConverterInterceptor(new ConverterInterceptor() {

				@Override
				public final void onError(Exception exception) {
				}

				@Override
				public final Object beforeConvert(Object value, Class<?> to) {

					// InetAddress
					if (value instanceof InetAddress) {
						return ((InetAddress) value).getCanonicalHostName();
					}

					// UUID
					if (value instanceof UUID) {
						return ((UUID) value).toString();
					}
					return value;
				}

				@Override
				public final Object afterConvert(Object result, Class<?> to) {
					return result;
				}

			});
		}
		return mapper;
	}

}