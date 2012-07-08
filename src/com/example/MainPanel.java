package com.example;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import com.example.model.Ripple;

public class MainPanel extends SurfaceView implements
        SurfaceHolder.Callback {

    private static final String TAG = MainPanel.class.getSimpleName();
    Ripple ripple;
    private MainThread thread;
    public int[] offScreenRaster;
    Bitmap image;


    public MainPanel(Context context) {
        super(context);
        // adding the callback (this) to the surface holder to intercept events
        getHolder().addCallback(this);

        Bitmap immutableBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.bg);
        initialise(immutableBitmap);
        // create the animation loop thread
        thread = new MainThread(getHolder(), this);

        // make the main panel focusable so it can handle events
        setFocusable(true);
    }

    private void initialise(Bitmap immutableBitmap) {
        Ripple.height = 480;
        Ripple.width = 320;
        this.offScreenRaster = new int[Ripple.height * Ripple.width];

        image = immutableBitmap.copy(Bitmap.Config.ARGB_8888, true);
        image.getPixels(offScreenRaster, 0, Ripple.width, 0, 0, Ripple.width, Ripple.height);

        ripple = new Ripple(this.offScreenRaster);
    }


    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


    }

    public void surfaceCreated(SurfaceHolder holder) {
        // at this point the surface is created and
        // we can safely start the animation loop
        thread.setRunning(true);
        thread.start();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.d(TAG, "Surface is being destroyed");
        // tell the thread to shut down and wait for it to finish
        // this is a clean shutdown
        boolean retry = true;
        while (retry) {
            try {
                thread.join();
                retry = false;
            } catch (InterruptedException e) {
                // try again shutting down the thread
            }
        }
        Log.d(TAG, "Thread was shut down cleanly");
    }

    boolean down;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                down = true;
                ripple.handleActionDown((int) event.getX(), (int) event.getY());
                break;
            }
            case MotionEvent.ACTION_UP: {
                down = false;
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                if (down) {
                    ripple.handleActionDown((int) event.getX(), (int) event.getY());
                }
                break;
            }
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        ripple.drawOffScreen(this.offScreenRaster);
        image.setPixels(offScreenRaster, 0, Ripple.width, 0, 0, Ripple.width, Ripple.height);
        canvas.drawBitmap(image, 0, 0, null);
    }

}