package com.palechip.hudpixelmod.extended.util

import com.palechip.hudpixelmod.extended.HudPixelExtendedEventHandler.registerIEvent
import com.palechip.hudpixelmod.extended.HudPixelExtendedEventHandler.unregisterIEvent
import com.palechip.hudpixelmod.extended.util.LoggerHelper.logInfo
import com.palechip.hudpixelmod.extended.util.LoggerHelper.logWarn
import net.minecraft.client.Minecraft.getMinecraft
import net.minecraft.client.renderer.texture.DynamicTexture
import net.minecraft.util.ResourceLocation
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.awt.image.BufferedImage
import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.concurrent.Callable
import java.util.concurrent.ExecutionException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors.newSingleThreadExecutor
import java.util.concurrent.Future
import javax.imageio.ImageIO.read

/* **********************************************************************************************************************
 * HudPixelReloaded - License
 * <p>
 * The repository contains parts of Minecraft Forge and its dependencies. These parts have their licenses
 * under forge-docs/. These parts can be downloaded at files.minecraftforge.net.This project contains a
 * unofficial copy of pictures from the official Hypixel website. All copyright is held by the creator!
 * Parts of the code are based upon the Hypixel Public API. These parts are all in src/main/java/net/hypixel/api and
 * subdirectories and have a special copyright header. Unfortunately they are missing a license but they are obviously
 * intended for usage in this kind of application. By default, all rights are reserved.
 * The original version of the HudPixel Mod is made by palechip and published under the MIT license.
 * The majority of code left from palechip's creations is the component implementation.The ported version to
 * Minecraft 1.8.9 and up HudPixel Reloaded is made by PixelModders/Eladkay and also published under the MIT license
 * (to be changed to the new license as detailed below in the next minor update).
 * <p>
 * For the rest of the code and for the build the following license applies:
 * <p>
 * # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
 * #  HudPixel by PixelModders, Eladkay & unaussprechlich is licensed under a Creative Commons         #
 * #  Attribution-NonCommercial-ShareAlike 4.0 International License with the following restrictions.  #
 * #  Based on a work at HudPixelExtended & HudPixel.                                                  #
 * # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
 * <p>
 * Restrictions:
 * <p>
 * The authors are allowed to change the license at their desire. This license is void for members of PixelModders and
 * to unaussprechlich, except for clause 3. The licensor cannot revoke these freedoms in most cases, as long as you follow
 * the following license terms and the license terms given by the listed above Creative Commons License, however in extreme
 * cases the authors reserve the right to revoke all rights for usage of the codebase.
 * <p>
 * 1. PixelModders, Eladkay & unaussprechlich are the authors of this licensed material. GitHub contributors are NOT
 * considered authors, neither are members of the HudHelper program. GitHub contributers still hold the rights for their
 * code, but only when it is used separately from HudPixel and any license header must indicate that.
 * 2. You shall not claim ownership over this project and repost it in any case, without written permission from at least
 * two of the authors.
 * 3. You shall not make money with the provided material. This project is 100% non commercial and will always stay that
 * way. This clause is the only one remaining, should the rest of the license be revoked. The only exception to this
 * clause is completely cosmetic features. Only the authors may sell cosmetic features for the mod.
 * 4. Every single contibutor owns copyright over his contributed code when separated from HudPixel. When it's part of
 * HudPixel, it is only governed by this license, and any copyright header must indicate that. After the contributed
 * code is merged to the release branch you cannot revoke the given freedoms by this license.
 * 5. If your own project contains a part of the licensed material you have to give the authors full access to all project
 * related files.
 * 6. You shall not act against the will of the authors regarding anything related to the mod or its codebase. The authors
 * reserve the right to take down any infringing project.
 **********************************************************************************************************************/
@SideOnly(Side.CLIENT)
class LoadPlayerHead(private val username: String, private val callback: ILoadPlayerHeadCallback) : IEventHandler {

    private var image: BufferedImage? = null
    private var resourceLocation: ResourceLocation? = null
    private var imageLoaded = false
    private var imageSetup = false

    init {
        registerIEvent(this)
        loadSkinFromURL()
    }

    private fun setupImage() {
        imageSetup = true
        if (image == null) {
            callback.onLoadPlayerHeadResponse(null)
            unregisterIEvent(this)
            return
        }
        image = image!!.getSubimage(8, 8, 8, 8)
        val texture = DynamicTexture(image!!)
        resourceLocation = getMinecraft().textureManager.getDynamicTextureLocation(username, texture)
        logInfo("[LoadPlayer]: Loaded skin for $username @ http://skins.minecraft.net/MinecraftSkins/$username.png")
        callback.onLoadPlayerHeadResponse(this.resourceLocation)
        unregisterIEvent(this)
    }

    /**
     * helper function to load the minecraft skin at "http://skins.minecraft.net/MinecraftSkins/.png"
     * uses a callback class so the mainthread isn't stopped while loading the image
     * had to move to waiting code into a external thread ... so the mainthread is mot stopped
     * while waiting
     */
    private fun loadSkinFromURL() {

        object : Thread() {
            override fun run() {

                val service: ExecutorService
                val task: Future<BufferedImage>

                service = newSingleThreadExecutor()
                task = service.submit(callURL())
                val failed: Boolean
                try {
                    image = task.get()
                    logInfo("[LoadPlayer]: Skin loaded for " + username)
                } catch (ex: InterruptedException) {
                    logWarn("[LoadPlayer]:Something went wrong while loading the skin for" + username)
                    ex.printStackTrace()
                } catch (ex: ExecutionException) {
                    logWarn("[LoadPlayer]:Something went wrong while loading the skin for" + username)
                    ex.printStackTrace()
                    try {
                        image = read(URL("http://skins.minecraft.net/MinecraftSkins/$username.png"))
                        imageLoaded = true
                    } catch (e: MalformedURLException) {
                        failed = true
                        logWarn("[LoadPlayer]: Couldn't load skin for $username @ http://skins.minecraft.net/MinecraftSkins/$username.png")
                    } catch (e: IOException) {
                        failed = true
                        logWarn("[LoadPlayer]: Couldn't read skin for $username @ http://skins.minecraft.net/MinecraftSkins/$username.png")
                    }

                }

                imageLoaded = true

                service.shutdownNow()
            }
        }.start()
    }

    override fun onClientTick() {
        if (imageLoaded && !imageSetup) setupImage()
    }

    override fun onChatReceived(e: ClientChatReceivedEvent) {

    }

    override fun onRender() {

    }

    override fun handleMouseInput(i: Int, mX: Int, mY: Int) {

    }

    override fun onMouseClick(mX: Int, mY: Int) {

    }

    /**
     * Helper class to get the image via url request and filereader
     */
    internal inner class callURL : Callable<BufferedImage> {

        @Throws(Exception::class)
        override fun call(): BufferedImage {
            return read(URL("http://skins.minecraft.net/MinecraftSkins/$username.png"))
        }
    }


}
