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
package com.palechip.hudpixelmod

import com.palechip.hudpixelmod.config.CCategory
import com.palechip.hudpixelmod.config.ConfigPropertyBoolean
import com.palechip.hudpixelmod.extended.HudPixelExtendedEventHandler
import com.palechip.hudpixelmod.modulargui.ModularGuiHelper
import com.palechip.hudpixelmod.modulargui.components.TimerModularGuiProvider
import com.palechip.hudpixelmod.util.GameType
import com.palechip.hudpixelmod.util.ScoreboardReader
import net.minecraft.client.Minecraft
import net.minecraft.client.entity.EntityPlayerSP
import net.minecraft.util.text.TextComponentString
import net.minecraftforge.client.event.ClientChatReceivedEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.entity.EntityJoinWorldEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import net.minecraftforge.fml.common.network.FMLNetworkEvent
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly
import java.util.regex.Pattern

@SideOnly(Side.CLIENT)
class GameDetector {

    private var cooldown = 0
    private var schedule = false
    private var scheduleWhereami = -1

    @SubscribeEvent
    fun onServerChange(event: EntityJoinWorldEvent) {
        if (event.entity !is EntityPlayerSP || !enabled) return
        val player = event.entity as EntityPlayerSP
        player.sendChatMessage("/whereami")
        cooldown = 5
    }


    @SubscribeEvent
    fun onLogin(event: FMLNetworkEvent.ClientConnectedToServerEvent) {
        scheduleWhereami = 10
    }

    fun update(s: String) {
        var s = s.toLowerCase()
        when (s) {
            "hypixel" -> currentGameType = GameType.UNKNOWN //main lobby
            "hypixel.net" -> {
                ScoreboardReader.resetCache()
                schedule = true
            }
            "", " " -> schedule = true
            " smash heroes", "smash heroes" -> {
                currentGameType = GameType.SMASH_HEROES
                schedule = false

                val game = currentGameType
                for (type in GameType.values())
                    if (s.toLowerCase().replace(" ", "").contains(type.scoreboardName.toLowerCase().replace(" ", ""))) {
                        currentGameType = type
                        isLobby0 = false
                        ModularGuiHelper.providers.forEach({ it.setupNewGame() })
                        ModularGuiHelper.providers.forEach({ it.onGameStart() })
                    }
                if (game != currentGameType && Minecraft.getMinecraft().thePlayer != null) {
                    //success!
                    if (HudPixelMod.IS_DEBUGGING)
                        Minecraft.getMinecraft().thePlayer.addChatMessage(TextComponentString("Changed server! Game is now " + currentGameType))
                } else {
                    currentGameType = GameType.UNKNOWN
                    schedule = true
                }

                cooldown = -1
            }
            else -> {
                schedule = false
                val game = currentGameType
                for (type in GameType.values())
                    if (s.toLowerCase().replace(" ", "").contains(type.scoreboardName.toLowerCase().replace(" ", ""))) {
                        currentGameType = type
                        isLobby0 = false
                        ModularGuiHelper.providers.forEach({ it.setupNewGame() })
                        ModularGuiHelper.providers.forEach({ it.onGameStart() })
                    }
                if (game != currentGameType && Minecraft.getMinecraft().thePlayer != null) {
                    if (HudPixelMod.IS_DEBUGGING)
                        Minecraft.getMinecraft().thePlayer.addChatMessage(TextComponentString("Changed server! Game is now " + currentGameType))
                } else {
                    currentGameType = GameType.UNKNOWN
                    schedule = true
                }
                cooldown = -1
            }
        }
    }

    @SubscribeEvent
    fun tickly(event: TickEvent.ClientTickEvent) {
        if (!enabled) return
        var title = ScoreboardReader.getScoreboardTitle()
        title = stripColor(title)!!.toLowerCase()
        cooldown--
        scheduleWhereami--
        if (schedule || cooldown == 0) update(title)
        if (scheduleWhereami == 0 && Minecraft.getMinecraft().thePlayer != null) {
            scheduleWhereami = -1
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/whereami")
        }
    }

    @SubscribeEvent
    fun onChatMessage(event: ClientChatReceivedEvent) {
        if (!enabled) return
        val message = event.message.unformattedText
        if (message.equals("The game starts in 1 second!", ignoreCase = true)) {
            HudPixelExtendedEventHandler.onGameStart()
            gameHasntBegan = false
            TimerModularGuiProvider.onGameStart()
        }
        if (message.equals("                            Reward Summary", ignoreCase = true)) {
            HudPixelExtendedEventHandler.onGameEnd()
            gameHasntBegan = true
        }
        if (message.toLowerCase().contains("currently on server".toLowerCase())) {
            if (LOBBY_MATCHER.asPredicate().test(message)) { //lobby
                isLobby0 = true
                gameHasntBegan = true
                ModularGuiHelper.providers.forEach { it.onGameEnd() }
            }
            event.isCanceled = true
        }
    }

    companion object {
        @JvmField
        val LOBBY_MATCHER = Pattern.compile("\\w*lobby\\d+")
        @JvmField
        val COLOR_CHAR = '\u00A7'
        private val STRIP_COLOR_PATTERN = Pattern.compile("(?i)" + COLOR_CHAR.toString() + "[0-9A-FK-OR]")
        @ConfigPropertyBoolean(category = CCategory.HUDPIXEL, id = "gameDetector", def = true, comment = "Disable game detector (Risky!)")
        @JvmStatic
        var enabled = true
        @JvmStatic
        var currentGameType = GameType.UNKNOWN
        private var isLobby0 = false
        private var gameHasntBegan = true

        init {
            MinecraftForge.EVENT_BUS.register(GameDetector())
        }

        @JvmStatic
        fun doesGameTypeMatchWithCurrent(type: GameType): Boolean {
            when (type) {
                GameType.UNKNOWN -> return currentGameType == GameType.UNKNOWN
                GameType.ALL_GAMES -> return true

                GameType.QUAKECRAFT -> return currentGameType == GameType.QUAKECRAFT

                GameType.THE_WALLS -> return currentGameType == GameType.THE_WALLS

                GameType.PAINTBALL -> return currentGameType == GameType.PAINTBALL

                GameType.BLITZ -> return currentGameType == GameType.BLITZ

                GameType.TNT_GAMES, GameType.BOW_SPLEEF, GameType.TNT_RUN, GameType.TNT_WIZARDS, GameType.TNT_TAG, GameType.ANY_TNT -> return currentGameType == GameType.ANY_TNT

                GameType.VAMPIREZ -> return currentGameType == GameType.VAMPIREZ

                GameType.MEGA_WALLS -> return currentGameType == GameType.MEGA_WALLS

                GameType.ARENA -> return currentGameType == GameType.ARENA

                GameType.UHC -> return currentGameType == GameType.UHC

                GameType.COPS_AND_CRIMS -> return currentGameType == GameType.COPS_AND_CRIMS

                GameType.WARLORDS -> return currentGameType == GameType.WARLORDS

                GameType.ARCADE_GAMES, GameType.BLOCKING_DEAD, GameType.BOUNTY_HUNTERS, GameType.BUILD_BATTLE, GameType.CREEPER_ATTACK, GameType.DRAGON_WARS, GameType.ENDER_SPLEEF, GameType.FARM_HUNT, GameType.GALAXY_WARS, GameType.PARTY_GAMES_1, GameType.PARTY_GAMES_2, GameType.TRHOW_OUT, GameType.TURBO_KART_RACERS, GameType.ANY_ARCADE, GameType.FOOTBALL -> return currentGameType == GameType.ANY_ARCADE

                GameType.SPEED_UHC -> return currentGameType == GameType.SPEED_UHC

                GameType.CRAZY_WALLS -> return currentGameType == GameType.CRAZY_WALLS

                GameType.SMASH_HEROES, GameType.SMASH_HEROES_WOSPACE -> return currentGameType == GameType.SMASH_HEROES || currentGameType == GameType.SMASH_HEROES_WOSPACE

                GameType.SKYWARS -> return currentGameType == GameType.SKYWARS

                else -> return false
            }
        }

        @JvmStatic
        fun isLobby(): Boolean {
            return isLobby0 || gameHasntBegan
        }

        @JvmStatic
        fun shouldProcessAfterstats(): Boolean {
            return isLobby0
        }

        @JvmStatic
        fun stripColor(input: String?): String? {
            if (input == null)
                return null
            return STRIP_COLOR_PATTERN.matcher(input).replaceAll("")
        }
    }
}
