package day20190111;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author : hao
 * @project : StudySjms
 * @description :
 * @time : 2019/1/11 15:37
 */
public class DateUtils {

    private static final ThreadLocal<SimpleDateFormat> format = new ThreadLocal<SimpleDateFormat>(){
        @Override
        protected SimpleDateFormat initialValue() {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS");
        }
    };


    public static final String format2sql(Date date) {
        return null == date ? "" : format.get().format(date);
    }


    public static final Date format2Java(String date) throws ParseException {
        if (null == date || "".equals(date)) {
            return null;
        }
        return format.get().parse(date);
    }
}
