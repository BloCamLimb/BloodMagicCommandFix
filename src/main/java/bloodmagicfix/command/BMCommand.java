package bloodmagicfix.command;

import java.util.List;
import java.util.Map;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class BMCommand extends CommandBase {
	
	private static Map<String, ISubCommand> commands = new Object2ObjectOpenHashMap<>();
	
	public BMCommand() {
		
		addSubCommand(new CommandNetwork(), commands);
		
	}
	
	public static boolean addSubCommand(ISubCommand subCommand, Map<String, ISubCommand> commands) {

		if (!commands.containsKey(subCommand.getName())) {
			
			commands.put(subCommand.getName(), subCommand);
			
			return true;
		}
		
		return false;
	}
	
	public static boolean getCommandExists(String command) {

		return commands.containsKey(command);
	}
	
	public static boolean canUseCommand(ICommandSender sender, int permission, String name) {

		return getCommandExists(name) && (sender.canUseCommand(permission, "bmx " + name) || (sender instanceof EntityPlayerMP && permission <= 0));
	}

	@Override
	public String getName() {
		
		return "bloodmagicx";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		
		return "/" + getName() + " help";
	}
	
	@Override
	public int getRequiredPermissionLevel() {
		
		return 2;
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, BlockPos targetPos) {
		
		if (args.length == 1) {
			
			return getListOfStringsMatchingLastWord(args, commands.keySet());
			
		} else if (commands.containsKey(args[0])) {
			
			return commands.get(args[0]).addTabCompletionOptions(server, sender, args);
		}
		
		return null;
		
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		if (args.length < 1) {
			args = new String[] {"help"};
		}
		
		ISubCommand command = commands.get(args[0]);
		
		if (command != null) {
			
			if (canUseCommand(sender, command.getPermissionLevel(), command.getName())) {
				
				command.execute(server, sender, args);
				
				return;
			}
			
			throw new CommandException("commands.generic.permission");
		}
		
		throw new CommandNotFoundException();
		
	}

}
