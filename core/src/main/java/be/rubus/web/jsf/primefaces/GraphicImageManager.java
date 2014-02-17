/**
  * Licensed to the Apache Software Foundation (ASF) under one
  * or more contributor license agreements.  See the NOTICE file
  * distributed with this work for additional information
  * regarding copyright ownership.  The ASF licenses this file
  * to you under the Apache License, Version 2.0 (the
  * "License"); you may not use this file except in compliance
  * with the License.  You may obtain a copy of the License at
  *
  *   http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing,
  * software distributed under the License is distributed on an
  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
  * KIND, either express or implied.  See the License for the
  * specific language governing permissions and limitations
  * under the License.
 */
package be.rubus.web.jsf.primefaces;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashMap;
import java.util.Map;
import javax.inject.Named;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

/**
 */
@Named("GraphicImageManager")
public class GraphicImageManager implements HttpSessionBindingListener {

	private Map<String, String> storedContent = new HashMap<String, String>();

	@Override
	public void valueBound(HttpSessionBindingEvent event) {
		// No action required
	}

	@Override
	public void valueUnbound(HttpSessionBindingEvent event) {
		for (String tempFile : storedContent.values()) {
			File f = new File(tempFile);
			f.delete();
		}
	}

	public void registerImage(StreamedContent content, String uniqueId) {
		try {
			File tempFile = File.createTempFile(uniqueId, "primefaces");

			storedContent.put(uniqueId, tempFile.getAbsolutePath());

			InputStream input = content.getStream();
			OutputStream output = new FileOutputStream(tempFile);
			// get a channel from the stream
			final ReadableByteChannel inputChannel = Channels.newChannel(input);
			final WritableByteChannel outputChannel = Channels.newChannel(output);
			// copy the channels
			fastChannelCopy(inputChannel, outputChannel);
			// closing the channels
			inputChannel.close();
			outputChannel.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public StreamedContent retrieveImage(String uniqueId) {
		StreamedContent result = null;
		String tempFile = storedContent.get(uniqueId);
		if (tempFile != null) {
			File f = new File(tempFile);
			try {
				result = new DefaultStreamedContent(new FileInputStream(f));
			} catch (FileNotFoundException e) {
				// FIXME
				e.printStackTrace();
			}
		}
		return result;
	}

	private static void fastChannelCopy(final ReadableByteChannel src, final WritableByteChannel dest) throws IOException {
		final ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
		while (src.read(buffer) != -1) {
			// prepare the buffer to be drained
			buffer.flip();
			// write to the channel, may block
			dest.write(buffer);
			// If partial transfer, shift remainder down
			// If buffer is empty, same as doing clear()
			buffer.compact();
		}
		// EOF will leave buffer in fill state
		buffer.flip();
		// make sure the buffer is fully drained.
		while (buffer.hasRemaining()) {
			dest.write(buffer);
		}
	}
}
