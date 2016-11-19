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
package com.palechip.hudpixelmod.util

import com.palechip.hudpixelmod.HudPixelMod
import net.minecraft.util.text.ITextComponent
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting
import net.minecraft.util.text.event.ClickEvent
import net.minecraft.util.text.event.HoverEvent
import net.minecraftforge.fml.client.FMLClientHandler
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.*

/**
 * A mighty chat message manager. His nickname is Skype. ;)

 * @author palechip
 */
@SideOnly(Side.CLIENT)
class ChatMessageComposer
/**
 * Creates a new ChatMessageComposer.

 * @param text Text of the chat message.
 */
(text: String) {

    private val chatComponent: ITextComponent
    private var appendedMessages: ArrayList<ChatMessageComposer>? = null

    init {
        this.chatComponent = TextComponentString(text)
    }

    /**
     * Creates a new ChatMessageComposer.

     * @param text  Text of the chat message.
     * *
     * @param color Color of the chat message.
     */
    constructor(text: String, color: TextFormatting) : this(text) {
        this.addFormatting(color)
    }

    /**
     * Adds a formatting to the text message. The ChatMessageComposer used is modified.

     * @return The ChatMessageComposer instance in order to make code more compact.
     */
    fun addFormatting(formatting: TextFormatting): ChatMessageComposer {
        val style = this.chatComponent.style
        when (formatting) {
            TextFormatting.ITALIC -> style.setItalic(true)
            TextFormatting.BOLD -> style.setBold(true)
            TextFormatting.UNDERLINE -> style.setUnderlined(true)
            TextFormatting.OBFUSCATED -> style.setObfuscated(true)
            TextFormatting.STRIKETHROUGH -> style.setStrikethrough(true)
            else -> style.setColor(formatting)
        }
        this.chatComponent.style = style

        return this
    }

    /**
     * Append a message to the an existing message. This is used to achieve multiple colors in one line.

     * @param message message to append
     * *
     * @return The ChatMessageComposer instance in order to make code more compact.
     */
    fun appendMessage(message: ChatMessageComposer): ChatMessageComposer {
        // Make sure appendedMessages gets created
        if (this.appendedMessages == null) {
            this.appendedMessages = ArrayList<ChatMessageComposer>()
        }
        // Add the message
        this.appendedMessages!!.add(message)
        // And add messages which were added to the message
        if (message.appendedMessages != null) {
            this.appendedMessages!!.addAll(message.appendedMessages!!)
        }

        return this
    }

    /**
     * Makes the chat message clickable.

     * @param action      Action performed by clicking
     * *
     * @param execute     URL or command to execute
     * *
     * @param description Shown message when hovering over the clickable chat.
     * *
     * @return The ChatMessageComposer instance in order to make code more compact.
     */
    fun makeClickable(action: ClickEvent.Action, execute: String, description: ChatMessageComposer): ChatMessageComposer {
        val style = this.chatComponent.style

        style.clickEvent = ClickEvent(action, execute)
        style.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, description.assembleMessage(false))

        this.chatComponent.style = style

        return this
    }

    /**
     * Makes the chat message link to a given url.

     * @param url The linked URL. MAKE SURE IT STARTS WITH HTTP:// or HTTPS://!
     * *
     * @return The ChatMessageComposer instance in order to make code more compact.
     */
    fun makeLink(url: String): ChatMessageComposer {
        // Compose a generic description
        val description = ChatMessageComposer("Click to visit ", TextFormatting.GRAY)
        description.appendMessage(ChatMessageComposer(url, TextFormatting.AQUA).addFormatting(TextFormatting.UNDERLINE))
        // and make it clickable
        this.makeClickable(ClickEvent.Action.OPEN_URL, url, description)

        return this
    }

    /**
     * Creates a tooltip like hover text.

     * @param text the message shown then hovering
     * *
     * @return The ChatMessageComposer instance in order to make code more compact.
     */
    fun makeHover(text: ChatMessageComposer): ChatMessageComposer {
        val style = this.chatComponent.style

        style.hoverEvent = HoverEvent(HoverEvent.Action.SHOW_TEXT, text.assembleMessage(false))

        this.chatComponent.style = style

        return this
    }

    /**
     * Send this message to the player. (with [HudPixel] prefix)
     */
    fun send() {
        this.send(true)
    }

    /**
     * Send this message to the player.

     * @param prefix Send the [HudPixel] prefix?
     */
    fun send(prefix: Boolean) {
        try {
            // send the assebled message to the player
            FMLClientHandler.instance().clientPlayerEntity.addChatMessage(this.assembleMessage(prefix))
        } catch (e: Exception) {
            HudPixelMod.logError("Failed to send chat message")
            e.printStackTrace()
        }

    }

    /**
     * Builds an ITextComponent including all appended messages.

     * @param prefix should [HudPixel] be added as chat prefix?
     * *
     * @return the ITextComponent containing all appended messages
     */
    protected fun assembleMessage(prefix: Boolean): ITextComponent {
        val result: ITextComponent
        if (prefix) {
            // Copy the prefix
            result = CHAT_PREFIX!!.createCopy()
        } else if (this.appendedMessages == null) {
            // Nothing to append
            return this.chatComponent
        } else {
            // this step is important so that the appended messages don't inherit the style
            result = TextComponentString("")
        }

        // add the main message
        result.appendSibling(this.chatComponent)
        // and add all appended messages
        if (this.appendedMessages != null) {
            for (m in this.appendedMessages!!) {
                result.appendSibling(m.chatComponent)
            }
        }

        return result
    }

    companion object {
        var SEPARATION_MESSAGE = "\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC"
        private var CHAT_PREFIX: ITextComponent? = null

        // Builds the chat prefix
        init {
            val name1 = TextComponentString("Hud")
            val name2 = TextComponentString("Pixel")
            name1.style.color = TextFormatting.GOLD
            name2.style.color = TextFormatting.YELLOW

            CHAT_PREFIX = TextComponentString("[").appendSibling(name1).appendSibling(name2).appendSibling(TextComponentString("] "))
        }

        /**
         * Prints a Hypixel style separaton message using the provided color.
         */
        fun printSeparationMessage(color: TextFormatting) {
            ChatMessageComposer(SEPARATION_MESSAGE, color).addFormatting(TextFormatting.BOLD).send(false)
        }
    }
}
