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

import static io.datatree.dom.adapters.BsonJackson.addSerializer;

import java.util.Date;
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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

/**
 * <b>JACKSON BSON EXTENSIONS</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class BsonJacksonBsonSerializers implements Consumer<ObjectMapper> {

	@Override
	public void accept(ObjectMapper mapper) {
		SimpleModule module = new SimpleModule();

		addSerializer(module, BsonBoolean.class, (value, gen) -> {
			gen.writeBoolean(value.getValue());
		});

		addSerializer(module, BsonDateTime.class, (value, gen) -> {
			gen.writeDateTime(new Date(value.getValue()));
		});

		addSerializer(module, BsonDouble.class, (value, gen) -> {
			gen.writeNumber(value.getValue());
		});

		addSerializer(module, BsonInt32.class, (value, gen) -> {
			gen.writeNumber(value.getValue());
		});

		addSerializer(module, BsonInt64.class, (value, gen) -> {
			gen.writeNumber(value.getValue());
		});

		addSerializer(module, BsonNull.class, (value, gen) -> {
			gen.writeNull();
		});

		addSerializer(module, BsonRegularExpression.class, (value, gen) -> {
			gen.writeString(value.getPattern());
		});

		addSerializer(module, BsonString.class, (value, gen) -> {
			gen.writeString(value.getValue());
		});

		addSerializer(module, BsonTimestamp.class, (value, gen) -> {
			gen.writeDateTime(new Date(value.getTime() * 1000L));
		});

		addSerializer(module, BsonUndefined.class, (value, gen) -> {
			gen.writeNull();
		});

		addSerializer(module, Binary.class, (value, gen) -> {
			gen.writeBinary(value.getData());
		});

		addSerializer(module, Code.class, (value, gen) -> {
			gen.writeString(value.getCode());
		});

		addSerializer(module, Decimal128.class, (value, gen) -> {
			gen.writeNumber(value.bigDecimalValue());
		});

		addSerializer(module, ObjectId.class, (value, gen) -> {
			gen.writeString(value.toHexString());
		});

		addSerializer(module, Symbol.class, (value, gen) -> {
			gen.writeString(value.getSymbol());
		});

		mapper.registerModule(module);
	}

}