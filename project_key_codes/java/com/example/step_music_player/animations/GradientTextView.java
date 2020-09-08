package com.example.step_music_player.animations;

import android.content.Context;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;

import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

public class GradientTextView extends AppCompatTextView{
    private int total_width,move_distance;
    private Matrix matrix;
    private LinearGradient color_linear_grad;
    private Paint circ_paint;

    // initialization
    public GradientTextView(Context context){ super(context); }
    public GradientTextView(Context context,AttributeSet attr_set){ super(context,attr_set); }
    public GradientTextView(Context context,AttributeSet attr_set,int style){ super(context,attr_set,style); }

    // size changed
    @Override
    protected void onSizeChanged(int width,int height,int old_width,int old_height){
        super.onSizeChanged(width,height,old_width,old_height);
        if(width!=0){
            total_width=width;
        }
        else{
            total_width=getMeasuredWidth();
        }
        circ_paint=getPaint();
        int[] colors=new int[] {0xFF008000,0xFF4169E1,0xFFA500,0xFF800080,0xFFFFFF66,0xFFB22222};
        float[] positions=new float[] {0.0f,0.2f,0.4f,0.6f,0.8f,1.0f};
        color_linear_grad=new LinearGradient(-width,0,0,0,colors,positions,Shader.TileMode.MIRROR);
        // drawing
        circ_paint.setShader(color_linear_grad);
        circ_paint.setColor(Color.BLACK);
        matrix=new Matrix();
    }

    // onDraw
    @Override
    protected void onDraw(Canvas canvas){
        if(matrix==null){
            return;
        }
        // move color gradient
        move_distance+=total_width/80;
        if(move_distance>2*total_width){ // passed all the color, go back
            move_distance-=2*total_width;
        }
        matrix.setTranslate(move_distance,0);
        color_linear_grad.setLocalMatrix(matrix);
        postInvalidateDelayed(100);
        super.onDraw(canvas);
    }

    @Override
    public boolean isFocused(){ return true; }
}