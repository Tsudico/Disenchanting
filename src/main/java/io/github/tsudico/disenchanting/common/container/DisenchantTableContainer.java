package io.github.tsudico.disenchanting.common.container;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Blocks;
import net.minecraft.container.BlockContext;
import net.minecraft.container.Container;
import net.minecraft.container.Property;
import net.minecraft.container.Slot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.InfoEnchantment;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.BasicInventory;
import net.minecraft.inventory.CraftingResultInventory;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ItemScatterer;

import java.util.Map;
import java.util.Random;

import static java.lang.Math.round;

public class DisenchantTableContainer extends Container {
    private static final String[] EMPTY_ARMOR_SLOT_IDS = new String[]{"item/empty_armor_slot_boots", "item/empty_armor_slot_leggings", "item/empty_armor_slot_chestplate", "item/empty_armor_slot_helmet"};
    private static final EquipmentSlot[] EQUIPMENT_SLOT_ORDER;
    private final Inventory result;
    private final Inventory inventoryInput;
    private final BlockContext context;
    private final Random random;
    private final Property levelCost;
    private Enchantment selectedEnchantment;
    private int selectedEnchantmentLevel;
    public final PlayerInventory playerInventory;

    public DisenchantTableContainer(int syncId, PlayerEntity player) {
        this(syncId, player.inventory, BlockContext.EMPTY);
    }

    public DisenchantTableContainer(int syncId, PlayerInventory playerInventory, BlockContext blockContext) {
        super(null, syncId);
        this.playerInventory = playerInventory;
        context = blockContext;
        result = new CraftingResultInventory();
        inventoryInput = new BasicInventory(2) {
            public void markDirty() {
                super.markDirty();
                onContentChanged(this);
            }
        };
        random = new Random();
        random.setSeed(playerInventory.player.getUuid().getMostSignificantBits());
        levelCost = Property.create();
        addProperty(levelCost);

        // Enchanted item slot
        addSlot(new Slot(inventoryInput, 0, 98, 8) {
            public boolean canInsert(ItemStack itemStack) {
                if(itemStack.getItem() == Items.ENCHANTED_BOOK) {
                    return (EnchantmentHelper.getEnchantments(itemStack).size() > 1);
                } else { return itemStack.hasEnchantments(); }
            }

            public int getMaxStackAmount() {
                return 1;
            }

            public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
                inventory.setInvStack(0, ItemStack.EMPTY);
                selectedEnchantment = null;
                updateResult();
                return itemStack;
            }

            public void setStack(ItemStack itemStack) {
                selectedEnchantment = null;
                this.inventory.setInvStack( 0, itemStack);
                this.markDirty();
            }
        });
        // Book slot
        addSlot(new Slot(inventoryInput, 1, 98, 44) {
            public boolean canInsert(ItemStack itemStack) {
                return itemStack.getItem() == Items.BOOK;
            }

            public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
                inventory.setInvStack(1, ItemStack.EMPTY);
                selectedEnchantment = null;
                updateResult();
                return itemStack;
            }
        });
        // Result slot
        addSlot(new Slot(result, 2, 152, 26) {
            public boolean canInsert(ItemStack itemStack) {
                return false;
            }

            public boolean canTakeItems(PlayerEntity playerEntity) {
                return (playerEntity.abilities.creativeMode || playerEntity.experienceLevel >= levelCost.get()) && levelCost.get() > 0 && hasStack();
            }

            public ItemStack onTakeItem(PlayerEntity playerEntity, ItemStack itemStack) {
                // Take experience from player
                if (!playerEntity.abilities.creativeMode) {
                    playerEntity.addExperienceLevels(-levelCost.get());
                }

                // Reduce inventoryInput of books by one
                ItemStack newBooks = inventoryInput.getInvStack(1);
                if(newBooks.getAmount() > 1) {
                    newBooks.setAmount(newBooks.getAmount() - 1);
                    inventoryInput.setInvStack( 1, newBooks);
                }
                else {
                    inventoryInput.setInvStack(1, ItemStack.EMPTY);
                }

                // Adjust enchanted item to remove enchantment
                ItemStack enchantedItem = inventoryInput.getInvStack( 0).copy();
                Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(enchantedItem);
                enchantments.remove(selectedEnchantment);
                if(enchantedItem.getItem() != Items.ENCHANTED_BOOK) {
                    enchantedItem.removeSubTag("Enchantments");
                    EnchantmentHelper.set(enchantments, enchantedItem);
                    inventoryInput.setInvStack(0, enchantedItem);
                } else {
                    Map.Entry<Enchantment, Integer> start = enchantments.entrySet().iterator().next();
                    ItemStack enchantedBookItem = EnchantedBookItem.makeStack(new InfoEnchantment(start.getKey(), start.getValue()));
                    enchantments.remove(start.getKey());
                    for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                        EnchantedBookItem.addEnchantment(enchantedBookItem, new InfoEnchantment(entry.getKey(), entry.getValue()));
                    }
                    inventoryInput.setInvStack( 0, enchantedBookItem);
                }

                // Reset experience cost and return enchanted book
                levelCost.set(0);
                selectedEnchantment = null;

                return itemStack;
            }
        });

        int invSlot;
        // Player Hotbar
        for(invSlot = 0; invSlot < 9; ++invSlot) {
            addSlot(new Slot(playerInventory, invSlot, 8 + invSlot * 18, 142));
        }
        // Player Inventory
        for(invSlot = 0; invSlot < 3; ++invSlot) {
            for(int column = 0; column < 9; ++column) {
                addSlot(new Slot(playerInventory, column + invSlot * 9 + 9, 8 + column * 18, 84 + invSlot * 18));
            }
        }
        // Player Armor
        for(invSlot = 0; invSlot < 4; ++invSlot) {
            final EquipmentSlot equipmentSlot = EQUIPMENT_SLOT_ORDER[invSlot];
            addSlot(new Slot(playerInventory, 39 - invSlot, 8, 8 + invSlot * 18) {
                public int getMaxStackAmount() {
                    return 1;
                }

                public boolean canInsert(ItemStack itemStack) {
                    return equipmentSlot == MobEntity.getPreferredEquipmentSlot(itemStack);
                }

                public boolean canTakeItems(PlayerEntity playerEntity) {
                    ItemStack itemStack = getStack();
                    return (itemStack.isEmpty() || playerEntity.isCreative() || !EnchantmentHelper.hasBindingCurse(itemStack)) && super.canTakeItems(playerEntity);
                }

                @Environment(EnvType.CLIENT)
                public String getBackgroundSprite() {
                    return EMPTY_ARMOR_SLOT_IDS[equipmentSlot.getEntitySlotId()];
                }
            });
        }
        // Player Offhand
        addSlot(new Slot(playerInventory, 40, 26, 62) {
            @Environment(EnvType.CLIENT)
            public String getBackgroundSprite() {
                return "item/empty_armor_slot_shield";
            }
        });
    }

    public void onContentChanged(Inventory inventory) {
        super.onContentChanged(inventory);
        if (this.inventoryInput == inventory) {
            random.setSeed(random.nextLong());
            updateResult();
        }
    }

    private void updateResult() {
        if(playerInventory.player.world.isClient) return;

        ItemStack enchantedItem = inventoryInput.getInvStack(0);
        ItemStack bookStack = inventoryInput.getInvStack(1);
        boolean enchantedItemIsBook = (enchantedItem.getItem() == Items.ENCHANTED_BOOK);
        if (enchantedItem.isEmpty() || bookStack.isEmpty() || (!enchantedItemIsBook && !enchantedItem.hasEnchantments())) {
            result.setInvStack(0, ItemStack.EMPTY);
            levelCost.set(0);
        } else if (enchantedItemIsBook && EnchantmentHelper.getEnchantments(enchantedItem).size() <= 1) {
            result.setInvStack(0, ItemStack.EMPTY);
            levelCost.set(0);
        } else {
            if(null != selectedEnchantment) return;
            levelCost.set(1);
            ItemStack enchantedItemCopy = enchantedItem.copy();
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(enchantedItemCopy);
            if(1 < enchantments.size()) {
                int count = 0;
                if(null == selectedEnchantment) {
                    int selected = random.nextInt(enchantments.size());
                    for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                        if (count == selected) {
                            selectedEnchantment = entry.getKey();
                            selectedEnchantmentLevel = entry.getValue();
                            break;
                        }
                        ++count;
                    }
                }
            } else {
                if(enchantments.entrySet().iterator().hasNext()) {
                    Map.Entry<Enchantment, Integer> entry = enchantments.entrySet().iterator().next();
                    selectedEnchantment = entry.getKey();
                    selectedEnchantmentLevel = entry.getValue();
                } else selectedEnchantment = null;
            }

            if(null != selectedEnchantment) {
                int weight = selectedEnchantment.getWeight().getWeight();
                int enchantLevel = selectedEnchantmentLevel;
                float cost = (enchantLevel * 5) * (1.6F - (weight * 0.03F));
                levelCost.set(round(cost));
                ItemStack enchantedBook = EnchantedBookItem.makeStack(new InfoEnchantment(selectedEnchantment, selectedEnchantmentLevel));
                result.setInvStack(0, enchantedBook);
            } else {
                result.setInvStack(0, ItemStack.EMPTY);
                levelCost.set(0);
            }
        }
        sendContentUpdates();
    }

    public ItemStack transferSlot(PlayerEntity playerEntity, int slot) {
        ItemStack newSlotStack = ItemStack.EMPTY;
        Slot oldSlot = slotList.get(slot);
        if (oldSlot != null && oldSlot.hasStack()) {
            ItemStack oldSlotStack = oldSlot.getStack();
            newSlotStack = oldSlotStack.copy();
            if (slot == 2) {
                if (!this.insertItem(oldSlotStack, 3, 39, true)) {
                    return ItemStack.EMPTY;
                }
                oldSlot.onStackChanged(oldSlotStack, newSlotStack);
            } else if (slot != 0 && slot != 1) {
                if (slot < 39 && !this.insertItem(oldSlotStack, 0, 2, false)) {
                    return ItemStack.EMPTY;
                }
                oldSlot.onStackChanged(oldSlotStack, newSlotStack);
            } else if (!this.insertItem(oldSlotStack, 3, 39, false)) {
                return ItemStack.EMPTY;
            }

            if (oldSlotStack.isEmpty()) {
                oldSlot.setStack(ItemStack.EMPTY);
            } else {
                oldSlot.markDirty();
            }

            if (oldSlotStack.getAmount() == newSlotStack.getAmount()) {
                return ItemStack.EMPTY;
            }

            oldSlot.onTakeItem(playerEntity, oldSlotStack);
        }
        updateResult();

        return newSlotStack;
    }

    public void close(PlayerEntity playerEntity) {
        super.close(playerEntity);
        for(int i = 0; i < inventoryInput.getInvSize(); i++) {
            ItemScatterer.spawn(playerEntity.world, playerEntity.getBlockPos(), inventoryInput);
        }
    }

    public boolean canUse(PlayerEntity playerEntity) {
        return canUse(context, playerEntity, Blocks.ENCHANTING_TABLE);
    }

    @Environment(EnvType.CLIENT)
    public int getLevelCost() {
        return levelCost.get();
    }

    static {
        EQUIPMENT_SLOT_ORDER = new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
    }
}
