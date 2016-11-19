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
package com.palechip.hudpixelmod.extended.staff

import com.palechip.hudpixelmod.HudPixelMod
import com.palechip.hudpixelmod.extended.HudPixelExtendedEventHandler
import com.palechip.hudpixelmod.extended.fancychat.FancyChat
import com.palechip.hudpixelmod.extended.util.IEventHandler
import com.palechip.hudpixelmod.util.McColorHelperJava
import net.minecraft.client.gui.GuiScreen
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.io.IOException
import java.io.InputStreamReader
import java.net.URL
import java.util.*

/**
 * A small "ego"-class to display the the HudPixel staff with a nice color and tag #abgehoben
 */
@SideOnly(Side.CLIENT)
class StaffManager : IEventHandler, McColorHelperJava {


    init {
        HudPixelExtendedEventHandler.registerIEvent(this)
        MinecraftForge.EVENT_BUS.register(this)
        //loades the staff
        AdminHandler()
    }

    /**
     * changes the tag above the player, when they join and are part of the hudpixel team

     * @param e
     */
    @SubscribeEvent
    fun onPlayerName(e: PlayerEvent.NameFormat) {
        if (tags!!.keys.contains(e.username) || e.username.contains("PixelPlus")) {
            e.displayname = tags!![e.username] + e.displayname
        }
    }

    override fun onClientTick() {

    }

    override fun everyTenTICKS() {

    }

    override fun everySEC() {

    }

    override fun everyFiveSEC() {

    }

    override fun everyMIN() {

    }

    override fun onRender() {

    }

    override fun handleMouseInput(i: Int, mX: Int, mY: Int) {

    }

    override fun onMouseClick(mX: Int, mY: Int) {

    }

    override fun openGUI(guiScreen: GuiScreen?) {

    }

    override fun onConfigChanged() {

    }

    private class AdminHandler : Thread() {
        init {
            name = "Admin Fanciness Thread"
            isDaemon = true
            start()
        }

        override fun run() {
            try {
                val url = URL("https://raw.githubusercontent.com/Eladkay/static/master/HudPixelAdmins")
                val props = Properties()
                InputStreamReader(url.openStream()).use { reader ->
                    props.load(reader)
                    load(props)
                }
            } catch (e: IOException) {
                HudPixelMod.logger!!.info("Could not load contributors list. Either you're offline or github is down. Nothing to worry about, carry on~")
            }

        }
    }


    /**
     * buts the admin/helper tag infront of a message a admin/helper has written

     * @param e chat event
     * *
     * @throws Throwable
     */
    @Throws(Throwable::class)
    override fun onChatReceived(e: ClientChatReceivedEvent) {
        if (e.type.toInt() != 0) return  //return if it isn't a normal chat message
        if (e.message.unformattedText.contains("http"))
            return  //return if the message contains a link .... so you can still click it :)


        for (s in tags!!.keys) { //for admins
            if (e.message.unformattedText.contains(s + ":") || e.message.unformattedText.startsWith(s + ":")) {
                e.message = TextComponentString(e.message.formattedText.replaceFirst(s.toRegex(), tags!![s] + s))
                FancyChat.addMessage(e.message.formattedText)
                return
            }
        }
    }

    companion object {

        operator fun TextFormatting.plus(string: String) = "$this$string"
        operator fun String.plus(string: TextFormatting) = "$this$string"
        private fun hudHelperTag(): String {
            return McColorHelperJava.GOLD + "[Hud" + McColorHelperJava.YELLOW + "Helper" + McColorHelperJava.GOLD + "]" + McColorHelperJava.YELLOW + " "
        }

        private fun hudAdminTag(): String {
            return McColorHelperJava.GOLD + "[Hud" + McColorHelperJava.RED + "Admin" + McColorHelperJava.GOLD + "]" + McColorHelperJava.RED + " "
        }

        @Volatile private var tags: MutableMap<String, String>? = null
        private fun load(props: Properties) {
            tags = HashMap<String, String>()
            println("Fanciness time!")
            for (key in props.stringPropertyNames()) {
                val value = props.getProperty(key)
                println("Loading fanciness for $key: $value")
                tags!!.put(key, if (value.contains("admin")) hudAdminTag() else if (value.contains("helper")) hudHelperTag() else value)
            }
        }
    }
}
/**
 * constructor -> requests the file
 */
