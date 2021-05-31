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
package io.datatree;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.util.Date;
import java.util.UUID;

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
import org.bson.Document;
import org.bson.types.Binary;
import org.bson.types.Code;
import org.bson.types.Decimal128;
import org.bson.types.ObjectId;
import org.bson.types.Symbol;
import org.junit.Test;

import io.datatree.Tree;
import io.datatree.dom.TreeReaderRegistry;
import io.datatree.dom.TreeWriterRegistry;
import io.datatree.dom.builtin.JsonBuiltin;
import junit.framework.TestCase;

/**
 * XML, BSON, MessagePack, ION, CSV, TSV, Java Properties tests.
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class DataFormatTest extends TestCase {

	// --- START TEST ---

	@Override
	protected void setUp() throws Exception {
		JsonBuiltin impl = new JsonBuiltin();
		TreeReaderRegistry.setReader("json", impl);
		TreeWriterRegistry.setWriter("json", impl);
	}

	// --- SAMPLE ---

	private static final String JSON = "{\"a\":{\"b\":{\"c\":{\"d\":[1,2,3]}}}}";

	// --- XML ---

	@Test
	public void testXml() throws Exception {

		// Builtin XML reader/writer
		testParser("XmlBuiltin", true);
		testMongoTypes("XmlBuiltin");

		// Jackson XML reader/writer
		testParser("XmlJackson", false);
		testMongoTypes("XmlJackson");

		// XStream XML reader/writer
		testParser("XmlXStream", false);
		testMongoTypes("XmlXStream");
	}

	private void testParser(String format, boolean doParserTest) throws Exception {

		// XML test
		testConvert(format);

		// Parser test
		if (doParserTest) {
			Tree t = new Tree(JSON);
			String source = t.toString(format, true);
			t = new Tree(source, format);
			testSerializationAndCloning(t);
			String o = t.toString(false);
			o = o.replace("\"1\"", "1");
			o = o.replace("\"2\"", "2");
			o = o.replace("\"3\"", "3");
			assertEquals(JSON, o);
		}
	}

	// --- PROPERTIES ---

	@Test
	public void testProperties() throws Exception {

		// Builtin Properties reader/writer
		testParser("PropertiesBuiltin", true);
		testPropertyGetters("PropertiesBuiltin");
		testMongoTypes("PropertiesBuiltin");

		// Jackson Properties reader/writer
		testParser("PropertiesJackson", true);
		testPropertyGetters("PropertiesJackson");
		testMongoTypes("PropertiesJackson");
	}

	private void testPropertyGetters(String format) throws Exception {

		String props = PerformanceTest.loadString("sample-small.properties");
		Tree t = new Tree(props, format);

		assertEquals("Test User", t.get("name", "xyz"));
		assertEquals(30, t.get("age", 0));
		assertEquals("Washington D.C.", t.get("address.city", "-"));
		assertEquals("User", t.get("account[1].name", ""));
		assertFalse(t.get("account[1].enabled", true));

		props = PerformanceTest.loadString("sample-large.properties");
		t = new Tree(props, format);

		assertEquals("Times-Bold", t.get("serif.latin1.bold", ""));
		assertEquals("Times-Roman", t.get("serif.latin1.plain", ""));
		assertEquals("Helvetica-Bold", t.get("helvetica.latin1.bold", ""));
	}

	// --- KRYO ---
	
	@Test
	public void testKryo() throws Exception {

		// Implementation based on SnakeYAML
		testConvert("KryoKryo");
		testMongoTypes("KryoKryo");
	}
	
	// --- YAML ---

	@Test
	public void testYaml() throws Exception {

		// Implementation based on SnakeYAML
		testYaml("YamlSnakeYaml");
		testMongoTypes("YamlSnakeYaml");

		// Jackson's implementation (it's also based on SnakeYAML)
		testYaml("YamlJackson");
		testMongoTypes("YamlJackson");
	}

	public void testYaml(String format) throws Exception {

		// YAML test
		testConvert(format);

		// Load file
		String yaml = PerformanceTest.loadString("sample-small.yaml");
		Tree t = new Tree(yaml, format);

		assertEquals("Test User", t.get("name", "xyz"));
		assertEquals(30, t.get("age", 0));
		assertEquals("Washington D.C.", t.get("address.city", "-"));
		assertEquals("User", t.get("roles[0]", ""));

		// Parser test
		t = new Tree(JSON);
		String source = t.toString(format, true);
		t = new Tree(source, format);
		testSerializationAndCloning(t);
		assertEquals(JSON, t.toString(false));
	}

	// --- TOML ---

	@Test
	public void testToml() throws Exception {

		// JToml (me.grison.jtoml) test
		testConvert("TomlJToml");
		testTomlGetters("TomlJToml");
		testTomlReaderWrite("TomlJToml");
		testMongoTypes("TomlJToml");

		// JToml (io.ous.jtoml) test
		// This API only a TOML reader (without writer), but better than
		// "me.grison.jtoml" API's reader
		testTomlGetters("TomlJToml2");

		// Toml4J test
		testConvert("TomlToml4j");
		testTomlGetters("TomlToml4j");
		testTomlReaderWrite("TomlToml4j");
	}

	private void testTomlGetters(String format) throws Exception {

		// Load file
		String toml = PerformanceTest.loadString("sample-small.toml");
		Tree t = new Tree(toml, format);

		assertEquals("TOML Example", t.get("title", "-"));
		assertEquals(8002, t.get("database.ports[2]", 0));
		assertEquals("password", t.get("database.credentials.password", "-"));
		assertEquals("10.0.0.1", t.get("servers.alpha.ip", "-"));

		// Array parser of 'me.grison.jtoml' is faulty
		if (!format.equals("TomlJToml")) {
			assertEquals("Level 1", t.get("networks[0].name", "-"));
			assertEquals("Level 2", t.get("networks[1].name", "-"));
			assertEquals("Level 3", t.get("networks[2].name", "-"));
			assertEquals(10, t.get("networks[0].status.bandwidth", 0));
		}

	}

	private void testTomlReaderWrite(String format) throws Exception {

		// Parser test
		String json = "{\"a\":{\"b\":{\"c\":{\"array\":[1,2,3]}}}}";
		Tree t = new Tree(json);
		String source = t.toString(format, true);
		t = new Tree(source, format);
		testSerializationAndCloning(t);
		assertEquals(json, t.toString(false));
	}

	// --- CSV / COMMA-SEPARATED VALUES ---

	@Test
	public void testCsv() throws Exception {

		// OpenCSV
		testCsv("CsvOpenCSV");
	}

	public void testCsv(String format) throws Exception {

		String csv = PerformanceTest.loadString("sample-small.csv");
		Tree t = new Tree(csv, format);

		testSerializationAndCloning(t);

		assertTrue(t.isEnumeration());
		assertEquals(10, t.size());
		assertEquals("Test0", t.get(0).get(0).asString());
		assertEquals(0, t.get(0).get(6).asInteger().intValue());
		assertEquals(9L, t.get(9).get(6).asInteger().longValue());
		
		String out = t.toString(format, true);
		csv = csv.replace("\r", "").replace("\n", "").trim();
		out = out.replace("\r", "").replace("\n", "").trim();
		assertEquals(csv, out);
	}

	// --- TSV / TAB-SEPARATED VALUES ---

	@Test
	public void testTsv() throws Exception {

		// TSV (with OpenCSV)
		testTsv("TsvOpenCSV");

	}

	public void testTsv(String format) throws Exception {

		String tsv = PerformanceTest.loadString("sample-small.tsv");
		Tree t = new Tree(tsv, format);
		
		testSerializationAndCloning(t);

		assertTrue(t.isEnumeration());
		assertEquals(10, t.size());
		assertEquals("Test0", t.get(0).get(0).asString());
		assertEquals(0, t.get(0).get(6).asInteger().intValue());
		assertEquals(9L, t.get(9).get(6).asInteger().longValue());
	}

	// --- BINARY JAVA OBJECT SERIALIZATION ---

	@Test
	public void testJava() throws Exception {

		// Java serialization test
		testConvert("java");
		testMongoTypes("java");
	}

	// --- BINARY CBOR ---

	@Test
	public void testCbor() throws Exception {

		// CBOR test
		testConvert("cbor");
		testMongoTypes("cbor");
	}

	// --- BINARY SMILE ---

	@Test
	public void testSmile() throws Exception {

		// SMILE test
		testConvert("smile");
		testMongoTypes("smile");
	}

	// --- BINARY BSON ---

	@Test
	public void testBson() throws Exception {

		// BSON test
		testConvert("bson");
		testMongoTypes("bson");
	}

	// --- BINARY MESSAGEPACK ---

	@Test
	public void testMsgPack() throws Exception {

		// MessagePack (org.msgpack.msgpack)
		testConvert("MsgPackOrg");
		testMongoTypes("MsgPackOrg");

		// MessagePack (org.msgpack.jackson-dataformat-msgpack)
		testConvert("MsgPackJackson");
		testMongoTypes("MsgPackJackson");

		// Read-write test
		Tree t = new Tree();
		Date date = new Date();
		InetAddress inet = InetAddress.getLocalHost();
		UUID uuid = UUID.randomUUID();

		t.put("null", (String) null);
		t.put("empty", "");
		t.put("bool", true);
		t.put("byte", (byte) 3);
		t.put("double", 4d);
		t.put("float", 5f);
		t.put("int", 6);
		t.put("long", 7l);
		t.put("short", (short) 8);
		t.put("string", "abcdefgh");
		t.put("bigDecimal", new BigDecimal("1.2"));
		t.put("bigInteger", new BigInteger("2"));
		t.put("bytes", "test".getBytes());
		t.put("inet", inet);
		t.put("date", date);
		t.put("uuid", uuid);

		byte[] b = new byte[]{1, 2, 3, 4, 5};
		t.put("array", b);
		
		byte[] bytes = t.toBinary("MsgPackOrg");
		Tree t2 = new Tree(bytes, "MsgPackJackson");

		assertNull(t2.get("null", (String) null));
		assertEquals(true, t2.get("bool", false));
		assertEquals((byte) 3, t2.get("byte", (byte) 0));
		assertEquals(4d, t2.get("double", 0d));
		assertEquals(5f, t2.get("float", 0f));
		assertEquals(6, t2.get("int", 0));
		assertEquals(7l, t2.get("long", 0L));
		assertEquals((short) 8, t2.get("short", (short) 0));
		assertEquals("abcdefgh", t2.get("string", ""));
		assertEquals(-1, (new BigDecimal("1.2").subtract(t2.get("bigDecimal", new BigDecimal("0")))
				.compareTo(new BigDecimal("0.0000001"))));
		assertEquals(new BigInteger("2"), t2.get("bigInteger", new BigInteger("0")));
		assertEquals("test", new String(t2.get("bytes", "ppp".getBytes())));
		assertEquals(inet, t2.get("inet", InetAddress.getLocalHost()));
		assertEquals(date.getTime() / 1000L, t2.get("date", new Date(0)).getTime() / 1000L);
		assertEquals(uuid, t2.get("uuid", UUID.randomUUID()));
		
		assertEquals(1, t2.get("array[0]", -1));
		assertEquals(2, t2.get("array[1]", -1));
		assertEquals(3, t2.get("array[2]", -1));
		assertEquals(4, t2.get("array[3]", -1));
		assertEquals(5, t2.get("array[4]", -1));
	}

	// --- BINARY ION ---

	@Test
	public void testIon() throws Exception {

		// ION test
		testConvert("IonIon");
		testMongoTypes("IonIon");
	}

	// ---XML-RPC ---

	@Test
	public void testXmlRpc() throws Exception {

		// XML-RPC test
		testConvert("XmlRpcSojo");
		testMongoTypes("XmlRpcSojo");
		
		Tree t = new Tree();
		t.getMeta().put("method", "sampleMethod");
		testConvert(t, "XmlRpcSojo");
	}

	// --- CONVERTER TEST ---

	private Tree testConvert(String format) throws Exception {
		return testConvert(new Tree(), format);
	}
	
	private Tree testConvert(Tree t, String format) throws Exception {
		Date date = new Date();
		InetAddress inet = InetAddress.getLocalHost();
		UUID uuid = UUID.randomUUID();

		// JSON-Simple and Toml4j doesn't support non-standard data types
		String writerClass = TreeWriterRegistry.getWriter(format).getClass().toString();
		boolean useSimpleAPI = writerClass.contains("Simple") || writerClass.contains("Toml4j");

		// Standard JSON types
		t.put("null", (String) null);
		
		if (!writerClass.contains("XmlRpcSojo")) {
			
			// XML-RPC Sojo implementation fails from empty strings
			t.put("empty", "");
		}
		
		t.put("bool", true);
		t.put("byte", (byte) 3);
		t.put("double", 4d);
		t.put("float", 5f);
		t.put("int", 6);
		t.put("long", 7l);
		t.put("short", (short) 8);
		t.put("string", "abcdefgh");

		// Non-standard JSON types
		if (!useSimpleAPI) {

			// JSON-Simple doesn't support non-standard data types
			t.put("bigDecimal", new BigDecimal("1.2"));
			t.put("bigInteger", new BigInteger("2"));
			t.put("bytes", "test".getBytes());
			t.put("inet", inet);
			t.put("date", date);
			t.put("uuid", uuid);
		}

		// Collections
		t.putMap("map").put("key1", "value1").put("key2", true).put("key3", 4.5);
		t.putSet("set").add(1).add(2).add(3);
		t.putList("list").add("a").add("b").add("c");

		// Meta
		t.getMeta().put("m1", 1);
		t.getMeta().put("m2", "abc");
		t.getMeta().put("m3", true);

		// Array
		if (!useSimpleAPI) {
			t.getMeta().putObject("m4", new String[] { "a", "b", "c" });
		}

		// Print output format
		System.out.println("-------------------- " + format.toUpperCase() + " --------------------");
		System.out.println("Output of " + TreeWriterRegistry.getWriter(format).getClass() + " serializer:");
		String source;
		try {
			source = t.toString(format, true, true);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
		System.out.println();
		System.out.println(source);

		Tree t2 = new Tree(source, format);
		source = t2.toString(format, false, true);
		t2 = new Tree(source, format);

		// Standard JSON types
		String nullValue = t2.get("null", (String) null);
		if (writerClass.contains("Jackson")) {

			// Jackson writes empty string instead of "null"
			assertEquals("", nullValue);
		} else {
			assertNull(nullValue);
		}
		assertEquals(true, t2.get("bool", false));
		assertEquals((byte) 3, t2.get("byte", (byte) 0));
		assertEquals(4d, t2.get("double", 0d));
		assertEquals(5f, t2.get("float", 0f));
		assertEquals(6, t2.get("int", 0));
		assertEquals(7l, t2.get("long", 0L));
		assertEquals((short) 8, t2.get("short", (short) 0));
		assertEquals("abcdefgh", t2.get("string", ""));

		// Non-standard JSON types
		// && TreeWriterRegistry.JSON.equals(format)
		if (!useSimpleAPI) {

			// JSON-Simple doesn't support non-standard data types
			assertEquals(-1, (new BigDecimal("1.2").subtract(t2.get("bigDecimal", new BigDecimal("0")))
					.compareTo(new BigDecimal("0.0000001"))));
			assertEquals(new BigInteger("2"), t2.get("bigInteger", new BigInteger("0")));
			assertEquals("test", new String(t2.get("bytes", "ppp".getBytes())));
			assertEquals(inet, t2.get("inet", InetAddress.getLocalHost()));
			assertEquals(date.getTime() / 1000L, t2.get("date", new Date(0)).getTime() / 1000L);
			assertEquals(uuid, t2.get("uuid", UUID.randomUUID()));
		}

		// Collections
		assertTrue(t2.get("map").isMap());

		// Jackson's XML map/set implementation is buggy
		if (!writerClass.contains("XmlJackson")) {

			assertTrue(t2.get("set").isEnumeration());
			assertTrue(t2.get("list").isEnumeration());

			assertEquals("value1", t2.get("map.key1", "x"));
			assertEquals(true, t2.get("map.key2", false));
			assertEquals(4.5, t2.get("map.key3", 5.6));

			assertEquals(1, t2.get("set[0]", 2));
			assertTrue(2 == t2.get("set").get(1).asInteger());
			assertEquals(3, t2.get("set[2]", 4));

			assertEquals("a", t2.get("list[0]", "x"));
			assertEquals("b", t2.get("list").get(1).asString());
			assertEquals("c", t2.get("list[2]", "y"));
		}

		return t;
	}

	// --- SERIALIZATION / DESERIALIZATION / CLONE ---

	private final void testSerializationAndCloning(Tree node) throws Exception {

		// Serialize
		ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(node);
		oos.flush();
		byte[] bytes = baos.toByteArray();
		// System.out.println(new String(bytes));

		// Deserialize
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		ObjectInputStream ois = new ObjectInputStream(bais);
		Tree copy = (Tree) ois.readObject();

		String txtOriginal = node.toString("debug");
		String txtCopy = copy.toString("debug");

		assertEquals(txtOriginal, txtCopy);

		// Cloning
		txtCopy = node.clone().toString("debug");
		assertEquals(txtOriginal, txtCopy);
	}

	// --- TEST MONGO TYPES ---

	@Test
	public void testMongoTypes(String format) throws Exception {
		String writerClass = TreeWriterRegistry.getWriter(format).getClass().toString();

		Document doc = new Document();
		doc.put("BsonBoolean", new BsonBoolean(true));
		long time = System.currentTimeMillis();
		doc.put("BsonDateTime", new BsonDateTime(time));
		doc.put("BsonDouble", new BsonDouble(123.456));
		doc.put("BsonInt32", new BsonInt32(123));
		doc.put("BsonInt64", new BsonInt64(123456));
		doc.put("BsonNull", new BsonNull());
		doc.put("BsonRegularExpression", new BsonRegularExpression("abc"));
		doc.put("BsonString", new BsonString("abcdefgh"));
		doc.put("BsonTimestamp", new BsonTimestamp(12, 23));
		doc.put("BsonUndefined", new BsonUndefined());
		doc.put("Binary", new Binary("abcdefgh".getBytes()));
		doc.put("Code", new Code("var a = 5;"));
		doc.put("Decimal128", new Decimal128(123456));
		ObjectId objectID = new ObjectId();
		doc.put("ObjectId", objectID);
		doc.put("Symbol", new Symbol("s"));

		Tree t = new Tree(doc, null);
		byte[] binary = t.toBinary(format);
		// System.out.println(new String(binary));
		t = new Tree(binary, format);

		assertTrue(t.get("BsonBoolean", false));

		Date date = t.get("BsonDateTime", new Date());
		assertEquals(time / 1000L, date.getTime() / 1000L);

		assertEquals(123.456, t.get("BsonDouble", 1d));
		assertEquals(123, t.get("BsonInt32", 1));
		assertEquals(123456L, t.get("BsonInt64", 1L));

		if (writerClass.contains("Jackson")) {

			// Jackson writes empty string instead of "null"
			assertEquals("", t.get("BsonNull", (String) null));
			assertEquals("", t.get("BsonUndefined", (String) null));
		} else {
			assertNull(t.get("BsonNull", (String) null));
			assertNull(t.get("BsonUndefined", (String) null));
		}
		
		assertEquals("abc", t.get("BsonRegularExpression", "?"));
		assertEquals("abcdefgh", t.get("BsonString", "?"));

		// String or Number
		date = t.get("BsonTimestamp", new Date());
		assertEquals(12000L, date.getTime());

		assertEquals("abcdefgh", new String(t.get("Binary", "?".getBytes())));
		assertEquals("var a = 5;", t.get("Code", "?"));
		assertEquals(123456L, t.get("Decimal128", 1L));
		assertEquals(objectID.toHexString(), t.get("ObjectId", "?"));
		assertEquals("s", t.get("Symbol", "?"));
	}

}