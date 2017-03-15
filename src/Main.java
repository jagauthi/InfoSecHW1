
public class Main {
	
	static int alicePublicKey, bobPublicKey;
	Alice alice;
	Bob bob;
	
	public Main() {
		alicePublicKey = 1;
		bobPublicKey = 1;
		alice = new Alice();
		bob = new Bob();
	}
}
