import javax.crypto.*;
import java.security.*;
import java.io.*; 

public class semana2
{
	public static void main(String[] args)
	{
	
	try 
	{
		KeyGenerator gerador = KeyGenerator.getInstance("RC4"); //instancia geradora de chaves
		Key chave = gerador.generateKey(); //gerar a chave

		Cipher cifra = Cipher.getInstance("RC4"); //gerar uma cifra
		cifra.init(Cipher.ENCRYPT_MODE, chave); //inicializa a cifra para dizer o que quero fazer com ela: encriptar

		//encriptar a mensagem usando o doFinal

		//byte[] data = "Hello World!".getBytes();
		//byte[] data = Files.getBytes(newFile("/home/Documentos/SO/zeto.txt").toPath());
		//byte[] resultado = cifra.doFinal(data);

		FileInputStream stream=null;
               	File txt = new File("/home/jp/Documentos/CRIPTO/mietium-crpt/src/RC4/enc.txt"); //local onde se encontra o ficheiro enc
               	byte[] data = new byte[(int) txt.length()];
        
        	//converter ficheiro para um array de bytes
		stream = new FileInputStream(txt);
		stream.read(data);
		stream.close();
		       
		for (int i = 0; i < data.length; i++) {
			System.out.println(/*(char)*/data[i]);}

		byte[] resultado = cifra.doFinal(data);
		System.out.println("Dados Encriptados: " + new String(resultado));	

		//reinicializar a cifra para desencriptar
		cifra.init(Cipher.DECRYPT_MODE, chave);
	        byte[] original = cifra.doFinal(resultado);
		System.out.println("Dados desencriptados: " + new String(original));

		//converter array de bytes para um ficheiro
		FileOutputStream saida = new FileOutputStream("/home/jp/Documentos/CRIPTO/mietium-crpt/src/RC4/dec.txt"); 
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
    
