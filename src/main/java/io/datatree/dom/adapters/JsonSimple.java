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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.json.simple.JSONArray;
import org.json.simple.JSONAware;
import org.json.simple.JSONObject;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;

import io.datatree.dom.Config;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;
import io.datatree.dom.builtin.JsonBuiltin;

/**
 * <b>JSON.SIMPLE JSON ADAPTER</b><br>
 * <br>
 * Description: JSON.simple is a simple Java toolkit for JSON. You can use
 * JSON.simple to encode or decode JSON text.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.googlecode.json-simple/json-simple
 * <br>
 * compile group: 'com.googlecode.json-simple', name: 'json-simple', version:
 * '1.1.1'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one JSON implementation (Jackson, Bson, Gson, etc.) on
 * classpath, the preferred implementation is adjustable with the following
 * System Properties. If there is only one (eg. only the "json-simple")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this JSON API automatically.<br>
 * <br>
 * -Ddatatree.json.reader=io.datatree.dom.adapters.JsonSimple<br>
 * -Ddatatree.json.writer=io.datatree.dom.adapters.JsonSimple<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonSimple jsonSimple = new JsonSimple();<br>
 * TreeReaderRegistry.setReader("json", jsonSimple);<br>
 * TreeWriterRegistry.setWriter("json", jsonSimple);<br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "JsonSimple");<br>
 * String outputString = node.toString("JsonSimple");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(40)
public class JsonSimple extends AbstractTextAdapter {

	// --- READER CACHE ---

	public Queue<JSONParser> parsers = new ConcurrentLinkedQueue<>();

	// --- CONTAINER FACTORY ---

	public ContainerFactory containerFactory = new LinkedContainerFactory();

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

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		JSONParser parser = parsers.poll();
		if (parser == null) {
			parser = new JSONParser();
		}
		final Object result = parser.parse(source, containerFactory);
		if (parsers.size() > Config.POOL_SIZE) {
			return result;
		}
		parsers.add(parser);
		return result;
	}

	// --- CONTAINER FACTORY ---

	@SuppressWarnings("rawtypes")
	protected static class LinkedContainerFactory implements ContainerFactory {

		@Override
		public Map createObjectContainer() {
			return new LinkedHashMap();
		}

		@Override
		public List creatArrayContainer() {
			return new LinkedList();
		}

	}

}
