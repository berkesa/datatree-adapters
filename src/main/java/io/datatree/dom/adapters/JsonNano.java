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

import com.grack.nanojson.ExtendedWriter;
import com.grack.nanojson.JsonParser;
import com.grack.nanojson.JsonParser.JsonParserContext;

import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;

/**
 * <b>NANOJSON JSON ADAPTER</b><br>
 * <br>
 * Description: A tiny, compliant JSON parser and writer for Java.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.grack/nanojson<br>
 * compile group: 'com.grack', name: 'nanojson', version: '1.4'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one JSON implementation (Jackson, Bson, Gson, etc.) on
 * classpath, the preferred implementation is adjustable with the following
 * System Properties. If there is only one (eg. only the "nanojson")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this JSON API automatically.<br>
 * <br>
 * -Ddatatree.json.reader=io.datatree.dom.adapters.JsonNano<br>
 * -Ddatatree.json.writer=io.datatree.dom.adapters.JsonNano<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonNano jsonNano = new JsonNano();<br>
 * TreeReaderRegistry.setReader("json", jsonNano);<br>
 * TreeWriterRegistry.setWriter("json", jsonNano);<br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "JsonNano");<br>
 * String outputString = node.toString("JsonNano");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(50)
public class JsonNano extends AbstractTextAdapter {

	// --- OBJECT MAPPER INSTANCE ---

	public JsonParserContext<Object> mapper = JsonParser.any();

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			ExtendedWriter writer = new ExtendedWriter(pretty ? "  " : null);
			writer.value(input);
			return writer.done();
		});
	}

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		return mapper.from(source);
	}

}
