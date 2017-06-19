package org.datatree;

import org.datatree.dom.TreeReaderRegistry;
import org.datatree.dom.TreeWriterRegistry;
import org.datatree.dom.adapters.JsonIon;

public class PreformanceTestWithIon extends PerformanceTest {

	protected void setUp() throws Exception {
		JsonIon impl = new JsonIon();
		TreeReaderRegistry.setReader("json", impl);
		TreeWriterRegistry.setWriter("json", impl);
	}

}