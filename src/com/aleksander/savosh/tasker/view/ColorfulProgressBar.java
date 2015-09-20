package com.aleksander.savosh.tasker.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.*;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import com.aleksander.savosh.tasker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ColorfulProgressBar extends ProgressBar {
    public float sizeInPercent; //view size in percent calculated from min length of screen
    private int redrawStep; //frequency redraw view in milliseconds

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
    private static final List<Paint> PAINTS = new ArrayList<Paint>(){{
        add(new Paint(){{ setStyle(Paint.Style.FILL); setColor(Color.BLUE); }});
        add(new Paint(){{ setStyle(Paint.Style.FILL); setColor(Color.RED); }});
        add(new Paint(){{ setStyle(Paint.Style.FILL); setColor(Color.GREEN); }});
        add(new Paint(){{ setStyle(Paint.Style.FILL); setColor(Color.YELLOW); }});
        add(new Paint(){{ setStyle(Paint.Style.FILL); setColor(Color.WHITE); }});
        add(new Paint(){{ setStyle(Paint.Style.FILL); setColor(Color.BLACK); }});
    }};
    private static final Paint BACKGROUND = new Paint(){{ setStyle(Style.FILL); setARGB(125, 0, 0, 0); }};
    private static final float CORNER_RADIUS_IN_PERCENT = 2.5f; //corner radius in percent


    public ColorfulProgressBar(Context context) {
        super(context);
        setupAttributes(null);
    }

    public ColorfulProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupAttributes(attrs);
    }

    public ColorfulProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setupAttributes(attrs);
    }

    private void setupAttributes(AttributeSet attrs){
        // Obtain a typed array of attributes
        TypedArray a = getContext().getTheme().obtainStyledAttributes(attrs, R.styleable.ColorfulProgressBar, 0, 0);
        // Extract custom attributes into member variables
        try {
            sizeInPercent = a.getFloat(R.styleable.ColorfulProgressBar_progressBarSizeInPercent, 15);
            redrawStep = a.getInt(R.styleable.ColorfulProgressBar_progressBarRedrawTime, 750);
        } finally {
            // TypedArray objects are shared and must be recycled.
            a.recycle();
        }
    }

    /**
     * calculate min size
     * @return size in pixel
     */
    private int getSize(){
        WindowManager wm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        int width;
        int height;
        if(Build.VERSION.SDK_INT >= 13) {
            display.getSize(size);
            width = size.x;
            height = size.y;
        } else {
            width = display.getWidth();
            height = display.getHeight();
        }
        int common = width < height ? width : height;
        return (int)((common * sizeInPercent)/100);
    }



    private List<RectF> rects = new ArrayList<RectF>();
    private int cornerRadius = 0;
    private Random random = new Random();

    private AsyncTask<Void, Void, Void> reDrawNone;
    @Override
    protected synchronized void onDraw(final Canvas canvas) {
        if(reDrawNone == null && isShown()) {
            reDrawNone = new AsyncTask<Void, Void, Void>() {
                @Override
                protected void onPreExecute() {
                    // draw background shadow
                    int h = getMeasuredHeight();
                    int w = getMeasuredWidth();
                    canvas.drawRect(0, 0, w, h, BACKGROUND);

                    //draw rectangles
                    for(RectF rectF : rects){
                        int i = random.nextInt();
                        i = i < 0 ? -i : i;
                        canvas.drawRoundRect(rectF, cornerRadius, cornerRadius, PAINTS.get(i % PAINTS.size()));
                    }

                    //TODO сделать для прямоугольников рамку
                }
                @Override
                protected Void doInBackground(Void... params) {
                    //wait
                    try {
                        Thread.sleep(redrawStep);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    if(isShown()) {
                        invalidate();
                    }
                    reDrawNone = null;
                }
            };
            if(Build.VERSION.SDK_INT >= 11) {
                reDrawNone.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                reDrawNone.execute();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measuredHeight = measure(heightMeasureSpec);
        int measuredWidth = measure(widthMeasureSpec);
        createRectangles(measuredWidth, measuredHeight);
        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    private int measure(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        // Размер по умолчанию, если ограничения не были установлены.
        int result = getSize();//нужный размер

        if (specMode == MeasureSpec.AT_MOST) {
            // Рассчитайте идеальный размер вашего
            // элемента в рамках максимальных значений.
            // Если ваш элемент заполняет все доступное
            // пространство, верните внешнюю границу.
            if(result < specSize) {
                result = specSize;
            }
        } else if (specMode == MeasureSpec.EXACTLY) {
            // Если ваш элемент может поместиться внутри этих границ, верните это значение.
            if(result < specSize) {
                result = specSize;
            }
        }
        return result;
    }

    /**
     * расчет положения и размеры прямоугольников исходя из допустимых размеров view
     * @param w - доступное пространство по ширине
     * @param h - доступное пространство по высоте
     */
    private void createRectangles(int w, int h){
        final int viewSize = getSize();
        final int xOffset = (w - viewSize)/2; //смещение по оси х
        final int yOffset = (h - viewSize)/2; //смещение по оси y

        this.rects = new ArrayList<RectF>(){{
            for(PercentFactor factor : RECT_FACTORS) {
                int length = (int)(viewSize * factor.length / 100);

                int xLeft = (int)(viewSize * factor.xPos / 100) + xOffset;
                int yTop = (int)(viewSize * factor.yPos / 100) + yOffset;
                int xRight = xLeft + length;
                int yBottom = yTop + length;

                add(new RectF(xLeft, yTop, xRight, yBottom));
            }
        }};
        cornerRadius = (int) (viewSize * CORNER_RADIUS_IN_PERCENT / 100);
    }
}
