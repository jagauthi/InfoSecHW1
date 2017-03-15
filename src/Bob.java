import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;

import javax.crypto.Cipher;

public class Bob {

	KeyPair keyPair;
	private int k;

	public Bob() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPair = keyPairGenerator.generateKeyPair();
		} 
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	public PublicKey getPublicKey() {
		return keyPair.getPublic();
	}

	public void decryptRandomKey(String filePath) {
		Cipher decrypter;
		try {
			decrypter = Cipher.getInstance("RSA");
			decrypter.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
			byte[] encryptedK = readBytesFromFile("randomKey.txt");
			byte[] decryptedK = decrypter.doFinal(encryptedK);
			k = ByteBuffer.wrap(decryptedK).getInt();
			System.out.println("Bob's decrypted key: " + k);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public byte[] readBytesFromFile(String filePath) throws IOException {
		File f = new File(filePath);
		InputStream is = new FileInputStream(f);
		long length = f.length();
		byte[] bytes = new byte[(int) length];
		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}
		if (offset < bytes.length) {
			is.close();
			throw new IOException("Couldn't read all of " + f.getName());
		}
		is.close();
		return bytes;
	}
}
