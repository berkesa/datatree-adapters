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

import static org.datatree.dom.adapters.IonIon.addSerializer;

import java.util.Date;
import java.util.HashMap;
import java.util.function.Consumer;
import java.util.function.Function;

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

/**
 * <b>AMAZON ION BSON EXTENSIONS</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class IonIonBsonSerializers implements Consumer<HashMap<Class<?>, Function<Object, Object>>> {

	@Override
	public void accept(HashMap<Class<?>, Function<Object, Object>> converters) {
		
		addSerializer(converters, BsonBoolean.class, (value) -> {
			return value.getValue();
		});

		addSerializer(converters, BsonDateTime.class, (value) -> {
			return new Date(value.getValue());
		});

		addSerializer(converters, BsonDouble.class, (value) -> {
			return value.getValue();
		});

		addSerializer(converters, BsonInt32.class, (value) -> {
			return value.getValue();
		});

		addSerializer(converters, BsonInt64.class, (value) -> {
			return value.getValue();
		});

		addSerializer(converters, BsonNull.class, (value) -> {
			return null;
		});

		addSerializer(converters, BsonRegularExpression.class, (value) -> {
			return value.getPattern();
		});

		addSerializer(converters, BsonString.class, (value) -> {
			return value.getValue();
		});

		addSerializer(converters, BsonTimestamp.class, (value) -> {
			return new Date(value.getTime() * 1000L);
		});

		addSerializer(converters, BsonUndefined.class, (value) -> {
			return null;
		});

		addSerializer(converters, Binary.class, (value) -> {
			return value.getData();
		});

		addSerializer(converters, Code.class, (value) -> {
			return value.getCode();
		});

		addSerializer(converters, Decimal128.class, (value) -> {
			return value.bigDecimalValue();
		});

		addSerializer(converters, ObjectId.class, (value) -> {
			return value.toHexString();
		});

		addSerializer(converters, Symbol.class, (value) -> {
			return value.getSymbol();
		});
		
	}
	
}