/**
 * This software is licensed under the Apache 2 license, quoted below.<br>
 * <br>
 * Copyright 2020 Andras Berkes [andras.berkes@programmer.net]<br>
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

import static io.datatree.dom.adapters.JsonJsoniter.addSerializer;

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

import io.datatree.dom.BASE64;
import io.datatree.dom.Config;
import io.datatree.dom.converters.DataConverterRegistry;

/**
 * <b>JSON ITERATOR BSON EXTENSIONS</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class JsonJsoniterBsonSerializers implements Consumer<Object> {

	@Override
	public void accept(Object t) {
		addSerializer(BsonBoolean.class, (value, stream) -> {
			stream.writeVal(Boolean.toString(value.getValue()));
		});

		addSerializer(BsonDateTime.class, (value, stream) -> {
			if (Config.USE_TIMESTAMPS) {
				stream.writeVal(DataConverterRegistry.convert(String.class, new Date(value.getValue())));
			} else {
				stream.writeVal(Long.toString(value.getValue()));
			}
		});

		addSerializer(BsonDouble.class, (value, stream) -> {
			stream.writeVal(Double.toString(value.getValue()));
		});

		addSerializer(BsonInt32.class, (value, stream) -> {
			stream.writeVal(Integer.toString(value.getValue()));
		});

		addSerializer(BsonInt64.class, (value, stream) -> {
			stream.writeVal(Long.toString(value.getValue()));
		});

		addSerializer(BsonNull.class, (value, stream) -> {
			stream.writeVal("null");
		});

		addSerializer(BsonRegularExpression.class, (value, stream) -> {
			stream.writeVal(value.getPattern());
		});

		addSerializer(BsonString.class, (value, stream) -> {
			stream.writeVal(value.getValue());
		});

		addSerializer(BsonTimestamp.class, (value, stream) -> {
			if (Config.USE_TIMESTAMPS) {
				stream.writeVal(DataConverterRegistry.convert(String.class, new Date(value.getTime() * 1000L)));
			} else {
				stream.writeVal(Integer.toString(value.getTime()));
			}
		});

		addSerializer(BsonUndefined.class, (value, stream) -> {
			stream.writeVal("null");
		});

		addSerializer(Binary.class, (value, stream) -> {
			stream.writeVal(BASE64.encode(value.getData()));
		});

		addSerializer(Code.class, (value, stream) -> {
			stream.writeVal(value.getCode());
		});

		addSerializer(Decimal128.class, (value, stream) -> {
			stream.writeVal(value.bigDecimalValue().toPlainString());
		});

		addSerializer(ObjectId.class, (value, stream) -> {
			stream.writeVal(value.toHexString());
		});

		addSerializer(Symbol.class, (value, stream) -> {
			stream.writeVal(value.getSymbol());
		});
	}

}
