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
import com.fasterxml.jackson.dataformat.smile.SmileFactory;

import io.datatree.dom.Priority;

/**
 * <b>JACKSON BINARY SMILE ADAPTER</b><br>
 * <br>
 * Description: Smile is a computer data interchange format based on JSON. It
 * can also be considered as a binary serialization of generic JSON data model,
 * which means that tools that operate on JSON may be used with Smile as well,
 * as long as proper encoder/decoder exists for tool to use. Compared to JSON,
 * Smile is both more compact and more efficient to process (both to read and
 * write).<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/
 * jackson-dataformat-smile<br>
 * compile group: 'com.fasterxml.jackson.dataformat', name:
 * 'jackson-dataformat-smile', version: '2.9.9'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one SMILE implementation on classpath, the
 * preferred implementation is adjustable with the following System Properties.
 * If there is only one (eg. only the "jackson-dataformat-smile")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this implementation automatically.<br>
 * <br>
 * -Ddatatree.smile.reader=io.datatree.dom.adapters.SmileJackson<br>
 * -Ddatatree.smile.writer=io.datatree.dom.adapters.SmileJackson<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * SmileJackson smile = new SmileJackson();<br>
 * TreeReaderRegistry.setReader("smile", smile);<br>
 * TreeWriterRegistry.setWriter("smile", smile);<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputBytes, "smile");<br>
 * byte[] outputBytes = node.toBytes("smile");<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputBytes, "SmileJackson");<br>
 * byte[] outputBytes = node.toBytes("SmileJackson");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(10)
public class SmileJackson extends AbstractJacksonBinaryAdapter {

	// --- NAME OF THE FORMAT ---

	@Override
	public String getFormat() {
		return "smile";
	}

	// --- CONSTRUCTOR ---

	public SmileJackson() {
		super(new ObjectMapper(new SmileFactory()));

		// Install MongoDB / BSON serializers
		// (Using JSON serializers)
		tryToAddSerializers("io.datatree.dom.adapters.JsonJacksonBsonSerializers", mapper);
	}

}