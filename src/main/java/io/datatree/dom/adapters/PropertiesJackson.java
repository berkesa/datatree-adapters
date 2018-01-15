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
package io.datatree.dom.adapters;

import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper;

import io.datatree.dom.Priority;

/**
 * <b>JACKSON JAVA PROPERTIES ADAPTER</b><br>
 * <br>
 * Description: Java Properties format (Jackson API).<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/
 * jackson-dataformat-properties<br>
 * compile group: 'com.fasterxml.jackson.dataformat', name:
 * 'jackson-dataformat-properties', version: '2.9.3'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one "properties" implementation on classpath, the
 * preferred implementation is adjustable with the following System Properties.
 * <br>
 * <br>
 * -Ddatatree.properties.reader=io.datatree.dom.adapters.PropertiesJackson <br>
 * -Ddatatree.properties.writer=io.datatree.dom.adapters.PropertiesJackson <br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * PropertiesJackson pj = new PropertiesJackson();<br>
 * TreeReaderRegistry.setReader("properties", pj);<br>
 * TreeWriterRegistry.setWriter("properties", pj);<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputString, "properties");<br>
 * String outputString = node.toString("properties");<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "PropertiesJackson");<br>
 * String outputString = node.toString("PropertiesJackson");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(10)
public class PropertiesJackson extends AbstractJacksonTextAdapter {

	// --- NAME OF THE FORMAT ---

	@Override
	public String getFormat() {
		return "properties";
	}

	// --- CONSTRUCTOR ---

	public PropertiesJackson() {
		super(new JavaPropsMapper());

		// Install MongoDB / BSON serializers
		// (Using JSON serializers)
		tryToAddSerializers("io.datatree.dom.adapters.JsonJacksonBsonSerializers", mapper, prettyMapper);
	}

}