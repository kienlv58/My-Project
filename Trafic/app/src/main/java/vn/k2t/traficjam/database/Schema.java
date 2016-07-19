
package vn.k2t.traficjam.database;
public class Schema {
	
    public static final String DATABASE_NAME = "traffic.sqlite";
    public static final int DATABASE_VERSION = 1;
    
    /**
     * Create table comic
     */
    public static final String TABLE_USER = "user";

    public static final String BLANK = " ";
    public static final String COMMA = ",";
    public static final String PRIMARY_KEY = "PRIMARY KEY";
    public static final String AUTOINCREMENT = "AUTOINCREMENT";
    public static final String INTEGER = "INTEGER";
    public static final String TEXT = "TEXT";
    public static final String DEFAULT_0 = "DEFAULT 0";
    public static final String DEFAULT_STATUS = "DEFAULT A";

    /* ------------- METADATA -------------- */
    public static class TABLE {

        /**
         * Table structure COMIC
         */
        public static class USER {
            public static final String TABLE_NAME = TABLE_USER;

            public static class FIELD {
                public static final String ID = "id";
                public static final String UID = "uid";
                public static final String NAME = "name";
                public static final String EMAIL = "email";
                public static final String AVATAR = "avatar";
                public static final String UIDPROVIDER = "uidProvider";
                public static final String RANK = "rank";
                public static final String LOCATION = "location";

                public static final String CREATED_AT = "createdat";
                public static final String UPDATED_AT = "updated_at";
            }

            public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
                    FIELD.ID + BLANK + INTEGER + BLANK + PRIMARY_KEY + BLANK + AUTOINCREMENT + BLANK + COMMA +
                    FIELD.UID + BLANK + TEXT + COMMA +
                    FIELD.NAME + BLANK + TEXT + COMMA +
                    FIELD.EMAIL + BLANK + TEXT + COMMA +
                    FIELD.AVATAR + BLANK + TEXT + COMMA +
                    FIELD.UIDPROVIDER + BLANK + TEXT  + COMMA +
                    FIELD.RANK + BLANK + TEXT  + COMMA +
                    FIELD.LOCATION + BLANK + TEXT  + COMMA +
                    FIELD.CREATED_AT + BLANK + TEXT + COMMA +
                    FIELD.UPDATED_AT + BLANK + TEXT + COMMA + " )";

            public static final String DROP_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;
        }
    }

}