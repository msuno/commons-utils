package cn.msuno.commons.util;

public class CommonsUtils {
    public static void reverse(byte[] array) {
        if (array != null) {
            int i = 0;
            for(int j = array.length - 1; j > i; ++i) {
                byte tmp = array[j];
                array[j] = array[i];
                array[i] = tmp;
                --j;
            }
            
        }
    }
    
    public static boolean isBlank(String str) {
        int strLen;
        if (str != null && (strLen = str.length()) != 0) {
            for(int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(str.charAt(i))) {
                    return false;
                }
            }
            
            return true;
        } else {
            return true;
        }
    }
}
