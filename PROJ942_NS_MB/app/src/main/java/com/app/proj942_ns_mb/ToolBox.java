package com.app.proj942_ns_mb;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Sarion on 13/10/2015.
 */
public class ToolBox {

    /**
     * @param toTest0 : first IP byte
     * @param toTest1 : second IP byte
     * @param toTest2 : third IP byte
     * @param toTest3 : fourth IP byte
     * @return true if all bytes are between 0 and 255; 0 otherwise
     * @function : checkIP
     * @description : check each byte of an IP address
     */
    public static boolean checkIP(int toTest0, int toTest1, int toTest2, int toTest3) {
        //Declarations
        boolean bResult = false;

        //Check if each byte is between 0 and 255
        if ((toTest0 >= 0 && toTest0 < 256) && (toTest1 >= 0 && toTest1 < 256) && (toTest2 >= 0 && toTest2 < 256) && (toTest3 >= 0 && toTest3 < 256)) {
            bResult = true;
        }

        return bResult;
    }

    /**
     * @param toTest : string to test
     * @return true if there are no special characters; 0 otherwise
     * @function : checkPHPFileName
     * @description : the string contain special characters
     */
    public static boolean checkName(String toTest){
        //Declarations
        boolean         bResult      = false;
        char[]          tabFileName  = null;
        int             iBcl;

        if(toTest.length() != 0) {
            tabFileName = toTest.toCharArray();

            for (iBcl = 0; iBcl < tabFileName.length; iBcl++) {
                if (((int) tabFileName[iBcl] > 46 && (int) tabFileName[iBcl] < 57)              //Forward slash + Numbers
                        || ((int) tabFileName[iBcl] > 64 && (int) tabFileName[iBcl] < 98)      //Upper case
                        || ((int) tabFileName[iBcl] > 96 && (int) tabFileName[iBcl] < 123)    //Lower case
                        || ((int) tabFileName[iBcl] == 95)                                    //Underscore
                        || ((int) tabFileName[iBcl] == 32)                                    //Space
                        || ((int) tabFileName[iBcl] == 45)                                      //Dash or minus
                        )
                {

                    bResult = true;
                } else {
                    bResult = false;
                }
            }
        }
        return bResult;
    }

    public static String translateHTTPAnswer(String stReceived){
        String stResult         = null;

        Calendar cal            = Calendar.getInstance();
        SimpleDateFormat sdf    = new SimpleDateFormat("HH:mm:ss");
        stResult                = sdf.format(cal.getTime());
        stResult                = stResult + " " + stReceived.substring(5, stReceived.length() - 6);

        return stResult;
    }

}