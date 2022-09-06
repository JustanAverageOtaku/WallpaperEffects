package com.zero.zerolivewallpaper.wallpaper.effects;

import com.zero.zerolivewallpaper.wallpaper.common.MyApplication;
import com.zero.zerolivewallpaper.wallpaper.Parallax;

public class EffectWithParalax extends Effect
{
    protected final float[] modelMatrix = new float[16];
    protected float deltaXMax;
    protected float deltaYMax;
    protected float[][] deltaArrayNew = new float[1][2];
    protected float[][] deltaArrayOld;

    protected final Parallax parallax = new Parallax(MyApplication.getInstance());
}
