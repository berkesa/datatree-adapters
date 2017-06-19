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

import static io.datatree.dom.adapters.JsonBoon.addSerializer;

import java.util.Date;
import java.util.function.Consumer;

import org.boon.json.JsonSerializerFactory;
import org.boon.json.serializers.CustomObjectSerializer;
import org.boon.json.serializers.JsonSerializerInternal;
import org.boon.primitive.CharBuf;
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

import io.datatree.dom.Config;
import io.datatree.dom.converters.DataConverterRegistry;

/**
 * <b>BOON BSON EXTENSIONS</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class JsonBoonBsonSerializers implements Consumer<JsonSerializerFactory> {

	@Override
	public void accept(JsonSerializerFactory factory) {

		addSerializer(factory, BsonBoolean.class, (instance, builder) -> {
			builder.addBoolean(instance.getValue());
		});

		addSerializer(factory, BsonDateTime.class, (instance, builder) -> {
			if (Config.USE_TIMESTAMPS) {
				builder.addJsonEscapedString(
						DataConverterRegistry.convert(String.class, new Date(instance.getValue())).toCharArray());
			} else {
				builder.addLong(instance.getValue());
			}
		});

		addSerializer(factory, BsonDouble.class, (instance, builder) -> {
			builder.addDouble(instance.getValue());
		});

		addSerializer(factory, BsonInt32.class, (instance, builder) -> {
			builder.addInt(instance.getValue());
		});

		addSerializer(factory, BsonInt64.class, (instance, builder) -> {
			builder.addLong(instance.getValue());	
		});

		addSerializer(factory, BsonNull.class, (instance, builder) -> {
			builder.addNull();
		});

		addSerializer(factory, BsonRegularExpression.class, (instance, builder) -> {
			builder.addJsonEscapedString(instance.getPattern().toCharArray());
		});

		addSerializer(factory, BsonString.class, (instance, builder) -> {
			builder.addJsonEscapedString(instance.getValue().toCharArray());
		});

		addSerializer(factory, BsonTimestamp.class, (instance, builder) -> {
			if (Config.USE_TIMESTAMPS) {
				builder.addJsonEscapedString(DataConverterRegistry
						.convert(String.class, new Date(instance.getTime() * 1000L)).toCharArray());
			} else {
				builder.addInt(instance.getTime());
			}
		});

		addSerializer(factory, BsonUndefined.class, (instance, builder) -> {
			builder.addNull();
		});

		addSerializer(factory, Code.class, (instance, builder) -> {
			builder.addJsonEscapedString(instance.getCode().toCharArray());
		});

		addSerializer(factory, Decimal128.class, (instance, builder) -> {
			builder.addBigDecimal(instance.bigDecimalValue());
		});

		addSerializer(factory, ObjectId.class, (instance, builder) -> {
			builder.addQuoted(instance.toHexString());
		});

		addSerializer(factory, Symbol.class, (instance, builder) -> {
			builder.addJsonEscapedString(instance.getSymbol().toCharArray());
		});

		// Serialize BSON binaries in Boon style (as array, not BASE64)
		factory.addTypeSerializer(Binary.class, new CustomObjectSerializer<Binary>() {

			@Override
			public Class<Binary> type() {
				return Binary.class;
			}

			@Override
			public void serializeObject(JsonSerializerInternal serializer, Binary instance, CharBuf builder) {
				serializer.serializeArray(instance.getData(), builder);
			}

		});

	}

}