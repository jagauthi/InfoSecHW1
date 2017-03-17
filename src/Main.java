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
		step2();
		step3();
	}
	
	public void step1() {
		System.out.println("Step 1:");
		alice.generateRandomKey("randomKey.txt");
		bob.decryptRandomKey("randomKey.txt");
		System.out.println();
	}
	
	public void step2() {
		System.out.println("Step 2:");
		alice.sendAesMessage("aesMessage.txt");
		bob.receiveAesMessage("aesMessage.txt");
		System.out.println();
	}
	
	public void step3() {
		System.out.println("Step 3:");
		alice.sendHmacAuthMessage("step3Message.txt", "step3Hash.txt");
		bob.receiveHmacAuthMessage("step3Message.txt", "step3Hash.txt");
		System.out.println();
	}
	
	public static void main(String args[]) {
		new Main();
	}
}
