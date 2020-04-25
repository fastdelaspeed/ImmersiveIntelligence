package pl.pabilo8.immersiveintelligence.client.manual;

import blusunrize.immersiveengineering.api.ManualHelper;
import blusunrize.immersiveengineering.api.ManualPageMultiblock;
import blusunrize.lib.manual.ManualPages;
import net.minecraft.item.ItemStack;
import pl.pabilo8.immersiveintelligence.api.Utils;
import pl.pabilo8.immersiveintelligence.client.ClientProxy;
import pl.pabilo8.immersiveintelligence.common.CommonProxy;
import pl.pabilo8.immersiveintelligence.common.blocks.multiblocks.metal.tileentities.first.MultiblockPacker;
import pl.pabilo8.immersiveintelligence.common.blocks.multiblocks.wooden.MultiblockSkyCratePost;
import pl.pabilo8.immersiveintelligence.common.blocks.multiblocks.wooden.MultiblockSkyCrateStation;
import pl.pabilo8.immersiveintelligence.common.blocks.types.IIBlockTypes_Connector;

/**
 * Created by Pabilo8 on 18-01-2020.
 */
public class IIManualLogistics extends IIManual
{
	public static IIManualLogistics INSTANCE = new IIManualLogistics();

	@Override
	public String getCategory()
	{
		return ClientProxy.CAT_LOGISTICS;
	}

	@Override
	public void addPages()
	{
		ManualHelper.addEntry("logistics", getCategory(),
				new ManualPages.Text(ManualHelper.getManual(), "logistics0")
		);

		ItemStack inserter0, inserter1, inserter2;
		inserter0 = new ItemStack(CommonProxy.block_data_connector, 1, IIBlockTypes_Connector.INSERTER.getMeta());
		inserter1 = new ItemStack(CommonProxy.block_data_connector, 1, IIBlockTypes_Connector.ADVANCED_INSERTER.getMeta());
		inserter2 = new ItemStack(CommonProxy.block_data_connector, 1, IIBlockTypes_Connector.FLUID_INSERTER.getMeta());

		ManualHelper.addEntry("inserters", getCategory(),
				new ManualPages.ItemDisplay(ManualHelper.getManual(), "inserters0", inserter0, inserter1, inserter2),
				new ManualPages.Crafting(ManualHelper.getManual(), "inserters_basic", inserter0),
				new ManualPages.Text(ManualHelper.getManual(), "inserters_basic_desc"),
				new ManualPages.Crafting(ManualHelper.getManual(), "inserters_advanced", inserter1),
				new ManualPages.Text(ManualHelper.getManual(), "inserters_advanced_desc"),
				new ManualPages.Crafting(ManualHelper.getManual(), "inserters_fluid", inserter2),
				new ManualPages.Text(ManualHelper.getManual(), "inserters_fluid_desc")
		);

		ManualHelper.addEntry("packer", getCategory(),
				new ManualPageMultiblock(ManualHelper.getManual(), "packer0", MultiblockPacker.instance),
				new ManualPages.Text(ManualHelper.getManual(), "packer1")
		);

		ManualHelper.addEntry("skycrates", getCategory(),
				new ManualPages.Text(ManualHelper.getManual(), "skycrates0"),
				new ManualPageMultiblock(ManualHelper.getManual(), "skycrates1", MultiblockSkyCrateStation.instance),
				new ManualPages.Text(ManualHelper.getManual(), "skycrates2"),
				new ManualPageMultiblock(ManualHelper.getManual(), "skycrates3", MultiblockSkyCratePost.instance),
				new ManualPages.ItemDisplay(ManualHelper.getManual(), "skycrates4",
						Utils.getItemWithMetaName(CommonProxy.item_minecart, "wooden_crate"),
						Utils.getItemWithMetaName(CommonProxy.item_minecart, "reinforced_crate"),
						Utils.getItemWithMetaName(CommonProxy.item_minecart, "steel_crate"),
						Utils.getItemWithMetaName(CommonProxy.item_minecart, "wooden_barrel"),
						Utils.getItemWithMetaName(CommonProxy.item_minecart, "metal_barrel")
				),
				new ManualPages.CraftingMulti(ManualHelper.getManual(), "skycrates5",
						Utils.getItemWithMetaName(CommonProxy.item_skycrate_mount, "mechanical"),
						Utils.getItemWithMetaName(CommonProxy.item_skycrate_mount, "electric"))
		);


	}
}
