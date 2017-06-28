/**
 * This software is licensed under the Apache 2 license, quoted below.<br>
 * <br>
 * Copyright 2017 Andras Berkes [andras.berkes@europe.com]<br>
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

import io.datatree.dom.TreeReaderRegistry;
import io.datatree.dom.TreeWriterRegistry;
import io.datatree.dom.adapters.SmileJackson;

/**
 * PerformanceBinaryTestWithSmile.java
 * 
 * @author Andras Berkes [andras.berkes@programmer.net]
 */
public class PerformanceBinaryTestWithSmile extends PerformanceTest {

	protected void setUp() throws Exception {
		binaryTest = true;
		format = "smile"; 

		SmileJackson impl = new SmileJackson();
		TreeReaderRegistry.setReader(format, impl);
		TreeWriterRegistry.setWriter(format, impl);
	}
	
}