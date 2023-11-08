package org.jasonf.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.jasonf.Constant.DATE_FORMAT_PATTERN;

/**
 * @Author jasonf
 * @Date 2023/11/7
 * @Description
 */

public class DateUtil {
    private static final SimpleDateFormat FORMAT = new SimpleDateFormat(DATE_FORMAT_PATTERN);

    public static Date parse(String date) {
        try {
            return FORMAT.parse(date);
        } catch (ParseException ex) {
            throw new RuntimeException("日期格式错误, 请参考: " + DATE_FORMAT_PATTERN);
        }
    }
}
