
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.SecureRandom;

import javax.crypto.Cipher;

public class Alice {

	KeyPair keyPair;
	private int k;

	public Alice() {
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

	public void generateRandomKey(String filePath) {
		SecureRandom r = new SecureRandom();
		k = r.nextInt();
		System.out.println("Alice's unencrypted key: " + k);
		Cipher encrypter;
		try {
			encrypter = Cipher.getInstance("RSA");
			encrypter.init(Cipher.ENCRYPT_MODE, Main.bobPublicKey);
			byte[] kToBytes = ByteBuffer.allocate(4).putInt(k).array();
			byte[] encryptedKey = encrypter.doFinal(kToBytes);
			writeBytesToFile(filePath, encryptedKey);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void writeBytesToFile(String filePath, byte[] data) throws IOException {
		BufferedOutputStream bos = null;
		try {
			FileOutputStream fos = new FileOutputStream(new File(filePath));
			bos = new BufferedOutputStream(fos);
			bos.write(data);
		} 
		finally {
			if (bos != null) {
				try {
					bos.flush();
					bos.close();
				} 
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
}
