import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

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

	public void decryptRandomKey(String filePath) {
		try {
			//Creates the cipher that will decrypt the key using Bob's private key
			Cipher decrypter = Cipher.getInstance("RSA");
			decrypter.init(Cipher.DECRYPT_MODE, keyPair.getPrivate());
			
			//Reads the encrypted key from the file and decrypts it
			byte[] encryptedK = readBytesFromFile(filePath);
			byte[] decryptedK = decrypter.doFinal(encryptedK);
			k = ByteBuffer.wrap(decryptedK).getInt();
			System.out.println("Bob's decrypted key: " + k);
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receiveAesMessage(String filePath) {
		try {
			//Creates the cipher that will decrypt Alice's message using the previous k
			Cipher decrypter = Cipher.getInstance("AES");
			SecretKeySpec secretKey = new SecretKeySpec(ByteBuffer.allocate(16).putInt(k).array(), "AES");
			decrypter.init(Cipher.DECRYPT_MODE, secretKey);
			
			//Reads the encrypted message from the file and decrypts it
			byte[] encryptedMessage = readBytesFromFile(filePath);
			byte[] decryptedMessage = decrypter.doFinal(encryptedMessage);
			System.out.print("Bob's decrypted message (in bytes): \n\t");
			for(int i = 0; i < decryptedMessage.length; i++) {
				System.out.print(decryptedMessage[i]);
				if(i != decryptedMessage.length-1)
					System.out.print(", ");
			}
			System.out.println();
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receiveHmacAuthMessage(String messagePath, String hashPath) {
		try {
			//Reads Alice's message and hash			
			byte[] message = readBytesFromFile(messagePath);
			byte[] hash = readBytesFromFile(hashPath);
			
			//Creates the message authenticator that will encrypt the message using previous k as the key
			Mac hMac = Mac.getInstance("HmacSHA256");
			SecretKeySpec secret_key = new SecretKeySpec(ByteBuffer.allocate(16).putInt(k).array(), "HmacSHA256");
			hMac.init(secret_key);

			//Hashes Alice's message and compares it with her hash to authenticate
			byte[] newHash = hMac.doFinal(message);
			System.out.print("Bob's computed hash (in bytes): \n\t");
			for(int i = 0; i < newHash.length; i++) {
				System.out.print(newHash[i]);
				if(i != newHash.length-1)
					System.out.print(", ");
			}
			System.out.println();
			
			//Checks that the hashes are equal
			if(Arrays.equals(newHash, hash)) {
				System.out.println("Alice's message has not been modified!");
			}
			else {
				System.out.println("This hash is not the same as Alice's, could have been modified.");
			}
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void receiveSignedMessage(String messagePath, String sigPath) {
		try {
			//Reads Alice's message and signature
			byte[] message = readBytesFromFile(messagePath);
			byte[] signature = readBytesFromFile(sigPath);
						
			//Creates the object that will verify alice's message using her public key
			final Signature getSignature = Signature.getInstance("SHA256withRSA");
			getSignature.initVerify(Main.alicePublicKey);
			getSignature.update(message);

			//Verifies the message
		    if(getSignature.verify(signature)) {
		    	System.out.println("This signature is verified for the message received!");
		    }
		    else {
		    	System.out.println("The signature is not verified, it may not be from Alice.");
		    }
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}

	public PublicKey getPublicKey() {
		return keyPair.getPublic();
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
