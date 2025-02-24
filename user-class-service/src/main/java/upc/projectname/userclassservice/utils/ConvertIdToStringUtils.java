package upc.projectname.userclassservice.utils;

public class ConvertIdToStringUtils {

    public static String convertIdToStringUtils(int number, int length) {
        final String ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
        int base = ALPHABET.length();
        StringBuilder sb = new StringBuilder();
        while (number > 0) {
            int remainder = number % base;
            sb.append(ALPHABET.charAt(remainder));
            number /= base;
        }
        // 反转字符串
        sb.reverse();
        // 不足指定长度的左侧补 0
        while (sb.length() < length) {
            sb.insert(0, "0");
        }
        return sb.toString();
    }


}
