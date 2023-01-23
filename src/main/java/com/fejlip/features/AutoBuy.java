package com.fejlip.features;

import com.fejlip.Macro;
import com.fejlip.config.Config;
import com.fejlip.helpers.Helpers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
    private boolean buying = false;
    public AutoBuy() {
        this.service = Executors.newSingleThreadScheduledExecutor();
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onInventoryRendering(GuiScreenEvent.DrawScreenEvent.Post post) {
        Config config = Macro.getInstance().getConfig();
        if (config != null && config.isAutoBuyEnabled() && (post.gui instanceof GuiChest) ) {
            ContainerChest chest = (ContainerChest) ((GuiChest) post.gui).inventorySlots;
            if (chest != null) {
                String name = chest.getLowerChestInventory().getName();
                if (name.contains("BIN Auction View")) {
                    ItemStack stack = chest.getSlot(31).getStack();
                    if (stack != null) {
                        if (!buying) buying = true;
                        if (Items.feather != stack.getItem()) {
                            if (Items.potato == stack.getItem()) {
                                Helpers.sendDebugMessage("Someone bought the auction already, skipping...");
                                Minecraft.getMinecraft().thePlayer.closeScreen();
                                buying = false;
                                Macro.getInstance().getQueue().setRunning(false);
                            } else if (Items.bed == stack.getItem()) {
                                if (chest.windowId != this.earlierWindowId) {
                                    int bedDelay = config.getBedClickDelay();
                                    this.earlierWindowId = chest.windowId;
                                    this.service.scheduleWithFixedDelay(() -> {
                                        if (buying) {
                                            clickNugget(chest.windowId);
                                        }
                                    }, 1L, bedDelay, TimeUnit.MILLISECONDS);
                                }
                            } else if (Items.gold_nugget == stack.getItem() || Item.getItemFromBlock(Blocks.gold_block) == stack.getItem()) {
                                clickNugget(chest.windowId);
                            } else {
                                Helpers.sendDebugMessage("Something went wrong, skipping...");
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
            if (str.contains("This auction wasn't found") || str.contains("There was an error with the auction")) {
                Helpers.sendDebugMessage("Error or not found");
                buying = false;
                Macro.getInstance().getQueue().setRunning(false);
            }
            if (str.contains("You don't have enough coins to afford this bid!")) {
                Helpers.sendDebugMessage("Not enough coins to buy this auction, skipping...");
                Minecraft.getMinecraft().thePlayer.closeScreen();
                buying = false;
                Macro.getInstance().getQueue().setRunning(false);

            }
            if (str.contains("Putting coins in")) {
                Helpers.sendDebugMessage("Putting coins in escrow...");
                buying = false;
                Macro.getInstance().getQueue().setRunning(false);
            }
        }
        else {
            if (str.contains("Putting coins in")) {
                buying = false;
                Helpers.sendDebugMessage("Putting coins in escrow...");
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

