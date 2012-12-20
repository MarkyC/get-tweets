package name.marcocirillo;

public class DebugCrash {
	
	public static void printDebugInfo(String errMsg, Exception error) {
        System.out.println(errMsg + ": " + error.getMessage());
        System.out.println("Please paste this stackTrace when filing a bug report: ");
		error.printStackTrace();
		System.exit(-1);
	}

}
