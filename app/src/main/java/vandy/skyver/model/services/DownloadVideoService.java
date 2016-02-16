package vandy.skyver.model.services;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.Context;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import vandy.skyver.model.mediator.VideoDataMediator;
import vandy.skyver.provider.ContentProviderHelper;


public class DownloadVideoService extends IntentService {

    /**
     * Custom Action that will be used to send Broadcast to the
     * ViewerActivity.
     */
    public static final String ACTION_DOWNLOAD_SERVICE_RESPONSE =
            "vandy.skyver.services.DownloadVideoService.RESPONSE";

    /**
     * It is used by Notification Manager to send Notifications.
     */
    private static final int NOTIFICATION_ID = 2;

    /**
     * VideoDataMediator mediates the communication between Video
     * Service and local storage in the Android device.
     */
    private VideoDataMediator mVideoMediator;

    /**
     * Manages the Notification displayed in System UI.
     */
    private NotificationManager mNotifyManager;

    /**
     * Builder used to build the Notification.
     */
    private NotificationCompat.Builder mBuilder;



    //Constructors

    public DownloadVideoService(String name) {
        super("DownloadVideoService");
    }

    public DownloadVideoService() {
        super("DownloadVideoService");
    }

    /**
     * Factory method that makes the explicit intent another Activity
     * uses to call this Service.
     *
     * @param context
     * @param videoId
     * @return
     */
    public static Intent makeIntent(Context context,
                                    long videoId, String title) {
        return new Intent(context,
                DownloadVideoService.class).putExtra("id", videoId).putExtra("title", title);

    }

    /**
     * Hook method that is invoked on the worker thread with a request
     * to process. Only one Intent is processed at a time, but the
     * processing happens on a worker thread that runs independently
     * from other application logic.
     *
     * @param intent
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        // Starts the Notification to show the progress of video
        // upload.
        startNotification();

        // Create VideoDataMediator that will mediate the communication
        // between Server and Android Storage.
        mVideoMediator =
                new VideoDataMediator(getApplicationContext());

        // Check if Video Upload is successful.
        long id = intent.getLongExtra("id", -1);
        String name = intent.getStringExtra("title");

        String result = mVideoMediator.downloadVideo(id, name);
        finishNotification(result);

        // Send the Broadcast to ViewerActivity that the Video
        // Upload is completed.
        if(result.equals(VideoDataMediator.STATUS_DOWNLOAD_ERROR)){
            sendBroadcast(id, VideoDataMediator.STATUS_DOWNLOAD_ERROR);
        }
        else{ sendBroadcast(id, name);}
    }

    /**
     * Send the Broadcast to Activity that the Video Upload is
     * completed.
     */
    private void sendBroadcast(long mId, String title){
        // Use a LocalBroadcastManager to restrict the scope of this
        // Intent to the VideoUploadClient application.
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(ACTION_DOWNLOAD_SERVICE_RESPONSE)
                        .addCategory(Intent.CATEGORY_DEFAULT)
                        .putExtra("id", mId)
                        .putExtra("title", title));
    }
    //////////////.addCategory(Intent.CATEGORY_DEFAULT)

    /**
     * Finish the Notification after the Video is Uploaded.
     *
     * @param status
     */
    private void finishNotification(String status) {
        // When the loop is finished, updates the notification.
        mBuilder.setContentTitle(status)
                // Removes the progress bar.
                .setProgress (0,
                        0,
                        false)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentText("")
                .setTicker(status);

        // Build the Notification with the given
        // Notification Id.
        mNotifyManager.notify(NOTIFICATION_ID,
                mBuilder.build());
    }

    /**
     * Starts the Notification to show the progress of video upload.
     */
    private void startNotification() {
        // Gets access to the Android Notification Service.
        mNotifyManager = (NotificationManager)
                getSystemService(Context.NOTIFICATION_SERVICE);

        // Create the Notification and set a progress indicator for an
        // operation of indeterminate length.
        mBuilder = new NotificationCompat
                .Builder(this)
                .setContentTitle("Video Upload")
                .setContentText("Upload in progress")
                .setSmallIcon(android.R.drawable.stat_sys_download)
                .setTicker("Uploading video")
                .setProgress(0,
                        0,
                        true);

        // Build and issue the notification.
        mNotifyManager.notify(NOTIFICATION_ID,
                mBuilder.build());
    }



}
