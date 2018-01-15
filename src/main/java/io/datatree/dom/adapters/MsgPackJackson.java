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

import org.msgpack.jackson.dataformat.MessagePackFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.datatree.dom.Priority;

/**
 * <b>JACKSON MESSAGEPACK BINARY ADAPTER</b><br>
 * <br>
 * Description: MessagePack is an efficient binary serialization format. It lets
 * you exchange data among multiple languages like JSON. But it's faster and
 * smaller. Small integers are encoded into a single byte, and typical short
 * strings require only one extra byte in addition to the strings themselves.
 * <br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/org.msgpack/jackson-dataformat-msgpack<br>
 * compile group: 'org.msgpack', name: 'jackson-dataformat-msgpack', version:
 * '0.8.14'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one MessagePack implementation on classpath, the
 * preferred implementation is adjustable with the following System Properties.
 * If there is only one (eg. only the "jackson-dataformat-msgpack")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this implementation automatically.<br>
 * <br>
 * -Ddatatree.msgpack.reader=io.datatree.dom.adapters.MsgPackJackson<br>
 * -Ddatatree.msgpack.writer=io.datatree.dom.adapters.MsgPackJackson<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * MsgPackJackson msgpack = new MsgPackJackson();<br>
 * TreeReaderRegistry.setReader("msgpack", msgpack);<br>
 * TreeWriterRegistry.setWriter("msgpack", msgpack);<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputBytes, "msgpack");<br>
 * byte[] outputBytes = node.toBytes("msgpack");<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputBytes, "MsgPackJackson");<br>
 * byte[] outputBytes = node.toBytes("MsgPackJackson");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(10)
public class MsgPackJackson extends AbstractJacksonBinaryAdapter {

	// --- NAME OF THE FORMAT ---

	@Override
	public String getFormat() {
		return "msgpack";
	}

	// --- CONSTRUCTOR ---

	public MsgPackJackson() {
		super(new ObjectMapper(new MessagePackFactory()));

		// Install MongoDB / BSON serializers
		// (Using JSON serializers)
		tryToAddSerializers("io.datatree.dom.adapters.JsonJacksonBsonSerializers", mapper);
	}

}