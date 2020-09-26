package com.uabn.gss.uabnlink.Utility;

import com.uabn.gss.uabnlink.DatabaseTable.UABNSQLitedatabase;

public class TemporaryHelper {
    public static UABNSQLitedatabase uabnsqLitedatabase;

    public static void setUabnsqLitedatabase(UABNSQLitedatabase uabnsqLitedatabase) {
        TemporaryHelper.uabnsqLitedatabase = uabnsqLitedatabase;
    }
}
