package nl.daanmc.euphoria.util;

import net.minecraft.block.Block;
import net.minecraft.item.Item;

import java.util.HashMap;

public interface Meta {
    HashMap<Item, Integer> ITEM_META = new HashMap<>();
    HashMap<Block, Integer> BLOCK_META = new HashMap<>();
}
