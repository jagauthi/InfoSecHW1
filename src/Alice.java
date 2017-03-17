
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
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

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

	public void generateRandomKey(String filePath) {
		//Generates a random int and saves it as the key
		SecureRandom r = new SecureRandom();
		k = r.nextInt();
		System.out.println("Alice's unencrypted key: " + k);
		try {
			//Creates the cipher that will encrypt the key using Bob's public key
			Cipher encrypter = Cipher.getInstance("RSA");
			encrypter.init(Cipher.ENCRYPT_MODE, Main.bobPublicKey);
			
			//Turns k into a byte array, encrypts it, and writes it to the file
			byte[] kToBytes = ByteBuffer.allocate(4).putInt(k).array();
			byte[] encryptedKey = encrypter.doFinal(kToBytes);
			writeBytesToFile(filePath, encryptedKey);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendAesMessage(String filePath) {
		byte[] message = new byte[] {
				'A', 'B', 'C', 'D', 'E', 
				'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O',
				'P', 'Q', 'R', 'S', 'T',
				'U', 'V', 'W', 'X', 'Y'
				};
		System.out.print("Alice's unencrypted message (in bytes): \n\t");
		for(int i = 0; i < message.length; i++) {
			System.out.print(message[i]);
			if(i != message.length-1)
				System.out.print(", ");
		}
		System.out.println();
		
		try {
			//Creates the cipher that will encrypt the message using previous k as the key
			Cipher encrypter = Cipher.getInstance("AES");
			
			//converts the integer k to a secret key with AES
			SecretKeySpec secretKey = new SecretKeySpec(ByteBuffer.allocate(16).putInt(k).array(), "AES");
			encrypter.init(Cipher.ENCRYPT_MODE, secretKey);
			
			//Encrypts the message and writes it to the file
			byte[] encryptedMessage = encrypter.doFinal(message);
			writeBytesToFile(filePath, encryptedMessage);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void sendHmacAuthMessage(String messagePath, String hashPath) {
		byte[] message = new byte[] {
				'a', 'b', 'c', 'd', 'e',
				'A', 'B', 'C', 'D', 'E', 
				'F', 'G', 'H', 'I', 'J',
				'K', 'L', 'M', 'N', 'O',
				'P', 'Q', 'R', 'S', 'T',
				'U', 'V', 'W', 'X', 'Y'
				};
		
		try{
			//Creates the message authenticator that will encrypt the message using previous k as the key
			Mac hMac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(ByteBuffer.allocate(16).putInt(k).array(), "HmacSHA256");
			hMac.init(secret_key);

			//Hashes the message and writes both the message and the hash to their files
			byte[] hash = hMac.doFinal(message);
			System.out.print("Alice's hash (in bytes): \n\t");
			for(int i = 0; i < hash.length; i++) {
				System.out.print(hash[i]);
				if(i != hash.length - 1)
					System.out.print(", ");
			}
			System.out.println();
			writeBytesToFile(messagePath, message);
			writeBytesToFile(hashPath, hash);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public PublicKey getPublicKey() {
		return keyPair.getPublic();
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
