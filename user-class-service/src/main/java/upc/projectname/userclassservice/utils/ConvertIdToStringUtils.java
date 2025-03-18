package upc.projectname.userclassservice.utils;

public class ConvertIdToStringUtils {

    public static String convertIdToStringUtils(int classId) {
        if (classId < 0) {
            throw new IllegalArgumentException("class_id must be positive");
        }

        // 只使用大写字母，基数为26
        char[] letters = new char[7];
        int tempId = classId;

        for (int i = 6; i >= 0; i--) {
            int remainder = tempId % 26;
            tempId = tempId / 26;

            // 只使用大写字母 A-Z (ASCII 65-90)
            letters[i] = (char) (65 + remainder);
        }

        return new String(letters);
    }
}
