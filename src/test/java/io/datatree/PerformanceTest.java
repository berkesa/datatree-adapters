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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.junit.Test;

import io.datatree.dom.TreeReaderRegistry;
import io.datatree.dom.TreeWriterRegistry;
import junit.framework.TestCase;

/**
 * PerformanceTest.java
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public abstract class PerformanceTest extends TestCase {

	// --- SMALL JSON TEST ---

	private static final boolean DO_SMALL_TEST = true;

	private static final int SMALL_READER_LOOPS = 1000000;

	private static final int SMALL_WRITER_LOOPS = 1000000;

	// --- LARGE JSON TEST ---

	private static final boolean DO_LARGE_TEST = false;

	private static final int LARGE_READER_LOOPS = 10;

	private static final int LARGE_WRITER_LOOPS = 10;

	// --- TEST CASES ---

	@Test
	public final void testSmallRead() throws Exception {
		if (!DO_SMALL_TEST) {
			return;
		}
		long duration = doReaderTest("sample-small.json", SMALL_READER_LOOPS);
		printResult(duration, true, true);
	}

	@Test
	public final void testLargeRead() throws Exception {
		if (!DO_LARGE_TEST) {
			return;
		}

		// TODO Missing file
		long duration = doReaderTest("sample-large.json", LARGE_READER_LOOPS);
		printResult(duration, false, true);
	}

	@Test
	public final void testSmallWrite() throws Exception {
		if (!DO_SMALL_TEST) {
			return;
		}
		long duration = doWriterTest("sample-small.json", SMALL_WRITER_LOOPS);
		printResult(duration, true, false);
	}

	@Test
	public final void testLargeWrite() throws Exception {
		if (!DO_LARGE_TEST) {
			return;
		}

		// TODO Missing file
		long duration = doWriterTest("sample-large.json", LARGE_WRITER_LOOPS);
		printResult(duration, true, false);
	}

	// --- UTILITIES ---

	protected final long doWriterTest(String name, int loops) throws Exception {
		Tree node = loadQNode(name);
		System.gc();
		long start = System.currentTimeMillis();
		for (int i = 0; i < loops; i++) {
			node.toString(null, false);
		}
		return System.currentTimeMillis() - start;
	}

	protected final long doReaderTest(String name, int loops) throws Exception {
		String json = loadString(name);
		System.gc();
		long start = System.currentTimeMillis();
		for (int i = 0; i < loops; i++) {
			new Tree(json);
		}
		return System.currentTimeMillis() - start;
	}

	protected final void printResult(long duration, boolean small, boolean reader) {
		String api;
		if (reader) {
			api = TreeReaderRegistry.getReader("json").getClass().toString();
			api = api.replace("Reader", "");
		} else {
			api = TreeWriterRegistry.getWriter("json").getClass().toString();
			api = api.replace("Writer", "");
		}
		int i = api.lastIndexOf('.');
		if (i > -1) {
			api = api.substring(i + 1);
		}
		System.out.println(
				api + "\t" + duration + "\t" + (small ? "small" : "large") + "\t" + (reader ? "reader" : "writer"));
	}

	// --- LOAD TEST DATA ---

	private static final HashMap<String, Tree> nodes = new HashMap<>();

	protected static final Tree loadQNode(String name) throws Exception {
		Tree node = nodes.get(name);
		if (node != null) {
			return node;
		}
		node = new Tree(loadString(name));
		nodes.put(name, node);
		return node;
	}

	private static final HashMap<String, String> jsons = new HashMap<>();

	public static final String loadString(String name) throws Exception {
		String json = jsons.get(name);
		if (json != null) {
			return json;
		}

		InputStream in = PerformanceTest.class.getResourceAsStream("/" + name);
		if (in == null) {
			in = new FileInputStream(System.getProperty("user.dir") + "/src/test/resources/" + name);
		}
		byte[] bytes = readFully(in);
		json = new String(bytes, "UTF8");
		json = json.trim();
		jsons.put(name, json);
		return json;
	}

	private static final byte[] readFully(InputStream in) throws IOException {
		byte[] data = new byte[0];
		try {
			byte[] packet = new byte[2000000];
			int i;
			while ((i = in.read(packet)) != -1) {
				byte[] s = new byte[data.length + i];
				System.arraycopy(data, 0, s, 0, data.length);
				System.arraycopy(packet, 0, s, data.length, i);
				data = s;
			}
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (Exception ingored) {
			}
		}
		return data;
	}

}
