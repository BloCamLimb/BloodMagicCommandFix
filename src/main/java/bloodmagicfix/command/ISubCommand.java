package bloodmagicfix.command;

import java.util.Collections;
import java.util.List;

import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public interface ISubCommand {
	
	String getName();
	
	default int getPermissionLevel() {
		
		return 2;
	};
	
	void execute(MinecraftServer server, ICommandSender sender, String[] arguments) throws CommandException;

	List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args);

}
