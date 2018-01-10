package com.bsi.doublewave;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/* @author QianMang
 * @Date 2017/1/10.
 * @Email qianmang@51bsi.com
 */
public class DoubleWaveView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    private static final int SPEED = 3; //波的默认移动速度
    private static final int DEFAULT_HEIGHT = 250; //view的默认高

    private Thread mThread;
    private boolean isRunning;
    private SurfaceHolder mHolder;
    private Canvas mCanvas;
    private Path mPath;
    private Paint mPaint;
    private int waveWidth, waveHeight, screenWidth;
    private int dx;


    private int bg; //背景色
    private int firstWaveColor; //第一个波的颜色
    private int secondWaveColor; //第二个波的颜色

    private int speed; //波移动的速度


    public DoubleWaveView(Context context) {
        this(context, null);
    }

    public DoubleWaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mHolder = getHolder();
        mHolder.addCallback(this);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setKeepScreenOn(false); //屏幕常亮功能关闭

        screenWidth = getResources().getDisplayMetrics().widthPixels;

        final TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.DoubleWaveView);
        bg = typedArray.getColor(R.styleable.DoubleWaveView_qm_bg, getColorByRes(R.color.bg)); //背景色
        firstWaveColor = typedArray.getColor(R.styleable.DoubleWaveView_qm_firstWaveColor, getColorByRes(R.color.blue)); //第一个波的颜色
        secondWaveColor = typedArray.getColor(R.styleable.DoubleWaveView_qm_secondWaveColor, getColorByRes(R.color.light_blue)); //第二个波的颜色
        waveHeight = (int) typedArray.getDimension(R.styleable.DoubleWaveView_qm_waveHeight, UnitConvert.px2dp(context, 35)); //波的高度
        waveWidth = (int) typedArray.getDimension(R.styleable.DoubleWaveView_qm_waveWidth, UnitConvert.px2dp(context, 300)); //波的宽度
        speed = (int) typedArray.getDimension(R.styleable.DoubleWaveView_qm_speed, SPEED); //波移动的速度
        typedArray.recycle();
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int measureWidth = getMeasureWidth(widthMeasureSpec);
        final int measureHeight = getMeasureHeight(heightMeasureSpec);
        setMeasuredDimension(measureWidth, measureHeight);
    }

    private int getMeasureHeight(int heightMeasureSpec) {

        final int mode = MeasureSpec.getMode(heightMeasureSpec);
        final int size = MeasureSpec.getSize(heightMeasureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = UnitConvert.px2dp(getContext(), DEFAULT_HEIGHT);
                break;
            case MeasureSpec.EXACTLY:
                result = size;
                break;
        }
        return result;
    }

    private int getMeasureWidth(int widthMeasureSpec) {

        final int mode = MeasureSpec.getMode(widthMeasureSpec);
        final int size = MeasureSpec.getSize(widthMeasureSpec);
        int result = 0;
        switch (mode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.UNSPECIFIED:
                result = UnitConvert.px2dp(getContext(), DEFAULT_HEIGHT);
                break;
            case MeasureSpec.EXACTLY:
                result = size;
                break;
        }
        return result;
    }


    private void init() {
        mPath = new Path();
        mPaint = new Paint();
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        isRunning = true;
        mThread = new Thread(this);
        mThread.start();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isRunning = false;
    }


    @Override
    public void run() {


        while (isRunning) {
            draw();
        }
    }

    private void draw() {
        try {
            mCanvas = mHolder.lockCanvas();
            mCanvas.drawColor(bg);

            if (mCanvas != null) {

//                绘制第一个波
                mPath.reset();
                mPaint.setColor(firstWaveColor);
                mPath.moveTo(0, 0);
                mPath.lineTo(-waveWidth + dx, getHeight() - waveHeight);
                for (int i = -waveWidth; i < waveWidth + waveWidth; i += waveWidth) {
                    mPath.rQuadTo(waveWidth / 4, waveHeight, waveWidth / 2, 0);
                    mPath.rQuadTo(waveWidth / 4, -waveHeight, waveWidth / 2, 0);
                }

                mPath.lineTo(getWidth(), 0);
                mPath.close();
                mCanvas.drawPath(mPath, mPaint);

                //绘制第二个波
                mPath.reset();
                mPaint.setColor(secondWaveColor);
                mPath.moveTo(0, 0);
                mPath.lineTo(-waveWidth - (waveWidth / 2) + dx, getHeight() - waveHeight);
                for (int i = -waveWidth; i < waveWidth + screenWidth; i += waveWidth) {

                    mPath.rQuadTo(waveWidth / 4, waveHeight, waveWidth / 2, 0);
                    mPath.rQuadTo(waveWidth / 4, -waveHeight, waveWidth / 2, 0);
                }
                mPath.lineTo(getWidth(), 0);
                mPath.close();
                mPath.close();
                mCanvas.drawPath(mPath, mPaint);

                if ((dx += speed) > waveWidth) {
                    dx = 0;
                }
            }
        } catch (Exception e) {

        } finally {
            if (mCanvas != null) {
                mHolder.unlockCanvasAndPost(mCanvas); //释放canvas
            }
        }
    }

    private int getColorByRes(@ColorRes int res) {
        return ContextCompat.getColor(getContext(), res);
    }


    public void setSpeed(int speed){
        this.speed = speed;
        invalidate();
    }

    public void setWaveWidth(int waveWidth){
        this.waveWidth = waveWidth;
        invalidate();
    }
    public void setWaveHeight(int waveHeight){
        this.waveHeight = waveHeight;
        invalidate();
    }
}