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
package org.datatree;

import org.datatree.dom.TreeReaderRegistry;
import org.datatree.dom.TreeWriterRegistry;
import org.datatree.dom.adapters.JsonBoon;

/**
 * PerformanceTestWithBoon.java
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class PerformanceTestWithBoon extends PerformanceTest {

	protected void setUp() throws Exception {
		JsonBoon impl = new JsonBoon();
		TreeReaderRegistry.setReader("json", impl);
		TreeWriterRegistry.setWriter("json", impl);
	}

}
