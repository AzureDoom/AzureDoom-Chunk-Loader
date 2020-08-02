package mod.azure.chunk;

import mod.azure.chunk.util.ItemBlockRegistry;
import mod.azure.chunk.util.LoaderInterface;
import mod.azure.chunk.util.ModEvents;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(AzureChunk.MODID)
public class AzureChunk {
	public static final String MODID = "chunk";
	public static AzureChunk instance;

	public AzureChunk() {
		instance = this;
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::setup);
		MinecraftForge.EVENT_BUS.register(new ModEvents());
		ItemBlockRegistry.ITEMS.register(modEventBus);
		ItemBlockRegistry.BLOCKS.register(modEventBus);
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event) {
		CapabilityManager.INSTANCE.register(LoaderInterface.class, new ChunkLoader.Storage(),
				() -> new ChunkLoader(null));
	}
}