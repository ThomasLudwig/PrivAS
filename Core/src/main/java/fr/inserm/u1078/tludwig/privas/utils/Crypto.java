package fr.inserm.u1078.tludwig.privas.utils;

import fr.inserm.u1078.tludwig.privas.constants.MSG;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;


/**
 * Class to perform all the Cryptographic operations for this Program All methods and fields are static
 *
 * SecureRandom might be unsupported on some JDK installation. It is disabled by default and one must copy a specific jar to activate it
 * 
 * @author Thomas E. Ludwig (INSERM - U1078) 2019-01-23
 *
 * Javadoc complete on 2019-08-06
 */
public final class Crypto {

  private static final String AES = "AES";
  private static final String RSA = "RSA";
  private static final String SHA256 = "HmacSHA256";
  private static final String AES_CIPHER = "AES/GCM/NoPadding";
  private static final String RSA_CIPHER = "RSA/ECB/PKCS1Padding";
  private static final String N = "\n";
  private static final String NOTHING = "";
  private static final String BYTE_FORMAT = "%02X";
  private static final int RSA_BITS = 2048;
  private static final int SHA_BITS = 256;
  private static final int AES_BITS = 128; //256 is not supported by license
  private static final String RSA_PRIVATE_BEGIN = "-----BEGIN RSA PRIVATE KEY-----";
  private static final String RSA_PRIVATE_END = "-----END RSA PRIVATE KEY-----";
  private static final String RSA_PUBLIC_BEGIN = "-----BEGIN RSA PUBLIC KEY-----";
  private static final String RSA_PUBLIC_END = "-----END RSA PUBLIC KEY-----";

  private Crypto() {
    //This class may not be instantiated
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////   AES   ////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * Generates a random AES key of AES_BITS bits
   *
   * @return
   */
  public static String generateAESKey() {
    byte[] key = new byte[AES_BITS / 8];
    new SecureRandom().nextBytes(key);
    SecretKey secretKey = new SecretKeySpec(key, AES);
    return Base64.getEncoder().withoutPadding().encodeToString(secretKey.getEncoded());
  }

  /**
   * Encrypts the given message with AES
   *
   * @param key     the AES Key
   * @param message the message
   * @return the encrypted message
   * @throws NoSuchAlgorithmException
   * @throws NoSuchPaddingException
   * @throws InvalidKeyException
   * @throws InvalidAlgorithmParameterException
   * @throws IllegalBlockSizeException
   * @throws BadPaddingException
   */
  public static String encryptAES(String key, String message) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
    //Convert String to AES SecretKey
    SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), AES);

    //Create a Random Initialization Vector
    byte[] iv = new byte[12]; //For GCM a 12 byte (not 16!) random (or counter) byte-array is recommend by NIST because it’s faster and more secure.
    new SecureRandom().nextBytes(iv); //NEVER REUSE THIS IV WITH SAME KEY

    //Create a Cipher and init to parameters
    final Cipher cipher = Cipher.getInstance(AES_CIPHER);
    cipher.init(Cipher.ENCRYPT_MODE, secretKey, new GCMParameterSpec(AES_BITS, iv));

    //Encrypt the message
    byte[] cipherText = cipher.doFinal(message.getBytes(StandardCharsets.UTF_8));

    //Concat everything into a Message
    ByteBuffer byteBuffer = ByteBuffer.allocate(4 + iv.length + cipherText.length);
    byteBuffer.putInt(iv.length);
    byteBuffer.put(iv);
    byteBuffer.put(cipherText);
    byte[] cipherMessage = byteBuffer.array();

    //Encode the message as a String
    return Base64.getEncoder().withoutPadding().encodeToString(cipherMessage);
  }

  /**
   * Decrypts the given message with AES
   *
   * @param key              the AES key
   * @param encryptedMessage the encrypted message
   * @return the decrypted message
   * @throws NoSuchAlgorithmException
   * @throws NoSuchPaddingException
   * @throws InvalidKeyException
   * @throws InvalidAlgorithmParameterException
   * @throws IllegalBlockSizeException
   * @throws BadPaddingException
   */
  public static String decryptAES(String key, String encryptedMessage) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
    //Convert String to AES SecretKey
    SecretKey secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), AES);

    //Decode the String in a byte[] containing the whole message
    ByteBuffer byteBuffer = ByteBuffer.wrap(Base64.getDecoder().decode(encryptedMessage));

    //Recover the Initialization Vector
    int ivLength = byteBuffer.getInt();
    if (ivLength < 12 || ivLength >= 16) // check input parameter
      throw new IllegalArgumentException("invalid iv length");
    byte[] iv = new byte[ivLength];
    byteBuffer.get(iv);

    //Create a Cipher and init to parameters
    final Cipher cipher = Cipher.getInstance(AES_CIPHER);
    cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(AES_BITS, iv));

    //Recover the encryptedText
    byte[] cipherText = new byte[byteBuffer.remaining()];
    byteBuffer.get(cipherText);

    //Decrypt the Message
    byte[] plainText = cipher.doFinal(cipherText);

    //return as a String
    return new String(plainText);
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////   SHA   ////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * Format bytes array to String in Hex Format (0-9A-F) (For an array of N bytes, the resulting String has 2*N Bits)
   *
   * @param bytes bytes array to be formatted
   * @return
   */
  private static String bytes2Hex(byte[] bytes) {
    StringBuilder sb = new StringBuilder();
    for (byte b : bytes)
      sb.append(String.format(BYTE_FORMAT, b));
    return sb.toString();
  }

  /**
   * Generates a random byte array
   *
   * @param bits number of bits in the array, must be a multiple of 16 to be stored as bytes
   * @return the Hex representation of the byte array (2 chars per byte)
   */
  public static String generateRandomBytesAsString(int bits) {
    byte[] bytes = new byte[bits / 8];
    new SecureRandom().nextBytes(bytes);
    return bytes2Hex(bytes);
  }

  /**
   * Generate an SHA HashKey of SHA_BITS bits
   *
   * @return the HEX representation of the Key
   */
  public static String generateHashKey() {
    return generateRandomBytesAsString(SHA_BITS);
  }

  /**
   * Hashes the given message with SHA-256
   *
   * @param salt    the salt used to hash
   * @param message the message
   * @return the hashed message
   * @throws NoSuchAlgorithmException          - when Mac.getInstance(SHA256) fails
   * @throws java.security.InvalidKeyException
   */
  public static String hashSHA256(String salt, String message) throws NoSuchAlgorithmException, InvalidKeyException {
    //TODO START DEBUGGING PLUG - REMOVE BEFORE COMMITTING 1.0.4
    if(true)
      return message;
    //TODO END DEBUGGING PLUG
    Mac sha256_HMAC = Mac.getInstance(SHA256);
    sha256_HMAC.init(new SecretKeySpec(salt.getBytes(StandardCharsets.UTF_8), SHA256));
    return bytes2Hex(sha256_HMAC.doFinal(message.getBytes(StandardCharsets.UTF_8)));
  }

  ///////////////////////////////////////////////////////////////////////////////////////////////////
  //////////////////////////////////////////   RSA   ////////////////////////////////////////////////
  ///////////////////////////////////////////////////////////////////////////////////////////////////
  /**
   * Converts bytes in pem String
   *
   * @param bytes the bytes array to be converted
   * @return
   */
  private static String bytes2Pem(byte[] bytes) {
    bytes = Base64.getEncoder().encode(bytes);
    StringBuilder sb = new StringBuilder();
    final int SIZE = 64;
    for (int j = 0; j < bytes.length; j += SIZE)
      for (int i = 0; i < SIZE && j + i < bytes.length; i++) {
        sb.append((char) bytes[j + i]);
        if (i == SIZE - 1)
          sb.append('\n');
      }
    return sb.toString();
  }

  /**
   * Convert bytes in pem String, as a unique line
   *
   * @param bytes the bytes array to be converted
   * @return
   */
  public static String bytes2OneLinePem(byte[] bytes) {
    return bytes2Pem(bytes).replace(N, NOTHING);
  }

  /**
   * Encrypts a message with RSA
   *
   * @param key     the RSA Encryption Key
   * @param message the message to encrypted
   * @return the encrypted message in bytes
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   * @throws InvalidKeyException
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   */
  private static byte[] encryptRSAAsBytes(PublicKey key, String message) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
    Cipher cipher = Cipher.getInstance(RSA_CIPHER);
    cipher.init(Cipher.ENCRYPT_MODE, key);
    return cipher.doFinal(message.getBytes());
  }

  /**
   * Encrypts a message with RSA
   *
   * @param key     the RSA Encryption Key
   * @param message the message to encrypted
   * @return the encrypted message as a String
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   * @throws InvalidKeyException
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   */
  public static String encryptRSA(PublicKey key, String message) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
    return Base64.getEncoder().encodeToString(encryptRSAAsBytes(key, message));
  }

  /**
   * Decrypts a message with RSA
   *
   * @param key              the RSA Decryption Key
   * @param encryptedMessage the encrypted message
   * @return the encrypted message as a String
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   */
  public static String decryptRSA(PrivateKey key, String encryptedMessage) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    return decryptRSA(key, java.util.Base64.getDecoder().decode(encryptedMessage.getBytes()));
  }

  /**
   * Decrypts a message with RSA
   *
   * @param privateKey       the RSA Decryption Key
   * @param encryptedMessage the encrypted message
   * @return the encrypted message in bytes
   * @throws NoSuchPaddingException
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   * @throws BadPaddingException
   * @throws IllegalBlockSizeException
   */
  private static String decryptRSA(PrivateKey privateKey, byte[] encryptedMessage) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
    Cipher cipher = Cipher.getInstance(RSA);
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    return new String(cipher.doFinal(encryptedMessage));
  }

  /**
   * Generates an RSA Key Pair
   *
   * @return The Key Pair, containing the Public Encryption Key and the Private Decryption Key
   * @throws NoSuchAlgorithmException - if the RSA Algorithm is unavailable
   */
  public static KeyPair generateRSAKeyPair() throws NoSuchAlgorithmException {
    KeyPairGenerator kpg = KeyPairGenerator.getInstance(RSA);
    kpg.initialize(RSA_BITS);
    return kpg.generateKeyPair();
  }

  /**
   * Stores an RSA Public Key in a file
   *
   * @param kp       the KeyPair containing the Public Key
   * @param filename the name of the file that will store the Key
   * @throws IOException
   */
  public static void savePublicRSAKey(KeyPair kp, String filename) throws IOException {
    PrintWriter out = new PrintWriter(new FileWriter(filename));
    out.println(Crypto.RSA_PUBLIC_BEGIN);
    out.println(Crypto.bytes2Pem(kp.getPublic().getEncoded()));
    out.println(Crypto.RSA_PUBLIC_END);
    out.close();
  }

  /**
   * Stores an RSA Private Key in a file
   *
   * @param kp       the KeyPair containing the Public Key
   * @param filename the name of the file that will store the Key
   * @throws IOException
   */
  public static void savePrivateRSAKey(KeyPair kp, String filename) throws IOException {
    PrintWriter out = new PrintWriter(new FileWriter(filename));
    out.println(Crypto.RSA_PRIVATE_BEGIN);
    out.println(Crypto.bytes2Pem(kp.getPrivate().getEncoded()));
    out.println(Crypto.RSA_PRIVATE_END);
    out.close();
  }

  /**
   * Gets the Public Key from an RSA Keypair as a one line pem String
   *
   * @param kp the KeyPair containing the public key
   * @return
   */
  public static String getPublicRSAKeyAsString(KeyPair kp) {
    return Crypto.bytes2OneLinePem(kp.getPublic().getEncoded());
  }

  /**
   * Reads a RSA key from a file
   *
   * @param filename the name of the file containing the Key
   * @return the key as a pem String
   * @throws Exception
   */
  private static String readRSA(String filename) throws IOException {
    BufferedReader in = new BufferedReader(new FileReader(filename));
    StringBuilder pem = new StringBuilder();
    String line;
    while ((line = in.readLine()) != null)
      pem.append(line);
    in.close();
    return pem.toString().replace(RSA_PRIVATE_BEGIN, NOTHING).replace(RSA_PRIVATE_END, NOTHING);
  }

  /**
   * Reads an RSA Private key from a file
   * Once the key is loaded, the original file is deleted, insuring that it will not be reusable
   *
   * @param filename the name of the file containing the key
   * @return the Private Key
   * @throws java.io.IOException
   * @throws NoSuchAlgorithmException                   if the RSA Algorithm is unavailable
   * @throws java.security.spec.InvalidKeySpecException
   */
  public static PrivateKey readAndDeletePrivateRSAKey(String filename) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
    String privKeyPEM = readRSA(filename);
    File file = new File(filename);
    if (file.delete())
      return buildPrivateRSAKey(privKeyPEM);
    throw new IOException(MSG.cat(MSG.CRYPTO_FAIL_DELETE, filename));
  }

  /**
   * Builds a PrivateKey object from a Key represented as pem String
   *
   * @param privateKeyPEM the pem String representation of the Key
   * @return the PrivateKey object
   * @throws NoSuchAlgorithmException if the RSA Algorithm is unavailable
   * @throws InvalidKeySpecException
   */
  public static PrivateKey buildPrivateRSAKey(String privateKeyPEM) throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] decoded = Base64.getDecoder().decode(privateKeyPEM);
    PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decoded);
    KeyFactory kf = KeyFactory.getInstance(RSA);
    return kf.generatePrivate(spec);
  }

  /**
   * Builds a PublicKey object from a Key represented as pem String
   *
   * @param publicKeyPEM the pem String representation of the Key
   * @return the PublicKey object
   * @throws NoSuchAlgorithmException if the RSA Algorithm is unavailable
   * @throws InvalidKeySpecException
   */
  public static PublicKey buildPublicRSAKey(String publicKeyPEM) throws NoSuchAlgorithmException, InvalidKeySpecException {
    byte[] decoded = Base64.getDecoder().decode(publicKeyPEM);
    X509EncodedKeySpec spec = new X509EncodedKeySpec(decoded);
    KeyFactory kf = KeyFactory.getInstance(RSA);
    return kf.generatePublic(spec);
  }
}
