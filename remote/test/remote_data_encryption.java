import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class remote_data_encryption {
    public static void main(String[] args) throws Exception {
        int block_size = 16;
        String key = "A0A94477CA492BF4EA54C8B2A0617449";
        String iv = "C196C6DB0B0EDC093E4C5F513C75B75A";
        String input = "24_2021-05-09_15:30:57.837963";
        IvParameterSpec ivSpec = new IvParameterSpec(hexStringToByteArray(iv));
        byte[] keyBarray = hexStringToByteArray(key);
        SecretKeySpec keySpec = new SecretKeySpec (keyBarray, 0, keyBarray.length, "AES");

        String text = "24_2021-05-09_15:30:57.837963";

        Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec);
        System.out.println(input.length());
        System.out.println(pad(input, block_size));
        byte[] cipherText = cipher.doFinal(pad(input, block_size).getBytes());
        System.out.println(Base64.getEncoder().encodeToString(cipherText));
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                             + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    public static String pad(String s, int BLOCK_SIZE) {
        String new_s = s;
        for (int i = 0; i < BLOCK_SIZE - s.length() % BLOCK_SIZE; i++)
        {
            new_s = new_s + ((char)(BLOCK_SIZE - s.length() % BLOCK_SIZE));
        }
        return new_s;
    }
}
