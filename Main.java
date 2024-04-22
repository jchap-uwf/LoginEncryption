import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {

	static ArrayList<byte[]> hashes = new ArrayList<byte[]>(3);
	static ArrayList<String> hexes = new ArrayList<String>(3);

	private static String bytesToHex(byte[] hash) {
		StringBuilder hexString = new StringBuilder(2 * hash.length);
		for (int i = 0; i < hash.length; i++) {
			String hex = Integer.toHexString(0xff & hash[i]);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	private static ArrayList<String> createHash(String username, String password) throws NoSuchAlgorithmException {

		hexes.add(createHashUsr(username));

		MessageDigest digest = MessageDigest.getInstance("SHA-256");

		byte[] encodedhash = digest.digest(
				password.getBytes(StandardCharsets.UTF_8));

		hexes.add(bytesToHex(encodedhash));

		return hexes;
	}

	private static String createHashUsr(String username) throws NoSuchAlgorithmException {

		MessageDigest digest = MessageDigest.getInstance("SHA3-256");

		byte[] encodedhash = digest.digest(
				username.getBytes(StandardCharsets.UTF_8));

		return bytesToHex(encodedhash);
	}

	public static void replaceLines(String ltr) {
		try {
			// input the (modified) file content to the StringBuffer "input"
			BufferedReader file = new BufferedReader(new FileReader("/chaplin-ngenius-login-code-package/src/accounts.txt"));
			StringBuffer inputBuffer = new StringBuffer();
			String line;

			while ((line = file.readLine()) != null) {
				line = ltr;// replace the line here
				inputBuffer.append(line);
				inputBuffer.append('\n');
			}
			file.close();

			// write the new string with the replaced line OVER the same file
			FileOutputStream fileOut = new FileOutputStream("/chaplin-ngenius-login-code-package/src/accounts.txt");
			fileOut.write(inputBuffer.toString().getBytes());
			fileOut.close();

		} catch (Exception e) {
			System.out.println("Problem reading file.");
		}
	}

	public static void main(String[] args) throws NoSuchAlgorithmException, IOException {
		Scanner scnr = new Scanner(System.in);

		ArrayList<String> hashList = new ArrayList<>(2);

		HashMap<String, String> hashMap = new HashMap<String, String>();

		File file = new File("/chaplin-ngenius-login-code-package/src/accounts.txt");
		FileReader fr = new FileReader(file);
		BufferedReader br = new BufferedReader(fr);
		FileWriter fw = new FileWriter(file);
		BufferedWriter bw = new BufferedWriter(fw);

		String username = "";
		char[] pword;
		String password = "";
		String choice;
		String hashString;
		String fileString;
		int c;
		boolean loginFlag = false;

		do {
			Console console = System.console();
			System.out.println("Welcome to the NextGen Navigator! Please enter an option below.");
			System.out.println("1. Login with existing account");
			System.out.println("2. Create account");
			System.out.println("3. Reset Password");
			System.out.println("4. Exit");

			c = scnr.nextInt();

			switch (c) {
				case 1:
					try {
						hashList.clear();
						hashString = "";
						fileString = "";
						username = "";
						password = "";

						// prompt user for info

						username = console.readLine("Username: ");
						pword = console.readPassword("Password: ");

						for (int x = 0; x < pword.length; x++) {
							password += Character.toString(pword[x]);
						}

						username += "4078";
					

						if (hashMap.containsKey(username) && loginFlag == false) {
							hashString = hashMap.get(username);

							do {
								fileString = br.readLine();
								if (hashMap.containsValue(fileString)) {
									System.out.println("Welcome, " + username + ", you are now logged in.");
									loginFlag = true;
									break;
								}

							} while (br.read() != -1);

						} else if (loginFlag == true) {
							if (hashMap.containsKey(username)) {
								System.out.println("Welcome, " + username + ", you are now logged in.");
								loginFlag = true;
								break;
							}
						} else {
							System.out.println("Account not found!");
						}

					} catch (FileNotFoundException e) {
						System.out.println("File not found.");
						e.printStackTrace();
						System.exit(0);
					}

					break;
				case 2:
					password = "";
					hashList.clear();
					System.out.println("Create a new User? y or n: ");
					choice = scnr.next();
					choice.toLowerCase();
					switch (choice) {
						case "n":
							break;
						case "y":

							username = console.readLine("Username: ");
							pword = console.readPassword("Password: ");

							for (int x = 0; x < pword.length; x++) {
								password += Character.toString(pword[x]);
							}

							username += "4078";

							hashList = createHash(username, password); // contains the salted username and password

							System.out.println(hashList.get(0));
							System.out.println(hashList.get(1));

							hashString = hashList.get(0) + hashList.get(1); // concat the hashes for the username and
																			// password
							hashMap.put(username, hashString); // place key and hash value in map

							bw.write(hashString); // writes user and pass hash to one line then newlines
							bw.newLine();

							System.out.println("Username and password successfully created!");
							bw.flush();
							break;
						default:
							System.out.println("Please enter either y or n.");
							break;
					}

					break;
				case 3:
					char[] p;
					password = "";
					br = new BufferedReader(fr);
					if (loginFlag == true) {

						p = console.readPassword("Enter a new password: ");

						if (hashMap.containsKey(username)) {
							for (int x = 0; x < p.length; x++) {
								password += Character.toString(p[x]);
							}

						}

						hashList = createHash(username, password);

						hashString = hashList.get(0) + hashList.get(1);

						if (hashMap.containsKey(username)) {

							hashMap.put(username, hashString);
							replaceLines(hashString);

						}

						System.out.println("Password reset successfully");

					} else {
						System.out.println("You are not logged in!");
					}
					break;
				case 4:
					System.exit(0);
					break;
				default:
					System.out.println("Please enter 1, 2, 3 or 4");
					break;
			}

		} while (c != 1 || c != 2 || c != 3 || c != 4);// end of outermost loop

		fw.close();
		br.close();
	} // end of main

}
