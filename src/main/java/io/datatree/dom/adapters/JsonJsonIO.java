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

import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import com.cedarsoftware.io.JsonIo;
import com.cedarsoftware.io.JsonWriter.JsonClassWriter;
import com.cedarsoftware.io.ReadOptions;
import com.cedarsoftware.io.ReadOptionsBuilder;
import com.cedarsoftware.io.WriteOptions;
import com.cedarsoftware.io.WriteOptionsBuilder;
import com.cedarsoftware.io.WriterContext;

import io.datatree.dom.BASE64;
import io.datatree.dom.Config;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;
import io.datatree.dom.converters.DataConverterRegistry;

/**
 * <b>JSONIO JSON ADAPTER</b><br>
 * <br>
 * Description: JsonIO - Convert Java to JSON. Convert JSON to Java.
 * PrettyFormatter print JSON. Java JSON serializer.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.cedarsoftware/json-io<br>
 * compile group: 'com.cedarsoftware', name: 'json-io', version: '4.56.0' <br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one JSON implementation (Jackson, Bson, Gson, etc.) on
 * classpath, the preferred implementation is adjustable with the following
 * System Properties. If there is only one (eg. only the "json-io")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this JSON API automatically.<br>
 * <br>
 * -Ddatatree.json.reader=io.datatree.dom.adapters.JsonJsonIO<br>
 * -Ddatatree.json.writer=io.datatree.dom.adapters.JsonJsonIO<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonJsonIO jsonIO = new JsonJsonIO();<br>
 * TreeReaderRegistry.setReader("json", jsonIO);<br>
 * TreeWriterRegistry.setWriter("json", jsonIO);<br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "JsonJsonIO");<br>
 * String outputString = node.toString("JsonJsonIO");
 *
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(20)
public class JsonJsonIO extends AbstractTextAdapter {

	// --- CUSTOM WRITERS (collected before the immutable options are built) ---

	private static final Map<Class<?>, JsonClassWriter> customWriters = new LinkedHashMap<>();

	// --- NORMAL AND PRETTY FORMATS ---

	public static final WriteOptions normalFormat;
	public static final WriteOptions prettyFormat;

	// --- READER OPTIONS (return native Maps / Object[] instead of POJOs) ---

	public static final ReadOptions readOptions = new ReadOptionsBuilder().returnAsJsonObjects().build();

	// --- CUSTOM WRITERS ---

	static {

		// Install MongoDB / BSON serializers
		tryToAddSerializers("io.datatree.dom.adapters.JsonJsonIOBsonSerializers", new Object());

		// BigDecimal
		addSerializer(BigDecimal.class, (value, output) -> {
			output.write(value.toPlainString());
		});

		// BigInteger
		addSerializer(BigInteger.class, (value, output) -> {
			output.write(value.toString());
		});

		// BASE64
		addSerializer(byte[].class, (value, output) -> {
			output.write('"');
			output.write(BASE64.encode(value));
			output.write('"');
		});

		// UUID
		addSerializer(UUID.class, (value, output) -> {
			output.write('"');
			output.write(value.toString());
			output.write('"');
		});

		// Date
		if (Config.USE_TIMESTAMPS) {
			addSerializer(Date.class, (value, output) -> {
				output.write('"');
				output.write(DataConverterRegistry.convert(String.class, value));
				output.write('"');
			});
		} else {
			addSerializer(Date.class, (value, output) -> {
				output.write(Long.toString(value.getTime()));
			});
		}

		// InetAddress
		addSerializer(InetAddress.class, (value, output) -> {
			output.write('"');
			output.write(value.getCanonicalHostName());
			output.write('"');
		});
		addSerializer(Inet4Address.class, (value, output) -> {
			output.write('"');
			output.write(value.getCanonicalHostName());
			output.write('"');
		});
		addSerializer(Inet6Address.class, (value, output) -> {
			output.write('"');
			output.write(value.getCanonicalHostName());
			output.write('"');
		});

		// Build the immutable write options from the registered custom writers
		normalFormat = new WriteOptionsBuilder()
				.prettyPrint(false)
				.showTypeInfoNever()
				.addCustomWrittenClasses(customWriters)
				.build();
		prettyFormat = new WriteOptionsBuilder()
				.prettyPrint(true)
				.shortMetaKeys(false)
				.showTypeInfoNever()
				.addCustomWrittenClasses(customWriters)
				.build();
	}

	// --- CONSTRUCTOR ---

	public JsonJsonIO() {
	}

	// --- IMPLEMENTED WRITER METHOD ---

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, input -> {
			return JsonIo.toJson(input, pretty ? prettyFormat : normalFormat);
		});
	}

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {

		// In "returnAsJsonObjects" mode json-io only accepts native root types
		// (Map for objects, Object[] for arrays), not java.lang.Object.
		if (source.charAt(0) == '[') {
			return JsonIo.toJava(source, readOptions).asClass(Object[].class);
		}
		return JsonIo.toJava(source, readOptions).asClass(Map.class);
	}

	// --- ADD CUSTOM SERIALIZER ---

	public static <T> void addSerializer(Class<T> type, JsonIOWriter<T> writer) {
		customWriters.put(type, new JsonClassWriter() {

			@Override
			public boolean hasPrimitiveForm(WriterContext context) {
				return true;
			}

			@SuppressWarnings("unchecked")
			@Override
			public void writePrimitiveForm(Object o, Writer output, WriterContext context) throws IOException {
				writer.writePrimitiveForm((T) o, output);
			}

		});
	}

	public interface JsonIOWriter<T> {
		public void writePrimitiveForm(T o, Writer output) throws IOException;
	}

}
