/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package be.rubus.web.jsf.primefaces;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.primefaces.component.graphicimage.GraphicImage;
import org.primefaces.model.StreamedContent;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@gmail.com>
 */
public class UUIDHelperTest {

	private ByteArrayInputStream stream;

	@Before
	public void before() {
		StringBuilder builder = new StringBuilder();
		builder.append("start");
		for (int i = 0; i < 100; i++) {
			builder.append("[123456789:tekst]");
		}
		stream = new ByteArrayInputStream(builder.toString().getBytes());
	}

	/**
	 * Test of createObjectId method, of class UUIDHelper.
	 */
	@Test
	public void testCreateObjectId() throws IOException {
		StreamedContent content = Mockito.mock(StreamedContent.class);
		when(content.getContentType()).thenReturn("image/jpeg");
		when(content.getName()).thenReturn("an stream of image");
		GraphicImage image = Mockito.mock(GraphicImage.class);
		when(image.getName()).thenReturn("an image");
		when(image.getValue()).thenReturn(content);
		when(content.getStream()).thenReturn(stream);
		UUIDHelper instance = new UUIDHelper();
		String expResult = "0db0049e-7b4c-c7a3-2175-531c0c26a4b8";
		String result = instance.createObjectId(image);
		assertEquals(expResult, result);
		result = instance.createObjectId(image);
		assertEquals(expResult, result);
		byte[] buffer = new byte[5];
		stream.read(buffer);
		assertEquals("start", new String(buffer));
	}

}
