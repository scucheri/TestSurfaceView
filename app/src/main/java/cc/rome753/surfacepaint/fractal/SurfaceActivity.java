package cc.rome753.surfacepaint.fractal;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import cc.rome753.surfacepaint.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by rome753 on 2016/12/14.
 */

public class SurfaceActivity extends BaseActivity {
    private final String TAG = "xiaoyu_SurfaceActivity";

    SurfaceHolder holder;
    private FrameLayout container;
    private FrameLayout surfaceViewContainer;
    private SurfaceView surface;
    private Handler handler = new MyHandler();
    private final int MSG_SHOW_SURFACE_CONTAINER = 0x10;
    private final int MSG_HIDE_SURFACE_CONTAINER = 0x11;

    private  class MyHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SHOW_SURFACE_CONTAINER:
                    surfaceViewContainer.setVisibility(View.VISIBLE);
                    handler.sendEmptyMessageDelayed(MSG_HIDE_SURFACE_CONTAINER, 10000);
                    break;
                case MSG_HIDE_SURFACE_CONTAINER:
                    surfaceViewContainer.setVisibility(View.GONE);
                    handler.sendEmptyMessageDelayed(MSG_SHOW_SURFACE_CONTAINER, 10000);
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        holder.addCallback(mCallback);
    }

    @Override
    protected View initContentView() {
        container = (FrameLayout) LayoutInflater.from(this)
            .inflate(R.layout.surface_activity, container, true);
        surface = container.findViewById(R.id.surfaceView);
        /**
         * 如果只是设置了setZOrderOnTop 为true，没有设置setZOrderMediaOverlay，在调用了surfaceViewContainer set为gone之后，surfaceView还是可见的
         * 默认情况下，surfaceView的窗口在activity的窗口下面，需要通过挖洞，设置activity的window对应surfaceview的区域设置透明，才可以将下面的surfaceView显示出来不被遮挡
         * 而这个挖洞的过程，依赖于获取surfaceView在activity中的位置，自然依赖surfaceView的父view的可见性，如果父view不可见了，surfaceView的位置没有挖洞出来，就会被activity的画面遮挡
         *
         * 而如果设置了setZOrderOnTop为true，这个时候surfaceView的窗口在activity的窗口之上，不需要通过挖洞设置透明就可以将这个surfaceView显示出来，不依赖于surfaceView的父view的可见性
         *
         * 如果设置了setZOrderOnTop为true，这个时候surfaceView设置为gone或者从在调用了surfaceViewContainer detach之后，不会出现黑块，因为这个时候不会走SurfaceView中挖洞的逻辑
         *
         */
        surface.setZOrderOnTop(false);
        //surface.setZOrderMediaOverlay(true);
        surfaceViewContainer = container.findViewById(R.id.surfaceView_container);
        holder = surface.getHolder();
        container.findViewById(R.id.gone_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handler.sendEmptyMessage(MSG_HIDE_SURFACE_CONTAINER);
                //surfaceViewContainer.setVisibility(View.GONE); //这个不会回调到surfaceDestroyed， 10s之后才会让surfaceview消失，不会出现黑块
                ////surface.setVisibility(View.GONE);//这个会回调到surfaceDestroyed，设置为INVISIBLE也是一样
                //try {
                //    Thread.sleep(10000);
                //} catch (InterruptedException e) {
                //    e.printStackTrace();
                //}


                //handler.sendEmptyMessage(MSG_HIDE_SURFACE_CONTAINER);


                //surfaceViewContainer.removeView(surface);
                //surfaceViewContainer.addView(surface);


                ((ViewGroup) surfaceViewContainer.getParent()).removeView(surfaceViewContainer);
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        container.findViewById(R.id.remove_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                surfaceViewContainer.removeView(surface); //这个会回调到surfaceDestroyed， surfaceView立即就会消失，会出现黑块
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        return container;
    }

    SurfaceHolder.Callback mCallback = new SurfaceHolder.Callback() {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.i(TAG, "surfaceCreated");
            isAvailable.set(true);

            //draw background
            Canvas canvas = holder.lockCanvas();
            //canvas.drawColor(Color.BLACK);

            Paint clearPaint = new Paint();
            clearPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
            canvas.drawRect(0, 0, 0, 0, clearPaint);
            holder.unlockCanvasAndPost(canvas);

            go();
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.i(TAG, "surfaceChanged");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.i(TAG, "surfaceDestroyed");
            isAvailable.set(false);
        }
    };

    protected void go() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);

        ArrayList<Rect> list = new ArrayList<>(xCount * yCount);

        for (int i = 0; i < xCount; i++) {
            for (int j = 0; j < yCount; j++) {
                Rect r = new Rect(i * xSize, j * ySize, (i + 1) * xSize, (j + 1) * ySize);
                list.add(r);

            }
        }

        //打乱排序
        //Collections.shuffle(list);

        for (final Rect r : list) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    Bitmap bitmap = calculateBitmap(r);
                    syncDraw(r, bitmap);
                }
            });
        }
    }

    synchronized void syncDraw(Rect r, Bitmap bitmap){
        drawBitmap(holder, r, bitmap);
    }
}
