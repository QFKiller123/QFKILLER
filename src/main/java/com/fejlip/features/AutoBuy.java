package com.fejlip.features;

import com.fejlip.Macro;
import com.fejlip.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class AutoBuy {
    private int lastAuctionBought = 0;
    private final ScheduledExecutorService service;
    private int earlierWindowId = 0;

    public AutoBuy() {
        this.service = Executors.newSingleThreadScheduledExecutor();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInventoryRendering(GuiScreenEvent.DrawScreenEvent.Post post) {
        Config config = Macro.getInstance().getConfig();
        if (config != null && config.isEnabled() && Macro.getInstance().getQueue().isRunning() && (post.gui instanceof GuiChest) ) {
            boolean isDebug = config.isDebug();
            ContainerChest chest = (ContainerChest) ((GuiChest) post.gui).inventorySlots;
            if (chest != null) {
                String name = chest.getLowerChestInventory().getName();
                if (name.contains("BIN Auction View")) {
                    ItemStack stack = chest.getSlot(31).getStack();
                    if (stack != null) {
                        if (Items.feather != stack.getItem()) {
                            if (Items.potato == stack.getItem()) {
                                if (isDebug) Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§d[Macro] §fPotato auction found, skipping..."));
                                Minecraft.getMinecraft().thePlayer.closeScreen();
                                Macro.getInstance().getQueue().setRunning(false);
                            } else if (Items.bed == stack.getItem()) {
                                if (chest.windowId != this.earlierWindowId) {
                                    int bedDelay = config.getBedDelay();
                                    this.earlierWindowId = chest.windowId;
                                    this.service.scheduleWithFixedDelay(() -> {
                                        if (Macro.getInstance().getQueue().isRunning()) {
                                            clickNugget(chest.windowId);
                                        }
                                    }, 1L, bedDelay, TimeUnit.MILLISECONDS);
                                }
                            } else if (Items.gold_nugget == stack.getItem()) {
                                clickNugget(chest.windowId);
                            } else {
                                if (isDebug) Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§d[Macro] §fSomething went wrong"));
                                Minecraft.getMinecraft().thePlayer.closeScreen();
                                Macro.getInstance().getQueue().setRunning(false);
                            }
                        }
                    }
                } else if (name.contains("Confirm Purchase")) {
                    if (chest.windowId != this.lastAuctionBought) {
                        clickConfirm(chest.windowId);
                        this.lastAuctionBought = chest.windowId;
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onClientChatMessage(ClientChatReceivedEvent event) {
        String str = event.message.getUnformattedText();
        if (Macro.getInstance().getQueue().isRunning()) {
            boolean isDebug = Macro.getInstance().getConfig().isDebug();
            if (str.contains("This auction wasn't found") || str.contains("There was an error with the auction")) {
                if (isDebug) Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§d[Macro] §fError or not found"));
                Macro.getInstance().getQueue().setRunning(false);
            }
            if (str.contains("You don't have enough coins to afford this bid!")) {
                if (isDebug) Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§d[Macro] §fNot enough coins"));
                Minecraft.getMinecraft().thePlayer.closeScreen();
                Macro.getInstance().getQueue().setRunning(false);

            }
            if (str.contains("You purchased")) {
                if (isDebug) Minecraft.getMinecraft().thePlayer.addChatMessage(new ChatComponentText("§d[Macro] §fPutting coins in escrow..."));
                Macro.getInstance().getQueue().setRunning(false);
            }
        }
    }

    private void clickNugget(int id) {
        click(id, 31);
    }

    private void clickConfirm(int id) {
        click(id, 11);
        Minecraft.getMinecraft().thePlayer.closeScreen();
    }

    private void click(int id, int index) {
        (Minecraft.getMinecraft()).playerController.windowClick(id, index, 0, 3, Minecraft.getMinecraft().thePlayer);
    }
}

