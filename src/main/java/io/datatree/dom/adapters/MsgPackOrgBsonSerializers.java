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

import static io.datatree.dom.adapters.MsgPackOrg.addSerializer;

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
import org.msgpack.MessagePack;

import io.datatree.dom.BASE64;

/**
 * <b>ORG.MSGPACK BSON EXTENSIONS</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class MsgPackOrgBsonSerializers implements Consumer<MessagePack> {

	@Override
	public void accept(MessagePack mapper) {

		addSerializer(mapper, BsonBoolean.class, (packer, value) -> {
			packer.write(value.getValue());
		});

		addSerializer(mapper, BsonDateTime.class, (packer, value) -> {
			packer.write(value.getValue());
		});

		addSerializer(mapper, BsonDouble.class, (packer, value) -> {
			packer.write(value.getValue());
		});

		addSerializer(mapper, BsonInt32.class, (packer, value) -> {
			packer.write(value.getValue());
		});

		addSerializer(mapper, BsonInt64.class, (packer, value) -> {
			packer.write(value.getValue());
		});

		addSerializer(mapper, BsonNull.class, (packer, value) -> {
			packer.writeNil();
		});

		addSerializer(mapper, BsonRegularExpression.class, (packer, value) -> {
			packer.write(value.getPattern());
		});

		addSerializer(mapper, BsonString.class, (packer, value) -> {
			packer.write(value.getValue());
		});

		addSerializer(mapper, BsonTimestamp.class, (packer, value) -> {
			packer.write(value.getTime() * 1000L);
		});

		addSerializer(mapper, BsonUndefined.class, (packer, value) -> {
			packer.writeNil();
		});

		addSerializer(mapper, Binary.class, (packer, value) -> {
			packer.write(BASE64.encode(value.getData()));
		});

		addSerializer(mapper, Code.class, (packer, value) -> {
			packer.write(value.getCode());
		});

		addSerializer(mapper, Decimal128.class, (packer, value) -> {
			packer.write(value.bigDecimalValue());
		});

		addSerializer(mapper, ObjectId.class, (packer, value) -> {
			packer.write(value.toHexString());
		});

		addSerializer(mapper, Symbol.class, (packer, value) -> {
			packer.write(value.getSymbol());
		});

	}

}