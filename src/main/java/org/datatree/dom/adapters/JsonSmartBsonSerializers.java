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

import static org.datatree.dom.adapters.JsonSmart.addSerializer;

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

import net.minidev.json.reader.JsonWriter;

/**
 * <b>JSON-SMART BSON EXTENSIONS</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class JsonSmartBsonSerializers implements Consumer<JsonWriter> {

	@Override
	public void accept(JsonWriter writer) {
	
		addSerializer(writer, BsonBoolean.class, (value, out) -> {
			out.append(Boolean.toString(value.getValue()));
		});
		
		addSerializer(writer, BsonDateTime.class, (value, out) -> {
			if (Config.USE_TIMESTAMPS) {
				out.append('\"');
				out.append(DataConverterRegistry.convert(String.class, new Date(value.getValue())));
				out.append('\"');
			} else {
				out.append(Long.toString(value.getValue()));
			}
		});
		
		addSerializer(writer, BsonDouble.class, (value, out) -> {
			out.append(Double.toString(value.getValue()));
		});
		
		addSerializer(writer, BsonInt32.class, (value, out) -> {
			out.append(Integer.toString(value.getValue()));
		});
		
		addSerializer(writer, BsonInt64.class, (value, out) -> {
			out.append(Long.toString(value.getValue()));
		});
		
		addSerializer(writer, BsonNull.class, (value, out) -> {
			out.append("null");
		});
		
		addSerializer(writer, BsonRegularExpression.class, (value, out) -> {
			out.append('\"');
			out.append(value.getPattern());
			out.append('\"');
		});
		
		addSerializer(writer, BsonString.class, (value, out) -> {
			out.append('\"');
			out.append(value.getValue());
			out.append('\"');
		});
		
		addSerializer(writer, BsonTimestamp.class, (value, out) -> {
			if (Config.USE_TIMESTAMPS) {
				out.append('\"');
				out.append(DataConverterRegistry.convert(String.class, new Date(value.getTime() * 1000L)));
				out.append('\"');
			} else {
				out.append(Integer.toString(value.getTime()));
			}
		});
		
		addSerializer(writer, BsonUndefined.class, (value, out) -> {
			out.append("null");
		});
		
		addSerializer(writer, Binary.class, (value, out) -> {
			out.append('\"');
			out.append(BASE64.encode(value.getData()));
			out.append('\"');
		});
		
		addSerializer(writer, Code.class, (value, out) -> {
			out.append('\"');
			out.append(value.getCode());
			out.append('\"');
		});
		
		addSerializer(writer, Decimal128.class, (value, out) -> {
			out.append(value.bigDecimalValue().toPlainString());
		});
		
		addSerializer(writer, ObjectId.class, (value, out) -> {
			out.append('\"');
			out.append(value.toHexString());
			out.append('\"');
		});
		
		addSerializer(writer, Symbol.class, (value, out) -> {
			out.append('\"');
			out.append(value.getSymbol());
			out.append('\"');
		});
		
	}

}