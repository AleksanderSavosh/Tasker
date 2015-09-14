package com.aleksander.savosh.tasker.view;

import android.content.Context;
import android.graphics.*;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.Display;
import android.view.WindowManager;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ColorfulProgressBar extends ProgressBar {

    public static final int SIZE_FROM_WIDTH_SCREEN_IN_PERCENT = 20;
    static class PercentFactor {
        public String name;
        public float xPos;
        public float yPos;
        public float length;
    }
    private static final List<PercentFactor> RECT_FACTORS = new ArrayList<PercentFactor>(){{
        add(new PercentFactor(){{ name = "s1"; xPos = 2.5f; yPos = 2.9f; length = 20.1f; }});
        add(new PercentFactor(){{ name = "s2"; xPos = 29.6f; yPos = 6.2f; length = 14.5f; }});
        add(new PercentFactor(){{ name = "s3"; xPos = 51.4f; yPos = 8.2f; length = 14.7f; }});
        add(new PercentFactor(){{ name = "s4"; xPos = 68.1f; yPos = 11.7f; length = 19.5f; }});
        add(new PercentFactor(){{ name = "s5"; xPos = 88.7f; yPos = 0.1f; length = 11.1f; }});
        add(new PercentFactor(){{ name = "s6"; xPos = 23.8f; yPos = 22.5f; length = 19.7f; }});
        add(new PercentFactor(){{ name = "s7"; xPos = 46.2f; yPos = 24.5f; length = 19.5f; }});
        add(new PercentFactor(){{ name = "s8"; xPos = 67.9f; yPos = 33.3f; length = 11.1f; }});
        add(new PercentFactor(){{ name = "s9"; xPos = 10.6f; yPos = 31.3f; length = 11.3f; }});
        add(new PercentFactor(){{ name = "s10"; xPos = 0.2f; yPos = 44.2f; length = 9.5f; }});
        add(new PercentFactor(){{ name = "s11"; xPos = 12.2f; yPos = 44.4f; length = 32.1f; }});
        add(new PercentFactor(){{ name = "s12"; xPos = 45.9f; yPos = 45.9f; length = 17.4f; }});
        add(new PercentFactor(){{ name = "s13"; xPos = 65.2f; yPos = 45.9f; length = 19.9f; }});
        add(new PercentFactor(){{ name = "s14"; xPos = 45.9f; yPos = 64.9f; length = 11.3f; }});
        add(new PercentFactor(){{ name = "s15"; xPos = 58.4f; yPos = 67.8f; length = 25.8f; }});
        add(new PercentFactor(){{ name = "s16"; xPos = 85.7f; yPos = 67.8f; length = 11.3f; }});
        add(new PercentFactor(){{ name = "s17"; xPos = 12f; yPos = 77f; length = 11.3f; }});
        add(new PercentFactor(){{ name = "s18"; xPos = 24.7f; yPos = 77f; length = 19f; }});
        add(new PercentFactor(){{ name = "s19"; xPos = 0.5f; yPos = 88.7f; length = 11.3f; }});
    }};
    private static final float CORNER_RADIUS_IN_PERCENT = 2.5f;


    public int getSize(){
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        return (width * SIZE_FROM_WIDTH_SCREEN_IN_PERCENT)/100;
    }

    public ColorfulProgressBar(Context context) {
        super(context);
    }

    public ColorfulProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ColorfulProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private List<Paint> paints = new ArrayList<Paint>(){{
        add(new Paint(){{ setStyle(Paint.Style.FILL); setColor(Color.BLUE); }});
        add(new Paint(){{ setStyle(Paint.Style.FILL); setColor(Color.RED); }});
        add(new Paint(){{ setStyle(Paint.Style.FILL); setColor(Color.GREEN); }});
        add(new Paint(){{ setStyle(Paint.Style.FILL); setColor(Color.YELLOW); }});
        add(new Paint(){{ setStyle(Paint.Style.FILL); setColor(Color.WHITE); }});
        add(new Paint(){{ setStyle(Paint.Style.FILL); setColor(Color.BLACK); }});
    }};

    private List<RectF> rects = new ArrayList<RectF>();
    private int cornerRadius = 0;
    private Random random = new Random();


    private AsyncTask<Void, Void, Void> reDraw;
    @Override
    protected synchronized void onDraw(Canvas canvas) {
        for(RectF rectF : rects){
            int i = random.nextInt();
            i = i < 0 ? -i : i;
            canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, paints.get(i % paints.size()));
        }
        if(reDraw == null) {
            reDraw = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... params) {
                    //wait one second
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    invalidate();
                    reDraw = null;
                }
            };
            reDraw.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredHeight = measure(heightMeasureSpec);
        int measuredWidth = measure(widthMeasureSpec);
        int commonSize = measuredHeight < measuredWidth ? measuredHeight : measuredWidth;
        createRectangles(commonSize);
        setMeasuredDimension(commonSize, commonSize);
    }

    private int measure(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        // Размер по умолчанию, если ограничения не были установлены.
        int result = getSize();

        if (specMode == MeasureSpec.AT_MOST) {
            // Рассчитайте идеальный размер вашего
            // элемента в рамках максимальных значений.
            // Если ваш элемент заполняет все доступное
            // пространство, верните внешнюю границу.
            if(specSize < result){
                result = specSize;
            }
        } else if (specMode == MeasureSpec.EXACTLY) {
            // Если ваш элемент может поместиться внутри этих границ, верните это значение.
            if(specSize < result){
                result = specSize;
            }
        }
        return result;
    }


    private void createRectangles(final int viewSize){
        this.rects = new ArrayList<RectF>(){{
            for(PercentFactor factor : RECT_FACTORS) {
                int length = (int)(viewSize * factor.length / 100);

                int xLeft = (int)(viewSize * factor.xPos / 100);
                int yTop = (int)(viewSize * factor.yPos / 100);
                int xRight = xLeft + length;
                int yBottom = yTop + length;

                add(new RectF(xLeft, yTop, xRight, yBottom));


            }
        }};
        cornerRadius = (int) (viewSize * CORNER_RADIUS_IN_PERCENT / 100);
    }
}
