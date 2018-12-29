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

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;

import io.datatree.dom.Priority;

/**
 * <b>JACKSON YAML ADAPTER</b><br>
 * <br>
 * Description: SnakeYAML API via Jackson API.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.fasterxml.jackson.dataformat/
 * jackson-dataformat-yaml<br>
 * compile group: 'com.fasterxml.jackson.dataformat', name:
 * 'jackson-dataformat-yaml', version: '2.9.8'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one YAML implementation on classpath, the
 * preferred implementation is adjustable with the following System Properties.
 * If there is only one (eg. only the "jackson-dataformat-yaml")
 * implementation on the classpath, this step is NOT necessary, the DataTree API
 * will use this implementation automatically.<br>
 * <br>
 * -Ddatatree.yaml.reader=io.datatree.dom.adapters.YamlJackson<br>
 * -Ddatatree.yaml.writer=io.datatree.dom.adapters.YamlJackson<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * YamlJackson yaml = new YamlJackson();<br>
 * TreeReaderRegistry.setReader("yaml", yaml);<br>
 * TreeWriterRegistry.setWriter("yaml", yaml);<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputString, "yaml");<br>
 * String outputString = node.toString("yaml");<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "YamlJackson");<br>
 * String outputString = node.toString("YamlJackson");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(20)
public class YamlJackson extends AbstractJacksonTextAdapter {

	// --- NAME OF THE FORMAT ---
	
	@Override
	public String getFormat() {
		return "yaml";
	}
	
	// --- CONSTRUCTOR ---

	public YamlJackson() {
		super(new YAMLMapper());
		
		// Install MongoDB / BSON serializers
		// (Using JSON serializers)
		tryToAddSerializers("io.datatree.dom.adapters.JsonJacksonBsonSerializers", mapper, prettyMapper);
	}
	
}
