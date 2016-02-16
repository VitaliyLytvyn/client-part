package vandy.skyver.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import vandy.skyver.R;
import vandy.skyver.model.mediator.VideoDataMediator;
import vandy.skyver.model.services.DownloadVideoService;
import vandy.skyver.model.services.RatingService;
import vandy.skyver.model.services.UploadVideoService;
import vandy.skyver.provider.ContentProviderHelper;
import vandy.skyver.utils.VideoMediaStoreUtils;

public class ViewerActivity extends Activity implements View.OnClickListener {

    /**
     * The Broadcast Receiver that registers itself to receive the
     * result from UploadVideoService when a video upload completes.
     */
    private DownloadResultReceiver mDownloadResultReceiver;
    private RatingReceiver mRatingReceiver;
    private TextView mTxt;
    private Button btDownLoad;
    private Button btVew;
    private RatingBar rateBar;
    private ContentProviderHelper mContProvider;
    private Uri mUri;
    private long mId;
    private String mTitle;
    private int mNumberInList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewer);
        mTxt = (TextView) findViewById(R.id.textView);
        btDownLoad = (Button) findViewById(R.id.button);
        btDownLoad.setOnClickListener(this);

        rateBar = (RatingBar)findViewById(R.id.ratingBar);
        rateBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {


                if(fromUser){
                getApplicationContext().startService
                        (RatingService.makeIntent
                                (getApplicationContext(),
                                        mId, rating));
                }

            }
        });

        btVew = (Button) findViewById(R.id.button2);
        btVew.setOnClickListener(this);



        // Receiver for the notification.
        mDownloadResultReceiver =  new DownloadResultReceiver();
        mRatingReceiver = new RatingReceiver();

        mTitle = getIntent().getStringExtra("title");
        mTxt.setText(mTitle);

        mId = getIntent().getLongExtra("id", -1L);
        mNumberInList = getIntent().getIntExtra("numberInList", -1);
        if(mId == -1L || mNumberInList == -1){
            finish();
        }

        double rt = getIntent().getDoubleExtra("rating", 0);
        rateBar.setRating((float)rt);

        mContProvider = new ContentProviderHelper(getApplicationContext());

        String str = mContProvider.getUriById(mId);

        if(str == null){
            btVew.setActivated(false);
            btVew.setVisibility(View.GONE);
            //rateBar.setVisibility(View.GONE);
        }

        else{

            mUri = Uri.parse(str);

            String path = VideoMediaStoreUtils.getPath(getApplicationContext(), mUri);
            File fl = new File(path);
            if(fl.exists()){

                btDownLoad.setActivated(false);
                btDownLoad.setVisibility(View.GONE);

            }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register BroadcastReceiver that receives result from
        // DownloadVideoService when a video download completes.
        registerReceiver();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregister BroadcastReceiver.
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mDownloadResultReceiver);
        LocalBroadcastManager.getInstance(this)
                .unregisterReceiver(mRatingReceiver);
    }


    @Override
    public void onBackPressed() {

        /////////////////////////////  return rating to be saved in the list
        Intent intent = new Intent();
        intent.putExtra("numberInList", mNumberInList);
        intent.putExtra("rating", (double) rateBar.getRating());
        setResult(RESULT_OK, intent);
        finish();
        super.onBackPressed();
    }

    @Override
    public void onClick(View v) {

        if(v.getId() == btDownLoad.getId()){
            getApplicationContext().startService
                (DownloadVideoService.makeIntent
                        (getApplicationContext(),
                                mId, mTitle));

        }
        else if(v.getId() == btVew.getId()){


            if(!uriExists(mId)){
                Toast.makeText(getApplicationContext(),
                        "URI PROBLEM - Does Not Exists. Try download", Toast.LENGTH_LONG).show();
                return;
            }

            Intent intent = new Intent(Intent.ACTION_VIEW, mUri);
            intent.setDataAndType(mUri, "video/*");
            startActivity(intent);

        }

    }

    public boolean uriExists(long id){

        if(mContProvider == null){
            mContProvider = new ContentProviderHelper(getApplicationContext());
        }

        String str = mContProvider.getUriById(id);

        if(str == null){
            return false;
        }

        mUri = Uri.parse(str);

        String path = VideoMediaStoreUtils.getPath(getApplicationContext(), mUri);
        File fl = new File(path);
        if(fl.exists())
             return true;
        else
            return false;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_viewer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Register a BroadcastReceiver that receives a result from the
     * UploadVideoService when a video upload completes.
     */
    private void registerReceiver() {

        // Create an Intent filter that handles Intents from the
        // UploadVideoService.
        IntentFilter intentFilter =
                new IntentFilter(DownloadVideoService.ACTION_DOWNLOAD_SERVICE_RESPONSE);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        //Create Intent filter for StarRatingServce
        IntentFilter intentFilter2 =
                new IntentFilter(RatingService.ACTION_RATING_SERVICE_RESPONSE);
        intentFilter2.addCategory(Intent.CATEGORY_DEFAULT);


        // Register the BroadcastReceiver.
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mDownloadResultReceiver,
                        intentFilter);

        // register for RatingService response
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(mRatingReceiver,
                        intentFilter2);
    }


    /**
     * The Broadcast Receiver that registers itself to receive result
     * from UploadVideoService.
     */
    private class DownloadResultReceiver
            extends BroadcastReceiver {
        /**
         * Hook method that's dispatched when the UploadService has
         * uploaded the Video.
         */
        @Override
        public void onReceive(Context context,
                              Intent intent) {

            String result = intent.getStringExtra("title");
            if(result != null &&
                    !result.equals(VideoDataMediator.STATUS_DOWNLOAD_ERROR)){

                btDownLoad.setActivated(false);
                btDownLoad.setVisibility(View.GONE);

                btVew.setActivated(true);
                btVew.setVisibility(View.VISIBLE);

                Toast.makeText(context.getApplicationContext(),
                        "Video: " + result + "successfully downloaded", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(context.getApplicationContext(),
                        "Video: " + result + "was not downloaded", Toast.LENGTH_LONG).show();
            }
//                Intent mIntent = new Intent(getApplicationContext(), ViewerActivity.class);
//                intent.putExtra("id", intent.getLongExtra("id", -1))
//                        .putExtra("title", intent.getStringExtra("title"));
//                startActivity(mIntent);

        }
    }

    /**
     * The Broadcast Receiver that registers itself to receive result
     * from RatingService.
     */
    private class RatingReceiver
            extends BroadcastReceiver {
        /**
         * Hook method that's dispatched when the UploadService has
         * uploaded the Video.
         */
        @Override
        public void onReceive(Context context,
                              Intent intent) {

                double rate = intent.getDoubleExtra("rating", 0);
                rateBar.setRating((float)rate);

//            Intent mIntent = new Intent(getApplicationContext(), ViewerActivity.class);
//            intent.putExtra("id", 1)
//                    .putExtra("title", intent.getStringExtra("title"));
//            startActivity(mIntent);


        }
    }
}
