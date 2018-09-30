package com.joyfulchurch.util;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.mobile.AWSMobileClient;

import java.util.Iterator;

public abstract class DatabaseHandler {
    final DynamoDBMapper mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
    PaginatedScanList<Object> results;
    private Iterator<Object> resultsIterator;

    public void save(Object obj) {
        mapper.save(obj);
    }

    public Iterator<Object> scan(Class objectClass, DynamoDBScanExpression scanExpression) {
        results = mapper.scan(objectClass, scanExpression);
        if (results != null) {
            resultsIterator = results.iterator();
            if (resultsIterator.hasNext()) {
                return resultsIterator;
            }
        }
        return null;
    }
}
