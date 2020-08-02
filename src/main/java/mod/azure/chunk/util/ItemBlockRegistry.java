package mod.azure.chunk.util;

import mod.azure.chunk.AzureChunk;
import mod.azure.chunk.blocks.AzureBlock;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemBlockRegistry {

	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, AzureChunk.MODID);
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS,
			AzureChunk.MODID);

	public static final RegistryObject<Block> BLOCK = BLOCKS.register("loader",
			() -> new AzureBlock(Block.Properties.create(Material.ROCK).hardnessAndResistance(1.5F)));
	public static final RegistryObject<Item> ITEM = ITEMS.register("loader",
			() -> new BlockItem(BLOCK.get(), new Item.Properties().group(ItemGroup.MISC)));
}