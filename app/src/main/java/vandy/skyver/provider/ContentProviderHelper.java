package vandy.skyver.provider;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import vandy.skyver.model.mediator.webdata.Video;
import vandy.skyver.provider.VideoContract.VideoEntry;

/**
 * Created by Skyver on 15.07.2015.
 */
public class ContentProviderHelper {

    /**
     * Store the context to allow access to application-specific
     * resources and classes.
     */
    private Context mContext;

    //constructor
    public ContentProviderHelper(Context context){
        mContext = context;
    }


    public Uri putVideo(Video v){

        ContentValues cv = new ContentValues();

        cv.put(VideoEntry._ID, v.getId());
        cv.put(VideoEntry.COLUMN_TITLE, v.getTitle());
        cv.put(VideoEntry.COLUMN_DURATION, v.getDuration());
        cv.put(VideoEntry.COLUMN_CONTENT_TYPE, v.getContentType());
        cv.put(VideoEntry.COLUMN_DATA_URL, v.getUrl());
        cv.put(VideoEntry.COLUMN_STAR_RATING, v.getStarRating());

        return mContext.getContentResolver()
                .insert(VideoEntry.CONTENT_URI, cv);
    }

    public String getUriById(Long id){

        // Selection clause to find rows with given acronym.
        final String SELECTION_VIDEO =
                VideoEntry._ID
                        + " = ?";

        // Initializes an array to contain selection arguments.
        String[] selectionArgs = { id.toString() };

        try (Cursor cursor =
                     mContext.getContentResolver().query
                             (VideoEntry.CONTENT_URI,
                                     null,
                                     SELECTION_VIDEO,
                                     selectionArgs,
                                     null)){
            if(!cursor.moveToFirst())
                return null;
            else{
                String ret = cursor.getString(cursor.getColumnIndex(VideoEntry.COLUMN_DATA_URL));
                return ret;
            }
        }
    }

    public void updateUriById(Long id, Uri mUri){

        // Selection clause to find rows with given acronym.
        final String SELECTION_VIDEO =
                VideoEntry._ID
                        + " = ?";

        // Initializes an array to contain selection arguments.
        String[] selectionArgs = { id.toString() };

        Video vd ;
        try (Cursor cursor =
                     mContext.getContentResolver().query
                             (VideoEntry.CONTENT_URI,
                                     null,
                                     SELECTION_VIDEO,
                                     selectionArgs,
                                     null)){
            if(!cursor.moveToFirst())
                return ;
            else{
                long mId = cursor.getLong(cursor.getColumnIndex(VideoEntry._ID));
                String title = cursor.getString(cursor.getColumnIndex(VideoEntry.COLUMN_TITLE));
                long dur = cursor.getLong(cursor.getColumnIndex(VideoEntry.COLUMN_DURATION));
                String conType = cursor.getString(cursor.getColumnIndex(VideoEntry.COLUMN_CONTENT_TYPE));
                String dUrl = cursor.getString(cursor.getColumnIndex(VideoEntry.COLUMN_DATA_URL));
                double star = cursor.getDouble(cursor.getColumnIndex(VideoEntry.COLUMN_STAR_RATING));

                vd = new Video(mId, title, dur, conType, dUrl, star);
            }
        }

        vd.setUrl(mUri.toString());
        updateVideo(vd);

    }

    public Video getVideoById(Long id){

        // Selection clause to find rows with given acronym.
        final String SELECTION_VIDEO =
                VideoEntry._ID
                        + " = ?";

        // Initializes an array to contain selection arguments.
        String[] selectionArgs = { id.toString() };

        Video vd ;
        try (Cursor cursor =
                     mContext.getContentResolver().query
                             (VideoEntry.CONTENT_URI,
                                     null,
                                     SELECTION_VIDEO,
                                     selectionArgs,
                                     null)){
            if(!cursor.moveToFirst())
                return null;
            else{
                long mId = cursor.getLong(cursor.getColumnIndex(VideoEntry._ID));
                String title = cursor.getString(cursor.getColumnIndex(VideoEntry.COLUMN_TITLE));
                long dur = cursor.getLong(cursor.getColumnIndex(VideoEntry.COLUMN_DURATION));
                String conType = cursor.getString(cursor.getColumnIndex(VideoEntry.COLUMN_CONTENT_TYPE));
                String dUrl = cursor.getString(cursor.getColumnIndex(VideoEntry.COLUMN_DATA_URL));
                float star = cursor.getFloat(cursor.getColumnIndex(VideoEntry.COLUMN_STAR_RATING));

                vd = new Video(mId, title, dur, conType, dUrl, star);

                return  vd;
            }
        }
    }

    public void updateVideo(Video v){
        // Selection clause to find rows with given acronym.
        final String SELECTION_VIDEO =
                VideoEntry._ID
                        + " = ?";

        // Initializes an array to contain selection arguments.
        String[] selectionArgs = { Long.toString(v.getId()) };

        ContentValues cv = new ContentValues();

        cv.put(VideoEntry._ID, v.getId());
        cv.put(VideoEntry.COLUMN_TITLE, v.getTitle());
        cv.put(VideoEntry.COLUMN_DURATION, v.getDuration());
        cv.put(VideoEntry.COLUMN_CONTENT_TYPE, v.getContentType());
        cv.put(VideoEntry.COLUMN_DATA_URL, v.getUrl());
        cv.put(VideoEntry.COLUMN_STAR_RATING, v.getStarRating());


        mContext.getContentResolver().
                update(VideoEntry.CONTENT_URI, cv, SELECTION_VIDEO, selectionArgs);

    }
}
