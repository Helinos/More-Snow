package net.helinos.moresnow.block;

import net.minecraft.core.block.Block;
import net.minecraft.core.block.BlockLogic;
import net.minecraft.core.block.Blocks;

import java.util.Collections;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

public abstract class BlockLogicSnowyMultiple<T extends BlockLogic> extends BlockLogicSnowy<T> {
	protected final Map<Integer, Integer> METADATA_TO_BLOCK_ID;
	protected final List<Integer> USED_IDS;
	private final int idOffset;
	private final int idMask;

	public BlockLogicSnowyMultiple(Block<T> block, @NotNull Class<?> blockLogicClass, @NotNull List<Integer> excludedIds, int maxLayers, int lowestLayerHeight, boolean supportsOwnSnow, int idOffset, int idMask) {
		super(block, maxLayers, lowestLayerHeight, supportsOwnSnow);
		
		this.idOffset = idOffset;
		this.idMask = idMask;
		
		this.METADATA_TO_BLOCK_ID = this.initMetadataToBlockId(blockLogicClass, excludedIds);
		this.USED_IDS = METADATA_TO_BLOCK_ID.values().stream().map(i -> i).collect(Collectors.toList());
	}

	protected Map<Integer, Integer> initMetadataToBlockId(@NotNull Class<?> blockLogicClass, List<Integer> excludedIds) {
		Hashtable<Integer, Integer> tmp = new Hashtable<>();
		int metadataID = 0;
		
		for (Block<?> b : Blocks.blocksList) {
			if (b == null)
				continue;
			int id = b.id();
			if (!blockLogicClass.isInstance(b.getLogic()) || excludedIds.contains(id))
				continue;
			tmp.put(metadataID++, id);
		}

		return Collections.unmodifiableMap(tmp);
	}
	
	@Override
	public boolean canReplaceBlock(int id, int metadata) {
		return this.METADATA_TO_BLOCK_ID.containsValue(id);
	}

	@Override
	public int getStoredBlockId(int metadata) {
		int blockKey = (metadata >> this.idOffset) & this.idMask;
		return (int) this.METADATA_TO_BLOCK_ID.getOrDefault(blockKey, 0);
	}

	@Override
	protected int blockToMetadata(int blockId, int metadata) {
		for (Map.Entry<Integer, Integer> entry : this.METADATA_TO_BLOCK_ID.entrySet()) {
			if (entry.getValue() == blockId) {
				return entry.getKey() << this.idOffset;
			}
		}

		return 0;
	}
}
