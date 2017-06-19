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

import com.dslplatform.json.BoolConverter;
import com.dslplatform.json.DslJson;
import com.dslplatform.json.NumberConverter;
import com.dslplatform.json.StringConverter;

/**
 * <b>DSLJSON BSON EXTENSIONS</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class JsonDSLBsonSerializers implements Consumer<DslJson<Object>> {

	@Override
	public void accept(DslJson<Object> dslJson) {

		dslJson.registerWriter(BsonBoolean.class, (writer, value) -> {
			BoolConverter.serialize(value.getValue(), writer);
		});

		dslJson.registerWriter(BsonDateTime.class, (writer, value) -> {
			if (Config.USE_TIMESTAMPS) {
				StringConverter.serialize(DataConverterRegistry.convert(String.class, new Date(value.getValue())),
						writer);
			} else {
				NumberConverter.serialize(value.getValue(), writer);
			}
		});

		dslJson.registerWriter(BsonDouble.class, (writer, value) -> {
			NumberConverter.serialize(value.getValue(), writer);
		});

		dslJson.registerWriter(BsonInt32.class, (writer, value) -> {
			NumberConverter.serialize(value.getValue(), writer);
		});

		dslJson.registerWriter(BsonInt64.class, (writer, value) -> {
			NumberConverter.serialize(value.getValue(), writer);
		});

		dslJson.registerWriter(BsonNull.class, (writer, value) -> {
			writer.writeNull();
		});

		dslJson.registerWriter(BsonRegularExpression.class, (writer, value) -> {
			StringConverter.serializeNullable(value.getPattern(), writer);
		});

		dslJson.registerWriter(BsonString.class, (writer, value) -> {
			StringConverter.serializeNullable(value.getValue(), writer);
		});

		dslJson.registerWriter(BsonTimestamp.class, (writer, value) -> {
			if (Config.USE_TIMESTAMPS) {
				StringConverter.serialize(
						DataConverterRegistry.convert(String.class, new Date(value.getTime() * 1000L)), writer);
			} else {
				NumberConverter.serialize(value.getTime(), writer);
			}
		});

		dslJson.registerWriter(BsonUndefined.class, (writer, value) -> {
			writer.writeNull();
		});

		dslJson.registerWriter(Binary.class, (writer, value) -> {
			StringConverter.serialize(BASE64.encode(value.getData()), writer);
		});

		dslJson.registerWriter(Code.class, (writer, value) -> {
			StringConverter.serializeNullable(value.getCode(), writer);
		});

		dslJson.registerWriter(Decimal128.class, (writer, value) -> {
			NumberConverter.serialize(value.bigDecimalValue(), writer);
		});

		dslJson.registerWriter(ObjectId.class, (writer, value) -> {
			StringConverter.serialize(value.toHexString(), writer);
		});

		dslJson.registerWriter(Symbol.class, (writer, value) -> {
			StringConverter.serializeNullable(value.getSymbol(), writer);
		});

	}

}