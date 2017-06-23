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

import org.kopitubruk.util.json.IndentPadding;
import org.kopitubruk.util.json.JSONConfig;
import org.kopitubruk.util.json.JSONParser;
import org.kopitubruk.util.json.JSONUtil;

import io.datatree.dom.Config;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;

/**
 * <b>JSONUTIL JSON ADAPTER</b><br>
 * <br>
 * Description: JSON generation and parsing utility library for Java
 * (http://kopitubruk.org/JSONUtil/).<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/org.kopitubruk.util/JSONUtil<br>
 * compile group: 'org.kopitubruk.util', name: 'JSONUtil', version: '1.10.4'
 * <br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one JSON implementation (Jackson, Bson, Gson, etc.) on
 * classpath, the preferred implementation is adjustable with the following
 * System Properties. If there is only one (eg. only the "JSONUtil")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this JSON API automatically.<br>
 * <br>
 * -Ddatatree.json.reader=io.datatree.dom.adapters.JsonUtil<br>
 * -Ddatatree.json.writer=io.datatree.dom.adapters.JsonUtil<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonUtil jsonUtil = new JsonUtil();<br>
 * TreeReaderRegistry.setReader("json", jsonUtil);<br>
 * TreeWriterRegistry.setWriter("json", jsonUtil);<br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "JsonUtil");<br>
 * String outputString = node.toString("JsonUtil");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(60)
public class JsonUtil extends AbstractTextAdapter {

	// --- CONFIGURATION OF MAPPERS ---

	public JSONConfig parser = create(false, false);
	public JSONConfig config = create(false, true);
	public JSONConfig prettyConfig = create(true, true);

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			return JSONUtil.toJSON(input, pretty ? prettyConfig : config);
		});
	}

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		return JSONParser.parseJSON(source, parser);
	}

	// --- FACTORY ---

	public static final JSONConfig create(boolean pretty, boolean enableDateProcessing) {
		JSONConfig config = new JSONConfig();
		config.setAllowReservedWordsInIdentifiers(true);
		config.setValidatePropertyNames(false);
		config.setDetectDataStructureLoops(false);
		config.setEncodeNumericStringsAsNumbers(false);
		config.setBadCharacterPolicy(JSONConfig.PASS);

		// Date format
		if (enableDateProcessing) {
			config.setEncodeDatesAsStrings(true);
			config.setDateGenFormat(Config.TIMESTAMP_FORMATTER);
		} else {

			// Parser config
			config.setEncodeDatesAsStrings(false);
			config.setEncodeDatesAsObjects(false);
		}

		// Enably pretty formatter
		if (pretty) {
			config.setIndentPadding(new IndentPadding());
		}

		return config;
	}

}