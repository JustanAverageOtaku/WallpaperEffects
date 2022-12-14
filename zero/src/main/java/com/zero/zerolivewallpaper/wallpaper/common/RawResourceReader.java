package com.zero.zerolivewallpaper.wallpaper.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class RawResourceReader {

    public static String readTextFileFromRawResource(final int resourceId)
    {
        //if (context == null)
        //{
        //    System.out.println("Null");
        //}
        final InputStream inputStream = MyApplication.getInstance().getResources().openRawResource(resourceId);
        final InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        final BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        String nextLine;
        final StringBuilder body = new StringBuilder();

        try
        {
            while ((nextLine = bufferedReader.readLine()) != null)
            {
                body.append(nextLine);
                body.append('\n');
            }
        }
        catch (IOException e)
        {
            return null;
        }

        return body.toString();
    }
}
