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

import org.datatree.dom.Priority;

/**
 * <b>OPENCSV TSV ADAPTER</b><br>
 * <br>
 * Description: TSV format (based on OpenCSV writer).<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/net.sf.opencsv/opencsv<br>
 * compile group: 'net.sf.opencsv', name: 'opencsv', version: '2.3'<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputString, "tsv");<br>
 * String outputString = node.toString("tsv");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(10)
public class TsvOpenCSV extends CsvOpenCSV {

	// --- NAME OF THE FORMAT ---
	
	@Override
	public String getFormat() {
		return "tsv";
	}
	
	// --- COSNTRUCTOR ---
	
	public TsvOpenCSV() {
		defaultSeparatorChar = '\t';
	}
	
}