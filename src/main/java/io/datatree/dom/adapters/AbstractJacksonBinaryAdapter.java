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

import com.fasterxml.jackson.databind.ObjectMapper;

import io.datatree.dom.BASE64;

/**
 * Abstract class for Jackson-based BSON, CBOR, SMILE, etc. readers / writers.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public abstract class AbstractJacksonBinaryAdapter extends AbstractJacksonAdapter {

	// --- CONSTRUCTOR ---

	public AbstractJacksonBinaryAdapter(ObjectMapper mapper) {
		super(mapper);
	}
	
	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return BASE64.encode(toBinary(value, meta, insertMeta));
	}
	
	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		return parse(BASE64.decode(source));
	}

}
