package com.example.circlieprogress;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

/**
 * 类描述：圆弧型的进度条展示
 * create on：2019/2/26 3:57 PM
 */
public class CircleProgress extends View {

    private int mMaxProgress;
    private int desColor;
    private int mOutSideColor;
    private float mOutSideWidth;
    private float mProgressWidth;
    private int mProgressColor;
    private int mProgressTextColor;
    private float mProgressTextSize;
    private float desTextSize;
    private int mStartProgress;
    private int mWidth;
    private int mHeight;
    private Paint mPaintArc;
    private Paint mTextPaint;
    private float centerX =0, centerY = 0;
    private RectF rectArc = new RectF();
    private String mDesStr = "/天";

    private Context mContext;
    private int startAngle = 135;
    private int sweepAngle =  270;
    private int mCurrentValue = 0;
    private int padding;
    private float lastAngle =0,mCurrentAngle = 0;

    public CircleProgress(Context context) {
        super(context);
        init(context,null);
    }

    public CircleProgress(Context context,  @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }


    public CircleProgress(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }


    /**
     * 自定义属性初始化
     * @param context
     * @param attrs
     */
    private void init(Context context, AttributeSet attrs) {
        this.mContext = context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.Circle_Progress);
        mMaxProgress = a.getInt(R.styleable.Circle_Progress_max_progrss, 100);
        mStartProgress = a.getInt(R.styleable.Circle_Progress_start_progress, 0);

        mOutSideColor = a.getColor(R.styleable.Circle_Progress_outside_color, Color.WHITE);
        mOutSideWidth = a.getDimension(R.styleable.Circle_Progress_outside_width, 5);

        mProgressWidth = a.getDimension(R.styleable.Circle_Progress_progress_width, 20);
        mProgressColor = a.getColor(R.styleable.Circle_Progress_progress_color, Color.WHITE);

        mProgressTextColor = a.getColor(R.styleable.Circle_Progress_progress_text_color, Color.WHITE);
        mProgressTextSize = a.getDimension(R.styleable.Circle_Progress_progress_text_size, 10);

        desTextSize = a.getDimension(R.styleable.Circle_Progress_des_text_size, 10);
        desColor = a.getColor(R.styleable.Circle_Progress_des_text_color, Color.WHITE);
        mDesStr = a.getString(R.styleable.Circle_Progress_des_str);
        mDesStr = TextUtils.isEmpty(mDesStr) ? "/天" : mDesStr;
        a.recycle();
        initPaint();
    }

    /**
     * 初始化画笔
     */
    private void initPaint() {
        mPaintArc = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaintArc.setStrokeWidth(mOutSideWidth);
        mPaintArc.setColor(mOutSideColor);
        mPaintArc.setStrokeCap(Paint.Cap.ROUND);
        mPaintArc.setStrokeJoin(Paint.Join.ROUND);
        mPaintArc.setStyle(Paint.Style.STROKE);


        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setColor(desColor);
        mTextPaint.setTextSize(desTextSize);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mWidth = MeasureSpec.getSize(widthMeasureSpec);
        mHeight = MeasureSpec.getSize(heightMeasureSpec);
        centerX = mWidth / 2;
        centerY = mHeight / 2;
        padding = mContext.getResources().getDimensionPixelOffset(R.dimen.padding);
        rectArc.set(mProgressWidth+ padding,mProgressWidth+ padding,mWidth-mProgressWidth -padding,mHeight-mProgressWidth -padding);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawArc(canvas);
        drawText(canvas);
    }


    /**
     * 绘制圆弧
     * @param canvas
     */
    private void drawArc(Canvas canvas) {
        mPaintArc.setStrokeWidth(mOutSideWidth);
        mPaintArc.setColor(mOutSideColor);
        canvas.drawArc(rectArc,startAngle,sweepAngle,false,mPaintArc);
        mPaintArc.setStrokeWidth(mProgressWidth);
        mPaintArc.setColor(mProgressColor);
        canvas.drawArc(rectArc,startAngle,mCurrentAngle,false,mPaintArc);

    }

    /**
     * 绘制文本
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        Rect rectProgress = new Rect();
        Rect rectDes = new Rect();
        mTextPaint.setColor(desColor);
        mTextPaint.setTextSize(desTextSize);
        mTextPaint.getTextBounds(mDesStr,0,mDesStr.length(),rectDes);


        mTextPaint.setColor(mProgressTextColor);
        mTextPaint.setTextSize(mProgressTextSize);
        String currentProgress = String.valueOf(mCurrentValue);
        mTextPaint.getTextBounds(currentProgress,0,currentProgress.length(),rectProgress);
        canvas.drawText(currentProgress,centerX - rectProgress.width() / 2 - rectDes.width() /2,centerY  + rectProgress.height() / 2 +padding,mTextPaint);

        mTextPaint.setColor(desColor);
        mTextPaint.setTextSize(desTextSize);
        canvas.drawText(mDesStr,centerX + rectProgress.width() / 2 -padding,centerY + rectProgress.height() / 2 ,mTextPaint);

    }


    public void setProgress(int currentValue){
        if(currentValue > mMaxProgress){
            Toast.makeText(mContext,"数值超出范围",Toast.LENGTH_LONG).show();
            return;
        }
        int currentAngle = ((int) ((currentValue * 1.0 / mMaxProgress) * sweepAngle));
        lastAngle = mCurrentAngle;
        startAnim(100,currentValue,currentAngle);
    }


    /**
     * 构建动画进行数据修改
     */
    private void startAnim(int duration, int currentValue, final int currentAngle) {
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(lastAngle, currentAngle);
        valueAnimator.setDuration(duration);
        valueAnimator.setTarget(mCurrentAngle);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentAngle = ((float) animation.getAnimatedValue());
                postInvalidate();
            }

        });

        valueAnimator.start();
        ValueAnimator textAnimator = ValueAnimator.ofInt(mCurrentValue, currentValue);
        textAnimator.setDuration(duration);
        textAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentValue = ((int) animation.getAnimatedValue());
            }
        });
        textAnimator.start();


    }


}
