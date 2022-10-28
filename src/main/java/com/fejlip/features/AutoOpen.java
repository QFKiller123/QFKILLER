package com.fejlip.features;

import com.fejlip.Macro;
import com.fejlip.helpers.QueueItem;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import java.io.PrintStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AutoOpen {
    public AutoOpen() {
        System.setOut(new PrintStream(System.out) {
            public void println(String str) {
                AutoOpen.this.handleMessage(str);
                super.println(str);
            }
        });
    }

    public void handleMessage(String str) {
        if (!Macro.getInstance().getConfig().isEnabled()) return;
        Pattern pattern = Pattern.compile("type[\\\":]*flip");
        Matcher matcher = pattern.matcher(str);
         if (matcher.find()) {
            pattern = Pattern.compile("/viewauction \\w+");
            matcher = pattern.matcher(str);
            if (matcher.find()) {
                String command = matcher.group();
                if (Macro.getInstance().getConfig().isDebug()) Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§d[Macro] §fAdding to queue: " + command));
                Macro.getInstance().getQueue().add(new QueueItem(command));
            }
        }
    }
}
