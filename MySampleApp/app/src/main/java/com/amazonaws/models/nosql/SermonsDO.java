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

@DynamoDBTable(tableName = "joyfulchurch-mobilehub-687604833-Sermons")

public class SermonsDO {
    private String _userId;
    private Double _creationDate;
    private Set<String> _keywords;
    private String _sermon;
    private String _sermonId;
    private String _title;
    private String _verse;

    @DynamoDBHashKey(attributeName = "userId")
    @DynamoDBAttribute(attributeName = "userId")
    public String getUserId() {
        return _userId;
    }

    public void setUserId(final String _userId) {
        this._userId = _userId;
    }
    @DynamoDBRangeKey(attributeName = "creationDate")
    @DynamoDBAttribute(attributeName = "creationDate")
    public Double getCreationDate() {
        return _creationDate;
    }

    public void setCreationDate(final Double _creationDate) {
        this._creationDate = _creationDate;
    }
    @DynamoDBAttribute(attributeName = "keywords")
    public Set<String> getKeywords() {
        return _keywords;
    }

    public void setKeywords(final Set<String> _keywords) {
        this._keywords = _keywords;
    }
    @DynamoDBAttribute(attributeName = "sermon")
    public String getSermon() {
        return _sermon;
    }

    public void setSermon(final String _sermon) {
        this._sermon = _sermon;
    }
    @DynamoDBAttribute(attributeName = "sermonId")
    public String getSermonId() {
        return _sermonId;
    }

    public void setSermonId(final String _sermonId) {
        this._sermonId = _sermonId;
    }
    @DynamoDBAttribute(attributeName = "title")
    public String getTitle() {
        return _title;
    }

    public void setTitle(final String _title) {
        this._title = _title;
    }
    @DynamoDBAttribute(attributeName = "verse")
    public String getVerse() {
        return _verse;
    }

    public void setVerse(final String _verse) {
        this._verse = _verse;
    }

}
