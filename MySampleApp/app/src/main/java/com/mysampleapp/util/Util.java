package com.mysampleapp.util;



import android.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Util {

    public static List<String> getAllChannels() {
        return Arrays.asList("죠이플 창", "교회 소식 (전체 공지)", "카리스마 대학부", "카이로스 청년부", "남성 기도회", "월요 여성 중보기도 모임", "여성 커피브레이크", "교육부", "찬양팀", "여름 선교", "겨울 선교", "목자 모임", "사역부장 모임");
    }


    public static String convertMillisecondsToString(int milliseconds) {
        List<Long> timeList = new ArrayList<>();

        int seconds = (milliseconds / 1000) % 60;
        long minutes = (milliseconds / 1000)  / 60;

        if (minutes >= 60) {
            long hours = minutes / 60;
            minutes = minutes % 60;
            timeList.add(hours);
        }
        timeList.add(minutes);
        timeList.add(new Long(seconds));

        StringBuilder sb = new StringBuilder();
        String separator = ":";
        for (int i = 0; i < timeList.size(); i++) {
            sb.append(String.format("%02d", timeList.get(i)));
            if (i != timeList.size() - 1) {
                sb.append(separator);
            }
        }
        return sb.toString();
    }

    public static int JFColorGreen() {
        return Color.rgb(76, 162, 20);
    }

    public static int JFColorBlue() {
        return Color.rgb(80,168,215);
    }

    public static int JFColorBlack() {
        return Color.BLACK;
    }
}
