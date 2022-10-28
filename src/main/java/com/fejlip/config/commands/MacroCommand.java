package com.fejlip.config.commands;

import com.fejlip.Macro;
import com.fejlip.config.Config;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class MacroCommand extends CommandBase {

    public MacroCommand() {
    }

    public String getCommandName() {
        return "fm";
    }

    public String getCommandUsage(ICommandSender sender) {
        return "/fm <setting> <value>";
    }

    @Override
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return true;
    }

    public void processCommand(ICommandSender sender, String[] args) {
        Config config = Macro.getInstance().getConfig();
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText("§d[Macro] §fNo arguments!"));
            return;
        }
        switch (args[0]) {
            case "toggle":
                boolean enabled = config.toggleEnabled();
                if (Macro.getInstance().getThread().isAlive()) {
                    Macro.getInstance().getQueue().clear();
                    Macro.getInstance().getQueue().setRunning(false);
                    Macro.getInstance().getThread().interrupt();
                } else {
                    Macro.getInstance().getThread().start();
                }
                sender.addChatMessage(new ChatComponentText("§d[Macro] §fMacro - " + (enabled ? "on" : "off")));
                break;
            case "bed":
                if (args.length == 1) {
                    sender.addChatMessage(new ChatComponentText("§d[Macro] §fInvalid arguments for command bed!"));
                    return;
                }
                try {
                    int bedDelay = Integer.parseInt(args[1]);
                    config.setBedDelay(bedDelay);
                    sender.addChatMessage(new ChatComponentText("§d[Macro] §fBed click speed: " + bedDelay));
                } catch (NumberFormatException e) {
                    sender.addChatMessage(new ChatComponentText("§d[Macro] §fInvalid bed click speed!"));
                }
                break;
            case "debug":
                boolean debug = config.toggleDebug();
                sender.addChatMessage(new ChatComponentText("§d[Macro] §fDebug - " + (debug ? "on" : "off")));
                break;
            default:
                sender.addChatMessage(new ChatComponentText("§d[Macro] §fInvalid arguments!"));
        }
    }



}
