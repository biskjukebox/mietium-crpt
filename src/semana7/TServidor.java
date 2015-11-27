import javax.crypto.*;
import javax.crypto.spec.*;
import java.io.*;
import java.math.BigInteger;
import java.net.*;
import java.security.*;
import java.util.*;

public class TServidor extends Thread {

    static BigInteger p = new BigInteger("99494096650139337106186933977618513974146274831566768179581759037259788798151499814653951492724365471316253651463342255785311748602922458795201382445323499931625451272600173180136123245441204133515800495917242011863558721723303661523372572477211620144038809673692512025566673746993593384600667047373692203583");
    static BigInteger g = new BigInteger("44157404837960328768872680677686802650999163226766694797650810379076416463147265401084491113667624054557335394761604876882446924929840681990106974314935015501571333024773172440352475358750668213444607353872754650805031912866692119819377041901642732455911509867728218394542745330014071040326856846990119719675");
    private int ct;
    protected Socket s;

    public TServidor(Socket s, int c) {
        ct = c;
        this.s = s;
    }

    public void run() {
        try {
            ObjectInputStream ois = new ObjectInputStream(s.getInputStream());
            ObjectOutputStream oos = new ObjectOutputStream(s.getOutputStream());

            BigInteger y = new BigInteger(g.bitLength(), new Random()); B=gy
            BigInteger gx = (BigInteger) ois.readObject();
            BigInteger gxy = g.modPow(gx, p);
            BigInteger gy = g.modPow(b, p);
            oos.writeObject(gy);
            //chave para decifrar
            MessageDigest sha256 = MessageDigest.getInstance("SHA-256");
            byte[] arrayRawBits = sha256.digest(gxy.toByteArray());
            SecretKey key = new SecretKeySpec(arrayRawBits, 0, 16, "AES");
            //lê o IV
            byte[] ivbits = (byte[]) ois.readObject();
            IvParameterSpec iv = new IvParameterSpec(ivbits);
            //inicia a cifra com o IV recebido
            Cipher c = Cipher.getInstance("AES/CTR/NoPadding");
            c.init(Cipher.DECRYPT_MODE, key, iv);
            //inicia o mac
            Mac m = Mac.getInstance("HmacSHA1");
            m.init(new SecretKeySpec(arrayRawBits, 16, 16, "HmacSHA1"));
            byte[] cipherText, clearText, mac;

            try {
                while (true) {
                    cipherText = (byte[]) ois.readObject();
                    mac = (byte[]) ois.readObject();
                    if (Arrays.equals(mac, m.doFinal(cipherText))) {
                        clearText = c.update(cipherText);
                        System.out.println(ct + ":" + new String(clearText));
                    } else {
                        System.out.println(ct + ": Falhou!");
                    }
                }
            } catch (EOFException e) {
                System.out.println("[" + ct + "]");
            } finally {
                if (ois != null) ois.close();
                if (oos != null) oos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
