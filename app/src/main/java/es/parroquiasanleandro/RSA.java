package es.parroquiasanleandro;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class RSA {
    public static final String OPCION_RSA = "RSA/ECB/OAEPwithSHA-1andMGF1Padding";

    public PrivateKey PrivateKey = null;
    public PublicKey PublicKey = null;

    public Context context;

    public RSA() {}

    public RSA(Context context){
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setPrivateKeyString(String key) throws NoSuchAlgorithmException, InvalidKeySpecException{
        byte[] encodedPrivateKey = Base64.decode(key, Base64.DEFAULT);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(encodedPrivateKey);
        this.PrivateKey = keyFactory.generatePrivate(privateKeySpec);
    }

    public void setPublicKeyString(String key) throws NoSuchAlgorithmException, InvalidKeySpecException{

        byte[] encodedPublicKey = Base64.decode(key, Base64.DEFAULT);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(encodedPublicKey);

        this.PublicKey = keyFactory.generatePublic(publicKeySpec);
    }

    public String getPrivateKeyString(){
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(this.PrivateKey.getEncoded());
        return bytesToString(pkcs8EncodedKeySpec.getEncoded());
    }

    public String getPublicKeyString(){
        X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(this.PublicKey.getEncoded());
        return bytesToString(x509EncodedKeySpec.getEncoded());
    }


    public void genKeyPair(int size) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException  {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(size);
        KeyPair kp = kpg.genKeyPair();

        PublicKey publicKey = kp.getPublic();
        PrivateKey privateKey = kp.getPrivate();

        this.PrivateKey = privateKey;
        this.PublicKey = publicKey;
    }

    public String encrypt(String plain) throws NoSuchAlgorithmException,NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, NoSuchProviderException {
        byte[] encryptedBytes;

        Cipher cipher = Cipher.getInstance(OPCION_RSA);
        cipher.init(Cipher.ENCRYPT_MODE, this.PublicKey);
        encryptedBytes = cipher.doFinal(plain.getBytes());

        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    public String decrypt(String result) throws NoSuchAlgorithmException,NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {

        byte[] decryptedBytes;

        Cipher cipher = Cipher.getInstance(OPCION_RSA);
        cipher.init(Cipher.DECRYPT_MODE, this.PrivateKey);
        decryptedBytes = cipher.doFinal(Base64.decode(result, Base64.DEFAULT));

        return new String(decryptedBytes);
    }

    public JSONObject decriptJsonObject (JSONObject jsonObject, String[] objectNamesToDecrypt) {
        try {
            for (String x : objectNamesToDecrypt) {
                if (!jsonObject.getJSONObject("usuario").get(x).equals("")) {
                    jsonObject.getJSONObject("usuario").put(x, decrypt((String) jsonObject.getJSONObject("usuario").get(x)));
                }
            }
        } catch (JSONException | IllegalBlockSizeException | NoSuchPaddingException |
                 NoSuchAlgorithmException | BadPaddingException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }
        return jsonObject;
    }

    public Map<String, String> encryptLongText(String plain) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidKeySpecException, NoSuchProviderException, InvalidAlgorithmParameterException {
        SecureRandom secureRandom = new SecureRandom();
        byte[] iv = new byte[16];
        secureRandom.nextBytes(iv);
        IvParameterSpec ivspec = new IvParameterSpec(iv);


        KeyGenerator generator = KeyGenerator.getInstance("AES");
        generator.init(256); // The AES key size in number of bits
        SecretKey secKey = generator.generateKey();
        SecretKeySpec secretKeySpec = new SecretKeySpec(secKey.getEncoded(), "AES");

        Log.d("RSA secKey", bytesToString(secretKeySpec.getEncoded()));
        Log.d("RSA secKey", new String(secretKeySpec.getEncoded()));
        Log.d("RSA secKey", Base64.encodeToString(secretKeySpec.getEncoded(), Base64.DEFAULT));

        Cipher aesCipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        aesCipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivspec);

        //        byte[] iv = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, };
        //        IvParameterSpec  ivParameterSpec;
        //        ivParameterSpec = new IvParameterSpec(iv);
        //
        //        try {
        //            aesCipher.init(Cipher.ENCRYPT_MODE, secKey, ivParameterSpec);
        //        } catch (InvalidAlgorithmParameterException e) {
        //            throw new RuntimeException(e);
        //        }
//        aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
        Log.d("RSA getIV() ", bytesToString(aesCipher.getIV()));

        byte[] byteCipherText = aesCipher.doFinal(plain.getBytes());
        byte[] encryptedData = new byte[iv.length + byteCipherText.length];

        System.arraycopy(iv, 0, encryptedData, 0, iv.length);
        System.arraycopy(byteCipherText, 0, encryptedData, iv.length, byteCipherText.length);

        Log.d("RSA encryptedData", Base64.encodeToString(encryptedData, Base64.DEFAULT));


        String encryptedRSAKey = encrypt(Base64.encodeToString(secKey.getEncoded(), Base64.DEFAULT));
//        Log.d("RSA encryptedRSAKey", encryptedRSAKey);
        String encryptText = Base64.encodeToString(byteCipherText, Base64.DEFAULT);

        Map<String, String> encryptedTextObject = new HashMap<>();
        encryptedTextObject.put("encryptedKey", encryptedRSAKey);
        encryptedTextObject.put("encryptText", encryptText);

        return encryptedTextObject;

    }

    public String bytesToString(byte[] b) {
        byte[] b2 = new byte[b.length + 1];
        b2[0] = 1;
        System.arraycopy(b, 0, b2, 1, b.length);
        return new BigInteger(b2).toString(36);
    }

    public byte[] stringToBytes(String s) {
        byte[] b2 = new BigInteger(s, 36).toByteArray();
        return Arrays.copyOfRange(b2, 1, b2.length);
    }


    public void saveToDiskPrivateKey(String path){
        try {
            FileOutputStream outputStream = null;
            outputStream =  this.context.openFileOutput(path, Context.MODE_PRIVATE);
            outputStream.write(this.getPrivateKeyString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.d("RSA:","Error write PrivateKey");
        }
    }

    public void saveToDiskPublicKey(String path) {
        try {
            FileOutputStream outputStream = null;
            outputStream =  this.context.openFileOutput(path, Context.MODE_PRIVATE);
            outputStream.write(this.getPublicKeyString().getBytes());
            outputStream.close();
        } catch (Exception e) {
            Log.d("RSA:","Error write Public");
        }
    }

    public void openFromDiskPublicKey(String path) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        String content = this.readFileAsString(path);
        this.setPublicKeyString(content);
    }

    public void openFromDiskPrivateKey(String path) throws InvalidKeySpecException, NoSuchAlgorithmException, IOException {
        String content = this.readFileAsString(path);
        this.setPrivateKeyString(content);
    }


    private String readFileAsString(String filePath) throws IOException {

        BufferedReader fin = new BufferedReader(new InputStreamReader(context.openFileInput(filePath)));
        String txt = fin.readLine();
        fin.close();
        return txt;

    }

}
