package mod.azure.chunk.util;

import mod.azure.chunk.AzureChunk;
import mod.azure.chunk.ChunkLoader;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ModEvents {

	@CapabilityInject(LoaderInterface.class)
	public static Capability<LoaderInterface> CAPABILITY = null;

	@SubscribeEvent
	public void attachWorldCaps(AttachCapabilitiesEvent<World> event) {
		final LazyOptional<LoaderInterface> inst = LazyOptional
				.of(() -> new ChunkLoader((ServerWorld) event.getObject()));
		final ICapabilitySerializable<INBT> provider = new ICapabilitySerializable<INBT>() {
			@Override
			public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
				return CAPABILITY.orEmpty(cap, inst);
			}

			@Override
			public INBT serializeNBT() {
				return CAPABILITY.writeNBT(inst.orElse(null), null);
			}

			@Override
			public void deserializeNBT(INBT nbt) {
				CAPABILITY.readNBT(inst.orElse(null), null, nbt);
			}
		};
		event.addCapability(new ResourceLocation(AzureChunk.MODID, "loader"), provider);
		event.addListener(() -> inst.invalidate());
	}
}