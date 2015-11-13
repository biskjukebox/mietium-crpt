import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import java.io.*;
import java.nio.file.*;
import java.security.KeyStore;
import java.util.Arrays;

public class Semana34 {

    public static void main(String[] args) throws Exception {

        char password[] = "mietiumcrpt".toCharArray();
        KeyStore.PasswordProtection pass = new KeyStore.PasswordProtection(password);

        byte mensagem[], hash[] = null;

        if (args[0].contains("-keygen"))
        {
            KeyGenerator kg = KeyGenerator.getInstance("AES");
            kg.init(128);
            SecretKey sk = kg.generateKey();
            byte[] chave = sk.getEncoded();
            //operacoes com a keystore
            KeyStore keyStore = crearKeyStore(args[1], "KeyStore");
            KeyStore.SecretKeyEntry skEntry = new KeyStore.SecretKeyEntry(sk);
            keyStore.setEntry("chave", skEntry, pass);
            keyStore.store(new FileOutputStream(args[1]), "KeyStore".toCharArray());
        }

        if (args[0].contains("-enc"))
        {
            KeyStore keyStore = crearKeyStore(args[1], "KeyStore");
            KeyStore.Entry entrada = keyStore.getEntry("chave", pass);
            SecretKey encontraChave = ((KeyStore.SecretKeyEntry) entrada).getSecretKey();
            //inicializa o mac
            Mac MD5 = Mac.getInstance("HMACMD5");
            MD5.init(encontraChave);
            //vetor de inicializacao do algoritmo
            byte[] iv = { 1, 2, 1, 0, 1, 2, 1, 6, 8, 3, 7, 1, 1, 1, 4, 5 };
            IvParameterSpec ivSpec = new IvParameterSpec(iv); //IV incializado
            Path caminho = Paths.get(args[2]); //ficheiro a encriptar
            byte[] dados = Files.readAllBytes(caminho);
            //comecar a cifrar
            Cipher cif = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cif.init(Cipher.ENCRYPT_MODE, encontraChave, ivSpec);
            mensagem = cif.doFinal(dados);
            hash=MD5.doFinal(mensagem);
            byte[] criptograma = new byte[mensagem.length + MD5.getMacLength()]; //concatena a mensagem com o padding
            //envia o criptograma
            FileOutputStream out = new FileOutputStream(args[3]);
            out.write(criptograma);
            out.close();
        }

        if (args[0].contains("-dec"))
        {
            byte[] mac, input = null;

            KeyStore keyStore = crearKeyStore(args[1], "KeyStore");
            KeyStore.Entry entrada = keyStore.getEntry("chave", pass);
            SecretKey encontraChave = ((KeyStore.SecretKeyEntry) entrada).getSecretKey();
            Mac MD5 = Mac.getInstance("HMACMD5");
            MD5.init(encontraChave);
            byte[] iv = { 1, 2, 1, 0, 1, 2, 1, 6, 8, 3, 7, 1, 1, 1, 4, 5 };
            IvParameterSpec ivSpec = new IvParameterSpec(iv);
            Cipher cif = Cipher.getInstance("AES/CBC/PKCS5Padding");
            //comecar a decifrar
            cif.init(Cipher.DECRYPT_MODE, encontraChave, ivSpec);
            Path caminho = Paths.get(args[2]);
            byte[] dados = Files.readAllBytes(caminho);
            mensagem = MD5.doFinal(dados);
            hash = Arrays.copyOfRange(dados, 0, 32);
            mac = Arrays.copyOfRange(dados, 32,48);
            input = MD5.doFinal(hash);

            if(mac.equals(input))
            {
                byte[] dadosFinais = cif.doFinal(hash);
                FileOutputStream out = new FileOutputStream(args[3]);
                out.write(dadosFinais);
                out.close();
            }
            else
            {
                byte[] dadosFinais = cif.doFinal(hash);
                FileOutputStream out = new FileOutputStream(args[3]);
                out.write(dadosFinais);
                out.close();
            }
        }
    }

    private static KeyStore crearKeyStore(String nomeFicheiro, String nome) throws Exception {
        File ficheiro = new File(nomeFicheiro);
        KeyStore keyStore = KeyStore.getInstance("JCEKS");

        if (ficheiro.exists()) 
        {   keyStore.load(new FileInputStream(nomeFicheiro), nome.toCharArray());} 
        else 
        {
            keyStore.load(null, null);
            keyStore.store(new FileOutputStream(nomeFicheiro), nome.toCharArray());
        }
        return keyStore;
    }
}
