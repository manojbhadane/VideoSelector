package com.manoj.videoselector;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private String TAG = "Main";
    private final int BROWSE_VIDEO_REQUEST = 101;
    private String mVideoPath = "";

    private TextView mTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTxt = (TextView) findViewById(R.id.txt);

        ((Button) findViewById(R.id.btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("*/*");
                String[] mimetypes = {"video/*"};
                intent.putExtra(Intent.EXTRA_MIME_TYPES, mimetypes);
                startActivityForResult(intent, BROWSE_VIDEO_REQUEST);
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // TODO Auto-generated method stub
        switch (requestCode) {
            case BROWSE_VIDEO_REQUEST:
                if (resultCode == RESULT_OK) {
                    try {
                        String PathHolder = data.getData().getPath();
                        if (isFileExist(PathHolder)) {  // /storage/UsbDriveA/React_Native.mp4
                            Log.e(TAG, "SWAPLOG PATH = exist");
                            if (PathHolder.endsWith(".mp4") || PathHolder.endsWith(".3gp") || PathHolder.endsWith(".avi")) {
                                //Found Real path
                                mVideoPath = PathHolder;
                                mTxt.setText("file path : " + mVideoPath);
                            } else {
                                Toast.makeText(this, "Error-1-" + mVideoPath, Toast.LENGTH_SHORT).show();
                            }
                        } else {  // /document/video:27948
                            FromSDCard(data.getData());
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Error-2-" + mVideoPath, Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }

    /**
     * get Data from SDCard
     */
    private void FromSDCard(Uri uri) {
        try {
            String selectedFilePath = RealPathUtil.getPath(MainActivity.this, uri);
            if (selectedFilePath != null) {
                final File file = new File(selectedFilePath);
                if (file.exists()) {
                    String filePath = file.getPath();
                    mVideoPath = filePath;
                    mTxt.setText("file path : " + mVideoPath);
                } else {
                    getFromDocument(uri);
                }
            } else {
                getFromDocument(uri);
            }
        } catch (Exception e) {
            e.printStackTrace();
            getFromDocument(uri);
        }
    }

    /**
     * get Data from document path
     */
    private void getFromDocument(Uri uri) {
        String orignalPath = uri.getPath();
        try {
            if (orignalPath.endsWith(".mp4") || orignalPath.endsWith(".3gp") || orignalPath.endsWith(".avi")) { // /document/C26B-6A27:React_Native.mp4
                String getFilePath = RealPathUtil.getDocumentPath(uri);
                if (getFilePath != null) {
                    if (isFileExist(getFilePath)) {
                        //Found Real path
                        mVideoPath = getFilePath;
                        mTxt.setText("file path : " + mVideoPath);
//                        goToPlayVideo();
                    } else {
                        Toast.makeText(this, "Error-4-" + mVideoPath, Toast.LENGTH_SHORT).show();
                    }
                } else {
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error-5-" + mVideoPath, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isFileExist(String filePath) {
        File file = new File(filePath);
        if (file.exists())
            return true;
        else return false;

    }
}
