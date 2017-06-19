/**
 * This software is licensed under the Apache 2 license, quoted below.<br>
 * <br>
 * Copyright 2017 Andras Berkes [andras.berkes@programmer.net]<br>
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

import java.util.Date;

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
 * <b>JTOML BSON EXTENSIONS</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class TomlJtomlBsonSerializers extends TomlJtomlJavaSerializers {

	public TomlJtomlBsonSerializers() {

		converters.put(BsonBoolean.class, (Object o) -> {
			return Boolean.toString(((BsonBoolean) o).getValue());
		});

		converters.put(BsonDateTime.class, (Object o) -> {
			if (Config.USE_TIMESTAMPS) {
				return quotedString(
						DataConverterRegistry.convert(String.class, new Date(((BsonDateTime) o).getValue())));
			}
			return Long.toString(((BsonDateTime) o).getValue());
		});

		converters.put(BsonDouble.class, (Object o) -> {
			return Double.toString(((BsonDouble) o).getValue());
		});

		converters.put(BsonInt32.class, (Object o) -> {
			return Integer.toString(((BsonInt32) o).getValue());
		});

		converters.put(BsonInt64.class, (Object o) -> {
			return Long.toString(((BsonInt64) o).getValue());
		});

		converters.put(BsonNull.class, (Object o) -> {
			return "null";
		});

		converters.put(BsonRegularExpression.class, (Object o) -> {
			return quotedString(((BsonRegularExpression) o).getPattern());
		});

		converters.put(BsonString.class, (Object o) -> {
			return quotedString(((BsonString) o).getValue());
		});

		converters.put(BsonTimestamp.class, (Object o) -> {
			if (Config.USE_TIMESTAMPS) {
				return quotedString(
						DataConverterRegistry.convert(String.class, new Date(((BsonTimestamp) o).getTime() * 1000L)));
			}
			return Long.toString(((BsonTimestamp) o).getTime());
		});

		converters.put(BsonUndefined.class, (Object o) -> {
			return "null";
		});

		converters.put(Binary.class, (Object o) -> {
			return quotedString(BASE64.encode(((Binary) o).getData()));
		});

		converters.put(Code.class, (Object o) -> {
			return quotedString(((Code) o).getCode());
		});

		converters.put(Decimal128.class, (Object o) -> {
			return ((Decimal128) o).bigDecimalValue().toPlainString();
		});

		converters.put(ObjectId.class, (Object o) -> {
			return quotedString(((ObjectId) o).toHexString());
		});

		converters.put(Symbol.class, (Object o) -> {
			return quotedString(((Symbol) o).getSymbol());
		});

	}

}