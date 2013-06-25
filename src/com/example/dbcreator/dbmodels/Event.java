package com.example.dbcreator.dbmodels;

import android.net.Uri;

import com.example.dbcreator.database.DatabaseProvider;
import com.slobodastudio.dbcreator.database.SQLiteDBObject;
import com.slobodastudio.dbcreator.database.annotations.CreateField;
import com.slobodastudio.dbcreator.database.annotations.CreateTable;


@CreateTable(TableName = "events")
public class Event extends SQLiteDBObject {

    @CreateField(FieldName = "_id", PrimaryKey = true, Autoincrement = true, NotNull = true)
    private long id;
    //this field in db will looks like type_id, because added SplitWordsCharacter parameter.
    @CreateField(ForeignKey = "event_types(_id)", SplitWordsCharacter = "_", NotNull = true)
    private long typeId;
    @CreateField(SplitWordsCharacter = "_", NotNull = true)
    private String userName;
    @CreateField(SplitWordsCharacter = "_", NotNull = true)
    private long eventTime;

    public static final String TABLE_NAME = "events";

    public static final String ID = "_id";
    public static final String TYPE_ID = "type_id";
    public static final String USER_NAME = "user_name";
    public static final String EVENT_TIME = "event_time";

    public static final Uri CONTENT_URI = DatabaseProvider.BASE_CONTENT_URI.buildUpon()
            .appendPath(TABLE_NAME).build();
    public static final String CONTENT_TYPE = "vnd.android.cursor.dir/"
            + DatabaseProvider.AUTHORITY_CONTENT.replace("com", "vnd") + "." + TABLE_NAME;
    public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/"
            + DatabaseProvider.AUTHORITY_CONTENT.replace("com", "vnd") + "." + TABLE_NAME;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getTypeId() {
        return typeId;
    }

    public void setTypeId(long typeId) {
        this.typeId = typeId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("Event{");
        sb.append("id=").append(id);
        sb.append(", typeId=").append(typeId);
        sb.append(", userName='").append(userName).append('\'');
        sb.append(", eventTime=").append(eventTime);
        sb.append('}');
        return sb.toString();
    }
}
