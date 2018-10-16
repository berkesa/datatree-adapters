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

import java.nio.charset.StandardCharsets;
import java.util.Queue;
import java.util.TimeZone;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.moandjiezana.toml.Toml;
import com.moandjiezana.toml.TomlWriter;

import io.datatree.dom.Config;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;

/**
 * <b>TOML4J TOML ADAPTER</b><br>
 * <br>
 * Description: Toml4j is a TOML 0.4.0 parser (reader and writer) for Java.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.moandjiezana.toml/toml4j<br>
 * compile group: 'com.moandjiezana.toml', name: 'toml4j', version: '0.7.2' <br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one TOML implementation on classpath, the preferred
 * implementation is adjustable with the following System Properties. If there
 * is only one (eg. only the "toml4j") implementation on the classpath, this
 * step is NOT necessary, the DataTree API will use this implementation
 * automatically.<br>
 * <br>
 * -Ddatatree.toml.reader=io.datatree.dom.adapters.TomlToml4j<br>
 * -Ddatatree.toml.writer=io.datatree.dom.adapters.TomlToml4j<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * TomlToml4j toml = new TomlToml4j();<br>
 * TreeReaderRegistry.setReader("toml", toml);<br>
 * TreeWriterRegistry.setWriter("toml", toml);<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputString, "toml");<br>
 * String outputString = node.toString("toml");<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "TomlToml4j");<br>
 * String outputString = node.toString("TomlToml4j");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(20)
public class TomlToml4j extends AbstractTextAdapter {

	// --- OBJECT MAPPER INSTANCE ---

	public TomlWriter mapper;

	// --- PARSER CACHE ---

	public Queue<Toml> parsers = new ConcurrentLinkedQueue<>();

	// --- NAME OF THE FORMAT ---

	@Override
	public String getFormat() {
		return "toml";
	}

	// --- CONSTRUCTOR ---

	public TomlToml4j() {
		try {
			mapper = new TomlWriter.Builder().timeZone(TimeZone.getTimeZone(Config.DEFAULT_TIME_ZONE)).build();
		} catch (Exception ignored) {
			mapper = new TomlWriter();
		}
	}

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			return mapper.write(input);
		});
	}

	// --- IMPLEMENTED PARSER METHODS ---

	public Object parse(byte[] source) throws Exception {
		return parse(new String(source, StandardCharsets.UTF_8));
	}

	@Override
	public Object parse(String source) throws Exception {
		Toml parser = parsers.poll();
		if (parser == null) {
			parser = new Toml();
		}
		final Object result = parser.read(source).toMap();
		if (parsers.size() > Config.POOL_SIZE) {
			return result;
		}
		parsers.add(parser);
		return result;
	}

}