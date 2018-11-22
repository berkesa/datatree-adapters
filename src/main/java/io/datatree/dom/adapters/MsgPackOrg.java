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
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.UUID;

import org.msgpack.MessagePack;
import org.msgpack.packer.Packer;
import org.msgpack.template.Template;
import org.msgpack.type.ArrayValue;
import org.msgpack.type.MapValue;
import org.msgpack.type.RawValue;
import org.msgpack.type.Value;
import org.msgpack.unpacker.Unpacker;

import io.datatree.dom.BASE64;
import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractAdapter;

/**
 * <b>ORG.MSGPACK MESSAGEPACK BINARY ADAPTER</b><br>
 * <br>
 * Description: MessagePack is an efficient binary serialization
 * format. It lets you exchange data among multiple languages like JSON. But
 * it's faster and smaller. Small integers are encoded into a single byte, and
 * typical short strings require only one extra byte in addition to the strings
 * themselves.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/org.msgpack/msgpack<br>
 * compile group: 'org.msgpack', name: 'msgpack', version: '0.6.12'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one MessagePack implementation on classpath, the
 * preferred implementation is adjustable with the following System Properties.
 * If there is only one (eg. only the "msgpack")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this implementation automatically.<br>
 * <br>
 * -Ddatatree.msgpack.reader=io.datatree.dom.adapters.MsgPackOrg<br>
 * -Ddatatree.msgpack.writer=io.datatree.dom.adapters.MsgPackOrg<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * MsgPackOrg msgpack = new MsgPackOrg();<br>
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
 * Tree node = new Tree(inputBytes, "MsgPackOrg");<br>
 * byte[] outputBytes = node.toBytes("MsgPackOrg");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(20)
public class MsgPackOrg extends AbstractAdapter {

	// --- OBJECT MAPPER INSTANCE ---

	public MessagePack mapper = new MessagePack();

	// --- NAME OF THE FORMAT ---

	@Override
	public String getFormat() {
		return "msgpack";
	}

	// --- COSNTRUCTOR ---

	public MsgPackOrg() {

		// Install Java / Apache Cassandra serializers
		addDefaultSerializers();
		
		// Install MongoDB / BSON serializers
		tryToAddSerializers("io.datatree.dom.adapters.MsgPackOrgBsonSerializers", mapper);
	}

	public void addDefaultSerializers() {
		
		// InetAddress
		addSerializer(mapper, InetAddress.class, (packer, value) -> {
			packer.write(value.getCanonicalHostName());
		});

		// UUID
		addSerializer(mapper, UUID.class, (packer, value) -> {
			packer.write(value.toString());
		});
		
		// Byte array
		addSerializer(mapper, byte[].class, (packer, value) -> {
			packer.writeArrayBegin(value.length);
			for (int i = 0; i < value.length; i++) {
				packer.write(value[i]);
			}
			packer.writeArrayEnd();
		});
	}
	
	// --- IMPLEMENTED WRITER METHODS ---

	@Override
	public byte[] toBinary(Object value, Object meta, boolean insertMeta) {
		return toBinary(value, meta, insertMeta, (input) -> {
			return mapper.write(input);
		});
	}

	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return BASE64.encode(toBinary(value, meta, insertMeta));
	}

	// --- IMPLEMENTED PARSER METHODS ---

	@Override
	public Object parse(byte[] source) throws Exception {
		return toObject(mapper.read(source));
	}

	@Override
	public Object parse(String source) throws Exception {
		return parse(BASE64.decode(source));
	}

	// --- RECURSIVE CONVERTER ---

	protected static final Object toObject(Value value) {
		if (value == null || value.isNilValue()) {
			return null;
		}
		if (value.isBooleanValue()) {
			return value.asBooleanValue().getBoolean();
		}
		if (value.isFloatValue()) {
			return value.asFloatValue().getDouble();
		}
		if (value.isIntegerValue()) {
			return value.asIntegerValue().getLong();
		}
		if (value.isArrayValue()) {
			ArrayValue arrayValue = value.asArrayValue();
			LinkedList<Object> list = new LinkedList<>();
			for (Value item : arrayValue) {
				list.addLast(toObject(item));
			}
			return list;
		}
		if (value.isMapValue()) {
			MapValue mapValue = value.asMapValue();
			LinkedHashMap<Object, Object> map = new LinkedHashMap<>();
			for (Map.Entry<Value, Value> entry : mapValue.entrySet()) {
				map.put(toObject(entry.getKey()), toObject(entry.getValue()));
			}
			return map;
		}
		RawValue raw = value.asRawValue();
		try {
			return raw.getString();			
		} catch (Throwable ignored) {
			return raw.getByteArray();
		}
	}

	// --- ADD CUSTOM SERIALIZER ---

	public static final <T> void addSerializer(MessagePack mapper, Class<T> type, SimpleTemplate<T> template) {
		mapper.register(type, new Template<T>() {

			@Override
			public void write(Packer pk, T v) throws IOException {
				template.write(pk, v);
			}

			@Override
			public void write(Packer pk, T v, boolean required) throws IOException {
				template.write(pk, v);
			}

			@Override
			public T read(Unpacker u, T to) throws IOException {
				return null;
			}

			@Override
			public T read(Unpacker u, T to, boolean required) throws IOException {
				return null;
			}

		});
	}

	@FunctionalInterface
	public interface SimpleTemplate<T> {

		public void write(Packer pk, T v) throws IOException;

	}

}