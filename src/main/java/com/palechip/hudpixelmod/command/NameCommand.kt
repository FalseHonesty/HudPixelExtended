package com.palechip.hudpixelmod.command

import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.server.MinecraftServer

object NameCommand : CommandBase() {
    override fun getCommandName(): String {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getCommandUsage(sender: ICommandSender?): String {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun execute(server: MinecraftServer?, sender: ICommandSender?, args: Array<out String>?) {
        throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
    /*
    private fun isOp(sender: ICommandSender): Boolean {
        return MinecraftServer.getServer().isSinglePlayer
                || sender !is EntityPlayerMP
                || MinecraftServer.getServer().configurationManager.canSendCommands(sender.gameProfile)
    }

    val name: String
        get() = "names"

    override fun getRequiredPermissionLevel(): Int {
        return 0
    }

    fun canSenderUseCommand(sender: ICommandSender): Boolean {
        return true
    }

    /**
     * Gets the name of the command
     */
    override fun getCommandName(): String {
        return "names"
    }

    override fun getCommandUsage(sender: ICommandSender): String {
        return "/names <player>"
    }


    val aliases: List<Any>
        get() {
            val aliases = ArrayList<String>()
            aliases.add("names")
            aliases.add("name")
            aliases.add("grab")
            return aliases
        }

    override fun processCommand(sender: ICommandSender, args: Array<String>) {
        if (HudPixelMod.isHypixelNetwork() || Minecraft.getMinecraft().currentServerData == null) {
            try {
                val cSender = sender
                if (args.size == 0) {
                    cSender.addChatMessage(ChatComponentText("${ChatFormatting.GOLD}Usage: /names <playername>"))
                    cSender.addChatMessage(ChatComponentText("${ChatFormatting.GOLD}Example: /names Eladkay"))
                } else if (args.size == 1) {
                    val playername = args[0].toString()
                    if (playername.length < 3) {
                        cSender.addChatMessage(ChatComponentText("${ChatFormatting.GOLD}Usage: /names <playername>"))
                        cSender.addChatMessage(ChatComponentText("${ChatFormatting.GOLD}Example: /names Eladkay"))
                    } else {
                        object : Thread() {
                            override fun run() {
                                try {
                                    val uuidgrabber = URL("https://api.mojang.com/users/profiles/minecraft/" + playername)
                                    val br1 = BufferedReader(InputStreamReader(uuidgrabber.openStream()))
                                    val uuidfromweb: String
                                    uuidfromweb = br1.readLine()
                                    if (uuidfromweb != null) {
                                        val uuid = uuidfromweb.substring(7, 39)
                                        val namegrabber = URL("https://api.mojang.com/user/profiles/$uuid/names")
                                        val br2 = BufferedReader(InputStreamReader(namegrabber.openStream()))
                                        var webnames: String
                                        webnames = br2.readLine()
                                        if (webnames != null) {
                                            if (webnames.substring(10, webnames.length - 3 - playername.length).length <= 0) {
                                                cSender.addChatMessage(ChatComponentText(""))
                                                cSender.addChatMessage(ChatComponentText(StringUtils.center("" + ChatFormatting.LIGHT_PURPLE + ChatFormatting.BOLD + playername, 65)))
                                                cSender.addChatMessage(ChatComponentText(""))
                                                cSender.addChatMessage(ChatComponentText(StringUtils.center(ChatFormatting.YELLOW + "Player has never changed their name.", 65)))
                                                cSender.addChatMessage(ChatComponentText(""))
                                            } else {
                                                webnames = webnames.replace("{", "").replace("}", "").replace(",".toRegex(), ".").replace('"', ' ').replace(" ", "").replace("[", "").replace("]", "").replace(".c", "-c").replace(".", ",")
                                                val split = webnames.split(Pattern.quote(",").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

                                                cSender.addChatMessage(ChatComponentText(""))
                                                for (s in split) {
                                                    if (s.startsWith("name") && s.contains("changed")) {
                                                        val names = s.split(Pattern.quote("-").toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                                        var p1 = ""
                                                        var p2 = ""
                                                        for (d in names) {
                                                            if (d.startsWith("name")) {
                                                                p1 = "    " + ChatFormatting.GOLD + "- " + ChatFormatting.GREEN + d.replace("name:", "") + " "
                                                            }
                                                            if (d.startsWith("changedToAt")) {
                                                                val unixSeconds = java.lang.Long.parseLong(d.replace("changedToAt:", ""))
                                                                val date = Date(unixSeconds)
                                                                val sdf = SimpleDateFormat("dd-MM-yyyy")
                                                                sdf.timeZone = TimeZone.getTimeZone("GMT+1")
                                                                val formattedDate = sdf.format(date)
                                                                p2 = ChatFormatting.GRAY + "(Changed on " + formattedDate + ")"
                                                            }
                                                        }
                                                        cSender.addChatMessage(ChatComponentText("" + p1 + p2))
                                                    } else if (s.startsWith("name") && !s.contains("changed")) {
                                                        cSender.addChatMessage(ChatComponentText(StringUtils.center("" + ChatFormatting.LIGHT_PURPLE + ChatFormatting.BOLD + s.replace("name:", ""), 65)))
                                                        cSender.addChatMessage(ChatComponentText(""))
                                                    }
                                                }
                                                cSender.addChatMessage(ChatComponentText(""))
                                            }
                                        } else {
                                            cSender.addChatMessage(ChatComponentText("${ChatFormatting.DARK_RED}ERROR: Could not find player '" + playername + "'."))
                                            cSender.addChatMessage(ChatComponentText("${ChatFormatting.DARK_RED}This person changed their name or never existed."))
                                        }
                                        br2.close()
                                    } else {
                                        cSender.addChatMessage(ChatComponentText("${ChatFormatting.DARK_RED}ERROR: Could not find player '" + playername + "'."))
                                        cSender.addChatMessage(ChatComponentText("${ChatFormatting.DARK_RED}This person changed their name or never existed."))
                                    }
                                    br1.close()
                                } catch (e: Throwable) {
                                    e.printStackTrace()
                                }

                            }
                        }.start()
                    }
                } else if (args.size > 1) {
                    cSender.addChatMessage(ChatComponentText(ChatFormatting.GOLD + "Usage: /names <playername>"))
                    cSender.addChatMessage(ChatComponentText(ChatFormatting.GOLD + "Example: /names Kevy"))
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }

        }
    }*/
}
