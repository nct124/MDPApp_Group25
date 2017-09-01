package mdp.cz3004.ntu.com.mdpapp_group25.other;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

/**
 * Created by n on 1/9/2017.
 */

public class MazeCanvas extends View{
    //dimensions
    private int numColumns = 15;
    private int numRows = 20;
    private int maze_info;
    //maze
    private int grid_width;
    private int gap_width = 5;
    private int canvas_height;
    //Colors
    int background = Color.WHITE;
    int unexplored = Color.GRAY;
    int explored = Color.BLUE;
    int obstacle = Color.BLACK;
    int wayPoint = Color.MAGENTA;
    int robot = Color.YELLOW;
    int robotSurrounding = Color.YELLOW;
    int goalPoint = Color.RED;
    int startPoint = Color.GREEN;

    Paint p;

    public MazeCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        grid_width = (getWidth()-(gap_width*(numColumns+1)))/numColumns;
        canvas_height = (grid_width*numRows)+((numRows+1)*gap_width);
        Log.d("SIZE",getWidth()+" "+getHeight()+" "+(grid_width*numColumns+gap_width*(numColumns+1)));
        getLayoutParams().height = canvas_height;
        requestLayout();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        p.setStyle(Paint.Style.FILL);
        p.setColor(explored);
        //canvas.drawRect(0,0,getWidth(),getHeight(),p);
        p.setColor(unexplored);
        int y = -grid_width;
        for(int j=0;j<numRows;j++){
            y +=grid_width+gap_width;
            int x = -grid_width;
            for(int i=0;i<numColumns;i++){
                x +=grid_width+gap_width;
                canvas.drawRect(x,y,(x+grid_width),(y+grid_width),p);
                Log.d("DRAWING",x+" "+y);
            }

        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                int x = (int)event.getX();
                int y = (int)event.getY();
                int col = x/(grid_width+gap_width);
                int row = y/(grid_width+gap_width);
                Toast.makeText(getContext(),row+" "+col,Toast.LENGTH_LONG).show();
        }
        return true;//super.onTouchEvent(event);
    }
}
