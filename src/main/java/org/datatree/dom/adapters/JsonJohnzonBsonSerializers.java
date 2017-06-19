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

import static org.datatree.dom.adapters.JsonJohnzon.addSerializer;

import java.math.BigDecimal;
import java.util.Date;
import java.util.function.Consumer;

import org.apache.johnzon.mapper.MapperBuilder;
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

/**
 * <b>APACHE JOHNZON BSON EXTENSIONS</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class JsonJohnzonBsonSerializers implements Consumer<MapperBuilder> {

	@Override
	public void accept(MapperBuilder builder) {

		addSerializer(builder, BsonBoolean.class, Boolean.class, (value) -> {
			return value.getValue();
		});

		if (Config.USE_TIMESTAMPS) {
			addSerializer(builder, BsonDateTime.class, String.class, (value) -> {
				return DataConverterRegistry.convert(String.class, new Date(value.getValue()));
			});
		} else {
			addSerializer(builder, BsonDateTime.class, Long.class, (value) -> {
				return value.getValue();
			});
		}

		addSerializer(builder, BsonDouble.class, Double.class, (value) -> {
			return value.getValue();
		});

		addSerializer(builder, BsonInt32.class, Integer.class, (value) -> {
			return value.getValue();
		});

		addSerializer(builder, BsonInt64.class, Long.class, (value) -> {
			return value.getValue();
		});

		addSerializer(builder, BsonNull.class, Object.class, (value) -> {

			// Johnzon fails from null values
			return "null";
		});

		addSerializer(builder, BsonRegularExpression.class, String.class, (value) -> {
			return value.getPattern();
		});

		addSerializer(builder, BsonString.class, String.class, (value) -> {
			return value.getValue();
		});

		if (Config.USE_TIMESTAMPS) {
			addSerializer(builder, BsonTimestamp.class, String.class, (value) -> {
				return DataConverterRegistry.convert(String.class, new Date(value.getTime() * 1000L));
			});
		} else {
			addSerializer(builder, BsonTimestamp.class, Integer.class, (value) -> {
				return value.getTime();
			});
		}

		addSerializer(builder, BsonUndefined.class, String.class, (value) -> {

			// Johnzon fails from null values
			return "null";
		});

		addSerializer(builder, Binary.class, String.class, (value) -> {
			return BASE64.encode(value.getData());
		});

		addSerializer(builder, Code.class, String.class, (value) -> {
			return value.getCode();
		});

		addSerializer(builder, Decimal128.class, BigDecimal.class, (value) -> {
			return value.bigDecimalValue();
		});

		addSerializer(builder, ObjectId.class, String.class, (value) -> {
			return value.toHexString();
		});

		addSerializer(builder, Symbol.class, String.class, (value) -> {
			return value.getSymbol();
		});

	}

}