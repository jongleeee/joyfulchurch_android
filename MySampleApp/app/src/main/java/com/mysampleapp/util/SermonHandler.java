package com.mysampleapp.util;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;

import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.models.nosql.SermonsDO;

public class SermonHandler extends DatabaseHandler {

    public void save(Sermon sermon) throws AmazonClientException {
        final SermonsDO sermonItem = new SermonsDO();
        sermonItem.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
        sermonItem.setCreationDate((double) sermon.getDate().getTime());
        sermonItem.setTitle(sermon.getTitle());
        sermonItem.setSermon(sermon.getSermonURL());
        sermonItem.setVerse(sermon.getVerse());
        sermonItem.setSeries(sermon.getSeries());

        try {
            super.save(sermonItem);
        } catch (final AmazonClientException ex) {
            throw ex;
        }
    }

    public List<Sermon> getAllSermon() {
        List<Sermon> sermonList = new LinkedList<>();
        try {
            Iterator<Object> sermons = super.scan(SermonsDO.class, new DynamoDBScanExpression());
            if (sermons != null) {
                while (sermons.hasNext()) {
                    SermonsDO sermon = (SermonsDO) sermons.next();
                    sermonList.add(new Sermon(sermon.getTitle(), sermon.getSeries(), sermon.getVerse(),
                            new Date(sermon.getCreationDate().longValue() * 1000), sermon.getSermon()));
                }
            }
        } catch (final AmazonClientException e) {
            throw e;
        }
        Collections.sort(sermonList, new Comparator<Sermon>() {
            @Override
            public int compare(Sermon sermon, Sermon t1) {
                return -sermon.getDate().compareTo(t1.getDate());
            }
        });
        return sermonList;
    }
}
