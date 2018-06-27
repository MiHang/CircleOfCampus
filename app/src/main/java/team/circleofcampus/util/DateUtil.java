package team.circleofcampus.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


public class DateUtil {

    /**
     * 求两个时间之间的差值
     * @param dateOne
     * @param dateTwo
     * @return int[] [0] - days, [1] - hours, [2] - minutes,
     */
    public static int[] DateDifferenceValue(String dateOne, String dateTwo) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = df.parse(dateOne);
            Date d2 = df.parse(dateTwo);
            long diff = d1.getTime() - d2.getTime();
            if (diff < 0) diff = diff * -1;

            long days = diff / (1000 * 60 * 60 * 24);
            long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);

            int[] values = new int[3];
            values[0] = (int)days;
            values[1] = (int)hours;
            values[2] = (int)minutes;

            return values;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
