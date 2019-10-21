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

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.opencsv.CSVParserBuilder;
import com.opencsv.CSVReader;
import com.opencsv.CSVReaderBuilder;
import com.opencsv.CSVWriter;
import com.opencsv.ICSVParser;

import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;
import io.datatree.dom.converters.DataConverterRegistry;

/**
 * <b>OPENCSV CSV ADAPTER</b><br>
 * <br>
 * Description: CSV format (OpenCSV API).<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/com.opencsv/opencsv<br>
 * compile group: 'com.opencsv', name: 'opencsv', version: '5.0'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one CSV implementation on classpath, the preferred
 * implementation is adjustable with the following System Properties. If there
 * is only one (eg. only the "opencsv") implementation on the classpath, this
 * step is NOT necessary, the DataTree API will use this implementation
 * automatically.<br>
 * <br>
 * -Ddatatree.csv.reader=io.datatree.dom.adapters.CsvOpenCSV<br>
 * -Ddatatree.csv.writer=io.datatree.dom.adapters.CsvOpenCSV<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * CsvOpenCSV csvOpenCSV = new CsvOpenCSV();<br>
 * TreeReaderRegistry.setReader("csv", csvOpenCSV);<br>
 * TreeWriterRegistry.setWriter("csv", csvOpenCSV);<br>
 * <br>
 * <b>Invoke serializer and deserializer:</b><br>
 * <br>
 * Tree node = new Tree(inputString, "csv");<br>
 * String outputString = node.toString("csv");<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "CsvOpenCSV");<br>
 * String outputString = node.toString("CsvOpenCSV");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(10)
public class CsvOpenCSV extends AbstractTextAdapter {

	// --- PARSING PROPERTIES ---

	public char defaultSeparatorChar = ICSVParser.DEFAULT_SEPARATOR;
	public char defaultQuoteChar = ICSVParser.DEFAULT_QUOTE_CHARACTER;
	public char defaultEscapeChar = ICSVParser.DEFAULT_ESCAPE_CHARACTER;
	public boolean ignoreQuotations = ICSVParser.DEFAULT_IGNORE_QUOTATIONS;
	public boolean defaultIgnoreLeadingWhiteSpace = ICSVParser.DEFAULT_IGNORE_LEADING_WHITESPACE;

	public int defaultSkipLines = CSVReader.DEFAULT_SKIP_LINES;
	
	// --- WRITING PROPERTIES ---

	public String defaultLineEnd = CSVWriter.DEFAULT_LINE_END;

	// --- NAME OF THE FORMAT ---

	@Override
	public String getFormat() {
		return "csv";
	}

	// --- IMPLEMENTED WRITER METHOD ---

	@SuppressWarnings({ "resource" })
	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		if (value == null) {
			return "";
		}
		StringWriter writer = new StringWriter(512);
		CSVWriter mapper = new CSVWriter(writer, defaultSeparatorChar, defaultQuoteChar, defaultEscapeChar,
				defaultLineEnd);
		Collection<?> lines = objectToCollection(value);
		if (lines != null) {
			final Iterator<?> iterator = lines.iterator();
			Collection<?> cells;
			String[] array;
			Object line;
			int index;
			while (iterator.hasNext()) {
				line = iterator.next();
				cells = objectToCollection(line);
				if (cells != null) {
					array = new String[cells.size()];
					index = 0;
					for (Object cell : cells) {
						try {
							array[index++] = DataConverterRegistry.convert(String.class, cell);
						} catch (Exception e) {
							array[index++] = "";
						}
					}
					mapper.writeNext(array);
				}
			}
		}
		return writer.toString();
	}

	// --- OBJECT TO COLLECTION CONVERTER ---

	protected static final Collection<?> objectToCollection(Object object) {
		Collection<?> collection = null;
		if (object != null) {
			if (object instanceof Collection) {
				collection = (Collection<?>) object;
			} else if (object instanceof Map) {
				collection = ((Map<?, ?>) object).values();
			} else if (object.getClass().isArray()) {
				LinkedList<Object> list = new LinkedList<Object>();
				int len = Array.getLength(object);
				for (int i = 0; i < len; i++) {
					list.addLast(Array.get(object, i));
				}
				collection = list;
			} else {
				collection = Collections.singleton(object);
			}
		}
		return collection;
	}

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		CSVParserBuilder parserBuilder = new CSVParserBuilder();
		
		parserBuilder.withSeparator(defaultSeparatorChar);
		parserBuilder.withQuoteChar(defaultQuoteChar);
		parserBuilder.withEscapeChar(defaultEscapeChar);		
		parserBuilder.withIgnoreQuotations(ignoreQuotations);
		parserBuilder.withIgnoreLeadingWhiteSpace(defaultIgnoreLeadingWhiteSpace);
		
		CSVReaderBuilder readerBuilder = new CSVReaderBuilder(new StringReader(source));

		readerBuilder.withCSVParser(parserBuilder.build());	
		readerBuilder.withSkipLines(defaultSkipLines);
		
		return readerBuilder.build().readAll();
	}

}
