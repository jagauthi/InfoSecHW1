import java.security.PublicKey;

public class Main {
	
	static PublicKey alicePublicKey, bobPublicKey;
	Alice alice;
	Bob bob;
	
	public Main() {
		alice = new Alice();
		bob = new Bob();
		
		alicePublicKey = alice.getPublicKey();
		bobPublicKey = bob.getPublicKey();
		
		step1();
	}
	
	public void step1() {
		alice.generateRandomKey("randomKey.txt");
		bob.decryptRandomKey("randomKey.txt");
	}
	
	public static void main(String args[]) {
		new Main();
	}
}
