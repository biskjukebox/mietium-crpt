import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.*;

public class Cliente {
    static DHParameterSpec dHSpec;
    static public void main(String []args) {

        try {
            String test;
            Socket s = new Socket("localhost",4567);
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            AlgorithmParameterGenerator genAl= AlgorithmParameterGenerator.getInstance("DH");
            genAl.init(1024);
            AlgorithmParameters par = genAl.generateParameters();
            DHParameterSpec dHSpec = par.getParameterSpec(DHParameterSpec.class);
            //cria e envia os parametros G e P para o Servidor
            BigInteger g=dHSpec.getG();
            BigInteger p=dHSpec.getP();
            oos.writeObject(g);
            oos.writeObject(p);
            //gera o par chave publica-privada
            KeyPairGenerator keyGen = KeyPairGenerator.getInstance("DH");
            keyGen.initialize(dHSpec);
            KeyPair x = keyGen.generateKeyPair();
            oos.writeObject(x.getPublic());//envia A
            Key y = (Key) ois.readObject();//recebe B
            //conclui acordo
            KeyAgreement dH = KeyAgreement.getInstance("DH");
            dH.init(x.getPrivate());
            final Key kP = dH.doPhase(y, true);
            //constrói a chave para cifrar
            MessageDigest sha256=MessageDigest.getInstance("SHA-256");
            byte[] arrayRawBites=sha256.digest(dH.generateSecret()); //torna PASS mais segura
            SecretKey key = new SecretKeySpec(arrayRawBites,0,16,"AES");
            //cria a cifra
            Cipher c=Cipher.getInstance("AES/CTR/NoPadding");
            c.init(Cipher.ENCRYPT_MODE,key);
            byte[] iv=c.getIV();//cria IV a partir da cifra 'c'
            oos.writeObject(iv);//envia IV
            //cria MAC
            Mac m= Mac.getInstance("HmacSHA1");
            m.init(new SecretKeySpec(arrayRawBites,16,16,"HmacSHA1"));
            //envia a mensagem
            BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));
            byte[] cipher_Text, mac;
            while((test=stdIn.readLine())!=null)
            {
                cipher_Text=c.update(test.getBytes("UTF-8"));
                if(cipher_Text!=null)
                {
                    mac=m.doFinal(cipher_Text); //faz o mac do texto cifrado
                    oos.writeObject(cipher_Text); //envia cifra
                    oos.writeObject(mac); //enviar mac da cifra
                }
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
