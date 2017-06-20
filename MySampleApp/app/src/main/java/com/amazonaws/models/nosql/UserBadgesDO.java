package com.amazonaws.models.nosql;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

import java.util.List;
import java.util.Map;
import java.util.Set;

@DynamoDBTable(tableName = "joyfulchurch-mobilehub-687604833-UserBadges")

public class UserBadgesDO {
    private String _userId;
    private String _deviceToken;
    private Double _badgeCount;
    private Double _lastUpdated;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "deviceToken")
    @DynamoDBIndexHashKey(attributeName = "deviceToken", globalSecondaryIndexName = "deviceToken")
    public String getDeviceToken() {
        return _deviceToken;
    }

    public void setDeviceToken(final String _deviceToken) {
        this._deviceToken = _deviceToken;
    }
    @DynamoDBAttribute(attributeName = "badgeCount")
    public Double getBadgeCount() {
        return _badgeCount;
    }

    public void setBadgeCount(final Double _badgeCount) {
        this._badgeCount = _badgeCount;
    }
    @DynamoDBAttribute(attributeName = "lastUpdated")
    public Double getLastUpdated() {
        return _lastUpdated;
    }

    public void setLastUpdated(final Double _lastUpdated) {
        this._lastUpdated = _lastUpdated;
    }

}
