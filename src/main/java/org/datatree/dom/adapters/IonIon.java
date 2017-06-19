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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.datatree.dom.BASE64;
import org.datatree.dom.Config;
import org.datatree.dom.Priority;

import software.amazon.ion.system.IonBinaryWriterBuilder;

/**
 * <b>AMAZON ION BINARY ION ADAPTER</b><br>
 * <br>
 * Description: Amazon Ion is a richly-typed, self-describing, hierarchical data
 * serialization format offering interchangeable binary and text
 * representations. The binary representation is efficient to store, transmit,
 * and skip-scan parse. The rich type system provides unambiguous semantics for
 * long-term preservation of business data which can survive multiple
 * generations of software evolution. Ion was built to solve the rapid
 * development, decoupling, and efficiency challenges faced every day while
 * engineering large-scale, service-oriented architectures.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/software.amazon.ion/ion-java<br>
 * compile group: 'software.amazon.ion', name: 'ion-java', version: '1.0.2'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * -Ddatatree.ion.reader=org.datatree.dom.adapters.IonIon<br>
 * -Ddatatree.ion.writer=org.datatree.dom.adapters.IonIon<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * IonIon ion = new IonIon();<br>
 * TreeReaderRegistry.setReader("ion", ion);<br>
 * TreeWriterRegistry.setWriter("ion", ion);<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputBytes, "ion");<br>
 * byte[] outputBytes = node.toBytes("ion");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(10)
public class IonIon extends JsonIon {

	// --- NAME OF THE FORMAT ---

	@Override
	public String getFormat() {
		return "ion";
	}

	// --- WRITER FACTORY ---

	public CachedWriter createWriter(boolean pretty) throws IOException {
		CachedWriter writer = new CachedWriter();
		writer.buffer = new ByteArrayOutputStream(512);
		writer.writer = IonBinaryWriterBuilder.standard().build(writer.buffer);
		return writer;
	}

	// --- IMPLEMENTED WRITER METHODS ---

	@Override
	public byte[] toBinary(Object value, Object meta, boolean insertMeta) {
		return toBinary(value, meta, insertMeta, (input) -> {

			// Get ION writer from cache
			CachedWriter writer = writers.poll();
			if (writer == null) {
				writer = createWriter(false);
			} else {
				writer.buffer.reset();
			}

			// Serialize data
			write(writer.writer, null, input);
			writer.writer.finish();
			byte[] bytes = writer.buffer.toByteArray();
			if (writers.size() > Config.POOL_SIZE) {

				// Writer pool is full
				return bytes;
			}

			// Recycle ION writer instance
			writers.add(writer);
			return bytes;
		});
	}

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