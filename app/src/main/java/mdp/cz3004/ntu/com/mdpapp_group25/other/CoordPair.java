package mdp.cz3004.ntu.com.mdpapp_group25.other;

/**
 * Created by n on 4/9/2017.
 */

public class CoordPair {
    private int row;
    private int col;
    private int x;
    private int y;

    public CoordPair(int row, int col, int x, int y) {
        this.row = row;
        this.col = col;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRow() {
        return row;
    }
    public void setRow(int row) {
        this.row = row;
    }
    public int getCol() {
        return col;
    }
    public void setCol(int col) {
        this.col = col;
    }

    public static CoordPair findGrid(int x,int y,int grid_width,int gap_width,int maxrows,int maxcol){
        int col = x/(grid_width+gap_width);
        int row = maxrows - Math.abs(y/(grid_width+gap_width))-1;
        int real_x = col *(grid_width+gap_width)+gap_width;
        int real_y = (grid_width+gap_width)*(maxrows-1-row)+gap_width;//row *(grid_width+gap_width)+gap_width;
        return new CoordPair(row,col,real_x,real_y);
    }
    public static CoordPair findXY(int row,int col,int grid_width,int gap_width){
        int x = col *(grid_width+gap_width);
        int y = row *(grid_width+gap_width);
        return new CoordPair(row,col,x,y);
    }
    //starting from 0
    public int toSingleArray(){
        return (row*15)+col;
    }
}
