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

import java.util.LinkedHashMap;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import io.datatree.dom.Config;

/**
 * Abstract class for Jackson-based JSON, XML, YAML, etc. readers / writers.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public abstract class AbstractJacksonTextAdapter extends AbstractJacksonAdapter {

	// --- NAME OF THE FORMAT ---
	
	@Override
	public String getFormat() {
		return "json";
	}
	
	// --- PRETTY WRITER ---

	public ObjectMapper prettyMapper;
	
	// --- CONSTRUCTOR ---

	public AbstractJacksonTextAdapter(ObjectMapper mapper) {
		super(mapper);
		prettyMapper = mapper.copy();
		if (Config.USE_TIMESTAMPS) {
			prettyMapper.setDateFormat(Config.TIMESTAMP_FORMATTER);
		}
		prettyMapper.enable(SerializationFeature.INDENT_OUTPUT);
	}
	
	// --- IMPLEMENTED WRITER METHODS ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			if (pretty && prettyMapper != null) {
				return prettyMapper.writeValueAsString(input);
			}
			return mapper.writeValueAsString(input);
		});
	}
	
	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		return mapper.readValue(source, LinkedHashMap.class);
	}
	
}