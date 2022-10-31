package com.anubis.module_facelandmark.Tracking;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.PointF;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.anubis.module_facelandmark.Utils.AuthCallback;
import com.anubis.module_facelandmark.Utils.STUtils;

import java.util.List;

import zeusees.tracking.Face;
import zeusees.tracking.FaceTracking;


public class FaceOverlapFragment extends  CameraOverlapFragment {

    private static final int MESSAGE_DRAW_POINTS = 100;

    private FaceTracking mMultiTrack106 = null;
    private TrackCallBack mListener;
    private HandlerThread mHandlerThread;
    private Handler mHandler;

    private byte mNv21Data[];
    private byte[] mTmpBuffer;

    private int frameIndex = 0;

    private Paint mPaint;
    private Object lockObj = new Object();
    private boolean mIsPaused = false;

    @SuppressLint("NewApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        mNv21Data = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        mTmpBuffer = new byte[PREVIEW_WIDTH * PREVIEW_HEIGHT * 2];
        frameIndex= 0 ;
        mPaint = new Paint();
        mPaint.setColor(Color.rgb(57, 138, 243));
        int strokeWidth = Math.max(PREVIEW_HEIGHT / 240, 2);
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setStyle(Style.FILL);
        mHandlerThread = new HandlerThread("DrawFacePointsThread");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper()) {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DRAW_POINTS) {
                    synchronized (lockObj)
                    {
                        if(!mIsPaused) {
                            handleDrawPoints();
                        }
                    }

                }
            }
        };
        this.setPreviewCallback(new PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                synchronized (mNv21Data) {
                    System.arraycopy(data, 0, mNv21Data, 0, data.length);
                }

                mHandler.removeMessages(MESSAGE_DRAW_POINTS);
                mHandler.sendEmptyMessage(MESSAGE_DRAW_POINTS);
            }
        });
        return view;
    }

    private void handleDrawPoints() {

        synchronized (mNv21Data) {
            System.arraycopy(mNv21Data, 0, mTmpBuffer, 0, mNv21Data.length);
        }

        boolean frontCamera = (CameraFacing == Camera.CameraInfo.CAMERA_FACING_FRONT);




        if(frameIndex == 0 )
        {
            mMultiTrack106.FaceTrackingInit(mTmpBuffer,  PREVIEW_HEIGHT,PREVIEW_WIDTH);

        }
        else {
            mMultiTrack106.Update(mTmpBuffer, PREVIEW_HEIGHT,PREVIEW_WIDTH);
        }
        frameIndex+=1;

        List<Face> faceActions = mMultiTrack106.getTrackingInfo();



        if (faceActions != null) {

            if (!mOverlap.getHolder().getSurface().isValid()) {
                return;
            }

            Canvas canvas = mOverlap.getHolder().lockCanvas();
            if (canvas == null)
                return;

            canvas.drawColor(0, PorterDuff.Mode.CLEAR);
            canvas.setMatrix(getMatrix());
            boolean rotate270 = mCameraInfo.orientation == 270;
            for (Face r : faceActions) {

                Rect rect=new Rect(PREVIEW_HEIGHT - r.left,r.top,PREVIEW_HEIGHT - r.right,r.bottom);

                PointF[] points = new PointF[106];
                for(int i = 0 ; i < 106 ; i++)
                {
                    points[i]  = new PointF(r.landmarks[i*2],r.landmarks[i*2+1]);
                }

                float[] visibles =  new float[106];


                for (int i = 0; i < points.length; i++) {
                    visibles[i] = 1.0f;
                    if (rotate270) {
                        points[i].x = PREVIEW_HEIGHT-points[i].x;
                    }
                }

               STUtils.drawFaceRect(canvas,rect, PREVIEW_HEIGHT,
                        PREVIEW_WIDTH, frontCamera);
                 STUtils.drawPoints(canvas, mPaint, points,visibles, PREVIEW_HEIGHT,
                        PREVIEW_WIDTH, frontCamera);

            }
            mOverlap.getHolder().unlockCanvasAndPost(canvas);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();

        this.mIsPaused = false;

        if (mMultiTrack106 == null) {

          AuthCallback authCallback = new  AuthCallback() {
                @Override
                public void onAuthResult(boolean succeed, String errMessage) {
                    if (!TextUtils.isEmpty(errMessage)) {
                        Toast.makeText(getActivity(), errMessage, Toast.LENGTH_SHORT).show();
                    }

                }
            };

            if (authCallback != null) {
                mMultiTrack106 = new FaceTracking("/sdcard/ZeuseesFaceTracking/models");
            }

        }
    }

    @Override
    public void onPause() {
        mHandler.removeMessages(MESSAGE_DRAW_POINTS);
        mIsPaused = true;
        synchronized (lockObj)
        {
            if (mMultiTrack106 != null) {
                mMultiTrack106= null;

            }
        }

        super.onPause();
    }

    public void registTrackCallback(TrackCallBack callback) {
        mListener = callback;
    }

    public interface TrackCallBack {
        void onTrackdetected(int value, float pitch, float roll, float yaw, float eye_dist,
                             int id, int eyeBlink, int mouthAh, int headYaw, int headPitch, int browJump);
    }

}
