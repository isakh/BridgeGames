package ws.isak.bridge.database;

/*
 *
 *
 * @author isak
 */

public class SwapCardIDORM {

    private static final String TAG = "SwapCardIDORM";

    private static final String TABLE_NAME = "swap_card_id";

    private static final String COMMA_SEP = ", ";

    private static final String COLUMN_SPECIES_ID_TYPE = "INTEGER";
    private static final String COLUMN_SPECIES_ID = "speciesID";

    private static final String COLUMN_SEGMENT_ID_TYPE = "INTEGER";
    private static final String COLUMN_SEGMENT_ID = "segmentID";

    public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME +
            " (" +
            COLUMN_SPECIES_ID + " " + COLUMN_SPECIES_ID_TYPE + COMMA_SEP +
            COLUMN_SEGMENT_ID + " " + COLUMN_SEGMENT_ID_TYPE +
            ")";

    public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;






}
