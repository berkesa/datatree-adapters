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
package org.datatree.dom.adapters;

import static org.datatree.dom.adapters.JsonJodd.addSerializer;

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
import org.datatree.dom.BASE64;
import org.datatree.dom.Config;
import org.datatree.dom.converters.DataConverterRegistry;

import jodd.json.JsonSerializer;

/**
 * <b>JODD BSON EXTENSIONS</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class JsonJoddBsonSerializers implements Consumer<JsonSerializer> {

	@Override
	public void accept(JsonSerializer serializer) {

		addSerializer(serializer, BsonBoolean.class, (value, ctx) -> {
			ctx.write(Boolean.toString(value.getValue()));
		});

		addSerializer(serializer, BsonDateTime.class, (value, ctx) -> {
			if (Config.USE_TIMESTAMPS) {
				ctx.writeString(DataConverterRegistry.convert(String.class, new Date(value.getValue())));
			} else {
				ctx.writeNumber(value.getValue());
			}
		});

		addSerializer(serializer, BsonDouble.class, (value, ctx) -> {
			ctx.writeNumber(value.getValue());
		});

		addSerializer(serializer, BsonInt32.class, (value, ctx) -> {
			ctx.writeNumber(value.getValue());
		});

		addSerializer(serializer, BsonInt64.class, (value, ctx) -> {
			ctx.writeNumber(value.getValue());
		});

		addSerializer(serializer, BsonNull.class, (value, ctx) -> {
			ctx.write("null");
		});

		addSerializer(serializer, BsonRegularExpression.class, (value, ctx) -> {
			ctx.writeString(value.getPattern());
		});

		addSerializer(serializer, BsonString.class, (value, ctx) -> {
			ctx.writeString(value.getValue());
		});
		
		addSerializer(serializer, BsonTimestamp.class, (value, ctx) -> {
			if (Config.USE_TIMESTAMPS) {
				ctx.writeString(
						DataConverterRegistry.convert(String.class, new Date(value.getTime() * 1000L)));
			} else {
				ctx.writeNumber(value.getTime());
			}
		});

		addSerializer(serializer, BsonUndefined.class, (value, ctx) -> {
			ctx.write("null");
		});

		addSerializer(serializer, Binary.class, (value, ctx) -> {
			ctx.writeString(BASE64.encode(value.getData()));
		});

		addSerializer(serializer, Code.class, (value, ctx) -> {
			ctx.writeString(value.getCode());
		});

		addSerializer(serializer, Decimal128.class, (value, ctx) -> {
			ctx.writeNumber(value.bigDecimalValue());
		});

		addSerializer(serializer, ObjectId.class, (value, ctx) -> {
			ctx.writeString(value.toHexString());
		});

		addSerializer(serializer, Symbol.class, (value, ctx) -> {
			ctx.writeString(value.getSymbol());
		});
	}

}