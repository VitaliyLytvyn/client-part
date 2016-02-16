package vandy.skyver.provider;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Skyver on 15.07.2015.
 */
public class VideoContract {

    /**
     * The "Content authority" is a name for the entire content
     * provider, similar to the relationship between a domain name and
     * its website.  A convenient string to use for the content
     * authority is the package name for the app, which must be unique
     * on the device.
     */
    public static final String CONTENT_AUTHORITY =
            "vandy.skyver.videoprovider";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's that apps
     * will use to contact the content provider.
     */
    public static final Uri BASE_CONTENT_URI =
            Uri.parse("content://"
                    + CONTENT_AUTHORITY);

    /**
     * Possible paths (appended to base content URI for possible
     * URI's), e.g., content://vandy.skyver/video/ is a valid path for
     * Video data.
     */
    public static final String PATH_VIDEO =
            VideoEntry.TABLE_NAME;

    /**
     * Inner class that defines the contents of the Acronym table.
     */
    public static final class VideoEntry implements BaseColumns {

        /**
         * Use BASE_CONTENT_URI to create the unique URI for Acronym
         * Table that apps will use to contact the content provider.
         */
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon()
                        .appendPath(PATH_VIDEO).build();

        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 0..x items.
         */
        public static final String CONTENT_ITEMS_TYPE =
                "vnd.android.cursor.dir/"
                        + CONTENT_AUTHORITY
                        + "/"
                        + PATH_VIDEO;

        /**
         * When the Cursor returned for a given URI by the
         * ContentProvider contains 1 item.
         */
        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/"
                        + CONTENT_AUTHORITY
                        + "/"
                        + PATH_VIDEO;

        /**
         * Name of the database table.
         */
        public static final String TABLE_NAME =
                "video_table";

        /**
         * Columns to store Data of each Acronym Expansion.
         */
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DURATION = "duration";
        public static final String COLUMN_CONTENT_TYPE = "content_type";
        public static final String COLUMN_DATA_URL = "data_url";
        public static final String COLUMN_STAR_RATING = "star_rating";

        /**
         * Return a Uri that points to the row containing a given id.
         *
         * @param id
         * @return Uri
         */
        public static Uri buildVideoUri(Long id) {
            return ContentUris.withAppendedId(CONTENT_URI,
                    id);
        }

    }

}
