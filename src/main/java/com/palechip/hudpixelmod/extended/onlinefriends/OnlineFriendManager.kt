package com.palechip.hudpixelmod.extended.onlinefriends

/* *********************************************************************************************************************
  HudPixelReloaded - License
  <p>
  The repository contains parts of Minecraft Forge and its dependencies. These parts have their licenses
  under forge-docs/. These parts can be downloaded at files.minecraftforge.net.This project contains a
  unofficial copy of pictures from the official Hypixel website. All copyright is held by the creator!
  Parts of the code are based upon the Hypixel Public API. These parts are all in src/main/java/net/hypixel/api and
  subdirectories and have a special copyright header. Unfortunately they are missing a license but they are obviously
  intended for usage in this kind of application. By default, all rights are reserved.
  The original version of the HudPixel Mod is made by palechip and published under the MIT license.
  The majority of code left from palechip's creations is the component implementation.The ported version to
  Minecraft 1.8.9 and up HudPixel Reloaded is made by PixelModders/Eladkay and also published under the MIT license
  (to be changed to the new license as detailed below in the next minor update).
  <p>
  For the rest of the code and for the build the following license applies:
  <p>
  # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
  #  HudPixel by PixelModders, Eladkay & unaussprechlich is licensed under a Creative Commons         #
  #  Attribution-NonCommercial-ShareAlike 4.0 International License with the following restrictions.  #
  #  Based on a work at HudPixelExtended & HudPixel.                                                  #
  # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # # #
  <p>
  Restrictions:
  <p>
  The authors are allowed to change the license at their desire. This license is void for members of PixelModders and
  to unaussprechlich, except for clause 3. The licensor cannot revoke these freedoms in most cases, as long as you follow
  the following license terms and the license terms given by the listed above Creative Commons License, however in extreme
  cases the authors reserve the right to revoke all rights for usage of the codebase.
  <p>
  1. PixelModders, Eladkay & unaussprechlich are the authors of this licensed material. GitHub contributors are NOT
  considered authors, neither are members of the HudHelper program. GitHub contributers still hold the rights for their
  code, but only when it is used separately from HudPixel and any license header must indicate that.
  2. You shall not claim ownership over this project and repost it in any case, without written permission from at least
  two of the authors.
  3. You shall not make money with the provided material. This project is 100% non commercial and will always stay that
  way. This clause is the only one remaining, should the rest of the license be revoked. The only exception to this
  clause is completely cosmetic features. Only the authors may sell cosmetic features for the mod.
  4. Every single contibutor owns copyright over his contributed code when separated from HudPixel. When it's part of
  HudPixel, it is only governed by this license, and any copyright header must indicate that. After the contributed
  code is merged to the release branch you cannot revoke the given freedoms by this license.
  5. If your own project contains a part of the licensed material you have to give the authors full access to all project
  related files.
  6. You shall not act against the will of the authors regarding anything related to the mod or its codebase. The authors
  reserve the right to take down any infringing project.
 */

import com.mojang.realmsclient.gui.ChatFormatting
import com.palechip.hudpixelmod.config.CCategory
import com.palechip.hudpixelmod.config.ConfigPropertyBoolean
import com.palechip.hudpixelmod.config.ConfigPropertyInt
import com.palechip.hudpixelmod.extended.util.LoggerHelper
import com.palechip.hudpixelmod.extended.util.gui.FancyListManager
import com.palechip.hudpixelmod.extended.util.gui.FancyListObject
import com.palechip.hudpixelmod.util.plus
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiChat
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*
import java.util.function.Consumer

@ConfigPropertyInt(category = CCategory.FRIENDS_DISPLAY, id = "xOffsetFriendsDisplay", comment = "X offset for friends display", def = 2)
var xOffsetFriendsDisplay: Int = 2
@ConfigPropertyInt(category = CCategory.FRIENDS_DISPLAY, id = "yOffsetFriendsDisplay", comment = "Y offset for friends display", def = 2)
var yOffsetFriendsDisplay: Int = 2
@ConfigPropertyInt(category = CCategory.FRIENDS_DISPLAY, id = "friendsShownAtOnce", comment = "Friends shown at once", def = 10)
var friendsShownAtOnce = 10
@ConfigPropertyBoolean(category = CCategory.FRIENDS_DISPLAY, id = "shownFriendsDisplayRight", comment = "Show friends display on the right", def = false)
var shownFriendsDisplayRight: Boolean = false
@ConfigPropertyBoolean(category = CCategory.FRIENDS_DISPLAY, id = "hideOfflineFriends", comment = "Hide offline friends?", def = true)
var hideOfflineFriends = true
@ConfigPropertyBoolean(category = CCategory.FRIENDS_DISPLAY, id = "isOnlineFriendsDisplay", comment = "Enable or disable the BoosterDisplay", def = true)
var enabled = false

@SideOnly(Side.CLIENT)
object OnlineFriendManager : FancyListManager(5, xOffsetFriendsDisplay.toFloat(), yOffsetFriendsDisplay.toFloat(), shownFriendsDisplayRight), IUpdater {

    private val JOINED_MESSAGE = " joined."
    private val LEFT_MESSAGE = " left."
    private val UPDATE_COOLDOWN_RENDERING = 10 * 1000 // = 10sec
    private val UPDATE_COOLDOWN_ONLINE = 2 * 60 * 1000 // = 2min


    private var lastUpdateRendering: Long = 0
    private var lastUpdateOnline: Long = 0
    private val localStorageFCO = ArrayList<FancyListObject>()

    init {
        this.isButtons = true
        OnlineFriendsLoader()
        this.renderRightSide = shownFriendsDisplayRight
        this.shownObjects = friendsShownAtOnce
    }

    override val configxStart: Int
        get() = xOffsetFriendsDisplay

    override val configRenderRight: Boolean
        get() = shownFriendsDisplayRight

    override val configyStart: Int
        get() = yOffsetFriendsDisplay

    internal fun addFriend(fco: FancyListObject) {
        localStorageFCO.add(fco)
    }

    private fun updateRendering() {
        if (System.currentTimeMillis() > lastUpdateRendering + UPDATE_COOLDOWN_RENDERING) {
            lastUpdateRendering = System.currentTimeMillis()

            if (!localStorageFCO.isEmpty())

                localStorageFCO.forEach(Consumer<FancyListObject> { it.onClientTick() })

            //sort the list to display only friends first
            Collections.sort(localStorageFCO) { f1, f2 ->
                val o1 = f1 as OnlineFriend
                val o2 = f2 as OnlineFriend
                java.lang.Boolean.valueOf(o2.isOnline)!!.compareTo(o1.isOnline)
            }

            if (hideOfflineFriends) {
                val buff = ArrayList<FancyListObject>()
                for (fco in localStorageFCO) {
                    val of = fco as OnlineFriend
                    if (of.isOnline) buff.add(fco)
                }
                fancyListObjects = buff
            } else {
                fancyListObjects = localStorageFCO
            }
        }
    }

    override fun onClientTick() {
        if (System.currentTimeMillis() > lastUpdateOnline + UPDATE_COOLDOWN_ONLINE && !localStorageFCO.isEmpty()) {
            lastUpdateOnline = System.currentTimeMillis()
            OnlineFriendsUpdater(this)
        }
        updateRendering()
    }

    @Throws(Throwable::class)
    override fun onChatReceived(e: ClientChatReceivedEvent) {
        for (s in OnlineFriendsLoader.allreadyStored) {
            if (e.message.unformattedText.equals(s + JOINED_MESSAGE, ignoreCase = true))
                for (fco in localStorageFCO) {
                    val of = fco as OnlineFriend
                    if (of.username.equals(s)) {
                        of.isOnline = true
                        of.gamemode = ChatFormatting.WHITE + "not loaded yet!"
                    }
                }
            else if (e.message.unformattedText.equals(s + LEFT_MESSAGE, ignoreCase = true))
                for (fco in localStorageFCO) {
                    val of = fco as OnlineFriend
                    if (of.username.equals(s)) {
                        of.isOnline = false
                        of.gamemode = (ChatFormatting.DARK_GRAY + "currently offline")
                    }
                }
        }
    }

    override fun onRender() {
        if (!enabled) return
        if (Minecraft.getMinecraft().currentScreen is GuiChat && lastUpdateRendering != 0L && OnlineFriendsLoader.isApiLoaded && Minecraft.getMinecraft().displayHeight > 600) {
            this.renderDisplay()
            this.isMouseHander = true
        } else {
            this.isMouseHander = false
        }
    }

    override fun onUpdaterResponse(onlineFriends: HashMap<String, String>?) {
        if (onlineFriends == null) {
            LoggerHelper.logWarn("[OnlineFriends][Updater]: Something went wrong while calling a update!")
        } else if (!localStorageFCO.isEmpty()) {
            for (fco in localStorageFCO) {
                val of = fco as OnlineFriend
                if (onlineFriends.containsKey(of.username)) {
                    of.gamemode = (onlineFriends[of.username])
                    of.isOnline = true
                } else {
                    of.gamemode = (ChatFormatting.DARK_GRAY + "currently offline")
                    of.isOnline = false
                }
            }
        }
    }


}
