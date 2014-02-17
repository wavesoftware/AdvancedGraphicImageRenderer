
package be.rubus.web.jsf.primefaces;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;
import java.util.zip.CRC32;
import org.primefaces.component.graphicimage.GraphicImage;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Krzysztof Suszyński <krzysztof.suszynski@gmail.com>
 */
class UUIDHelper {
	/**
	 * Creates a constant ID of an object
	 *
	 * @param image a object to count
	 * @return a contant UUID
	 */
	public String createObjectId(GraphicImage image) {
		Random random = new Random(getSeedForImage(image));
		Long generated = random.nextLong();
		String md5 = md5(generated.toString());
		return String.format("%s-%s-%s-%s-%s", md5.substring(0, 8), md5.substring(8, 12), md5.substring(12, 16), md5.
				substring(16, 20), md5.substring(20));
	}

	/**
	 * Gets a seed for an image
	 *
	 * @param image a image
	 * @return a seed
	 */
	private long getSeedForImage(GraphicImage image) {
		long seed;
		Object value = image.getValue();
		if (value instanceof StreamedContent) {
			StreamedContent content = (StreamedContent) value;
			String streamId = getStreamId(content.getStream());
			String key = String.format("%s:%s:%s:%s", image.getName(), content.getContentType(), content.getName(),
					streamId);
			CRC32 crc = new CRC32();
			crc.update(key.getBytes());
			seed = crc.getValue();
		} else {
			seed = value.hashCode();
		}
		return seed;
	}

	/**
	 * Creates a md5 from input
	 *
	 * @param input an imput
	 * @return a md5
	 */
	private String md5(String input) {
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			byte[] array = digest.digest(input.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < array.length; ++i) {
				sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
			}
			return sb.toString();
		} catch (NoSuchAlgorithmException ex) {
			throw new RuntimeException("MD5 algorithm not found: " + ex.getLocalizedMessage(), ex);
		}
	}

	private String getStreamId(InputStream stream) {
		if (stream instanceof BufferedInputStream || stream instanceof ByteArrayInputStream) {
			try {
				byte[] buffer = new byte[60];
				stream.read(buffer);
				String ret = new String(buffer);
				stream.reset();
				return ret;
			} catch (IOException ex) {
				throw new RuntimeException("Error reading stream: " + ex.getLocalizedMessage(), ex);
			}
		} else {
			return "constant";
		}
	}
}
