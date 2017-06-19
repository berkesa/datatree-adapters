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

import static org.datatree.dom.adapters.YamlSnakeYaml.addSerializer;

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
import org.datatree.dom.adapters.YamlSnakeYaml.ExtensibleRepresenter;
import org.datatree.dom.converters.DataConverterRegistry;

/**
 * <b>SNAKEYAML BSON EXTENSIONS</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class YamlSnakeYamlBsonSerializers implements Consumer<ExtensibleRepresenter> {

	@Override
	public void accept(ExtensibleRepresenter representer) {

		addSerializer(representer, BsonBoolean.class, (value) -> {
			return Boolean.toString(value.getValue());
		});

		addSerializer(representer, BsonDateTime.class, (value) -> {
			if (Config.USE_TIMESTAMPS) {
				return DataConverterRegistry.convert(String.class, new Date(value.getValue()));
			}
			return Long.toString(value.getValue());
		});

		addSerializer(representer, BsonDouble.class, (value) -> {
			return Double.toString(value.getValue());
		});

		addSerializer(representer, BsonInt32.class, (value) -> {
			return Integer.toString(value.getValue());
		});

		addSerializer(representer, BsonInt64.class, (value) -> {
			return Long.toString(value.getValue());
		});

		addSerializer(representer, BsonNull.class, (value) -> {
			return null;
		});

		addSerializer(representer, BsonRegularExpression.class, (value) -> {
			return value.getPattern();
		});

		addSerializer(representer, BsonString.class, (value) -> {
			return value.getValue();
		});

		addSerializer(representer, BsonTimestamp.class, (value) -> {
			if (Config.USE_TIMESTAMPS) {
				return DataConverterRegistry.convert(String.class, new Date(value.getTime() * 1000L));
			}
			return Integer.toString(value.getTime());
		});

		addSerializer(representer, BsonUndefined.class, (value) -> {
			return null;
		});

		addSerializer(representer, Binary.class, (value) -> {
			return BASE64.encode(value.getData());
		});

		addSerializer(representer, Code.class, (value) -> {
			return value.getCode();
		});

		addSerializer(representer, Decimal128.class, (value) -> {
			return value.bigDecimalValue().toPlainString();
		});

		addSerializer(representer, ObjectId.class, (value) -> {
			return value.toHexString();
		});

		addSerializer(representer, Symbol.class, (value) -> {
			return value.getSymbol();
		});

	}

}