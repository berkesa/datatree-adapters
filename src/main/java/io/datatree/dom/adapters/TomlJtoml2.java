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

import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;
import io.ous.jtoml.JToml;

/**
 * <b>JTOML V2 TOML ADAPTER</b><br>
 * <br>
 * Description: TOML Parser for Java (JToml reader API).<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * // https://mvnrepository.com/artifact/io.ous/jtoml<br>
 * compile group: 'io.ous', name: 'jtoml', version: '2.0.0'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * -Ddatatree.toml.reader=io.datatree.dom.adapters.TomlJtoml2<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * TomlJtoml2 toml = new TomlJtoml2();<br>
 * TreeReaderRegistry.setReader("toml", toml);<br>
 * <br>
 * <b>Invoke deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputString, "toml");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(30)
public class TomlJtoml2 extends AbstractTextAdapter {

	// --- NAME OF THE FORMAT ---
	
	@Override
	public String getFormat() {
		return "toml";
	}
	
	// --- FAKE WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		throw new UnsupportedOperationException("The 'io.ous.jtoml' API is only a TOML reader, has no writer functionality!");
	}
	
	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		return JToml.parseString(source);
	}
	
}