import javax.crypto.*;
import javax.crypto.spec.*;
import java.math.BigInteger;
import java.net.*;
import java.io.*;
import java.security.*;
import java.util.Arrays;

public class TServidor extends Thread {
	static String CIPHER_MODE = "AES/CTR/NoPadding";
	static DHParameterSpec dHSpec;
	private int ct;
	protected Socket s;
	
	public TServidor(Socket s, int c) {
	ct = c;
	this.s=s;
	}

	public void run() {
	try {
		ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
		ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
		//lê os parametros P e G enviados por Alice
		BigInteger g=(BigInteger) ois.readObject();
		BigInteger p=(BigInteger) ois.readObject();
		dHSpec = new DHParameterSpec(p,g); //cria a instância com P e G
		Key x=(Key) ois.readObject();//Lê x
		//Gera e envia y
		KeyPairGenerator keyGen=KeyPairGenerator.getInstance("DH");
		keyGen.initialize(dHSpec);
		KeyPair y = keyGen.generateKeyPair();
		oos.writeObject(y.getPublic());
		KeyAgreement dH = KeyAgreement.getInstance("DH");
		dH.init(y.getPrivate());
		final Key kP=dH.doPhase(x, true);
		//constrói a chave para ser usada na decifragem
		MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
		byte[] arrayRawBits= sha256.digest(dH.generateSecret());
		SecretKey key = new SecretKeySpec(arrayRawBits,0,16,"AES");
		//lê o IV enviado pelo cliente
		byte[] ivbits=(byte[]) ois.readObject();
		IvParameterSpec iv=new IvParameterSpec(ivbits);
		//inicia a cifra segundo o modo descrito em cima e com o IV recebido
		Cipher c= Cipher.getInstance(CIPHER_MODE);
		c.init(Cipher.DECRYPT_MODE,key,iv);
		//inicia o mac
		Mac m= Mac.getInstance("HmacSHA1");
		m.init(new SecretKeySpec(arrayRawBits,16,16,"HmacSHA1"));
		byte[] cipherText, clearText, mac;
		
			try {
				while (true) 
				{
					cipherText=(byte[]) ois.readObject();
					mac=(byte[]) ois.readObject();
					if(Arrays.equals(mac,m.doFinal(cipherText)))
					{
						clearText=c.update(cipherText);
						System.out.println(ct+":"+new String(clearText));
					}
					else{System.out.println(ct+": Falhou!");}
				}
			} catch (EOFException e) {
				System.out.println("["+ct + "]");
			} finally {
				if (ois!=null) ois.close();
				if (oos!=null) oos.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

