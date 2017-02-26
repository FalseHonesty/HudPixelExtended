/*
 * ***************************************************************************
 *
 *         Copyright © 2016 unaussprechlich - ALL RIGHTS RESERVED
 *
 * ***************************************************************************
 */

package net.unaussprechlich.managedgui.lib.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

public class DisplayUtil {

    public static int getMcScale() {
        Minecraft mc = Minecraft.getMinecraft();
        int scale = 1;
        if (mc.gameSettings.guiScale == 0) {
            ScaledResolution res = new ScaledResolution(mc);
            scale = res.getScaleFactor();
        } else {
            scale = mc.gameSettings.guiScale;
        }
        return scale;
    }

    public static int getScaledMcWidth() {
        return Minecraft.getMinecraft().displayWidth / getMcScale();
    }

    public static int getScaledMcHeight() {
        return Minecraft.getMinecraft().displayHeight / getMcScale();
    }
}
