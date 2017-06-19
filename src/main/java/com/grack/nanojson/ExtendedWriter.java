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
package com.grack.nanojson;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.Map;

import io.datatree.dom.converters.DataConverterRegistry;

/**
 * Extended writer for NanoJSON API.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class ExtendedWriter extends JsonWriterBase<ExtendedWriter> {

	public ExtendedWriter(String indent) {
		super(new StringBuilder(256), indent);
	}

	public final String done() {
		super.doneInternal();
		return appendable.toString();
	}

	@Override
	public ExtendedWriter value(String key, Object o) {
		if (o == null)
			return nul(key);
		else if (o instanceof String)
			return value(key, (String) o);
		else if (o instanceof Boolean)
			return value(key, (boolean) (Boolean) o);
		else if (o instanceof Map)
			return object(key, (Map<?, ?>) o);
		else if (o instanceof byte[])
			return value(key, DataConverterRegistry.convert(String.class, o));
		else if (o instanceof Number)
			return value(key, (Number) o);
		else if (o instanceof Collection)
			return array(key, (Collection<?>) o);
		else if (o.getClass().isArray()) {
			int length = Array.getLength(o);
			array(key);
			for (int i = 0; i < length; i++)
				value(Array.get(o, i));
			return end();
		}
		
		// Serialize MongoDB / BSON values
		String txt = DataConverterRegistry.convert(String.class, o);
		if (txt == null) {
			return value(key, (String) null);
		}
		boolean isTrue = "true".equals(txt);
		if (isTrue || "false".equals(txt)) {
			return value(key, isTrue);
		}
		boolean isNumber = true;
		for (char c : txt.toCharArray()) {
			if (!Character.isDigit(c) && c != '.') {
				isNumber = false;
				break;
			}
		}
		if (isNumber) {
			return value(key, new BigDecimal(txt));
		}
		return value(key, txt);
	}

	@Override
	public ExtendedWriter value(Object o) {
		if (o == null)
			return nul();
		else if (o instanceof String)
			return value((String) o);
		else if (o instanceof Boolean)
			return value((boolean) (Boolean) o);
		else if (o instanceof Map)
			return object((Map<?, ?>) o);
		else if (o instanceof byte[])
			return value(DataConverterRegistry.convert(String.class, o));
		else if (o instanceof Number)
			return value(((Number) o));
		else if (o instanceof Collection)
			return array((Collection<?>) o);
		else if (o.getClass().isArray()) {
			int length = Array.getLength(o);
			array();
			for (int i = 0; i < length; i++)
				value(Array.get(o, i));
			return end();
		}
		return value(DataConverterRegistry.convert(String.class, o));
	}

}