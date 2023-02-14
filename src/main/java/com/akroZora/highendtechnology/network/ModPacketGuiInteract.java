package com.akroZora.highendtechnology.network;

import com.akroZora.highendtechnology.tile.custom.MachineAssemblicatorBlockEntity;
import mekanism.api.functions.TriConsumer;
import mekanism.common.network.IMekanismPacket;
import mekanism.common.tile.TileEntityLogisticalSorter;
import mekanism.common.tile.TileEntitySecurityDesk;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.qio.TileEntityQIOExporter;
import mekanism.common.tile.qio.TileEntityQIOImporter;
import mekanism.common.tile.qio.TileEntityQIORedstoneAdapter;
import mekanism.common.util.SecurityUtils;
import mekanism.common.util.WorldUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.network.NetworkEvent;

public class ModPacketGuiInteract implements IMekanismPacket {
    private final Type interactionType;
    private ModGuiInteraction interaction;
    private ModGuiInteractionItem itemInteraction;
    private ModGuiInteractionEntity entityInteraction;
    private BlockPos tilePosition;
    private ItemStack extraItem;
    private int entityID;
    private int extra;

    public ModPacketGuiInteract(ModGuiInteractionEntity interaction, Entity entity) {
        this((ModGuiInteractionEntity)interaction, (Entity)entity, 0);
    }

    public ModPacketGuiInteract(ModGuiInteractionEntity interaction, Entity entity, int extra) {
        this(interaction, entity.getId(), extra);
    }

    public ModPacketGuiInteract(ModGuiInteractionEntity interaction, int entityID, int extra) {
        this.interactionType = Type.ENTITY;
        this.entityInteraction = interaction;
        this.entityID = entityID;
        this.extra = extra;
    }

    public ModPacketGuiInteract(ModGuiInteraction interaction, BlockEntity tile) {
        this(interaction, tile.getBlockPos());
    }

    public ModPacketGuiInteract(ModGuiInteraction interaction, BlockEntity tile, int extra) {
        this(interaction, tile.getBlockPos(), extra);
    }

    public ModPacketGuiInteract(ModGuiInteraction interaction, BlockPos tilePosition) {
        this((ModGuiInteraction)interaction, (BlockPos)tilePosition, 0);
    }

    public ModPacketGuiInteract(ModGuiInteraction interaction, BlockPos tilePosition, int extra) {
        this.interactionType = Type.INT;
        this.interaction = interaction;
        this.tilePosition = tilePosition;
        this.extra = extra;
    }

    public ModPacketGuiInteract(ModGuiInteractionItem interaction, BlockEntity tile, ItemStack stack) {
        this(interaction, tile.getBlockPos(), stack);
    }

    public ModPacketGuiInteract(ModGuiInteractionItem interaction, BlockPos tilePosition, ItemStack stack) {
        this.interactionType = ModPacketGuiInteract.Type.ITEM;
        this.itemInteraction = interaction;
        this.tilePosition = tilePosition;
        this.extraItem = stack;
    }

    public void handle(NetworkEvent.Context context) {
        Player player = context.getSender();
        if (player != null) {
            if (this.interactionType == ModPacketGuiInteract.Type.ENTITY) {
                Entity entity = player.level.getEntity(this.entityID);
                if (entity != null) {
                    this.entityInteraction.consume(entity, player, this.extra);
                }
            } else {
                TileEntityMekanism tile = (TileEntityMekanism) WorldUtils.getTileEntity(TileEntityMekanism.class, player.level, this.tilePosition);
                if (tile != null) {
                    if (this.interactionType == ModPacketGuiInteract.Type.INT) {
                        this.interaction.consume(tile, player, this.extra);
                    } else if (this.interactionType == ModPacketGuiInteract.Type.ITEM) {
                        this.itemInteraction.consume(tile, player, this.extraItem);
                    }
                }
            }
        }

    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeEnum(this.interactionType);
        switch (this.interactionType) {
            case ENTITY:
                buffer.writeEnum(this.entityInteraction);
                buffer.writeVarInt(this.entityID);
                buffer.writeVarInt(this.extra);
                break;
            case INT:
                buffer.writeEnum(this.interaction);
                buffer.writeBlockPos(this.tilePosition);
                buffer.writeVarInt(this.extra);
                break;
            case ITEM:
                buffer.writeEnum(this.itemInteraction);
                buffer.writeBlockPos(this.tilePosition);
                buffer.writeItem(this.extraItem);
        }

    }

    public static ModPacketGuiInteract decode(FriendlyByteBuf buffer) {
        ModPacketGuiInteract var10000;
        switch ((ModPacketGuiInteract.Type)buffer.readEnum(ModPacketGuiInteract.Type.class)) {
            case ENTITY:
                var10000 = new ModPacketGuiInteract((ModGuiInteractionEntity)buffer.readEnum(ModGuiInteractionEntity.class), buffer.readVarInt(), buffer.readVarInt());
                break;
            case INT:
                var10000 = new ModPacketGuiInteract((ModGuiInteraction)buffer.readEnum(ModGuiInteraction.class), buffer.readBlockPos(), buffer.readVarInt());
                break;
            case ITEM:
                var10000 = new ModPacketGuiInteract((ModGuiInteractionItem)buffer.readEnum(ModGuiInteractionItem.class), buffer.readBlockPos(), buffer.readItem());
                break;
            default:
                throw new IncompatibleClassChangeError();
        }

        return var10000;
    }

    public static enum ModGuiInteractionEntity {
        NEXT_SECURITY_MODE((entity, player, extra) -> {
            SecurityUtils.INSTANCE.incrementSecurityMode((Player) player, (ICapabilityProvider) entity);
        });

        private final TriConsumer<Entity, Player, Integer> consumerForEntity;

        private ModGuiInteractionEntity(TriConsumer consumerForEntity) {
            this.consumerForEntity = consumerForEntity;
        }

        public void consume(Entity entity, Player player, int extra) {
            this.consumerForEntity.accept(entity, player, extra);
        }
    }

    private static enum Type {
        ENTITY,
        ITEM,
        INT;

        private Type() {
        }
    }

    public static enum ModGuiInteraction {
        QIO_REDSTONE_ADAPTER_COUNT((tile, player, extra) -> {
            if (tile instanceof TileEntityQIORedstoneAdapter redstoneAdapter) {
                redstoneAdapter.handleCountChange((long)extra);
            }

        }),
        QIO_TOGGLE_IMPORT_WITHOUT_FILTER((tile, player, extra) -> {
            if (tile instanceof TileEntityQIOImporter importer) {
                importer.toggleImportWithoutFilter();
            }

        }),
        QIO_TOGGLE_EXPORT_WITHOUT_FILTER((tile, player, extra) -> {
            if (tile instanceof TileEntityQIOExporter exporter) {
                exporter.toggleExportWithoutFilter();
            }

        }),
        ENCODE_FORMULA((tile, player, extra) -> {
            if (tile instanceof MachineAssemblicatorBlockEntity assemblicator) {
                System.out.println("EncodeFormulaButtonPress");
                assemblicator.encodeFormula();
            }

        }),
        STOCK_CONTROL_BUTTON((tile, player, extra) -> {
            if (tile instanceof MachineAssemblicatorBlockEntity assemblicator) {
                assemblicator.toggleStockControl();
            }

        }),
        CRAFT_SINGLE((tile, player, extra) -> {
            if (tile instanceof MachineAssemblicatorBlockEntity assemblicator) {
                assemblicator.craftSingle();
            }

        }),
        CRAFT_ALL((tile, player, extra) -> {
            if (tile instanceof MachineAssemblicatorBlockEntity assemblicator) {
                assemblicator.craftAll();
            }

        }),
        MOVE_ITEMS((tile, player, extra) -> {
            if (tile instanceof MachineAssemblicatorBlockEntity assemblicator) {
                assemblicator.moveItems();
            }

        }),
        ROUND_ROBIN_BUTTON((tile, player, extra) -> {
            if (tile instanceof TileEntityLogisticalSorter sorter) {
                sorter.toggleRoundRobin();
            }

        }),
        SINGLE_ITEM_BUTTON((tile, player, extra) -> {
            if (tile instanceof TileEntityLogisticalSorter sorter) {
                sorter.toggleSingleItem();
            }

        }),
        OVERRIDE_BUTTON((tile, player, extra) -> {
            if (tile instanceof TileEntitySecurityDesk desk) {
                desk.toggleOverride();
            }

        });

        private final TriConsumer<TileEntityMekanism, Player, Integer> consumerForTile;

        private ModGuiInteraction(TriConsumer consumerForTile) {
            this.consumerForTile = consumerForTile;
        }

        public void consume(TileEntityMekanism tile, Player player, int extra) {
            this.consumerForTile.accept(tile, player, extra);
        }
    }

    public static enum ModGuiInteractionItem {;

        private final TriConsumer<TileEntityMekanism, Player, ItemStack> consumerForTile;

        private ModGuiInteractionItem(TriConsumer consumerForTile) {
            this.consumerForTile = consumerForTile;
        }

        public void consume(TileEntityMekanism tile, Player player, ItemStack stack) {
            this.consumerForTile.accept(tile, player, stack);
        }
    }
}

