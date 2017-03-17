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
		step4();
	}
	
	public void step1() {
		System.out.println("Step 1:");
		alice.generateRandomKey("step1Key.txt");
		bob.decryptRandomKey("step1Key.txt");
		System.out.println();
	}
	
	public void step2() {
		System.out.println("Step 2:");
		alice.sendAesMessage("step2Message.txt");
		bob.receiveAesMessage("step2Message.txt");
		System.out.println();
	}
	
	public void step3() {
		System.out.println("Step 3:");
		alice.sendHmacAuthMessage("step3Message.txt", "step3Hash.txt");
		bob.receiveHmacAuthMessage("step3Message.txt", "step3Hash.txt");
		System.out.println();
	}
	
	public void step4() {
		System.out.println("Step 4:");
		alice.sendSignedMessage("step4Message.txt", "step4Signature.txt");
		bob.receiveSignedMessage("step4Message.txt", "step4Signature.txt");
		System.out.println();
	}
	
	public static void main(String args[]) {
		new Main();
	}
}
