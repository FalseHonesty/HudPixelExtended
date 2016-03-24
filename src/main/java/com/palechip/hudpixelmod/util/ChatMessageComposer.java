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
package com.palechip.hudpixelmod.util;

import com.palechip.hudpixelmod.HudPixelMod;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraft.util.text.event.HoverEvent;

import java.util.ArrayList;

/**
 * A mighty chat message manager. His nickname is Skype. ;)
 * @author palechip
 *
 */
public class ChatMessageComposer {
    public static String SEPARATION_MESSAGE = "\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC\u25AC";
    private static TextComponentString CHAT_PREFIX;

    // Builds the chat prefix
    static {
        TextComponentString name1 = new TextComponentString("Hud");
        TextComponentString name2 = new TextComponentString("Pixel");
        name1.getChatStyle().setColor(TextFormatting.GOLD);
        name2.getChatStyle().setColor(TextFormatting.YELLOW);
        CHAT_PREFIX = ((TextComponentString) new TextComponentString("[").appendText(name1.getFormattedText()).appendText(name2.getFormattedText()).appendText(new TextComponentString("] ").getFormattedText()));
    }

    private TextComponentString chatComponent;
    private ArrayList<ChatMessageComposer> appendedMessages;
    
    /**
     * Creates a new ChatMessageComposer.
     * @param text Text of the chat message.
     */
    public ChatMessageComposer(String text) {
        this.chatComponent = new TextComponentString(text);
    }
    
    /**
     * Creates a new ChatMessageComposer.
     * @param text Text of the chat message.
     * @param color Color of the chat message.
     */
    public ChatMessageComposer(String text, TextFormatting color) {
        this(text);
        this.addFormatting(color);
    }
    
    /**
     * Prints a Hypixel style separaton message using the provided color.
     */
    public static void printSeparationMessage(TextFormatting color) {
        new ChatMessageComposer(SEPARATION_MESSAGE, color).addFormatting(TextFormatting.BOLD).send(false);
    }
    
    /**
     * Adds a formatting to the text message. The ChatMessageComposer used is modified.
     * @return The ChatMessageComposer instance in order to make code more compact.
     */
    public ChatMessageComposer addFormatting(TextFormatting formatting) {
        Style style = this.chatComponent.getChatStyle();
        switch (formatting) {
        case ITALIC:
            style.setItalic(true);
            break;
        case BOLD:
            style.setBold(true);
            break;
        case UNDERLINE:
            style.setUnderlined(true);
            break;
        case OBFUSCATED:
            style.setObfuscated(true);
            break;
        case STRIKETHROUGH:
            style.setStrikethrough(true);
            break;
        default:
            style.setColor(formatting);
            break;
        }
        this.chatComponent.setChatStyle(style);

        return this;
    }
    
    /**
     * Append a message to the an existing message. This is used to achieve multiple colors in one line.
     * @param message message to append
     * @return The ChatMessageComposer instance in order to make code more compact.
     */
    public ChatMessageComposer appendMessage(ChatMessageComposer message) {
        // Make sure appendedMessages gets created
        if(this.appendedMessages == null) {
            this.appendedMessages = new ArrayList<ChatMessageComposer>();
        }
        // Add the message
        this.appendedMessages.add(message);
        // And add messages which were added to the message
        if(message.appendedMessages != null) {
            this.appendedMessages.addAll(message.appendedMessages);
        }

        return this;
    }
    
    /**
     * Makes the chat message clickable.
     * @param action Action performed by clicking
     * @param execute URL or command to execute
     * @param description Shown message when hovering over the clickable chat.
     * @return The ChatMessageComposer instance in order to make code more compact.
     */
    public ChatMessageComposer makeClickable(ClickEvent.Action action, String execute, ChatMessageComposer description) {
        Style style = this.chatComponent.getChatStyle();

        style.setChatClickEvent(new ClickEvent(action, execute));
        style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, description.assembleMessage(false)));

        this.chatComponent.setChatStyle(style);

        return this;
    }
    
    /**
     * Makes the chat message link to a given url.
     * @param url The linked URL. MAKE SURE IT STARTS WITH HTTP:// or HTTPS://!
     * @return The ChatMessageComposer instance in order to make code more compact.
     */
    public ChatMessageComposer makeLink(String url) {
        // Compose a generic description
        ChatMessageComposer description = new ChatMessageComposer("Click to visit ", TextFormatting.GRAY);
        description.appendMessage(new ChatMessageComposer(url, TextFormatting.AQUA).addFormatting(TextFormatting.UNDERLINE));
        // and make it clickable
        this.makeClickable(Action.OPEN_URL, url, description);

        return this;
    }
    
    /**
     * Creates a tooltip like hover text.
     * @param text the message shown then hovering
     * @return The ChatMessageComposer instance in order to make code more compact.
     */
    public ChatMessageComposer makeHover(ChatMessageComposer text) {
        Style style = this.chatComponent.getChatStyle();

        style.setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, text.assembleMessage(false)));

        this.chatComponent.setChatStyle(style);

        return this;
    }
    
    /**
     * Send this message to the player. (with [HudPixel] prefix)
     */
    public void send() {
        this.send(true);
    }
    
    /**
     * Send this message to the player.
     * @param prefix Send the [HudPixel] prefix?
     */
    public void send(boolean prefix) {
        try {
            TextComponentString result = new TextComponentString("");
            // send the assebled message to the player
            if(this.appendedMessages != null) {
                for (ChatMessageComposer m : this.appendedMessages) {

                    result.appendSibling(m.chatComponent);
                }

            }
            result.appendSibling(prefix ? CHAT_PREFIX : new TextComponentString(""));
            Minecraft.getMinecraft().thePlayer.addChatMessage(result);
        } catch(Exception e) {
            HudPixelMod.instance().logError("Failed to send chat message");
            e.printStackTrace();
        }
    }
    
    /**
     * Builds an TextComponentString including all appended messages.
     * @param prefix should [HudPixel] be added as chat prefix?
     * @return the TextComponentString containing all appended messages
     */
    protected  TextComponentString assembleMessage(boolean prefix) {
        TextComponentString result;
        if(prefix) {
            // Copy the prefix
            result = CHAT_PREFIX.createCopy();
        } else if(this.appendedMessages == null) {
            // Nothing to append
            return this.chatComponent;
        } else {
            // this step is important so that the appended messages don't inherit the style
            result = new TextComponentString("");
        }

        // add the main message
        result.appendSibling(this.chatComponent);
        // and add all appended messages
        if(this.appendedMessages != null) {
            for (ChatMessageComposer m : this.appendedMessages) {
                result.appendSibling(m.chatComponent);
            }
        }

        return result;
    }
}
