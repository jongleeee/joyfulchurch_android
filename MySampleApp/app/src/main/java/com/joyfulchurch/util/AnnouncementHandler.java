package com.joyfulchurch.util;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.models.nosql.AnnouncementsDO;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by minjungkim on 7/9/17.
 */

public class AnnouncementHandler extends DatabaseHandler {

    public void save(Announcement announcement) {
        final AnnouncementsDO announcementItem = new AnnouncementsDO();
        announcementItem.setUserId(AWSMobileClient.defaultMobileClient()
                .getIdentityManager().getCachedUserID());
        announcementItem.setTitle(announcement.getTitle());
        announcementItem.setCategory(announcement.getCategory());
        announcementItem.setContent(announcement.getContent());
        announcementItem.setCreationDate((double) announcement.getDate().getTime());

        try {
            super.save(announcementItem);
        } catch (AmazonClientException e) {
            throw e;
        }
    }

    public List<Announcement> getAnnouncementsWithChannels(List<String> channels) {
        if (channels.isEmpty() || channels == null) {
            return new ArrayList<>();
        }

        List<Announcement> announcementList = new LinkedList<>();

        final Map<String, String> filterExpressionAttributeNames = new HashMap<>();
        filterExpressionAttributeNames.put("#category", "category");

        final Map<String, AttributeValue> filterExpressionAttributeValues = new HashMap<>();
        filterExpressionAttributeValues.put(":subscribedChannels",
                new AttributeValue().withSS(channels));

        final DynamoDBScanExpression scanExpression = new DynamoDBScanExpression()
                .withFilterExpression("contains(:subscribedChannels, #category)")
                .withExpressionAttributeNames(filterExpressionAttributeNames)
                .withExpressionAttributeValues(filterExpressionAttributeValues);

        try {
            Iterator<Object> announcements = super.scan(AnnouncementsDO.class, scanExpression);

            if (announcements != null) {
                while (announcements.hasNext()) {
                    AnnouncementsDO announcement = (AnnouncementsDO) announcements.next();
                    announcementList.add(new Announcement(announcement.getTitle(), announcement.getCategory(),
                            announcement.getContent(), new Date(announcement.getCreationDate().longValue() * 1000)));
                }
            }
        } catch (AmazonClientException e) {
            throw e;
        }
        Collections.sort(announcementList, new Comparator<Announcement>() {
            @Override
            public int compare(Announcement announcement, Announcement t1) {
                return -announcement.getDate().compareTo(t1.getDate());
            }
        });
        return announcementList;
    }
}
