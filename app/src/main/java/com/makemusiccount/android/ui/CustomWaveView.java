package com.makemusiccount.android.ui;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.ColorInt;

import com.makemusiccount.android.R;

import static com.makemusiccount.android.activity.LevelActivity.child_count;

import static com.makemusiccount.android.activity.LevelActivity.progress_wave_count;
import static com.makemusiccount.android.activity.LevelActivity.total_wave_count;


public class CustomWaveView extends View {

  private Context mContext;
  private Paint stroke_paint,stroke_paint1, shadow_paint, fill_paint;


  public CustomWaveView(Context context) {
    super(context);
    init(context);
  }

  public CustomWaveView(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context);
  }

  public CustomWaveView(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context);
  }

  private void init(Context context) {
    mContext = context;

    //set stroke paint
    TypedValue typedValue=new TypedValue();
    Resources.Theme theme=context.getTheme();
    theme.resolveAttribute(R.attr.democolor1,typedValue,true);
    @ColorInt int color =typedValue.data;
    stroke_paint = new Paint();
    stroke_paint.setDither(true);
    stroke_paint.setAntiAlias(true);
    stroke_paint.setColor(color);
    stroke_paint.setStyle(Paint.Style.STROKE);
    stroke_paint.setStrokeCap(Paint.Cap.ROUND);
    stroke_paint.setStrokeJoin(Paint.Join.ROUND);
    stroke_paint.setStrokeWidth(dpToPx(8));

    TypedValue typedValue1=new TypedValue();
    Resources.Theme theme1=context.getTheme();
    theme1.resolveAttribute(R.attr.new_light,typedValue1,true);
    @ColorInt int color1 =typedValue1.data;

    stroke_paint1 = new Paint();
    stroke_paint1.setDither(true);
    stroke_paint1.setAntiAlias(true);
    stroke_paint1.setColor(color1);
    stroke_paint1.setStyle(Paint.Style.STROKE);
    stroke_paint1.setStrokeCap(Paint.Cap.ROUND);
    stroke_paint1.setStrokeJoin(Paint.Join.ROUND);
    stroke_paint1.setStrokeWidth(dpToPx(8));

    TypedValue typedValue2=new TypedValue();
    Resources.Theme theme2=context.getTheme();
    theme2.resolveAttribute(R.attr.new_light,typedValue2,true);
    @ColorInt int color2 =typedValue2.data;
    //set shadow paint
    shadow_paint = new Paint();
    shadow_paint.setDither(true);
    shadow_paint.setAntiAlias(true);
    shadow_paint.setColor(Color.parseColor("#cccccc"));
    shadow_paint.setStyle(Paint.Style.STROKE);
    shadow_paint.setStrokeCap(Paint.Cap.ROUND);
    shadow_paint.setStrokeJoin(Paint.Join.ROUND);
    shadow_paint.setStrokeWidth(dpToPx(3));

    //set fill paint

    TypedValue typedValue3=new TypedValue();
    Resources.Theme theme3=context.getTheme();
    theme3.resolveAttribute(R.attr.democolor,typedValue3,true);
    @ColorInt int color3 =typedValue3.data;
  fill_paint = new Paint();
   /* fill_paint.setDither(true);
    fill_paint.setAntiAlias(true);*/
    fill_paint.setColor(color3);
    fill_paint.setStyle(Paint.Style.FILL);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    //handling height
    int height = getHeight();
    int midHeight = height / 2;
    float amplitude = height / 10;

    //handling width
    int width = getWidth();
    int waveCount = total_wave_count;
    int children_per_sinWave = 2;
    float sinWaveCount = (float)waveCount/children_per_sinWave;
    float sinWaveWidth = width/sinWaveCount;
    float period = (float) Math.toDegrees(2 * Math.PI); //360
    float frequency = sinWaveWidth/period;
    float time = 1 / frequency;
    float shadow_offset = dpToPx(6);


    /* border stroke loop */


    /*shadow stroke loop*/
    for (int i = 0; i < 360 * total_wave_count ; i = i + 20) {
      float x1 = i;
      float y1 = midHeight + amplitude * (float) Math.sin(Math.toRadians(x1) * time);
      float x2 = i + 1;
      float y2 = midHeight + amplitude * (float) Math.sin(Math.toRadians(x2) * time);

      //shadow stroke
      canvas.drawLine(x1, y1+shadow_offset, x2, y2+shadow_offset, shadow_paint);
    }

    /*fill color loop*/
    Path path = new Path();
    for (int i = 0; i < 360 * total_wave_count; i++) {
      float x1 = i;
      float y1 = (midHeight + amplitude * (float) Math.sin(Math.toRadians(x1) * time))-10;

      canvas.drawLine(x1, y1+shadow_offset, x1, 0, fill_paint);
//      //fill path
     // canvas.drawPath(path,fill_paint);
   }
    for (int i = 0; i < 360 * total_wave_count; i = i + 1) {
      float x1 = i;
      float y1 = midHeight + amplitude * (float) Math.sin(Math.toRadians(x1) * time);
      float x2 = i + 1;
      float y2 = midHeight + amplitude * (float) Math.sin(Math.toRadians(x2) * time);

      //border stroke
      canvas.drawLine(x1, y1, x2, y2, stroke_paint);

    }
    for (int i = 0; i < 300 * progress_wave_count; i = i + 1) {
      float x1 = i;
      float y1 = midHeight + amplitude * (float) Math.sin(Math.toRadians(x1) * time);
      float x2 = i + 1;
      float y2 = midHeight + amplitude * (float) Math.sin(Math.toRadians(x2) * time);

      //border stroke
      canvas.drawLine(x1, y1, x2, y2, stroke_paint1);

    }
  }

  private float dpToPx(int x){
    return TypedValue.applyDimension((int)TypedValue.COMPLEX_UNIT_DIP,x,mContext.getResources().getDisplayMetrics());
  }

}