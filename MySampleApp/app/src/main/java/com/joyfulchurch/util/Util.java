package com.joyfulchurch.util;



import android.graphics.Color;

import com.amazonaws.mobile.push.SnsTopic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Util {

    static Map<String, SnsTopic> snsTopicMap;

    public static List<String> getAllChannels() {
        return Arrays.asList(
                "죠이플 창",
                "교회 소식 (전체 공지)",
                "카리스마 대학부",
                "카이로스 청년부",
                "남성 기도회",
                "월요 여성 중보기도 모임",
                "여성 커피브레이크",
                "교육부",
                "찬양팀",
                "여름 선교",
                "겨울 선교",
                "목자 모임",
                "사역부장 모임"
        );
    }

    public static List<String> getDefaultChannels() {
        return Arrays.asList("죠이플 창", "교회 소식 (전체 공지)");
    }

    // SNS PLATFORM TOPIC ARNS
    public static final String[] AMAZON_SNS_TOPIC_ARNS =
            {
                    "arn:aws:sns:us-west-1:115981409113:joyfulchurch_joyfulboard_MOBILEHUB_687604833",
                    "arn:aws:sns:us-west-1:115981409113:joyfulchurch_general_MOBILEHUB_687604833",
                    "arn:aws:sns:us-west-1:115981409113:joyfulchurch_karisma_MOBILEHUB_687604833",
                    "arn:aws:sns:us-west-1:115981409113:joyfulchurch_kairos_MOBILEHUB_687604833",
                    "arn:aws:sns:us-west-1:115981409113:joyfulchurch_prayerman_MOBILEHUB_687604833",
                    "arn:aws:sns:us-west-1:115981409113:joyfulchurch_prayerwoman_MOBILEHUB_687604833",
                    "arn:aws:sns:us-west-1:115981409113:joyfulchurch_coffeebreakwoman_MOBILEHUB_687604833",
                    "arn:aws:sns:us-west-1:115981409113:joyfulchurch_youtheducation_MOBILEHUB_687604833",
                    "arn:aws:sns:us-west-1:115981409113:joyfulchurch_praiseteam_MOBILEHUB_687604833",
                    "arn:aws:sns:us-west-1:115981409113:joyfulchurch_missionsummer_MOBILEHUB_687604833",
                    "arn:aws:sns:us-west-1:115981409113:joyfulchurch_missionwinter_MOBILEHUB_687604833",
                    "arn:aws:sns:us-west-1:115981409113:joyfulchurch_smallgroupleader_MOBILEHUB_687604833",
                    "arn:aws:sns:us-west-1:115981409113:joyfulchurch_departmentleader_MOBILEHUB_687604833",
                    "arn:aws:sns:us-west-1:115981409113:joyfulchurch_sermon_MOBILEHUB_687604833",
            };

    public static String getSermonTopicARNS() {
        return "arn:aws:sns:us-west-1:115981409113:joyfulchurch_sermon_MOBILEHUB_687604833";
    }

    public static SnsTopic getSermonSnsTopic() {
        return snsTopicMap.get(getSermonTopicARNS());
    }

    public static String getChannelNameFromTopicARNS(String arns) {
        int index = Arrays.asList(AMAZON_SNS_TOPIC_ARNS).indexOf(arns);
        if (index == -1) {
            return "";
        }
        return getAllChannels().get(index);
    }

    public static void saveSnsTopicMap(Map<String, SnsTopic> topicMap) {
        snsTopicMap = topicMap;
    }

    public static SnsTopic getSnsTopicFromChannelName(String channelName) {
        int index = getAllChannels().indexOf(channelName);
        if (index == -1) {
            return null;
        }
        return snsTopicMap.get(AMAZON_SNS_TOPIC_ARNS[index]);
    }

    public static int secondToMillisecond(int second) {
        return second * 1000;
    }

    public static int millisecondToSecond(int millisecond) {
        return millisecond / 1000;
    }

    public static String convertSecondToString(int second) {
        List<Long> timeList = new ArrayList<>();

        int seconds = second % 60;
        long minutes = second / 60;

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

    public static String convertMilliSecondToString(int millisecond) {
        List<Long> timeList = new ArrayList<>();

        int second = millisecond / 1000;
        int seconds = second % 60;
        long minutes = second / 60;

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
