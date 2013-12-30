package sg.edu.nus.protocol;

import java.io.IOException;
import java.io.Serializable;
import java.security.Key;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SealedObject;

import sg.edu.nus.util.EncryptionUtils;

/**
 * This class embrace a message inside and provide functions to encrypt/decrypt the message 
 * 
 * @author VHTam
 * @version 1.0 2010-03-18
 */

public class MessageCrypter implements Serializable{
	
	private static final long serialVersionUID = 1632642419434407888L;
	
	private SealedObject sealedObj = null;
	
	public MessageCrypter(){
		
	}
	
	public void encrypt(Serializable originObj, Cipher ecipher){
		try {
			sealedObj = new SealedObject(originObj, ecipher);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Object decrypt(Cipher dcipher){
		
		try {
			return sealedObj.getObject(dcipher);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		return null;
	}

	public String getAlgorithm(){
		return sealedObj.getAlgorithm();
	}

	private void test(){
	    try {
	        // Generate a temporary key. In practice, you would save this key.
	    	
	    	Key key = EncryptionUtils.getKey("236-117-181-248-76-176-76-84");	    	    	
	        // Prepare the encrypter
	        Cipher ecipher = Cipher.getInstance("DES");
	        ecipher.init(Cipher.ENCRYPT_MODE, key);
	        
	        
	        this.encrypt(new String("secret information..."), ecipher);
	        
	    
	        // Prepare the decrypter
	        
	        Cipher dcipher = Cipher.getInstance("DES");
	        dcipher.init(Cipher.DECRYPT_MODE, key);
	    
	        String o = null;
	        o = (String)this.decrypt(dcipher);
	        System.out.println("Object decrypted: "+o);

	    } catch (javax.crypto.NoSuchPaddingException e) {
	    } catch (java.security.NoSuchAlgorithmException e) {
	    } catch (java.security.InvalidKeyException e) {
	    }
		
	}
	
	public static void main (String[] args){
		
		new MessageCrypter().test();
	}
	

}
