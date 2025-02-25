package upc.projectname.userclassservice.utils;

public class ConvertIdToStringUtils {

    public static String convertIdToStringUtils(int classId) {
        if (classId < 0) {
            throw new IllegalArgumentException("class_id must be positive");
        }


        char[] letters = new char[7];
        int tempId = classId;

        for (int i = 6; i >= 0; i--) {
            int remainder = tempId % 52;
            tempId = tempId / 52;

            if (remainder < 26) {
                letters[i] = (char) (65 + remainder); // 大写字母 A-Z
            } else {
                letters[i] = (char) (71 + remainder); // 小写字母 a-z
            }
        }

        return new String(letters);
    }


}
