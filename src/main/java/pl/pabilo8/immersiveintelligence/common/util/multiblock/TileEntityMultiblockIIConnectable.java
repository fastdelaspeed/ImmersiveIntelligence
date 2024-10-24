package pl.pabilo8.immersiveintelligence.common.util.multiblock;

import blusunrize.immersiveengineering.ImmersiveEngineering;
import blusunrize.immersiveengineering.api.ApiUtils;
import blusunrize.immersiveengineering.api.TargetingInfo;
import blusunrize.immersiveengineering.api.energy.wires.IICProxy;
import blusunrize.immersiveengineering.api.energy.wires.IImmersiveConnectable;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler;
import blusunrize.immersiveengineering.api.energy.wires.ImmersiveNetHandler.Connection;
import blusunrize.immersiveengineering.api.energy.wires.WireType;
import blusunrize.immersiveengineering.client.ClientUtils;
import blusunrize.immersiveengineering.common.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.common.property.IExtendedBlockState;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import pl.pabilo8.immersiveintelligence.common.IILogger;
import pl.pabilo8.immersiveintelligence.common.util.multiblock.util.MultiblockPOI;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import static blusunrize.immersiveengineering.api.energy.wires.WireApi.canMix;
import static blusunrize.immersiveengineering.api.energy.wires.WireType.*;

/**
 * @author Pabilo8
 * @since 12.12.2023
 */
public abstract class TileEntityMultiblockIIConnectable<T extends TileEntityMultiblockIIConnectable<T>> extends TileEntityMultiblockIIBase<T> implements IImmersiveConnectable
{
	protected WireType limitType = null;

	public TileEntityMultiblockIIConnectable(MultiblockStuctureBase<T> multiblock)
	{
		super(multiblock);
	}

	@Override
	public void onEnergyPassthrough(int amount)
	{

	}

	@Override
	public boolean allowEnergyToPass(Connection con)
	{
		return true;
	}

	public boolean isRelay()
	{
		return false;
	}

	@Override
	public boolean canConnect()
	{
		return isPOI(MultiblockPOI.WIRE_MOUNT);
	}

	@Override
	public boolean isEnergyOutput()
	{
		return false;
	}

	@Override
	public int outputEnergy(int amount, boolean simulate, int energyType)
	{
		return 0;
	}

	@Override
	public BlockPos getConnectionMaster(WireType cableType, TargetingInfo target)
	{
		return getPos();
	}

	@Override
	public boolean canConnectCable(WireType cableType, TargetingInfo target, Vec3i offset)
	{
		return isMatchingCable(cableType)&&(limitType==null||(this.isRelay()&&canMix(limitType, cableType)));
	}

	protected abstract boolean isMatchingCable(WireType cableType);

	@Override
	public void connectCable(WireType cableType, TargetingInfo target, IImmersiveConnectable other)
	{
		this.limitType = cableType;
	}

	@Override
	public WireType getCableLimiter(TargetingInfo target)
	{
		return this.limitType;
	}

	@Override
	public void removeCable(Connection connection)
	{
		WireType type = connection!=null?connection.cableType: null;
		Set<Connection> outputs = ImmersiveNetHandler.INSTANCE.getConnections(world, Utils.toCC(this));
		if(outputs==null||outputs.size()==0)
		{
			if(type==limitType||type==null)
				this.limitType = null;
		}
		this.markDirty();
		if(world!=null)
		{
			IBlockState state = world.getBlockState(this.getPos());
			world.notifyBlockUpdate(this.getPos(), state, state, 3);
		}
	}

	private final List<Pair<Float, Consumer<Float>>> sources = new ArrayList<>();
	private long lastSourceUpdate = 0;

	@Override
	public void addAvailableEnergy(float amount, Consumer<Float> consume)
	{
		long currentTime = world.getTotalWorldTime();
		if(lastSourceUpdate!=currentTime)
		{
			sources.clear();
			Pair<Float, Consumer<Float>> own = getOwnEnergy();
			if(own!=null)
				sources.add(own);
			lastSourceUpdate = currentTime;
		}
		if(amount > 0&&consume!=null)
			sources.add(new ImmutablePair<>(amount, consume));
	}

	@Nullable
	protected Pair<Float, Consumer<Float>> getOwnEnergy()
	{
		return null;
	}

	@Override
	public float getDamageAmount(Entity e, Connection c)
	{
		float baseDmg = getBaseDamage(c);
		float max = getMaxDamage(c);
		if(baseDmg==0||world.getTotalWorldTime()-lastSourceUpdate > 1)
			return 0;
		float damage = 0;
		for(int i = 0; i < sources.size()&&damage < max; i++)
		{
			int consume = (int)Math.min(sources.get(i).getLeft(), (max-damage)/baseDmg);
			damage += baseDmg*consume;
		}
		return damage;
	}

	@Override
	public void processDamage(Entity e, float amount, Connection c)
	{
		float baseDmg = getBaseDamage(c);
		float damage = 0;
		for(int i = 0; i < sources.size()&&damage < amount; i++)
		{
			float consume = Math.min(sources.get(i).getLeft(), (amount-damage)/baseDmg);
			sources.get(i).getRight().accept(consume);
			damage += baseDmg*consume;
			if(consume==sources.get(i).getLeft())
			{
				sources.remove(i);
				i--;
			}
		}
	}

	protected float getBaseDamage(Connection c)
	{
		if(c.cableType==COPPER)
			return 8*2F/c.cableType.getTransferRate();
		else if(c.cableType==ELECTRUM)
			return 8*5F/c.cableType.getTransferRate();
		else if(c.cableType==STEEL)
			return 8*15F/c.cableType.getTransferRate();
		return 0;
	}

	protected float getMaxDamage(Connection c)
	{
		return c.cableType.getTransferRate()/8*getBaseDamage(c);
	}

	/**
	 * Retrieves packet to send to the client whenever this Tile Entity is resynced via World.notifyBlockUpdate. For modded
	 * TE's, this packet comes back to you clientside in {@link #onDataPacket}
	 */
	@Override
	public SPacketUpdateTileEntity getUpdatePacket()
	{
		NBTTagCompound nbttagcompound = new NBTTagCompound();
		this.writeToNBT(nbttagcompound);
		writeConnsToNBT(nbttagcompound);
		return new SPacketUpdateTileEntity(getPos(), 3, nbttagcompound);
	}

	@Override
	public void onDataPacket(@Nonnull NetworkManager net, @Nonnull SPacketUpdateTileEntity pkt)
	{
		NBTTagCompound nbt = pkt.getNbtCompound();
		this.readFromNBT(nbt);
		loadConnsFromNBT(nbt);
	}

	@Override
	public boolean receiveClientEvent(int id, int arg)
	{
		if(id==-1||id==255)
		{
			IBlockState state = world.getBlockState(getPos());
			world.notifyBlockUpdate(getPos(), state, state, 3);
			return true;
		}
		else if(id==254)
		{
			IBlockState state = world.getBlockState(getPos());
			if(state instanceof IExtendedBlockState)
			{
				state = state.getActualState(world, getPos());
				state = state.getBlock().getExtendedState(state, world, getPos());
				ImmersiveEngineering.proxy.removeStateFromSmartModelCache((IExtendedBlockState)state);
				ImmersiveEngineering.proxy.removeStateFromConnectionModelCache((IExtendedBlockState)state);
			}
			world.notifyBlockUpdate(getPos(), state, state, 3);
			return true;
		}
		return super.receiveClientEvent(id, arg);
	}

	@Override
	public void readCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket)
	{
		super.readCustomNBT(nbt, descPacket);
		try
		{
			if(nbt.hasKey("limitType"))
				limitType = ApiUtils.getWireTypeFromNBT(nbt, "limitType");
			else
				limitType = null;
			if(nbt.hasKey("connectionList"))
				loadConnsFromNBT(nbt);
		} catch(Exception e)
		{
			IILogger.error("TileEntityMultiblockIIConnectable encountered an error reading connection NBT.");
			IILogger.error(e);
		}
	}

	@Override
	public void writeCustomNBT(@Nonnull NBTTagCompound nbt, boolean descPacket)
	{
		super.writeCustomNBT(nbt, descPacket);
		try
		{
			if(limitType!=null)
				nbt.setString("limitType", limitType.getUniqueName());
			if(descPacket)
				writeConnsToNBT(nbt);
		} catch(Exception e)
		{
			IILogger.error("TileEntityMultiblockIIConenctable encountered an error writing NBT");
			IILogger.error(e);
		}
	}

	private void loadConnsFromNBT(NBTTagCompound nbt)
	{
		if(world!=null&&world.isRemote&&!ClientUtils.mc().isSingleplayer()&&nbt!=null)
		{
			NBTTagList connectionList = nbt.getTagList("connectionList", 10);
			ImmersiveNetHandler.INSTANCE.clearConnectionsOriginatingFrom(Utils.toCC(this), world);
			for(int i = 0; i < connectionList.tagCount(); i++)
			{
				NBTTagCompound conTag = connectionList.getCompoundTagAt(i);
				Connection con = Connection.readFromNBT(conTag);
				if(con!=null)
				{
					ImmersiveNetHandler.INSTANCE.addConnection(world, Utils.toCC(this), con);
				}
				else
					IILogger.error("CLIENT read connection as null from {}", nbt);
			}
		}
	}

	private void writeConnsToNBT(NBTTagCompound nbt)
	{
		if(world!=null&&!world.isRemote&&nbt!=null)
		{
			NBTTagList connectionList = new NBTTagList();
			Set<Connection> conL = ImmersiveNetHandler.INSTANCE.getConnections(world, Utils.toCC(this));
			if(conL!=null)
				for(Connection con : conL)
					connectionList.appendTag(con.writeToNBT());
			nbt.setTag("connectionList", connectionList);
		}
	}

	@Override
	public void onChunkUnload()
	{
		super.onChunkUnload();
		ImmersiveNetHandler.INSTANCE.addProxy(new IICProxy(this));
	}

	/**
	 * validates a tile entity
	 */
	@Override
	public void validate()
	{
		super.validate();
		ApiUtils.addFutureServerTask(world, () -> ImmersiveNetHandler.INSTANCE.onTEValidated(this));
	}

	/**
	 * invalidates a tile entity
	 */
	@Override
	public void invalidate()
	{
		super.invalidate();
		ImmersiveNetHandler.INSTANCE.clearAllConnectionsFor(getPos(), world, this, false);
	}
}
