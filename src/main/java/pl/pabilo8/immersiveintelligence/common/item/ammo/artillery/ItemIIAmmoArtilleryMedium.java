package pl.pabilo8.immersiveintelligence.common.item.ammo.artillery;

import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pl.pabilo8.immersiveintelligence.api.ammo.enums.CoreTypes;
import pl.pabilo8.immersiveintelligence.api.ammo.enums.FuseTypes;
import pl.pabilo8.immersiveintelligence.api.ammo.parts.IAmmoTypeItem.IIAmmoProjectile;
import pl.pabilo8.immersiveintelligence.client.model.builtin.IAmmoModel;
import pl.pabilo8.immersiveintelligence.client.model.builtin.ModelAmmoProjectile;
import pl.pabilo8.immersiveintelligence.common.IIConfigHandler.IIConfig.Ammunition;
import pl.pabilo8.immersiveintelligence.common.entity.ammo.types.EntityAmmoArtilleryProjectile;
import pl.pabilo8.immersiveintelligence.common.item.ammo.ItemIIAmmoBase;
import pl.pabilo8.immersiveintelligence.common.item.ammo.ItemIIAmmoBase.AmmoParts;
import pl.pabilo8.immersiveintelligence.common.item.ammo.ItemIIAmmoCasing.Casings;
import pl.pabilo8.modworks.annotations.item.GeneratedItemModels;
import pl.pabilo8.modworks.annotations.item.ItemModelType;

import javax.annotation.Nonnull;
import java.util.function.Function;

/**
 * @author Pabilo8
 * @since 30-08-2019
 */
//TODO: 08.03.2024 update values from notes
@IIAmmoProjectile
@GeneratedItemModels(itemName = "bullet_artillery_6bcal", type = ItemModelType.ITEM_SIMPLE_AUTOREPLACED, valueSet = AmmoParts.class)
public class ItemIIAmmoArtilleryMedium extends ItemIIAmmoBase<EntityAmmoArtilleryProjectile>
{
	public ItemIIAmmoArtilleryMedium()
	{
		super("artillery_6bCal_long", Casings.MEDIUM_ARTILLERY_6BCAL);
	}

	@Override
	public float getComponentAmount()
	{
		return 0.65f;
	}

	@Override
	public int getGunpowderNeeded()
	{
		return 650;
	}

	@Override
	public int getCoreMaterialNeeded()
	{
		return 3;
	}

	@Override
	public float getInitialMass()
	{
		return 0.85f;
	}

	@Override
	public float getDefaultVelocity()
	{
		return Ammunition.lightHowiVelocity;
	}

	@Override
	public int getCaliber()
	{
		return 6;
	}

	@Override
	public float getPenetrationDepth()
	{
		return 5;
	}

	@SideOnly(Side.CLIENT)
	@Nonnull
	@Override
	public Function<ItemIIAmmoBase<EntityAmmoArtilleryProjectile>,
			IAmmoModel<ItemIIAmmoBase<EntityAmmoArtilleryProjectile>, EntityAmmoArtilleryProjectile>> get3DModel()
	{
		return ModelAmmoProjectile::createProjectileModel;
	}

	@Override
	public float getDamage()
	{
		return 30;
	}

	@Override
	public CoreTypes[] getAllowedCoreTypes()
	{
		return new CoreTypes[]{CoreTypes.PIERCING, CoreTypes.PIERCING_SABOT, CoreTypes.SHAPED, CoreTypes.SHAPED_SABOT, CoreTypes.CANISTER};
	}

	@Override
	public FuseTypes[] getAllowedFuseTypes()
	{
		return new FuseTypes[]{FuseTypes.CONTACT, FuseTypes.TIMED, FuseTypes.PROXIMITY};
	}

	@Override
	public float getSupressionRadius()
	{
		return 6;
	}

	@Override
	public int getSuppressionPower()
	{
		return 20;
	}

	@Nonnull
	@Override
	public EntityAmmoArtilleryProjectile getAmmoEntity(World world)
	{
		return new EntityAmmoArtilleryProjectile(world);
	}
}