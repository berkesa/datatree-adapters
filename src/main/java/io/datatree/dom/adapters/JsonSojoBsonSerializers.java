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

import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;
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
import net.sf.sojo.core.Converter;
import net.sf.sojo.core.ConverterInterceptor;

/**
 * <b>SOJO BSON EXTENSIONS</b><br>
 * <br>
 * Install serializers for BSON / MongoDB types (BsonInt64, ObjectID, etc.).
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class JsonSojoBsonSerializers implements Consumer<Converter> {

	@Override
	public void accept(Converter converter) {
		converter.addConverterInterceptor(new ConverterInterceptor() {

			@Override
			public final void onError(Exception exception) {
				
				// Do nothing
			}

			@Override
			public final Object beforeConvert(Object value, Class<?> to) {

				// InetAddress
				if (value instanceof InetAddress) {
					return ((InetAddress) value).getCanonicalHostName();
				}

				// UUID
				if (value instanceof UUID) {
					return ((UUID) value).toString();
				}

				// BSON types...
				if (value instanceof BsonBoolean) {
					return ((BsonBoolean) value).getValue();
				}

				if (value instanceof BsonDateTime) {

					// SOJO serializes Date object simply with its "toString"
					// method
					return new Date(((BsonDateTime) value).getValue());
				}

				if (value instanceof BsonDouble) {
					return ((BsonDouble) value).getValue();
				}

				if (value instanceof BsonInt32) {
					return ((BsonInt32) value).getValue();
				}

				if (value instanceof BsonInt64) {

					// Serialize longs as String
					return Long.toString(((BsonInt64) value).getValue());
				}

				if (value instanceof BsonNull) {
					return null;
				}

				if (value instanceof BsonRegularExpression) {
					return ((BsonRegularExpression) value).getPattern();
				}

				if (value instanceof BsonString) {
					return ((BsonString) value).getValue();
				}

				if (value instanceof BsonTimestamp) {

					// SOJO serializes Date object simply with its "toString"
					// method
					return new Date(((BsonTimestamp) value).getTime() * 1000L);
				}

				if (value instanceof BsonUndefined) {
					return null;
				}

				if (value instanceof Binary) {
					return BASE64.encode(((Binary) value).getData());
				}

				if (value instanceof Code) {
					return ((Code) value).getCode();
				}

				if (value instanceof Decimal128) {

					// Serialize BigDecimals as String
					return ((Decimal128) value).bigDecimalValue().toPlainString();
				}

				if (value instanceof ObjectId) {
					return ((ObjectId) value).toHexString();
				}

				if (value instanceof Symbol) {
					return ((Symbol) value).getSymbol();
				}

				return value;
			}

			@Override
			public final Object afterConvert(Object result, Class<?> to) {
				return result;
			}

		});
	}

}