/** 
 *  Copyright (c) 2015 The original author or authors
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0

 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.reveno.atp.core.data;

import com.google.common.io.Files;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.reveno.atp.api.ChannelOptions;
import org.reveno.atp.core.RevenoConfiguration;
import org.reveno.atp.core.api.Journaler;
import org.reveno.atp.core.api.channel.Channel;
import org.reveno.atp.core.channel.FileChannel;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static org.reveno.atp.utils.MeasureUtils.mb;

public class DefaultJournalerTest {

	private File tempFile1, tempFile2;
	
	@Before
	public void setUp() throws IOException {
		tempFile1 = new File(Files.createTempDir(), "test1.dat");
		tempFile1.createNewFile();
		tempFile2 = new File(Files.createTempDir(), "test1.dat");
		tempFile2.createNewFile();
	}

	@After
	public void tearDown() throws IOException {
		tempFile1.delete();
	}
	
	@Test
	public void test() throws Exception {
		Journaler journaler = new DefaultJournaler();
		// TODO not accurate that we use FileChannel here, need some mock in future
		Channel fc = new FileChannel(tempFile1).init();
		journaler.startWriting(fc);
		testWithData(journaler, tempFile1);
		
		Channel fcRoll = new FileChannel(tempFile2).init();
		journaler.roll(fcRoll, () -> {});
		testWithData(journaler, tempFile2);
		
		fc.close();
		fcRoll.close();
	}

	// TODO other tests

	private void testWithData(Journaler journaler, File file) {
		for (int i = 0; i < 10; i++) {
			byte[] data = new byte[mb(1)];
			new Random().nextBytes(data);
			
			journaler.writeData(b -> b.writeBytes(data), false);
		}
		// when we call journaler.roll(..), we must to flush all previous data regardless 'endOfBatch' param
		Assert.assertEquals(file.length(), 0);
		journaler.writeData(b -> b.writeBytes(new byte[0]), true);
		Assert.assertEquals(file.length(), mb(10) + 4);
		journaler.writeData(b -> b.writeBytes(new byte[] { 1, 2, 3 }), true);
		Assert.assertEquals(file.length(), (mb(10) + 4) + (4 + 3));
	}
	
}
