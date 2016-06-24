/*******************************************************************************
 * HudPixel Reloaded (github.com/palechip/HudPixel), an unofficial Minecraft Mod for the Hypixel Network
 *
 * Copyright (c) 2014-2015 palechip (twitter.com/palechip) and contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *******************************************************************************/

package de.unaussprechlich.hudpixelextended;

import de.unaussprechlich.hudpixelextended.newcomponents.OnlineFriendsComponent;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.UUID;

public class HudPixelExtended {


    private static HudPixelExtended hudPixelExtendedInstance = null;
    private static HudPixelExtendedEventHandler hudPixelExtendedEventHandler = new HudPixelExtendedEventHandler();
    public static OnlineFriendsComponent onlineFriends = new OnlineFriendsComponent();
    public static UUID UUID;

    private HudPixelExtended(){}

    public static HudPixelExtended getInstance(){
        if(hudPixelExtendedInstance != null){
            return hudPixelExtendedInstance;
        } else {
            hudPixelExtendedInstance = new HudPixelExtended();
            return hudPixelExtendedInstance;
        }
    }

    public void setup(){

        UUID = Minecraft.getMinecraft().getSession().getProfile().getId();

        MinecraftForge.EVENT_BUS.register(hudPixelExtendedEventHandler);
        FMLCommonHandler.instance().bus().register(hudPixelExtendedEventHandler);

    }
}
