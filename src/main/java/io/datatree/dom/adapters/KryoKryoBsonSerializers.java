/**
 * This software is licensed under the Apache 2 license, quoted below.<br>
 * <br>
 * Copyright 2017 Andras Berkes [andras.berkes@europe.com]<br>
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

import java.math.BigDecimal;
import java.util.function.Consumer;

import org.bson.BsonBoolean;
import org.bson.BsonDateTime;
import org.bson.BsonDouble;
import org.bson.BsonInt32;
import org.bson.BsonInt64;
import org.bson.BsonNull;
import org.bson.BsonRegularExpression;
import org.bson.BsonString;
import org.bson.BsonTimestamp;
import org.bson.BsonUndefined;
import org.bson.types.Binary;
import org.bson.types.Code;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.bson.types.Symbol;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * <b>BSON EXTENSIONS FOR KRYO</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class KryoKryoBsonSerializers implements Consumer<Kryo> {

	@Override
	public void accept(Kryo mapper) {
		
		mapper.register(BsonBoolean.class, new Serializer<BsonBoolean>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final BsonBoolean obj) {
				output.writeBoolean(obj.getValue());
			}

			@Override
			public final BsonBoolean read(final Kryo kryo, final Input input, final Class<BsonBoolean> clazz) {
				return new BsonBoolean(input.readBoolean());
			}
			
		});

		mapper.register(BsonDateTime.class, new Serializer<BsonDateTime>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final BsonDateTime obj) {
				output.writeLong(obj.getValue());
			}

			@Override
			public final BsonDateTime read(final Kryo kryo, final Input input, final Class<BsonDateTime> clazz) {
				return new BsonDateTime(input.readLong());
			}
			
		});
		
		mapper.register(BsonDouble.class, new Serializer<BsonDouble>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final BsonDouble obj) {
				output.writeDouble(obj.getValue());
			}

			@Override
			public final BsonDouble read(final Kryo kryo, final Input input, final Class<BsonDouble> clazz) {
				return new BsonDouble(input.readDouble());
			}
			
		});
		
		mapper.register(BsonInt32.class, new Serializer<BsonInt32>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final BsonInt32 obj) {
				output.writeInt(obj.getValue());
			}

			@Override
			public final BsonInt32 read(final Kryo kryo, final Input input, final Class<BsonInt32> clazz) {
				return new BsonInt32(input.readInt());
			}
			
		});
		
		mapper.register(BsonInt64.class, new Serializer<BsonInt64>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final BsonInt64 obj) {
				output.writeLong(obj.getValue());
			}

			@Override
			public final BsonInt64 read(final Kryo kryo, final Input input, final Class<BsonInt64> clazz) {
				return new BsonInt64(input.readLong());
			}
			
		});
		
		mapper.register(BsonNull.class, new Serializer<BsonNull>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final BsonNull obj) {
			}

			@Override
			public final BsonNull read(final Kryo kryo, final Input input, final Class<BsonNull> clazz) {
				return new BsonNull();
			}
			
		});
		
		mapper.register(BsonRegularExpression.class, new Serializer<BsonRegularExpression>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final BsonRegularExpression obj) {
				output.writeString(obj.getPattern());
			}

			@Override
			public final BsonRegularExpression read(final Kryo kryo, final Input input, final Class<BsonRegularExpression> clazz) {
				return new BsonRegularExpression(input.readString());
			}
			
		});
		
		mapper.register(BsonString.class, new Serializer<BsonString>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final BsonString obj) {
				output.writeString(obj.getValue());
			}

			@Override
			public final BsonString read(final Kryo kryo, final Input input, final Class<BsonString> clazz) {
				return new BsonString(input.readString());
			}
			
		});
		
		mapper.register(BsonTimestamp.class, new Serializer<BsonTimestamp>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final BsonTimestamp obj) {
				output.writeInt(obj.getTime());
				output.writeInt(obj.getInc());
			}

			@Override
			public final BsonTimestamp read(final Kryo kryo, final Input input, final Class<BsonTimestamp> clazz) {
				return new BsonTimestamp(input.readInt(), input.readInt());
			}
			
		});
		
		mapper.register(BsonUndefined.class, new Serializer<BsonUndefined>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final BsonUndefined obj) {
			}

			@Override
			public final BsonUndefined read(final Kryo kryo, final Input input, final Class<BsonUndefined> clazz) {
				return new BsonUndefined();
			}
			
		});
		
		mapper.register(Binary.class, new Serializer<Binary>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final Binary obj) {
				byte[] bytes = obj.getData();
				output.writeInt(bytes.length);
				output.writeBytes(bytes);
			}

			@Override
			public final Binary read(final Kryo kryo, final Input input, final Class<Binary> clazz) {
				int len = input.readInt();
				byte[] bytes = new byte[len];
				input.read(bytes, 0, len);
				return new Binary(bytes);
			}
			
		});
		
		mapper.register(Code.class, new Serializer<Code>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final Code obj) {
				output.writeString(obj.getCode());
			}

			@Override
			public final Code read(final Kryo kryo, final Input input, final Class<Code> clazz) {
				return new Code(input.readString());
			}
			
		});
		
		mapper.register(Decimal128.class, new Serializer<Decimal128>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final Decimal128 obj) {
				output.writeString(obj.bigDecimalValue().toPlainString());
			
			}

			@Override
			public final Decimal128 read(final Kryo kryo, final Input input, final Class<Decimal128> clazz) {
				return new Decimal128(new BigDecimal(input.readString()));
			}
			
		});
		
		mapper.register(ObjectId.class, new Serializer<ObjectId>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final ObjectId obj) {
				output.writeString(obj.toHexString());
			}

			@Override
			public final ObjectId read(final Kryo kryo, final Input input, final Class<ObjectId> clazz) {
				return new ObjectId(input.readString());
			}
			
		});
		
		mapper.register(Symbol.class, new Serializer<Symbol>() {

			@Override
			public final void write(final Kryo kryo, final Output output, final Symbol obj) {
				output.writeString(obj.getSymbol());
			}

			@Override
			public final Symbol read(final Kryo kryo, final Input input, final Class<Symbol> clazz) {
				return new Symbol(input.readString());
			}
			
		});
	}
	
}
