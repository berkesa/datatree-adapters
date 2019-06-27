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

import java.net.InetAddress;
import java.util.Map;
import java.util.UUID;

import io.datatree.dom.Priority;
import io.datatree.dom.builtin.AbstractTextAdapter;
import net.sf.sojo.core.Converter;
import net.sf.sojo.core.ConverterInterceptor;
import net.sf.sojo.interchange.xmlrpc.XmlRpcSerializer;

/**
 * <b>SOJO XML-RPC ADAPTER</b><br>
 * <br>
 * Description: SOJO stands for Simplify your Old Java Objects or, in noun form,
 * Simplified Old Java Objects.<br>
 * <br>
 * <b>Dependency:</b><br>
 * <br>
 * https://mvnrepository.com/artifact/net.sf.sojo/sojo<br>
 * compile group: 'net.sf.sojo', name: 'sojo', version: '1.0.13'<br>
 * <br>
 * <b>Set as default (using Java System Properties):</b><br>
 * <br>
 * If there is more than one XML-RPC implementation on classpath, the preferred
 * implementation is adjustable with the following System Properties. If there
 * is only one (eg. only the "sojo") implementation on the classpath, this step
 * is NOT necessary, the DataTree API will use this JSON API automatically.<br>
 * <br>
 * -Ddatatree.xmlrpc.reader=io.datatree.dom.adapters.XmlRpcSojo<br>
 * -Ddatatree.xmlrpc.writer=io.datatree.dom.adapters.XmlRpcSojo<br>
 * <br>
 * <b>Set as default (using static methods):</b><br>
 * <br>
 * XmlRpcSojo sojo = new XmlRpcSojo();<br>
 * TreeReaderRegistry.setReader("xmlrpc", sojo);<br>
 * TreeWriterRegistry.setWriter("xmlrpc", sojo);<br>
 * <br>
 * Tree node = new Tree(inputString, "xmlrpc");<br>
 * String outputString = node.toString("xmlrpc");<br>
 * <br>
 * Innvoke this implementation directly:<br>
 * <br>
 * Tree node = new Tree(inputString, "XmlRpcSojo");<br>
 * String outputString = node.toString("XmlRpcSojo");
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
@Priority(10)
public class XmlRpcSojo extends AbstractTextAdapter {

	// --- OBJECT MAPPER ---

	public XmlRpcSerializer mapper = create();

	// --- NAME OF THE FORMAT ---

	@Override
	public String getFormat() {
		return "xmlrpc";
	}

	// --- IMPLEMENTED WRITER METHOD ---

	@SuppressWarnings("rawtypes")
	@Override
	public String toString(Object value, Object meta, boolean pretty, boolean insertMeta) {
		return toString(value, meta, insertMeta, (input) -> {
			String method = null;
			if (meta != null && meta instanceof Map) {
				Map map = (Map) meta;
				method = String.valueOf(map.get("method"));
			}
			String xml;
			if (method == null || method.equals("null")) {
				xml = (String) mapper.serializeXmlRpcResponse(input);				
			} else {
				xml = (String) mapper.serializeXmlRpcRequest(method, input);
			}
			if (xml.contains("ex:")) {
				if (xml.contains("<methodResponse>")) {
					xml = xml.replace("<methodResponse>", "<methodResponse xmlns:ex=\"http://xml-rpc.org\">");
				} else {
					xml = xml.replace("<methodCall>", "<methodCall xmlns:ex=\"http://xml-rpc.org\">");
				}
			}
			return xml;
		});
	}

	// --- IMPLEMENTED PARSER METHOD ---

	@Override
	public Object parse(String source) throws Exception {
		return mapper.deserialize(source);
	}

	// --- FACTORY ---

	public static final XmlRpcSerializer create() {
		XmlRpcSerializer mapper = new XmlRpcSerializer();
		Converter converter = mapper.getObjectUtil().getConverter();

		// Install MongoDB / BSON serializers
		boolean allInstalled = tryToAddSerializers("io.datatree.dom.adapters.JsonSojoBsonSerializers", converter);

		// Install serializers for Apache Cassandra
		if (!allInstalled) {
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
					return value;
				}

				@Override
				public final Object afterConvert(Object result, Class<?> to) {
					return result;
				}

			});
		}
		return mapper;
	}

}