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

@DynamoDBTable(tableName = "joyfulchurch-mobilehub-687604833-NotificationChannels")

public class NotificationChannelsDO {
    private String _deviceToken;
    private String _channel;

    @DynamoDBHashKey(attributeName = "deviceToken")
    @DynamoDBAttribute(attributeName = "deviceToken")
    public String getDeviceToken() {
        return _deviceToken;
    }

    public void setDeviceToken(final String _deviceToken) {
        this._deviceToken = _deviceToken;
    }
    @DynamoDBRangeKey(attributeName = "channel")
    @DynamoDBIndexHashKey(attributeName = "channel", globalSecondaryIndexName = "channel")
    public String getChannel() {
        return _channel;
    }

    public void setChannel(final String _channel) {
        this._channel = _channel;
    }

}
