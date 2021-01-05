package martialcoder.surajsahani.safeouts.api;

import java.util.List;

public class EmptyUtils {

    public static boolean isEmpty(String value) {
        return value == null || value.equals("") || value.equals(" ");
    }

    public static boolean isEmpty(List list) {
        if (list != null)
            return list.isEmpty();
        else
            return false;
    }

    public static boolean isNotNull(Object object) {
        return null != object;
    }
}
