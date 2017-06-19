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

import static io.datatree.dom.adapters.JsonFast.addSerializer;

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

import com.alibaba.fastjson.serializer.SerializeConfig;

import io.datatree.dom.BASE64;
import io.datatree.dom.Config;
import io.datatree.dom.converters.DataConverterRegistry;

/**
 * <b>FASTJSON BSON EXTENSIONS</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class JsonFastBsonSerializers implements Consumer<SerializeConfig> {

	@Override
	public void accept(SerializeConfig config) {

		addSerializer(config, BsonBoolean.class, (value, serializer) -> {
			serializer.write(value.getValue());
		});

		addSerializer(config, BsonDateTime.class, (value, serializer) -> {
			if (Config.USE_TIMESTAMPS) {
				serializer.write(DataConverterRegistry.convert(String.class, new Date(value.getValue())));
			} else {
				serializer.write(value.getValue());
			}
		});

		addSerializer(config, BsonDouble.class, (value, serializer) -> {
			serializer.write(value.getValue());
		});

		addSerializer(config, BsonInt32.class, (value, serializer) -> {
			serializer.write(value.getValue());
		});

		addSerializer(config, BsonInt64.class, (value, serializer) -> {
			serializer.write(value.getValue());
		});

		addSerializer(config, BsonNull.class, (value, serializer) -> {
			serializer.writeNull();
		});

		addSerializer(config, BsonRegularExpression.class, (value, serializer) -> {
			serializer.write(value.getPattern());
		});

		addSerializer(config, BsonString.class, (value, serializer) -> {
			serializer.write(value.getValue());
		});

		addSerializer(config, BsonTimestamp.class, (value, serializer) -> {
			if (Config.USE_TIMESTAMPS) {
				serializer.write(DataConverterRegistry.convert(String.class, new Date(value.getTime() * 1000L)));
			} else {
				serializer.write(value.getTime());
			}
		});

		addSerializer(config, BsonUndefined.class, (value, serializer) -> {
			serializer.writeNull();
		});

		addSerializer(config, Binary.class, (value, serializer) -> {
			serializer.write(BASE64.encode(value.getData()));
		});

		addSerializer(config, Code.class, (value, serializer) -> {
			serializer.write(value.getCode());
		});

		addSerializer(config, Decimal128.class, (value, serializer) -> {
		serializer.write(value.bigDecimalValue());
		});

		addSerializer(config, ObjectId.class, (value, serializer) -> {
			serializer.write(value.toHexString());
		});

		addSerializer(config, Symbol.class, (value, serializer) -> {
			serializer.write(value.getSymbol());
		});

	}

}