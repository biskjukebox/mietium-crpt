import javax.crypto.*;
import java.security.*;
import java.io.*; 
import javax.crypto.spec.*;

public class semana2
{
	public static void main(String[] args)
	{
	
	try 
	{
		KeyGenerator gerador = KeyGenerator.getInstance("RC4"); //instancia geradora de chaves
		Key key = gerador.generateKey(); //gerar a chave
		
		//alternativa: SecretKeySpec
		byte[] keykey = key.getEncoded();		
		SecretKeySpec chave = new SecretKeySpec(keykey,"RC4");
		
		//guardar a palavra-passe num ficheiro
		byte[] pass = chave.getEncoded();
		FileOutputStream passwd = new FileOutputStream("/home/jp/Documentos/CRIPTO/pass.txt"); 
	    	passwd.write(pass);
	    	passwd.close();

		Cipher cifra = Cipher.getInstance("RC4"); //gerar uma cifra
		cifra.init(Cipher.ENCRYPT_MODE, chave); //inicializa a cifra para dizer o que quero fazer com ela: encriptar

		//byte[] data = Files.getBytes(newFile("/home/Documentos/SO/zeto.txt").toPath());
		FileInputStream stream=null;
               	File txt = new File("/home/jp/Documentos/CRIPTO/enc.txt"); //local onde se encontra o ficheiro enc
               	byte[] data = new byte[(int) txt.length()];
        
        	//converter ficheiro para um array de bytes
		stream = new FileInputStream(txt);
		stream.read(data);
		stream.close();
		       
		for (int i = 0; i < data.length; i++) {
			System.out.println(/*(char)*/data[i]);}

		//encriptar a mensagem usando o doFinal
		byte[] resultado = cifra.doFinal(data);
		System.out.println("Dados Encriptados: " + new String(resultado));	

		//reinicializar a cifra para desencriptar
		cifra.init(Cipher.DECRYPT_MODE, chave);
	        byte[] original = cifra.doFinal(resultado);
		System.out.println("Dados desencriptados: " + new String(original));

		//converter array de bytes para um ficheiro
		FileOutputStream saida = new FileOutputStream("/home/jp/Documentos/CRIPTO/dec.txt"); 
	    	saida.write(original);
	    	saida.close();

	}
	catch (NoSuchAlgorithmException e) {
        	e.printStackTrace();}

	catch (InvalidKeyException e) {
        	e.printStackTrace();}

	catch (IllegalBlockSizeException e) {
        	e.printStackTrace();}

	catch (NoSuchPaddingException e) {
		e.printStackTrace();}

	catch (BadPaddingException e) {
            e.printStackTrace();}
	
	catch (FileNotFoundException e) {
            e.printStackTrace();}

	catch (IOException e) {
            e.printStackTrace();}
        }
} 
    
