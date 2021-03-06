package vandy.skyver.view;

import vandy.skyver.R;
import vandy.skyver.common.GenericActivity;
import vandy.skyver.common.Utils;
import vandy.skyver.model.mediator.webdata.Video;
import vandy.skyver.model.services.UploadVideoService;
import vandy.skyver.presenter.VideoOps;
import vandy.skyver.utils.VideoStorageUtils;
import vandy.skyver.view.ui.FloatingActionButton;
import vandy.skyver.view.ui.UploadVideoDialogFragment;
import vandy.skyver.view.ui.VideoAdapter;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.content.LocalBroadcastManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;

/**
 * This Activity can be used upload a selected video to a Video
 * Service and also displays a list of videos available at the Video
 * Service.  The user can record a video or get a video from gallery
 * and upload it.  It implements OnVideoSelectedListener that will
 * handle callbacks from the UploadVideoDialog Fragment.  It extends
 * GenericActivity that provides a framework for automatically
 * handling runtime configuration changes of an VideoOps object, which
 * plays the role of the "Presenter" in the MVP pattern.  The
 * VideoOps.View interface is used to minimize dependencies between
 * the View and Presenter layers.
 */
public class VideoListActivity 
       extends GenericActivity<VideoOps.View, VideoOps>
       implements UploadVideoDialogFragment.OnVideoSelectedListener,
                  VideoOps.View, AdapterView.OnItemClickListener {
    /**
     * The Request Code needed in Implicit Intent start Video
     * Recording Activity.
     */
    private final int REQUEST_VIDEO_CAPTURE = 0;

    /**
     * The Request Code needed in Implicit Intent to get Video from
     * Gallery.
     */
    private final int REQUEST_GET_VIDEO = 1;

    /**
     * The Request Code needed in Implicit Intent
     * to receive back the newer rating and store it in list to escape
     * the new round to server for all list.
     */
    private final int REQUEST_GET_RATING = 2;

    /**
     * The Broadcast Receiver that registers itself to receive the
     * result from UploadVideoService when a video upload completes.
     */
    private UploadResultReceiver mUploadResultReceiver;

    /**
     * The Floating Action Button that will show a Dialog Fragment to
     * upload Video when user clicks on it.
     */
    private FloatingActionButton mUploadVideoButton;
    
    /**
     * The ListView that contains a list of Videos available from
     * the Video Service.
     */
    private ListView mVideosList;

    /**
     * Uri to store the Recorded Video.
     */
    private Uri mRecordVideoUri;


    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., storing Views.
     * 
     * @param Bundle
     *            object that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize the default layout.
        setContentView(R.layout.video_list_activity);

        // Receiver for the notification.
        mUploadResultReceiver =
            new UploadResultReceiver();
        
        // Get reference to the ListView for displaying the results
        // entered.
        mVideosList =
            (ListView) findViewById(R.id.videoList);

        mVideosList.setOnItemClickListener(this);

        // Show the Floating Action Button.
        createPlusFabButton();

        // Invoke the special onCreate() method in GenericActivity,
        // passing in the VideoOps class to instantiate/manage and
        // "this" to provide VideoOps with the VideoOps.View instance.
        super.onCreate(savedInstanceState,
                       VideoOps.class,
                       this);
    }

    /**
     *  Hook method that is called when user resumes activity
     *  from paused state, onPause(). 
     */
    @Override
    protected void onResume() {
        // Call up to the superclass.
        super.onResume();

        // Register BroadcastReceiver that receives result from
        // UploadVideoService when a video upload completes.
        registerReceiver();
    }
    
    /**
     * Register a BroadcastReceiver that receives a result from the
     * UploadVideoService when a video upload completes.
     */
    private void registerReceiver() {
        
        // Create an Intent filter that handles Intents from the
        // UploadVideoService.
        IntentFilter intentFilter =
            new IntentFilter(UploadVideoService.ACTION_UPLOAD_SERVICE_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        // Register the BroadcastReceiver.
        LocalBroadcastManager.getInstance(this)
               .registerReceiver(mUploadResultReceiver,
                                 intentFilter);
    }

    
    /**
     * Hook method that gives a final chance to release resources and
     * stop spawned threads. onDestroy() may not always be called-when
     * system kills hosting process
     */
    @Override
    protected void onPause() {
        // Call onPause() in superclass.
        super.onPause();
        
        // Unregister BroadcastReceiver.
        LocalBroadcastManager.getInstance(this)
          .unregisterReceiver(mUploadResultReceiver);
    }

    /**
     * The Broadcast Receiver that registers itself to receive result
     * from UploadVideoService.
     */
    private class UploadResultReceiver 
            extends BroadcastReceiver {
        /**
         * Hook method that's dispatched when the UploadService has
         * uploaded the Video.
         */
        @Override
        public void onReceive(Context context,
                              Intent intent) {
            // Starts an AsyncTask to get fresh Video list from the
            // Video Service.
            getOps().getVideoList();
        }
    }

    /**
     * The user selected option to get Video from UploadVideoDialog
     * Fragment.  Based on what the user selects either record a Video
     * or get a Video from the Video Gallery.
     */
    @Override
    public void onVideoSelected(UploadVideoDialogFragment.OperationType which) {
        switch (which) {
        case VIDEO_GALLERY:
            // Create an intent that will start an Activity to get
            // Video from Gallery.
            final Intent videoGalleryIntent = 
                new Intent(Intent.ACTION_GET_CONTENT)
                .setType("video/*")
                .putExtra(Intent.EXTRA_LOCAL_ONLY,
                          true);

            // Verify the intent will resolve to an Activity.
            if (videoGalleryIntent.resolveActivity(getPackageManager()) != null) 
                // Start an Activity to get the Video from Video
                // Gallery.
                startActivityForResult(videoGalleryIntent,
                                       REQUEST_GET_VIDEO);
            break;
            
        case RECORD_VIDEO:
            // Create a file to save the video.
            mRecordVideoUri =
                VideoStorageUtils.getRecordedVideoUri
                                   (getApplicationContext());  
            
            // Create an intent that will start an Activity to get
            // Record Video.
            final Intent recordVideoIntent =
                new Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                .putExtra(MediaStore.EXTRA_OUTPUT,
                          mRecordVideoUri);

            // Verify the intent will resolve to an Activity.
            if (recordVideoIntent.resolveActivity(getPackageManager()) != null) 
                // Start an Activity to record a video.
                startActivityForResult(recordVideoIntent,
                                       REQUEST_VIDEO_CAPTURE);
            break;
        }
    }

    /**
     * Hook method called when an activity you launched exits, giving
     * you the requestCode you started it with, the resultCode it
     * returned, and any additional data from it.
     * 
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    public void onActivityResult(int requestCode,
                                 int resultCode,
                                 Intent data) {
        Uri videoUri = null; 

        // Check if the Result is Ok and upload the Video to the Video
        // Service.
        if (resultCode == Activity.RESULT_OK) {

            //save in list a new rating for the choosen video
            if(requestCode == REQUEST_GET_RATING){

                int number = data.getIntExtra("numberInList", -1);
                double rating = data.getDoubleExtra("rating", -1.0);
                if(number != -1 && rating != -1.0){
                    getOps().changeRating(number, rating);
                }
                return;
            }
            // Video picked from the Gallery.
            if (requestCode == REQUEST_GET_VIDEO)
                videoUri = data.getData();
                
            // Video is recorded.
            else if (requestCode == REQUEST_VIDEO_CAPTURE)
                videoUri = mRecordVideoUri;
              
            if (videoUri != null){
                Utils.showToast(this,
                                "Uploading video"); 
            
                // Upload the Video.
                getOps().uploadVideo(videoUri);
            }
        }

        // Pop a toast if we couldn't get a video to upload.
        if (videoUri == null)
            Utils.showToast(this,
                            "Could not get video to upload");
    }

    /**
     * Show the Floating Action Button that will show a Dialog
     * Fragment to upload Video when user clicks on it.
     */
    @SuppressWarnings("deprecation")
    private void createPlusFabButton() {
        final DisplayMetrics metrics =
            getResources().getDisplayMetrics();
        final int position =
            (metrics.widthPixels / 4) + 5;

        // Create Floating Action Button using the Builder pattern.
        mUploadVideoButton =
            new FloatingActionButton
            .Builder(this)
            .withDrawable(getResources()
                          .getDrawable(R.drawable.ic_video))
            .withButtonColor(getResources()
                             .getColor(R.color.theme_primary))
            .withGravity(Gravity.BOTTOM | Gravity.END)
            .withMargins(0, 
                         0,
                         position,
                         0)
            .create();

        // Show the UploadVideoDialog Fragment when user clicks the
        // button.
        mUploadVideoButton.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    new UploadVideoDialogFragment().show(getFragmentManager(),
                                                         "uploadVideo");
                }
            });
    }

    /**
     * Sets the Adapter that contains List of Videos to the ListView.
     */
    @Override
    public void setAdapter(VideoAdapter videoAdapter) {
        mVideosList.setAdapter(videoAdapter);
    }

    /**
     * Finishes this Activity.
     */
    @Override
    public void finish() {
        super.finish();
    }
    
    /**
     * Hook method called to initialize the contents of the Activity's
     * standard options menu.
     * 
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it
        // is present.
        getMenuInflater().inflate(R.menu.video_list,
                                  menu);
        return true;
    }

    /**
     * Hook method called whenever an item in your options menu is
     * selected
     * 
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


        Video vid = (Video) parent.getAdapter().getItem(position);
        Intent intent = new Intent(getApplicationContext(), ViewerActivity.class);
        intent.putExtra("id", vid.getId())
                .putExtra("title", vid.getTitle())
                .putExtra("rating", vid.getStarRating())
                .putExtra("numberInList", position);
        startActivityForResult(intent, REQUEST_GET_RATING);

    }
}
