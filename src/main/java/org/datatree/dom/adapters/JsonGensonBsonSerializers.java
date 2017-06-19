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

import static org.datatree.dom.adapters.JsonGenson.addSerializer;

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

import com.owlike.genson.GensonBuilder;

/**
 * <b>GENSON BSON EXTENSIONS</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class JsonGensonBsonSerializers implements Consumer<GensonBuilder> {

	@Override
	public void accept(GensonBuilder builder) {

		addSerializer(builder, BsonBoolean.class, (value, writer, ctx) -> {
			writer.writeBoolean(value.getValue());
		});

		addSerializer(builder, BsonDateTime.class, (value, writer, ctx) -> {
			if (Config.USE_TIMESTAMPS) {
				writer.writeString(DataConverterRegistry.convert(String.class, new Date(value.getValue())));
			} else {
				writer.writeNumber(value.getValue());
			}
		});

		addSerializer(builder, BsonDouble.class, (value, writer, ctx) -> {
			writer.writeNumber(value.getValue());
		});

		addSerializer(builder, BsonInt32.class, (value, writer, ctx) -> {
			writer.writeNumber(value.getValue());
		});

		addSerializer(builder, BsonInt64.class, (value, writer, ctx) -> {
			writer.writeNumber(value.getValue());
		});

		addSerializer(builder, BsonNull.class, (value, writer, ctx) -> {
			writer.writeNull();
		});

		addSerializer(builder, BsonRegularExpression.class, (value, writer, ctx) -> {
			writer.writeString(value.getPattern());
		});

		addSerializer(builder, BsonString.class, (value, writer, ctx) -> {
			writer.writeString(value.getValue());
		});

		addSerializer(builder, BsonTimestamp.class, (value, writer, ctx) -> {
			if (Config.USE_TIMESTAMPS) {
				writer.writeString(DataConverterRegistry.convert(String.class, new Date(value.getTime() * 1000L)));
			} else {
				writer.writeNumber(value.getTime());
			}
		});

		addSerializer(builder, BsonUndefined.class, (value, writer, ctx) -> {
			writer.writeNull();
		});

		addSerializer(builder, Binary.class, (value, writer, ctx) -> {
			writer.writeString(BASE64.encode(value.getData()));
		});

		addSerializer(builder, Code.class, (value, writer, ctx) -> {
			writer.writeString(value.getCode());
		});

		addSerializer(builder, Decimal128.class, (value, writer, ctx) -> {
			writer.writeNumber(value.bigDecimalValue());
		});

		addSerializer(builder, ObjectId.class, (value, writer, ctx) -> {
			writer.writeString(value.toHexString());
		});

		addSerializer(builder, Symbol.class, (value, writer, ctx) -> {
			writer.writeString(value.getSymbol());
		});

	}

}