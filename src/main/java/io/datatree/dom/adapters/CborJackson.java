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
import com.fasterxml.jackson.dataformat.cbor.CBORFactory;

import io.datatree.dom.Priority;

/**
 * <b>JACKSON BINARY CBOR ADAPTER</b><br>
 * <br>
 * Description: CBOR is based on the wildly successful JSON data model: numbers,
 * strings, arrays, maps (called objects in JSON), and a few values such as
 * false, true, and null. One of the major practical wins of JSON is that
 * successful data interchange is possible without casting a schema in concrete.
 * This works much better in a world where both ends of a communication
 * relationship may be evolving at high speed.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/
 * jackson-dataformat-cbor<br>
 * compile group: 'com.fasterxml.jackson.dataformat', name:
 * 'jackson-dataformat-cbor', version: '2.10.1'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one CBOR implementation on classpath, the preferred
 * implementation is adjustable with the following System Properties. If there
 * is only one (eg. only the "jackson-dataformat-cbor") implementation on the
 * classpath, this step is NOT necessary, the DataTree API will use this
 * implementation automatically.<br>
 * <br>
 * -Ddatatree.cbor.reader=io.datatree.dom.adapters.CborJackson<br>
 * -Ddatatree.cbor.writer=io.datatree.dom.adapters.CborJackson<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * CborJackson cbor = new CborJackson();<br>
 * TreeReaderRegistry.setReader("cbor", cbor);<br>
 * TreeWriterRegistry.setWriter("cbor", cbor);<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputBytes, "cbor");<br>
 * byte[] outputBytes = node.toBytes("cbor");<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputBytes, "CborJackson");<br>
 * byte[] outputBytes = node.toBytes("CborJackson");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(10)
public class CborJackson extends AbstractJacksonBinaryAdapter {

	// --- NAME OF THE FORMAT ---

	@Override
	public String getFormat() {
		return "cbor";
	}

	// --- CONSTRUCTOR ---

	public CborJackson() {
		super(new ObjectMapper(new CBORFactory()));

		// Install MongoDB / BSON serializers
		// (Using JSON serializers)
		tryToAddSerializers("io.datatree.dom.adapters.JsonJacksonBsonSerializers", mapper);
	}

}