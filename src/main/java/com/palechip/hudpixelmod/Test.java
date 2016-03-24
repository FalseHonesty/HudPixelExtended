package com.palechip.hudpixelmod;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.world.BossInfo;
import net.minecraft.world.BossInfoLerping;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

import java.util.HashMap;
import java.util.UUID;

public class Test {
    public static String getNameOfBoss() {
        for (BossInfoLerping lerp : ((HashMap<UUID, BossInfoLerping>) ReflectionHelper.getPrivateValue(GuiBossOverlay.class, Minecraft.getMinecraft().ingameGUI.getBossOverlay(), "mapBossInfos")).values())
            try {
                return (String) ReflectionHelper.getPrivateValue(BossInfo.class, lerp, "name");
            } catch (Exception e) {
            }
        return null;
    }
}
