package ws.isak.bridge.common;

import android.widget.ImageView;

/*
 * The class ComposeTrackerCellData hold the relevant information for a given cell on the tracker
 * board.  It is comprised of an address (row, col), an ImageView, and a ComposeSampleData object.
 *
 * @author isak
 */


public class ComposeTrackerCellData {

    int rowLoc;
    int colLoc;
    ImageView cellImageView;
    ComposeSampleData cellSampleData;       //initialized to null, this is updated when samples are placed

    //constructor
    public ComposeTrackerCellData () {
        setRowLoc (-1);
        setColLoc (-1);
        setCellImageView (null);
        setCellSampleData (null);
    }

    public ComposeTrackerCellData (int row, int col, ImageView iv, ComposeSampleData csd) {
        setRowLoc(row);
        setColLoc(col);
        setCellImageView(iv);
        setCellSampleData(csd);
    }

    //[1] set/get rowLoc
    public void setRowLoc (int row) { rowLoc = row; }
    public int getRowLoc () { return rowLoc; }

    //[2] set/get colLoc
    public void setColLoc (int col) { colLoc = col; }
    public int getColLoc () { return colLoc; }

    //[3] set/get cellImageView
    public void setCellImageView (ImageView iv) {cellImageView = iv; }
    public ImageView getCellImageView () { return cellImageView; }

    //4] set/get cellSampleData
    public void setCellSampleData (ComposeSampleData csd) { cellSampleData = csd; }
    public ComposeSampleData getCellSampleData () { return  cellSampleData; }
}
