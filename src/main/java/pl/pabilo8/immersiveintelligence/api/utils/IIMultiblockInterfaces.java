package pl.pabilo8.immersiveintelligence.api.utils;

import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IAdvancedCollisionBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IAdvancedSelectionBounds;
import blusunrize.immersiveengineering.common.blocks.IEBlockInterfaces.IComparatorOverride;
import blusunrize.immersiveengineering.common.util.Utils;
import blusunrize.immersiveengineering.common.util.inventory.IIEInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains all Immersive Intelligence multiblock interfaces.<br>
 * Feel free to extend.
 *
 * @author Pabilo8
 * @since 04.08.2022
 */
public class IIMultiblockInterfaces
{
	public interface IExplosionResistantMultiblock
	{
		float getExplosionResistance();
	}

	public interface ILadderMultiblock
	{
		boolean isLadder();
	}

	public interface IAdvancedBounds extends IAdvancedCollisionBounds, IAdvancedSelectionBounds
	{
		/**
		 * @param collision whether it's checking collision or selection
		 * @return a list of AABB
		 */
		List<AxisAlignedBB> getBounds(boolean collision);

		@Override
		default List<AxisAlignedBB> getAdvancedColisionBounds()
		{
			return getBounds(true);
		}

		@Override
		default List<AxisAlignedBB> getAdvancedSelectionBounds()
		{
			return getBounds(false);
		}

		@Override
		default boolean isOverrideBox(AxisAlignedBB box, EntityPlayer player, RayTraceResult mop, ArrayList<AxisAlignedBB> list)
		{
			return false;
		}

		@Override
		default float[] getBlockBounds()
		{
			return new float[]{0, 0, 0, 1, 1, 1};
		}
	}

	/**
	 * <b>I</b>
	 */
	public interface IIIInventory extends IIEInventory, IComparatorOverride
	{
		/**
		 * We don't do graphical updates here
		 */
		@Override
		default void doGraphicalUpdates(int slot)
		{

		}

		/**
		 * Unless Asie increases it, should be right, sometimes.
		 */
		@Override
		default int getSlotLimit(int slot)
		{
			return 64;
		}

		@Override
		default int getComparatorInputOverride()
		{
			return Utils.calcRedstoneFromInventory(this);
		}
	}
}
