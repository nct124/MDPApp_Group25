package mdp.cz3004.ntu.com.mdpapp_group25.other;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigInteger;

import mdp.cz3004.ntu.com.mdpapp_group25.activity.MainActivity;

/**
 * Created by n on 1/9/2017.
 */

public class MazeCanvas extends View{
    //dimensions
    private int numColumns = 15;
    private int numRows = 20;
    //info
    public static final int SP = 1000;
    public static final int GP = 1001;
    public static final int WP = 1002;
    public int rgIndex = -1;
    private int[] maze_info;
    public CoordPair sp;
    public CoordPair gp;
    public CoordPair wp;
    TextView tv;
    private int direction;
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
    int robotSurrounding = Color.BLUE;
    int robotDirection = Color.GRAY;
    int goalPoint = Color.RED;
    int startPoint = Color.GREEN;

    Paint p;

    public MazeCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
        maze_info = new int[numColumns*numRows];
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
        p.setColor(background);
        canvas.drawRect(0,0,getWidth(),getHeight(),p);
        int y = ((grid_width+gap_width)*numRows)-grid_width;//-grid_width;
        for(int j=0;j<numRows;j++){
            y -=grid_width+gap_width;
            int x = -grid_width;
            for(int i=0;i<numColumns;i++){
                if(maze_info[numColumns*j+i]==0){
                    p.setColor(unexplored);
                }else if(maze_info[numColumns*j+i]==1){
                    p.setColor(explored);
                }else if(maze_info[numColumns*j+i]==2){
                    p.setColor(obstacle);
                }
                x +=grid_width+gap_width;
                canvas.drawRect(x,y,(x+grid_width),(y+grid_width),p);
                Log.d("DRAWING",x+" "+y);
            }
        }
        if(sp!=null){
            drawCurrentPosition(sp,canvas,direction);
        }
        if(gp!=null){
            drawGoalPosition(gp,canvas);
        }
        if(wp!=null){
            drawWayPosition(wp,canvas);
        }
    }
    public void setTextView(TextView tv) {
        this.tv = tv;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                int x = (int)event.getX();
                int y = (int)event.getY();
                CoordPair coor = CoordPair.findGrid(x,y,grid_width,gap_width,numRows,numColumns);
                Toast.makeText(getContext(),"coor:"+coor.getCol()+" "+coor.getRow(),Toast.LENGTH_LONG).show();
                if(rgIndex==SP){
                    if(sp!=null &&sp.getCol()==coor.getCol() && sp.getRow()==coor.getRow()){
                        direction = (direction+1)%4;
                    }else{
                        sp = coor;
                        tv.setText(sp.getCol()+"   "+sp.getRow());
                    }
                    ((MainActivity)getContext()).sendText("SP:"+sp.getCol()+","+sp.getRow()+","+direction);
                }else if(rgIndex==GP){
                    gp = coor;
                    tv.setText(gp.getCol()+"   "+gp.getRow());
                    ((MainActivity)getContext()).sendText("GP:"+gp.getCol()+","+gp.getRow());
                }else if(rgIndex==WP) {
                    if(wp!=null &&wp.getCol()==coor.getCol() && wp.getRow()==coor.getRow()){
                        wp = null;
                        ((MainActivity)getContext()).sendText("WP:null");
                    }else{
                        wp = coor;
                        tv.setText(wp.getCol()+"   "+wp.getRow());
                        ((MainActivity)getContext()).sendText("WP:"+wp.getCol()+","+wp.getRow());
                    }

                }
                break;
        }
        this.invalidate();
        return true;//super.onTouchEvent(event);
    }
    private void drawGoalPosition(CoordPair pt,Canvas canvas) {
        p.setColor(goalPoint);
        canvas.drawRect(pt.getX(),pt.getY(),(pt.getX()+grid_width),(pt.getY()+grid_width),p);
    }
    private void drawWayPosition(CoordPair pt,Canvas canvas) {
        p.setColor(wayPoint);
        canvas.drawRect(pt.getX(),pt.getY(),(pt.getX()+grid_width),(pt.getY()+grid_width),p);
    }
	//direction 1(N),2(S),3(E),4(W)
	private void drawCurrentPosition(CoordPair pt,Canvas canvas,int direction){
        //draw current position
        p.setColor(robot);
        canvas.drawRect(pt.getX(),pt.getY(),(pt.getX()+grid_width),(pt.getY()+grid_width),p);
        Point pt1 = new Point();
        Point pt2 = new Point();
        Point pt3 = new Point();
        switch(direction){
            case 0: //N
                pt1.set(((int)(pt.getX()+((double)grid_width/2))),((int)(pt.getY()+((double)grid_width/100*10))));
                pt2.set(((int)(pt.getX()+((double)grid_width/100*10))),((int)(pt.getY()+((double)grid_width/100*90))));
                pt3.set(((int)(pt.getX()+((double)grid_width/100*90))),((int)(pt.getY()+((double)grid_width/100*90))));
                break;
            case 2: // S
                pt1.set(((int)(pt.getX()+((double)grid_width/2))),((int)(pt.getY()+((double)grid_width/100*90))));
                pt2.set(((int)(pt.getX()+((double)grid_width/100*10))),((int)(pt.getY()+((double)grid_width/100*10))));
                pt3.set(((int)(pt.getX()+((double)grid_width/100*90))),((int)(pt.getY()+((double)grid_width/100*10))));
                break;
            case 1: //E
                pt1.set(((int)(pt.getX()+((double)grid_width/100*90))),((int)(pt.getY()+((double)grid_width/2))));
                pt2.set(((int)(pt.getX()+((double)grid_width/100*10))),((int)(pt.getY()+((double)grid_width/100*10))));
                pt3.set(((int)(pt.getX()+((double)grid_width/100*10))),((int)(pt.getY()+((double)grid_width/100*90))));
                break;
            case 3: //W
                pt1.set(((int)(pt.getX()+((double)grid_width/100*10))),((int)(pt.getY()+((double)grid_width/2))));
                pt2.set(((int)(pt.getX()+((double)grid_width/100*90))),((int)(pt.getY()+((double)grid_width/100*10))));
                pt3.set(((int)(pt.getX()+((double)grid_width/100*90))),((int)(pt.getY()+((double)grid_width/100*90))));
                break;
        }
        drawTriangle(pt1,pt2,pt3,canvas);

        //draw surrounding
        p.setColor(robotSurrounding);
        canvas.drawRect(pt.getX()-(grid_width+gap_width),
                pt.getY()+(grid_width+gap_width),
                (pt.getX()+grid_width-(grid_width+gap_width)),
                (pt.getY()+grid_width+(grid_width+gap_width))
                ,p);
        canvas.drawRect(pt.getX(),
                pt.getY()+(grid_width+gap_width),
                (pt.getX()+grid_width),
                (pt.getY()+grid_width+(grid_width+gap_width))
                ,p);
        canvas.drawRect(pt.getX()+(grid_width+gap_width),
                pt.getY()+(grid_width+gap_width),
                (pt.getX()+grid_width+(grid_width+gap_width)),
                (pt.getY()+grid_width+(grid_width+gap_width))
                ,p);
        canvas.drawRect(pt.getX()-(grid_width+gap_width),
                pt.getY(),
                (pt.getX()+grid_width-(grid_width+gap_width)),
                (pt.getY()+grid_width)
                ,p);
        canvas.drawRect(pt.getX()+(grid_width+gap_width),
                pt.getY(),
                (pt.getX()+grid_width+(grid_width+gap_width)),
                (pt.getY()+grid_width)
                ,p);
        canvas.drawRect(pt.getX()-(grid_width+gap_width),
                pt.getY()-(grid_width+gap_width),
                (pt.getX()+grid_width-(grid_width+gap_width)),
                (pt.getY()+grid_width-(grid_width+gap_width))
                ,p);
        canvas.drawRect(pt.getX(),
                pt.getY()-(grid_width+gap_width),
                (pt.getX()+grid_width),
                (pt.getY()+grid_width-(grid_width+gap_width))
                ,p);
        canvas.drawRect(pt.getX()+(grid_width+gap_width),
                pt.getY()-(grid_width+gap_width),
                (pt.getX()+grid_width+(grid_width+gap_width)),
                (pt.getY()+grid_width-(grid_width+gap_width))
                ,p);
    }
    private void drawTriangle(Point pt1,Point pt2,Point pt3,Canvas canvas){
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(2);
        paint.setColor(robotDirection);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);

        Path path = new Path();
        path.setFillType(Path.FillType.EVEN_ODD);
        path.moveTo(pt1.x,pt1.y);
        path.lineTo(pt2.x,pt2.y);
        path.lineTo(pt3.x,pt3.y);
        path.lineTo(pt1.x,pt1.y);
        path.close();

        canvas.drawPath(path, paint);
    }

    public void updateMaze(String part1,String part2){
        String part1bin = new BigInteger(part1, 16).toString(2);
        int len = 304-part1bin.length();
        for(int i=0;i<len;i++){
            part1bin = "0"+part1bin;
        }
        String part2bin = new BigInteger(part2, 16).toString(2);
        len = part2.length()*4-part2bin.length();
        for(int i=0;i<len;i++){
            part2bin = "0"+part2bin;
        }
        maze_info = new int[numColumns*numRows];
        int j = 0; //number of bits for part2
        for(int i=2;i<part1bin.length()-2;i++){
            maze_info[i-2] += Character.getNumericValue(part1bin.charAt(i));
            if(maze_info[i-2]==1) {
                j++;
            }
        }
        int k=0; //index for part2
        for(int i=2;i<part1bin.length()-2;i++){
            if(maze_info[i-2]==1){
                maze_info[i-2] += Character.getNumericValue(part2bin.charAt(k));
                k++;
                if(k==j){
                    break;
                }
            }
        }
        this.invalidate();
    }
}
