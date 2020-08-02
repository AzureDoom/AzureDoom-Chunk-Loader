package mod.azure.chunk;

import javax.annotation.Nullable;

import it.unimi.dsi.fastutil.longs.Long2IntMap;
import it.unimi.dsi.fastutil.longs.Long2IntOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import mod.azure.chunk.util.LoaderInterface;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.LongArrayNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.concurrent.TickDelayedTask;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;

public class ChunkLoader implements LoaderInterface {
	public Long2IntMap intMap = new Long2IntOpenHashMap();
	public LongSet longSet = new LongOpenHashSet();
	public boolean loading = false;
	@Nullable
	public final ServerWorld world;

	public ChunkLoader(ServerWorld world) {
		intMap.defaultReturnValue(Integer.MIN_VALUE);
		this.world = world;
	}

	@Override
	public void add(BlockPos pos) {
		if (!longSet.contains(pos.toLong())) {
			if (!loading)
				forceload(pos, "add");
			intMap.put(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4),
					intMap.get(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4)));
			longSet.add(pos.toLong());
		}
	}

	@Override
	public void remove(BlockPos pos) {
		if (longSet.remove(pos.toLong())) {
			if (intMap.get(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4)) == Integer.MIN_VALUE) {
				if (!loading)
					forceload(pos, "remove");
				intMap.remove(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4));
			} else {
				intMap.put(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4),
						intMap.get(ChunkPos.asLong(pos.getX() >> 4, pos.getZ() >> 4)));
			}
		}
	}

	@Override
	public boolean contains(BlockPos pos) {
		return longSet.contains(pos.toLong());
	}

	public void force(BlockPos pos) {
		forceload(pos, "add");
	}

	public void forceload(BlockPos pos, String action) {
		this.world.getServer().getCommandManager().handleCommand(
				this.world.getServer().getCommandSource().withWorld(this.world),
				"forceload " + action + " " + pos.getX() + " " + pos.getZ());
	}

	public static class Storage implements IStorage<LoaderInterface> {
		@Override
		public INBT writeNBT(Capability<LoaderInterface> capability, LoaderInterface instance, Direction side) {
			if (!(instance instanceof ChunkLoader))
				return null;
			ChunkLoader list = (ChunkLoader) instance;
			long[] data = new long[list.longSet.size()];
			int idx = 0;
			for (long l : list.longSet)
				data[idx++] = l;
			return new LongArrayNBT(data);
		}

		@Override
		public void readNBT(Capability<LoaderInterface> capability, LoaderInterface instance, Direction side,
				INBT nbt) {
			if (!(instance instanceof ChunkLoader) || !(nbt instanceof LongArrayNBT))
				return;
			ChunkLoader list = (ChunkLoader) instance;
			list.loading = true;
			list.intMap.clear();
			list.longSet.clear();
			try {
				for (long l : ((LongArrayNBT) nbt).getAsLongArray()) {
					list.add(BlockPos.fromLong(l));
				}
				if (list.world != null) {
					list.world.getServer().enqueue(new TickDelayedTask(1, () -> {
						for (long l : list.intMap.keySet()) {
							ChunkPos chunk = new ChunkPos(l);
							list.force(new BlockPos(chunk.x << 4, 0, chunk.z << 4));
						}
					}));
				}
			} finally {
				list.loading = false;
			}
		}
	}
}
