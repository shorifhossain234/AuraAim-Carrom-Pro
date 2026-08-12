package com.auraim.engine.service;

import android.app.Service;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class OverlayService extends Service {

    private WindowManager windowManager;
    private OverlayView overlayView;
    private WindowManager.LayoutParams params;

    @Override
    public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        
        overlayView = new OverlayView(this);
        
        params = new WindowManager.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.START;
        
        windowManager.addView(overlayView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (overlayView != null) windowManager.removeView(overlayView);
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    class OverlayView extends View {
        private Paint paint;
        private float startX, startY, endX, endY;
        private boolean isDrawing = false;

        public OverlayView(android.content.Context context) {
            super(context);
            paint = new Paint();
            paint.setColor(Color.parseColor("#9C27B0")); // বেগুনি কালার ভিডিওর মতো
            paint.setStrokeWidth(12f);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setShadowLayer(20f, 0f, 0f, Color.MAGENTA);
            setLayerType(LAYER_TYPE_SOFTWARE, paint);
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getRawX();
                    startY = event.getRawY();
                    isDrawing = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    endX = event.getRawX();
                    endY = event.getRawY();
                    invalidate();
                    break;
                case MotionEvent.ACTION_UP:
                    isDrawing = false;
                    invalidate();
                    break;
            }
            return true;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (!isDrawing) return;

            // Ray Casting Logic - 5 Bounce
            float currentX = startX;
            float currentY = startY;
            float dirX = startX - endX;
            float dirY = startY - endY;
            
            float length = (float) Math.sqrt(dirX*dirX + dirY*dirY);
            if (length < 10) return;
            dirX /= length;
            dirY /= length;

            for (int i = 0; i < 5; i++) {
                float nextX = currentX + dirX * 2000;
                float nextY = currentY + dirY * 2000;

                // Wall collision check
                if (nextX < 0 || nextX > getWidth()) {
                    dirX = -dirX;
                    nextX = Math.max(0, Math.min(nextX, getWidth()));
                }
                if (nextY < 0 || nextY > getHeight()) {
                    dirY = -dirY;
                    nextY = Math.max(0, Math.min(nextY, getHeight()));
                }

                canvas.drawLine(currentX, currentY, nextX, nextY, paint);
                currentX = nextX;
                currentY = nextY;
            }
        }
    }
}
