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
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import io.datatree.dom.Config;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;

/**
 * <b>FASTJSON JSON ADAPTER</b><br>
 * <br>
 * Description: A fast JSON parser/generator for Java.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.alibaba/fastjson<br>
 * compile group: 'com.alibaba', name: 'fastjson', version: '1.2.44'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one JSON implementation (Jackson, Bson, Gson, etc.) on
 * classpath, the preferred implementation is adjustable with the following
 * System Properties. If there is only one (eg. only the "fastjson")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this JSON API automatically.<br>
 * <br>
 * -Ddatatree.json.reader=io.datatree.dom.adapters.JsonFast<br>
 * -Ddatatree.json.writer=io.datatree.dom.adapters.JsonFast<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonFast jsonFast = new JsonFast();<br>
 * TreeReaderRegistry.setReader("json", jsonFast);<br>
 * TreeWriterRegistry.setWriter("json", jsonFast);<br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "JsonFast");<br>
 * String outputString = node.toString("JsonFast");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(160)
public class JsonFast extends AbstractTextAdapter {

	// --- SETTINGS ---

	public SerializerFeature[] normalFeatures;
	public SerializerFeature[] prettyFeatures;

	// --- STATIC CONSTRUCTOR ---

	static {

		// Install BSON serializers
		tryToAddSerializers("io.datatree.dom.adapters.JsonFastBsonSerializers", SerializeConfig.getGlobalInstance());
	}

	// --- CONSTRUCTOR ---

	public JsonFast() {
		ArrayList<SerializerFeature> normalList = new ArrayList<SerializerFeature>();
		ArrayList<SerializerFeature> prettyList = new ArrayList<SerializerFeature>();

		normalList.add(SerializerFeature.DisableCircularReferenceDetect);
		prettyList.add(SerializerFeature.DisableCircularReferenceDetect);

		if (Config.USE_TIMESTAMPS) {
			normalList.add(SerializerFeature.UseISO8601DateFormat);
			prettyList.add(SerializerFeature.UseISO8601DateFormat);

			normalList.add(SerializerFeature.WriteDateUseDateFormat);
			prettyList.add(SerializerFeature.WriteDateUseDateFormat);
		}

		prettyList.add(SerializerFeature.PrettyFormat);

		normalFeatures = normalList.toArray(new SerializerFeature[normalList.size()]);
		prettyFeatures = prettyList.toArray(new SerializerFeature[prettyList.size()]);
	}

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			final SerializeWriter out = new SerializeWriter((Writer) null, JSON.DEFAULT_GENERATE_FEATURE,
					pretty ? prettyFeatures : normalFeatures);
			final JSONSerializer writer = new JSONSerializer(out);
			if (Config.USE_TIMESTAMPS) {
				writer.setDateFormat(Config.TIMESTAMP_FORMATTER);
			}
			writer.write(input);
			final String json = out.toString();
			writer.close();
			return json;
		});
	}

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		return JSON.parse(source);
	}

	// --- ADD CUSTOM SERIALIZER ---

	public static final <T> void addSerializer(SerializeConfig config, Class<T> type,
			CheckedBiConsumer<T, JSONSerializer> consumer) {
		config.put(type, new ObjectSerializer() {

			@SuppressWarnings("unchecked")
			@Override
			public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features)
					throws IOException {
				consumer.accept((T) object, serializer);
			}

		});
	}

}