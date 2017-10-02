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
    public static final int CP = 1000;
    public static final int GP = 1001;
    public static final int WP = 1002;
    public int rgIndex = -1;
    private int[] maze_info;
    public CoordPair sp;
    public CoordPair cp;
    public CoordPair gp;
    public CoordPair wp;
    TextView tvOne;
    TextView tvTwo;
    public int startDirection;
    public int direction;
    //maze
    private int grid_width;
    private int gap_width = 5;
    private int canvas_height;
    //Colors
    int background = Color.WHITE;
    int unexplored = Color.DKGRAY;
    int explored = Color.LTGRAY;
    int obstacle = Color.BLACK;
    int wayPoint = Color.GREEN;
    int currentPoint = Color.YELLOW;
    int currentSurrounding = Color.YELLOW;//Color.argb(0,88,216,243);
    int robotDirection = Color.GRAY;
    int goalPoint = Color.RED;
    int startPoint = Color.BLUE;

    Paint p;

    public MazeCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
        maze_info = new int[numColumns*numRows];
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        grid_width = (getWidth()-(gap_width*(numColumns+2)))/(numColumns+1); //(getWidth()-(gap_width*(numColumns+1)))/numColumns;
        canvas_height = (grid_width*numRows)+((numRows+2)*gap_width)+(grid_width+gap_width)-25;//(grid_width*numRows)+((numRows+1)*gap_width);
        Log.d("SIZE",getWidth()+" "+getHeight()+" "+(grid_width*numColumns+gap_width*(numColumns+1)));
        getLayoutParams().height = canvas_height;
        this.sp = CoordPair.findXY(1,1,grid_width,gap_width,numRows,numColumns);
        this.cp = CoordPair.findXY(1,1,grid_width,gap_width,numRows,numColumns);
        requestLayout();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        p.setStyle(Paint.Style.FILL);
        p.setColor(background);
        canvas.drawRect(0,0,getWidth(),getHeight(),p);
        p.setColor(Color.BLACK);
        p.setTextSize(40);
        //canvas.drawText(Integer.toString(0),(float)50,(float)50,p);
        int y = ((grid_width+gap_width)*numRows);//-grid_width;
        //int yforColNum = ((grid_width+gap_width)*(numRows+1)-20);

        for(int j=0;j<numRows;j++){
            y -=grid_width+gap_width;
            int x = -grid_width+(grid_width+gap_width);
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
            }
        }
        int xforRowNum = -grid_width;
        y = ((grid_width+gap_width)*numRows);
        for(int j=0;j<numRows;j++){
            y -=grid_width+gap_width;
            if(j>9){
                canvas.drawText(Integer.toString(j),(float)xforRowNum+45,(float)y+(grid_width)-10,p);
            }else{
                canvas.drawText(Integer.toString(j),(float)xforRowNum+65,(float)y+(grid_width)-10,p);
            }
        }
        int yforColNum = ((grid_width+gap_width)*(numRows+1)-20);
        int x = -grid_width+(grid_width+gap_width);
        for(int i=0;i<numColumns;i++){
            x +=grid_width+gap_width;
            if(i>9){
                canvas.drawText(Integer.toString(i),(float)x,(float)yforColNum,p);
            }else{
                canvas.drawText(Integer.toString(i),(float)x+10,(float)yforColNum,p);
            }
        }

        if(sp!=null){
            drawStartPosition(sp,canvas,startDirection);
        }
        if(gp!=null){
            drawGoalPosition(gp,canvas);
        }
        if(wp!=null){
            drawWayPosition(wp,canvas);
        }
        if(cp!=null){
            drawCurrentPosition(cp,canvas,direction);
        }
    }
    public void setCoorTextView(TextView tv) {
        this.tvOne = tv;
    }
    public void setErrorTextView(TextView tv) {
        this.tvTwo = tv;
    }
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_UP:
                int x = (int)event.getX();
                int y = (int)event.getY();
                CoordPair coor = CoordPair.findGrid(x,y,grid_width,gap_width,numRows,numColumns);
                //Toast.makeText(getContext(),"coor:"+coor.getCol()+" "+coor.getRow(),Toast.LENGTH_LONG).show();
                if(rgIndex==CP){
                    if (validate(coor, gp, wp, rgIndex)) {
                        tvTwo.setText("");
                        if (cp != null && cp.getCol() == coor.getCol() && cp.getRow() == coor.getRow()) {
                            direction = (direction + 1) % 4;
                            startDirection = direction;
                        } else {
                            cp = coor;
                            sp = coor;
                            tvOne.setText(cp.getCol() + "   " + cp.getRow());
                        }
                        ((MainActivity) getContext()).sendText("CP:" + cp.toSingleArray() + "," + direction);
                    }
                }else if(rgIndex==GP){
                    if (validate(coor, cp, wp, rgIndex)) {
                        tvTwo.setText("");
                        gp = coor;
                        tvOne.setText(gp.getCol() + "   " + gp.getRow());
                        ((MainActivity) getContext()).sendText("GP:" + gp.toSingleArray());
                    }
                }else if(rgIndex==WP) {
                    if (validate(coor, cp, gp, rgIndex)) {
                        tvTwo.setText("");
                        if (wp != null && wp.getCol() == coor.getCol() && wp.getRow() == coor.getRow()) {
                            wp = null;
                            ((MainActivity) getContext()).sendText("WP:null");
                        } else {
                            wp = coor;
                            tvOne.setText(wp.getCol() + "   " + wp.getRow());
                            ((MainActivity) getContext()).sendText("WP:" + wp.toSingleArray());
                        }
                    }
                }
                break;
        }
        this.invalidate();
        return true;//super.onTouchEvent(event);
    }
    private boolean validate(CoordPair currentPoint, CoordPair coordPairOne, CoordPair coordPairTwo, int index) {
        if (coordPairOne != null && currentPoint.getCol() == coordPairOne.getCol() && currentPoint.getRow() == coordPairOne.getRow()) {
            tvTwo.setText("Error: " + index);
            return false;
        } else if (coordPairTwo != null && currentPoint.getCol() == coordPairTwo.getCol() && currentPoint.getRow() == coordPairTwo.getRow()) {
            tvTwo.setText("Error: " + index);
            return false;
        }
        if(index!=CP){
            if(coordPairOne!=null &&
                    ((currentPoint.getRow()==(coordPairOne.getRow()-1)&&currentPoint.getCol()==(coordPairOne.getCol()-1))
                    ||(currentPoint.getRow()==(coordPairOne.getRow()-1)&&currentPoint.getCol()==(coordPairOne.getCol()))
                    ||(currentPoint.getRow()==(coordPairOne.getRow()-1)&&currentPoint.getCol()==(coordPairOne.getCol()+1))
                    ||(currentPoint.getRow()==(coordPairOne.getRow())&&currentPoint.getCol()==(coordPairOne.getCol()-1))
                    ||(currentPoint.getRow()==(coordPairOne.getRow())&&currentPoint.getCol()==(coordPairOne.getCol()+1))
                    ||(currentPoint.getRow()==(coordPairOne.getRow()+1)&&currentPoint.getCol()==(coordPairOne.getCol()-1))
                    ||(currentPoint.getRow()==(coordPairOne.getRow()+1)&&currentPoint.getCol()==(coordPairOne.getCol()))
                    ||(currentPoint.getRow()==(coordPairOne.getRow()+1)&&currentPoint.getCol()==(coordPairOne.getCol()+1))
                    )){
                tvTwo.setText("Error: " + index);
                return false;
            }
        }else {
            if (coordPairOne != null &&
                    ((coordPairOne.getRow() == (currentPoint.getRow() - 1) && coordPairOne.getCol() == (currentPoint.getCol() - 1))
                            || (coordPairOne.getRow() == (currentPoint.getRow() - 1) && coordPairOne.getCol() == (currentPoint.getCol()))
                            || (coordPairOne.getRow() == (currentPoint.getRow() - 1) && coordPairOne.getCol() == (currentPoint.getCol() + 1))
                            || (coordPairOne.getRow() == (currentPoint.getRow()) && coordPairOne.getCol() == (currentPoint.getCol() - 1))
                            || (coordPairOne.getRow() == (currentPoint.getRow()) && coordPairOne.getCol() == (currentPoint.getCol() + 1))
                            || (coordPairOne.getRow() == (currentPoint.getRow() + 1) && coordPairOne.getCol() == (currentPoint.getCol() - 1))
                            || (coordPairOne.getRow() == (currentPoint.getRow() + 1) && coordPairOne.getCol() == (currentPoint.getCol()))
                            || (coordPairOne.getRow() == (currentPoint.getRow() + 1) && coordPairOne.getCol() == (currentPoint.getCol() + 1))
                    )) {
                tvTwo.setText("Error: " + index);
                return false;
            }
            if (coordPairTwo != null &&
                    ((coordPairTwo.getRow() == (currentPoint.getRow() - 1) && coordPairTwo.getCol() == (currentPoint.getCol() - 1))
                            || (coordPairTwo.getRow() == (currentPoint.getRow() - 1) && coordPairTwo.getCol() == (currentPoint.getCol()))
                            || (coordPairTwo.getRow() == (currentPoint.getRow() - 1) && coordPairTwo.getCol() == (currentPoint.getCol() + 1))
                            || (coordPairTwo.getRow() == (currentPoint.getRow()) && coordPairTwo.getCol() == (currentPoint.getCol() - 1))
                            || (coordPairTwo.getRow() == (currentPoint.getRow()) && coordPairTwo.getCol() == (currentPoint.getCol() + 1))
                            || (coordPairTwo.getRow() == (currentPoint.getRow() + 1) && coordPairTwo.getCol() == (currentPoint.getCol() - 1))
                            || (coordPairTwo.getRow() == (currentPoint.getRow() + 1) && coordPairTwo.getCol() == (currentPoint.getCol()))
                            || (coordPairTwo.getRow() == (currentPoint.getRow() + 1) && coordPairTwo.getCol() == (currentPoint.getCol() + 1))
                    )) {
                tvTwo.setText("Error: " + index);
                return false;
            }
        }
        for(int i=0;i<numRows;i++){
            if(currentPoint.getRow()==i&&(currentPoint.getCol()==0||currentPoint.getCol()==numColumns-1)){
                tvTwo.setText("Error: " + index);
                return false;
            }
        }
        for(int i=0;i<numColumns;i++){
            if(currentPoint.getCol()==i&&(currentPoint.getRow()==0||currentPoint.getRow()==numRows-1)){
                tvTwo.setText("Error: " + index);
                return false;
            }
        }
        return true;
    }
    private void drawGoalPosition(CoordPair pt,Canvas canvas) {
        p.setColor(goalPoint);
        canvas.drawRect(pt.getX(),pt.getY()-gap_width,(pt.getX()+grid_width),(pt.getY()+grid_width-gap_width),p);
    }
    private void drawWayPosition(CoordPair pt,Canvas canvas) {
        p.setColor(wayPoint);
        canvas.drawRect(pt.getX(),pt.getY()-gap_width,(pt.getX()+grid_width),(pt.getY()+grid_width-gap_width),p);
    }
    private void drawStartPosition(CoordPair pt,Canvas canvas,int direction) {
        p.setColor(startPoint);
        canvas.drawRect(pt.getX(),pt.getY()-gap_width,(pt.getX()+grid_width),(pt.getY()+grid_width-gap_width),p);
        Point pt1 = new Point();
        Point pt2 = new Point();
        Point pt3 = new Point();
        switch(direction){
            case 0: //N
                pt1.set(((int)(pt.getX()+((double)grid_width/2))),((int)(pt.getY()-gap_width+((double)grid_width/100*10))));
                pt2.set(((int)(pt.getX()+((double)grid_width/100*10))),((int)(pt.getY()-gap_width+((double)grid_width/100*90))));
                pt3.set(((int)(pt.getX()+((double)grid_width/100*90))),((int)(pt.getY()-gap_width+((double)grid_width/100*90))));
                break;
            case 2: // S
                pt1.set(((int)(pt.getX()+((double)grid_width/2))),((int)(pt.getY()-gap_width+((double)grid_width/100*90))));
                pt2.set(((int)(pt.getX()+((double)grid_width/100*10))),((int)(pt.getY()-gap_width+((double)grid_width/100*10))));
                pt3.set(((int)(pt.getX()+((double)grid_width/100*90))),((int)(pt.getY()-gap_width+((double)grid_width/100*10))));
                break;
            case 1: //E
                pt1.set(((int)(pt.getX()+((double)grid_width/100*90))),((int)(pt.getY()-gap_width+((double)grid_width/2))));
                pt2.set(((int)(pt.getX()+((double)grid_width/100*10))),((int)(pt.getY()-gap_width+((double)grid_width/100*10))));
                pt3.set(((int)(pt.getX()+((double)grid_width/100*10))),((int)(pt.getY()-gap_width+((double)grid_width/100*90))));
                break;
            case 3: //W
                pt1.set(((int)(pt.getX()+((double)grid_width/100*10))),((int)(pt.getY()-gap_width+((double)grid_width/2))));
                pt2.set(((int)(pt.getX()+((double)grid_width/100*90))),((int)(pt.getY()-gap_width+((double)grid_width/100*10))));
                pt3.set(((int)(pt.getX()+((double)grid_width/100*90))),((int)(pt.getY()-gap_width+((double)grid_width/100*90))));
                break;
        }
        drawTriangle(pt1,pt2,pt3,canvas);
    }
	//direction 0(N),1(S),2(E),3(W)
	private void drawCurrentPosition(CoordPair pt,Canvas canvas,int direction){
        //draw current position
        p.setColor(currentPoint);
        canvas.drawRect(pt.getX(),pt.getY()-gap_width,(pt.getX()+grid_width),(pt.getY()+grid_width-gap_width),p);
        Point pt1 = new Point();
        Point pt2 = new Point();
        Point pt3 = new Point();
        switch(direction){
            case 0: //N
                pt1.set(((int)(pt.getX()+((double)grid_width/2))),((int)(pt.getY()-gap_width+((double)grid_width/100*10))));
                pt2.set(((int)(pt.getX()+((double)grid_width/100*10))),((int)(pt.getY()-gap_width+((double)grid_width/100*90))));
                pt3.set(((int)(pt.getX()+((double)grid_width/100*90))),((int)(pt.getY()-gap_width+((double)grid_width/100*90))));
                break;
            case 2: // S
                pt1.set(((int)(pt.getX()+((double)grid_width/2))),((int)(pt.getY()-gap_width+((double)grid_width/100*90))));
                pt2.set(((int)(pt.getX()+((double)grid_width/100*10))),((int)(pt.getY()-gap_width+((double)grid_width/100*10))));
                pt3.set(((int)(pt.getX()+((double)grid_width/100*90))),((int)(pt.getY()-gap_width+((double)grid_width/100*10))));
                break;
            case 1: //E
                pt1.set(((int)(pt.getX()+((double)grid_width/100*90))),((int)(pt.getY()-gap_width+((double)grid_width/2))));
                pt2.set(((int)(pt.getX()+((double)grid_width/100*10))),((int)(pt.getY()-gap_width+((double)grid_width/100*10))));
                pt3.set(((int)(pt.getX()+((double)grid_width/100*10))),((int)(pt.getY()-gap_width+((double)grid_width/100*90))));
                break;
            case 3: //W
                pt1.set(((int)(pt.getX()+((double)grid_width/100*10))),((int)(pt.getY()-gap_width+((double)grid_width/2))));
                pt2.set(((int)(pt.getX()+((double)grid_width/100*90))),((int)(pt.getY()-gap_width+((double)grid_width/100*10))));
                pt3.set(((int)(pt.getX()+((double)grid_width/100*90))),((int)(pt.getY()-gap_width+((double)grid_width/100*90))));
                break;
        }
        drawTriangle(pt1,pt2,pt3,canvas);

        //draw surrounding
        p.setColor(currentSurrounding);
        canvas.drawRect(pt.getX()-(grid_width+gap_width),
                pt.getY()-gap_width+(grid_width+gap_width),
                (pt.getX()+grid_width-(grid_width+gap_width)),
                (pt.getY()-gap_width+grid_width+(grid_width+gap_width))
                ,p);
        canvas.drawRect(pt.getX(),
                pt.getY()-gap_width+(grid_width+gap_width),
                (pt.getX()+grid_width),
                (pt.getY()-gap_width+grid_width+(grid_width+gap_width))
                ,p);
        canvas.drawRect(pt.getX()+(grid_width+gap_width),
                pt.getY()-gap_width+(grid_width+gap_width),
                (pt.getX()+grid_width+(grid_width+gap_width)),
                (pt.getY()-gap_width+grid_width+(grid_width+gap_width))
                ,p);
        canvas.drawRect(pt.getX()-(grid_width+gap_width),
                pt.getY()-gap_width,
                (pt.getX()+grid_width-(grid_width+gap_width)),
                (pt.getY()-gap_width+grid_width)
                ,p);
        canvas.drawRect(pt.getX()+(grid_width+gap_width),
                pt.getY()-gap_width,
                (pt.getX()+grid_width+(grid_width+gap_width)),
                (pt.getY()-gap_width+grid_width)
                ,p);
        canvas.drawRect(pt.getX()-(grid_width+gap_width),
                pt.getY()-gap_width-(grid_width+gap_width),
                (pt.getX()+grid_width-(grid_width+gap_width)),
                (pt.getY()-gap_width+grid_width-(grid_width+gap_width))
                ,p);
        canvas.drawRect(pt.getX(),
                pt.getY()-gap_width-(grid_width+gap_width),
                (pt.getX()+grid_width),
                (pt.getY()-gap_width+grid_width-(grid_width+gap_width))
                ,p);
        canvas.drawRect(pt.getX()+(grid_width+gap_width),
                pt.getY()-gap_width-(grid_width+gap_width),
                (pt.getX()+grid_width+(grid_width+gap_width)),
                (pt.getY()-gap_width+grid_width-(grid_width+gap_width))
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
    public void updateCP(int coor,int direction){
        if(direction<0){
            this.direction = 3;
        }else{
            this.direction = direction%4;
        }

        int row = (coor/numColumns);
        int col = (coor%numColumns);

        Log.d("CP",coor+" "+row+" "+col);
        cp = CoordPair.findXY(row,col,grid_width,gap_width,numRows,numColumns);
        for(int i=-1;i<2;i++){//row
            for(int j=-1;j<2;j++) {//col
                int index = cp.toSingleArray()+(j)+(i*numColumns);
                if (maze_info[index]==2){
                    ((MainActivity) getContext()).setStatus("COLLISION ");
                }
            }
        }
        this.invalidate();
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
            //part2bin = part2bin+"0";
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
