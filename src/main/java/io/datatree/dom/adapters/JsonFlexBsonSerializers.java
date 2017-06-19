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

import static io.datatree.dom.adapters.JsonFlex.addSerializer;

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

import flexjson.transformer.TypeTransformerMap;
import io.datatree.dom.BASE64;
import io.datatree.dom.Config;
import io.datatree.dom.converters.DataConverterRegistry;

/**
 * <b>FLEXJSON BSON EXTENSIONS</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class JsonFlexBsonSerializers implements Consumer<TypeTransformerMap> {

	@Override
	public void accept(TypeTransformerMap map) {

		addSerializer(map, BsonBoolean.class, (value, ctx) -> {
			ctx.write(Boolean.toString(value.getValue()));
		});

		addSerializer(map, BsonDateTime.class, (value, ctx) -> {
			if (Config.USE_TIMESTAMPS) {
				ctx.writeQuoted(DataConverterRegistry.convert(String.class, new Date(value.getValue())));
			} else {
				ctx.write(Long.toString(value.getValue()));
			}
		});

		addSerializer(map, BsonDouble.class, (value, ctx) -> {
			ctx.write(Double.toString(value.getValue()));
		});

		addSerializer(map, BsonInt32.class, (value, ctx) -> {
			ctx.write(Integer.toString(value.getValue()));
		});

		addSerializer(map, BsonInt64.class, (value, ctx) -> {
			ctx.write(Long.toString(value.getValue()));
		});

		addSerializer(map, BsonNull.class, (value, ctx) -> {
			ctx.write("null");
		});

		addSerializer(map, BsonRegularExpression.class, (value, ctx) -> {
			ctx.writeQuoted(value.getPattern());
		});

		addSerializer(map, BsonString.class, (value, ctx) -> {
			ctx.writeQuoted(value.getValue());
		});

		addSerializer(map, BsonTimestamp.class, (value, ctx) -> {
			if (Config.USE_TIMESTAMPS) {
				ctx.writeQuoted(DataConverterRegistry.convert(String.class, new Date(value.getTime() * 1000L)));
			} else {
				ctx.write(Integer.toString(value.getTime()));
			}
		});

		addSerializer(map, BsonUndefined.class, (value, ctx) -> {
			ctx.write("null");
		});

		addSerializer(map, Binary.class, (value, ctx) -> {
			ctx.writeQuoted(BASE64.encode(value.getData()));
		});

		addSerializer(map, Code.class, (value, ctx) -> {
			ctx.writeQuoted(value.getCode());
		});

		addSerializer(map, Decimal128.class, (value, ctx) -> {
			ctx.write(value.bigDecimalValue().toPlainString());
		});

		addSerializer(map, ObjectId.class, (value, ctx) -> {
			ctx.writeQuoted(value.toHexString());
		});

		addSerializer(map, Symbol.class, (value, ctx) -> {
			ctx.writeQuoted(value.getSymbol());
		});

	}

}