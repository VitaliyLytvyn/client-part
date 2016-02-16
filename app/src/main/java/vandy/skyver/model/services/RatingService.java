package vandy.skyver.model.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.support.v4.content.LocalBroadcastManager;

import vandy.skyver.model.mediator.VideoDataMediator;


public class RatingService extends IntentService {

    /**
     * Custom Action that will be used to send Broadcast to the
     * ViewerActivity.
     */
    public static final String ACTION_RATING_SERVICE_RESPONSE =
            "vandy.skyver.services.RatingService.RESPONSE";

    /**
     * VideoDataMediator mediates the communication between Video
     * Service and local storage in the Android device.
     */
    private VideoDataMediator mVideoMediator;

    //CONSTRUCTORS
    public RatingService(String name) {super("RatingService"); }
    public RatingService() {
        super("RatingService");
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
                                    long videoId, float rating) {
        return new Intent(context,
                RatingService.class).putExtra("id", videoId).putExtra("rating", rating);

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {

            // Create VideoDataMediator that will mediate the communication
            // between Server and Android Storage.
            mVideoMediator =
                    new VideoDataMediator(getApplicationContext());

            // Check if Video Upload is successful.
            long id = intent.getLongExtra("id", -1);
            float rate = intent.getFloatExtra("rating", 0);

            double resultRating = mVideoMediator.setRating(id, (int)rate);

            // Send the Broadcast to ViewerActivity that the Video
            // Upload is completed.
            sendBroadcast(id, resultRating);

        }
    }

    /**
     * Send the Broadcast to Activity that the Video Upload is
     * completed.
     */
    private void sendBroadcast(long mId, double rating){
        // Use a LocalBroadcastManager to restrict the scope of this
        // Intent to the VideoUploadClient application.
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(new Intent(ACTION_RATING_SERVICE_RESPONSE)
                        .addCategory(Intent.CATEGORY_DEFAULT)
                        .putExtra("id", mId)
                        .putExtra("rating", rating));
    }
    /////////////////.addCategory(Intent.CATEGORY_DEFAULT)

}
