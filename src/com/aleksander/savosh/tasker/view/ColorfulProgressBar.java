package com.aleksander.savosh.tasker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ColorfulProgressBar extends ProgressBar {

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
//        add(new Paint(){{ setStyle(Paint.Style.FILL); setColor(Color.BLACK); }});
    }};


    @Override
    protected synchronized void onDraw(Canvas canvas) {
//        super.onDraw(canvas);

        canvas.drawRect(0, 0, 100, 100, paints.get(new Random().nextInt() % paints.size()));

    }
}
