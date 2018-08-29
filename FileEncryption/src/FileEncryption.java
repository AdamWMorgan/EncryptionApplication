import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Provider.Service;
import java.security.Security;
import java.util.*;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.swing.JFileChooser;

public class FileEncryption {

	Cipher ecipher;
	Cipher dcipher;

	FileEncryption(SecretKey key) throws Exception {
		ecipher = Cipher.getInstance("DESede");
		dcipher = Cipher.getInstance("DESede");
		ecipher.init(Cipher.ENCRYPT_MODE, key);
		dcipher.init(Cipher.DECRYPT_MODE, key);
		System.out.println(ecipher);
		System.out.println(dcipher);
	}

	public String encrypt(String str) throws Exception {
		// encode string into bytes
		byte[] utf8 = str.getBytes("UTF8");

		// encrypt
		byte[] enc = ecipher.doFinal(utf8);

		// encode bytes to base64 to get string
		return new sun.misc.BASE64Encoder().encode(enc);
	}

	public String decrypt(String str) throws Exception {
		byte[] dec = new sun.misc.BASE64Decoder().decodeBuffer(str);

		byte[] utf8 = dcipher.doFinal(dec);

		return new String(utf8, "UTF8");
	}
	
	

	public static void main(String[] args) throws Exception {
//		TreeSet<String> algorithms = new TreeSet<>();
//		for (Provider provider : Security.getProviders())
//		    for (Service service : provider.getServices())
//		        if (service.getType().equals("Signature"))
//		            algorithms.add(service.getAlgorithm());
//		for (String algorithm : algorithms)
//		    System.out.println(algorithm);
		
		
		Scanner in = new Scanner(System.in);
		int enterOpt = 0;
		KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
		keyGenerator.init(112);
		SecretKey key = keyGenerator.generateKey();

		FileEncryption encrypter = new FileEncryption(key);

		do {
			
			System.out.println("Enter 1 to ENCRYPT file or 2 to DECRYPT file");

			enterOpt = in.nextInt();

			if (enterOpt == 1) {
				
				System.out.println("Choose a file to ENCRYPT...");
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(fc);
				long startTime = System.nanoTime();
				System.out.println("Start");
				String returnString = fc.getSelectedFile().getAbsolutePath();
				System.out.println(returnString);

				try {

					int i;
					String file_name = returnString;
					ReadFile file = new ReadFile(file_name);
					String[] aryLines = file.OpenFile();

					FileWriter outFile = new FileWriter(file_name);
					BufferedWriter outStream = new BufferedWriter(outFile);

					try {

						for (i = 0; i < aryLines.length; i++) {

							System.out.println(aryLines[i]);
							
							String encrypted = encrypter.encrypt(aryLines[i]);
							System.out.println(encrypted);

							outStream.write(encrypted);
							outStream.newLine();
						}
						// remove old data from the outStream
						outStream.flush();
						outStream.close();

					} catch (Exception IOException) {
						System.out.println("ERROR WRITING TO FILE!");
						System.out.println(IOException.getMessage());
					}

				} catch (IOException e) {
					System.out.println(e.getMessage());
				}
				long stopTime = System.nanoTime();
				System.out.println(stopTime - startTime);

			} else if (enterOpt == 2) {
				System.out.println("Choose a file to DECRYPT...");
				final JFileChooser fc = new JFileChooser();
				int returnVal = fc.showOpenDialog(fc);
				long startTime = System.nanoTime();
				String returnString = fc.getSelectedFile().getAbsolutePath();
				System.out.println(returnString);

				try {

					int i;
					String file_name = returnString;
					ReadFile file = new ReadFile(file_name);
					String[] aryLines = file.OpenFile();

					FileWriter outFile = new FileWriter(file_name);
					BufferedWriter outStream = new BufferedWriter(outFile);

					try {
						for (i = 0; i < aryLines.length; i++) {
							aryLines[i] = aryLines[i].trim();
							System.out.println(aryLines[i]);

							String decrypted = encrypter.decrypt(aryLines[i]);
							System.out.println(decrypted);

							outStream.write(decrypted);
							outStream.newLine();
						}
						// remove old data from the outStream
						outStream.flush();
						outStream.close();

					} catch (Exception IOException) {
						System.out.println("ERROR WRITING TO FILE!");
					}

				} catch (IOException e) {
					System.out.println(e.getMessage());
				}

				long stopTime = System.nanoTime();
				System.out.println(stopTime - startTime);
			}
		} while (enterOpt < 3);
		in.close();
	}

}


