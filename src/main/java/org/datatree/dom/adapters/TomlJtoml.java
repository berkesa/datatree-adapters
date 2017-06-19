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

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.datatree.dom.Config;
import org.datatree.dom.Priority;
import org.datatree.dom.builtin.AbstractTextAdapter;

import me.grison.jtoml.TomlParser;
import me.grison.jtoml.TomlSerializer;
import me.grison.jtoml.impl.SimpleTomlParser;

/**
 * <b>JTOML TOML ADAPTER</b><br>
 * <br>
 * Description: This is a parser for Tom Preson-Werner's (@mojombo) TOML markup
 * language, using Java.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/me.grison/jtoml<br>
 * compile group: 'me.grison', name: 'jtoml', version: '1.0.0'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * -Ddatatree.toml.reader=org.datatree.dom.adapters.TomlJtoml<br>
 * -Ddatatree.toml.writer=org.datatree.dom.adapters.TomlJtoml<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * TomlJtoml toml = new TomlJtoml();<br>
 * TreeReaderRegistry.setReader("toml", toml);<br>
 * TreeWriterRegistry.setWriter("toml", toml);<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputString, "toml");<br>
 * String outputString = node.toString("toml");<br>
 * <br>
 * The "io.ous.jtoml" is better TOML reader than "me.grison.jtoml", but has no
 * writer functionality. To combine the two APIs, use this settings:<br>
 * <br>
 * -Ddatatree.toml.reader=org.datatree.dom.adapters.TomlJtoml2<br>
 * -Ddatatree.toml.writer=org.datatree.dom.adapters.TomlJtoml<br>
 * <br>
 * You can use "TomlToml4j" as a TOML writer instead of "TomlJtoml".
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(10)
public class TomlJtoml extends AbstractTextAdapter {

	// --- NAME OF THE FORMAT ---

	@Override
	public String getFormat() {
		return "toml";
	}

	// --- OBJECT MAPPER INSTANCE ---

	public TomlSerializer mapper;

	// --- CONSTRUCTOR ---

	public TomlJtoml() {
		try {
			mapper = new TomlJtomlBsonSerializers();
		} catch (Throwable classNotFound) {
			mapper = new TomlJtomlJavaSerializers();
		}
	}

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			return mapper.serialize(null, input);
		});
	}

	// --- PARSER CACHE ---

	public Queue<TomlParser> parsers = new ConcurrentLinkedQueue<>();

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		TomlParser parser = parsers.poll();
		if (parser == null) {
			parser = new SimpleTomlParser();
		}
		final Object result = parser.parse(source);
		if (parsers.size() > Config.POOL_SIZE) {
			return result;
		}
		parsers.add(parser);
		return result;
	}

}