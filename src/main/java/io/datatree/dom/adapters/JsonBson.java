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

import static java.util.Arrays.asList;
import static org.bson.codecs.configuration.CodecRegistries.fromProviders;

import java.io.StringWriter;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.util.Map;

import org.bson.AbstractBsonWriter.State;
import org.bson.BsonReader;
import org.bson.Document;
import org.bson.codecs.BsonArrayCodec;
import org.bson.codecs.BsonTypeClassMap;
import org.bson.codecs.BsonValueCodecProvider;
import org.bson.codecs.Codec;
import org.bson.codecs.DecoderContext;
import org.bson.codecs.DocumentCodec;
import org.bson.codecs.DocumentCodecProvider;
import org.bson.codecs.EncoderContext;
import org.bson.codecs.IterableCodec;
import org.bson.codecs.ValueCodecProvider;
import org.bson.codecs.configuration.CodecProvider;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.json.JsonReader;
import org.bson.json.JsonWriter;
import org.bson.json.JsonWriterSettings;
import org.bson.types.Decimal128;

import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;

/**
 * <b>BSON JSON ADAPTER</b><br>
 * <br>
 * Description: Java API for MongoDB (JSON and BSON).<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/org.mongodb/bson<br>
 * compile group: 'org.mongodb', name: 'bson', version: '3.10.2'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one JSON implementation (Jackson, Bson, Gson, etc.) on
 * classpath, the preferred implementation is adjustable with the following
 * System Properties. If there is only one (eg. only the "bson") implementation
 * on the classpath, this step is NOT necessary, the DataTree API will use this
 * JSON API automatically.<br>
 * <br>
 * -Ddatatree.json.reader=io.datatree.dom.adapters.JsonBson<br>
 * -Ddatatree.json.writer=io.datatree.dom.adapters.JsonBson<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * JsonBson jsonBson = new JsonBson();<br>
 * TreeReaderRegistry.setReader("json", jsonBson);<br>
 * TreeWriterRegistry.setWriter("json", jsonBson);<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputString);<br>
 * String outputString = node.toString();<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "JsonBson");<br>
 * String outputString = node.toString("JsonBson");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(70)
public class JsonBson extends AbstractTextAdapter {

	// --- CODECS ---

	public EncoderContext encoderContext = EncoderContext.builder().isEncodingCollectibleDocument(true).build();

	public CodecRegistry codecRegistry = fromProviders(asList(new ValueCodecProvider(), new BsonValueCodecProvider(),
			new DocumentCodecProvider(), new CustomProvider()));

	public BsonTypeClassMap bsonTypeClassMap = new BsonTypeClassMap();

	public JsonWriterSettings normalJsonWriterSettings = JsonWriterSettings.builder().indent(false).build();
	public JsonWriterSettings prettyJsonWriterSettings = JsonWriterSettings.builder().indent(true).build();

	public DocumentCodec documentCodec = new DocumentCodec(codecRegistry, bsonTypeClassMap);
	public IterableCodec iterableCodec = new IterableCodec(codecRegistry, bsonTypeClassMap);

	public BsonArrayCodec arrayCodec = new BsonArrayCodec();
	public DecoderContext context = DecoderContext.builder().build();

	// --- IMPLEMENTED WRITER METHOD ---

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			StringWriter buffer = new StringWriter(512);
			CustomStateWriter writer = new CustomStateWriter(buffer,
					pretty ? prettyJsonWriterSettings : normalJsonWriterSettings);
			if (input instanceof Document) {

				documentCodec.encode(writer, (Document) input, encoderContext);

			} else if (input instanceof Map) {

				documentCodec.encode(writer, new Document((Map<String, Object>) input), encoderContext);

			} else if (input instanceof Iterable) {

				writer.setState(State.VALUE);
				iterableCodec.encode(writer, (Iterable) input, encoderContext);

			} else if (input.getClass().isArray()) {

				writer.setState(State.VALUE);
				iterableCodec.encode(writer, asList(input), encoderContext);

			} else {
				writer.close();
				throw new IllegalArgumentException("Unsupported data type: " + input);
			}
			return buffer.toString();
		});
	}

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		char c = source.charAt(0);
		if (c == '{') {
			return documentCodec.decode(new JsonReader(source), context);
		}
		if (c == '[') {
			return arrayCodec.decode(new JsonReader(source), context);
		}
		throw new IllegalArgumentException("Malformed JSON: " + source);
	}

	// --- CUSTOM CODECS ---

	protected class CustomProvider implements CodecProvider {

		protected BigDecimalCodec bigDecimalCodec = new BigDecimalCodec();
		protected BigIntegerCodec bigIntegerCodec = new BigIntegerCodec();
		protected InetAddressCodec inetAddressCodec = new InetAddressCodec();
		protected ArrayCodec arrayCodec = new ArrayCodec();

		@SuppressWarnings("unchecked")
		@Override
		public <T> Codec<T> get(Class<T> clazz, CodecRegistry registry) {
			if (clazz == BigDecimal.class) {
				return (Codec<T>) bigDecimalCodec;
			}
			if (clazz == BigInteger.class) {
				return (Codec<T>) bigIntegerCodec;
			}
			if (clazz == InetAddress.class || clazz == Inet4Address.class || clazz == Inet6Address.class) {
				return (Codec<T>) inetAddressCodec;
			}
			if (clazz.isArray()) {
				return (Codec<T>) arrayCodec;
			}
			return null;
		}

	}

	protected class InetAddressCodec implements Codec<InetAddress> {

		@Override
		public final void encode(final org.bson.BsonWriter writer, final InetAddress value,
				final EncoderContext encoderContext) {
			writer.writeString(value.getCanonicalHostName());
		}

		@Override
		public final InetAddress decode(final BsonReader reader, final DecoderContext decoderContext) {
			try {
				return InetAddress.getByName(reader.readString());
			} catch (Exception e) {
				throw new IllegalArgumentException(e);
			}
		}

		@Override
		public final Class<InetAddress> getEncoderClass() {
			return InetAddress.class;
		}

	}

	protected class BigDecimalCodec implements Codec<BigDecimal> {

		@Override
		public final void encode(final org.bson.BsonWriter writer, final BigDecimal value,
				final EncoderContext encoderContext) {
			writer.writeDecimal128(new Decimal128(value));
		}

		@Override
		public final BigDecimal decode(final BsonReader reader, final DecoderContext decoderContext) {
			return reader.readDecimal128().bigDecimalValue();
		}

		@Override
		public final Class<BigDecimal> getEncoderClass() {
			return BigDecimal.class;
		}

	}

	protected class BigIntegerCodec implements Codec<BigInteger> {

		@Override
		public final void encode(final org.bson.BsonWriter writer, final BigInteger value,
				final EncoderContext encoderContext) {
			writer.writeString(value.toString());
		}

		@Override
		public final BigInteger decode(final BsonReader reader, final DecoderContext decoderContext) {
			return new BigInteger(reader.readString());
		}

		@Override
		public final Class<BigInteger> getEncoderClass() {
			return BigInteger.class;
		}

	}

	protected class ArrayCodec implements Codec<Object[]> {

		private IterableCodec codec;

		@Override
		public final void encode(org.bson.BsonWriter writer, Object[] array, EncoderContext encoderContext) {
			if (codec == null) {
				codec = new IterableCodec(codecRegistry, bsonTypeClassMap);
			}
			codec.encode(writer, asList(array), encoderContext);
		}

		@Override
		public Object[] decode(BsonReader reader, DecoderContext decoderContext) {
			throw new UnsupportedOperationException("decode array");
		}

		@Override
		public final Class<Object[]> getEncoderClass() {
			return Object[].class;
		}

	}

	protected class CustomStateWriter extends JsonWriter {

		public CustomStateWriter(Writer writer, JsonWriterSettings settings) {
			super(writer, settings);
		}

		@Override
		public final void setState(State state) {
			super.setState(state);
		}

	}

}
