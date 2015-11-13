import java.security.KeyStore;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.security.*;
import java.io.*;
import java.security.cert.CertificateException;
import java.util.Enumeration;

public class semana34 {

    public static void main(String[] args) {

        try {
            KeyStore ks = KeyStore.getInstance("JCEKS");
            char[] password = "jukebox".toCharArray();
            ks.load(null, password); //inicializa sem nada la dentro

            //gerar a SecretKey
            KeyPairGenerator gera = KeyPairGenerator.getInstance("RSA");
            gera.initialize(1024);
            KeyPair kp = gera.generateKeyPair();
            PrivateKey priv = kp.getPrivate();
            SecretKeySpec secreta = new SecretKeySpec(priv.getEncoded(), "AES");

            KeyStore.ProtectionParameter parametro = new KeyStore.PasswordProtection(password);
            //grava a SecretKey na KeyStore
            KeyStore.SecretKeyEntry grava = new KeyStore.SecretKeyEntry(secreta);
            ks.setEntry("Grava a SecretKey", grava, parametro);
            //guarda a KeyStore
            FileOutputStream kss = new FileOutputStream("KeyStore");
            ks.store(kss, password);
            kss.close();

            //verificar o conteudo da keystore
            String alias = "ali";
            Enumeration e = ks.aliases();
            while (e.hasMoreElements()) {
                String name = (String) e.nextElement();
                System.out.print("   "+name+": ");
                if (ks.isKeyEntry(name)) System.out.println(" Key entry:"+ks.getEntry(alias,parametro));
                else System.out.println(" Certificate entry");
            }

            //criar a instancia MAC e inicializa-la com a chave acima
            Mac mac = Mac.getInstance(secreta.getAlgorithm());
            mac.init(secreta);

        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch(InvalidParameterException e){
            e.printStackTrace();
        } catch (UnrecoverableEntryException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }
    }
}
