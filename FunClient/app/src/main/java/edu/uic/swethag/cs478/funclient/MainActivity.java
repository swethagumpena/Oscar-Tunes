package edu.uic.swethag.cs478.funclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

import edu.uic.swethag.cs478.funcenter.IMediaPlaybackService;

public class MainActivity extends AppCompatActivity implements MusicListAdapter.OnSongListener {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    Context context;

    private boolean mIsBound = false;
    protected static final String TAG = "Music_client";
    public static IMediaPlaybackService mMediaPlaybackService;
    public static int musicPos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = getApplicationContext();

        setTitle("Oscar Tunes");

        final Button fetchImgButton = (Button) findViewById(R.id.fetchImage);

        fetchImgButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mIsBound) {
                    fetchImgButton.setVisibility(View.GONE); // hide the button once service is bounded

                    try {
                        ArrayList<ListItem> exampleList = new ArrayList<>();
                        String[] str = mMediaPlaybackService.getData();
                        for (int j = 0; j < 4; j++) {
                            String[] answer = str[j].split("&", -1);
                            byte[] imageBytes = Base64.decode(answer[2], Base64.DEFAULT);
                            Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                            exampleList.add(new ListItem(decodedImage, answer[0], answer[1]));
                        }

                        MusicListAdapter.OnSongListener listener = new MusicListAdapter.OnSongListener() {
                            @Override
                            public void onSongClick(int position) throws RemoteException {
                                // Handle song click event here
                                mMediaPlaybackService.stopMusic();
                                musicPos = position;
                                Intent intent = new Intent(context, MusicPlayerActivity.class);
                                String individualSongData = mMediaPlaybackService.getDataById(position + 1);
                                intent.putExtra("song_data", individualSongData);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        };

                        mRecyclerView = findViewById(R.id.recyclerView);
                        mRecyclerView.setHasFixedSize(true);
                        mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        mAdapter = new MusicListAdapter(exampleList, getApplicationContext(), listener);
                        mRecyclerView.setLayoutManager(mLayoutManager);
                        mRecyclerView.setAdapter(mAdapter);

                    } catch (RemoteException exception) {
                        exception.printStackTrace();
                    }
                } else {
                    fetchImgButton.setVisibility(View.VISIBLE);
                    Log.i(TAG, "Service was not bound!");
                    checkBindingAndBind();
                }
            }
        });
    }

    // Bind to KeyGenerator Service
    @Override
    protected void onStart() {
        super.onStart();
        checkBindingAndBind();
    }

    protected void checkBindingAndBind() {
        if (!mIsBound) {
            boolean b = false;
            Intent i = new Intent(IMediaPlaybackService.class.getName());
            ResolveInfo info = getPackageManager().resolveService(i, 0);
            i.setComponent(new ComponentName(info.serviceInfo.packageName, info.serviceInfo.name));

            b = bindService(i, this.mConnection, Context.BIND_AUTO_CREATE);
            if (b) {
                Log.i(TAG, "BindService() succeeded!");
            } else {
                Log.i(TAG, "BindService() failed!");
            }
        }
    }

    public final ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder iservice) {
            mMediaPlaybackService = IMediaPlaybackService.Stub.asInterface(iservice);
            mIsBound = true;
        }

        public void onServiceDisconnected(ComponentName className) {
            mMediaPlaybackService = null;
            mIsBound = false;
        }
    };

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onSongClick(int position) {

    }

    public static boolean pausePlay() throws RemoteException {
        if (mMediaPlaybackService.isMusicPlaying()) {
            mMediaPlaybackService.pauseMusic();
        } else {
            mMediaPlaybackService.playMusicById(musicPos);
        }
        return mMediaPlaybackService.isMusicPlaying();
    }

    public static void stop() throws RemoteException {
        mMediaPlaybackService.stopMusic();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mIsBound) {
            unbindService(this.mConnection);
            mIsBound = false;
        }
    }
}