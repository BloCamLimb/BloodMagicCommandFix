package bloodmagicfix;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.Logger;

import WayofTime.bloodmagic.BloodMagic;
import bloodmagicfix.command.BMCommand;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = BMFix.MODID, name = BMFix.NAME, version = BMFix.VERSION, dependencies = BloodMagic.DEPEND + "required-after:bloodmagic@[1.12.2-2.4.1-103,)", acceptedMinecraftVersions = "[1.12.2]", acceptableRemoteVersions = "*")

public class BMFix {
	
	public static final String MODID = "bloodmagicfix";
	public static final String NAME = "BloodMagic Command Fix";
	public static final String VERSION = "1.12.2-r3";
	
	@Instance(MODID)
	public static BMFix instance;
	
	public static Logger logger = (Logger) LogManager.getLogger("BMFix");
	
	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new BMCommand());
	}

}
