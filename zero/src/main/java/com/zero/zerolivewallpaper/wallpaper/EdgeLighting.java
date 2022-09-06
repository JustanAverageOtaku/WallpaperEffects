package com.zero.zerolivewallpaper.wallpaper;

public class EdgeLighting
{
    public static void LoopWithFixedStartingPoint(int startPos[], int endPos[], int timePassed)
    {
        startPos = new int[]{50, 50};
        int step = 5;

        if (endPos[0] < 1030 && endPos[1] == 50 && timePassed == 0)
        {
            endPos[0] += step;
        }
        else if (endPos[0] == 1030 && endPos[1] < 2350 && timePassed == 0)
        {
            endPos[1] += step;
        }
        else if (endPos[0] > 50 && endPos[1] == 2350 && timePassed == 0)
        {
            endPos[0] -= step;
        }
        else if (endPos[0] == 50 && endPos[1] > 50 && timePassed == 0)
        {
            endPos[1] -= step;
        }
    }
}
