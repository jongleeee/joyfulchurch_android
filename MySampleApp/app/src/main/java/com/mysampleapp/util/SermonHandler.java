package com.mysampleapp.util;


import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;

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
        try {
            Iterator<Object> sermons = super.scan(SermonsDO.class, new DynamoDBScanExpression());
            List<Sermon> sermonLst = new LinkedList<>();
            if (sermons != null) {
                while (sermons.hasNext()) {
                    SermonsDO sermon = (SermonsDO) sermons.next();
                    sermonLst.add(new Sermon(sermon.getTitle(), sermon.getSeries(), sermon.getVerse(),
                            new Date(sermon.getCreationDate().longValue()), sermon.getSermon()));
                }
                return sermonLst;
            }
        } catch (final Exception e) {
            throw e;
        }
        return null;
    }
}
