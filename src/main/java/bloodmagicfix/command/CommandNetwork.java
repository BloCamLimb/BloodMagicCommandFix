package bloodmagicfix.command;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import WayofTime.bloodmagic.core.data.SoulNetwork;
import WayofTime.bloodmagic.core.data.SoulTicket;
import WayofTime.bloodmagic.util.Utils;
import WayofTime.bloodmagic.util.helper.NetworkHelper;
import WayofTime.bloodmagic.util.helper.PlayerHelper;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandNotFoundException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public class CommandNetwork implements ISubCommand {
	
	private static Map<String, ISubCommand> commands = new Object2ObjectOpenHashMap<>();
	
	public CommandNetwork() {
		
		BMCommand.addSubCommand(new Add(), commands);
		BMCommand.addSubCommand(new Set(), commands);
		BMCommand.addSubCommand(new Remove(), commands);
		BMCommand.addSubCommand(new Max(), commands);
		
	}
	
	public String getHelp() {
		
		return "/bloodmagic network [add|remove|set|max] player [amount]";
	}

	@Override
	public String getName() {
		
		return "network";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		
		if(args.length < 2) {
			
			sender.sendMessage(new TextComponentString(getHelp()));
			return;
		}
		
		ISubCommand command = commands.get(args[1]);
		
		if (command != null) {
			
			if (sender.canUseCommand(command.getPermissionLevel(), "bmx " + command.getName()) || (sender instanceof EntityPlayerMP && command.getPermissionLevel() <= 0)) {
				
				command.execute(server, sender, args);
				
				return;
			}
			
			throw new CommandException("commands.generic.permission");
		}
		
		throw new CommandNotFoundException();
		
	}

	@Override
	public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {
		
		if(args.length == 2)
			return CommandBase.getListOfStringsMatchingLastWord(args, commands.keySet());
		else if(args.length == 3)
			return CommandBase.getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
		
		return Collections.<String>emptyList();
	}
	
	abstract class Network implements ISubCommand {
		
		public EntityPlayerMP player;
        public SoulNetwork network;
        public String uuid;
        
        public int getAmount(MinecraftServer server, ICommandSender sender, String[] args) {
        	
            int amount;
            
            if (Utils.isInteger(args[3]))
                amount = Integer.parseInt(args[3]);
            else
                return 0;
            
            if (amount < 0)
                return 0;
            
            return amount;
        }
        
        @Override
        public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        	
            this.player = CommandBase.getPlayer(server, sender, args[2]);
            this.uuid = PlayerHelper.getUUIDFromPlayer(player).toString();
            this.network = NetworkHelper.getSoulNetwork(uuid);
        }
       
	}
	
	class Add extends Network {

		@Override
		public String getName() {
			
			return "add";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			
			if (args.length < 4) {
                sender.sendMessage(new TextComponentString(getHelp()));
                return;
            }
			
			super.execute(server, sender, args);
			
			sender.sendMessage(new TextComponentTranslation("commands.bloodmagic.network.add.success", network.add(SoulTicket.command(sender, getName(), getAmount(server, sender, args)), NetworkHelper.getMaximumForTier(network.getOrbTier())), player.getDisplayName().getFormattedText()));
			
		}

		@Override
		public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {
			
			return Collections.<String>emptyList();
		}
		
	}
	
	class Set extends Network {
		
		@Override
		public String getName() {
			
			return "set";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			
			if (args.length < 4) {
                sender.sendMessage(new TextComponentString(getHelp()));
                return;
            }
			
			super.execute(server, sender, args);
			
			int amount = Math.min(getAmount(server, sender, args), NetworkHelper.getMaximumForTier(network.getOrbTier()));
			
			network.setCurrentEssence(amount);
			
            sender.sendMessage(new TextComponentTranslation("commands.bloodmagic.network.set.success", player.getDisplayName().getFormattedText(), amount));
        }

		@Override
		public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {
			
			return Collections.<String>emptyList();
		}
		
	}
	
	class Remove extends Network {
		
		@Override
		public String getName() {
			
			return "remove";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			
			if (args.length < 4) {
                sender.sendMessage(new TextComponentString(getHelp()));
                return;
            }
			
			super.execute(server, sender, args);
        	
        	int amount = getAmount(server, sender, args);

            int currE = network.getCurrentEssence();
            
            if (amount > currE) {
                sender.sendMessage(new TextComponentTranslation("commands.bloodmagic.network.syphon.amountTooHigh"));
                if (currE == 0)
                    return;
                amount = Math.min(amount, currE);
            }
            network.syphonAndDamage(player, SoulTicket.command(sender, this.getName(), amount));
            int newE = network.getCurrentEssence();
            sender.sendMessage(new TextComponentTranslation("commands.bloodmagic.network.syphon.success", currE - newE, player.getDisplayName().getFormattedText()));
        }

		@Override
		public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {
			
			return Collections.<String>emptyList();
		}
		
	}
	
	class Max extends Network {
		
		@Override
		public String getName() {
			
			return "max";
		}

		@Override
		public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
			
			if (args.length < 2) {
                sender.sendMessage(new TextComponentString(getHelp()));
                return;
            }
			
			super.execute(server, sender, args);
			
			network.setCurrentEssence(NetworkHelper.getMaximumForTier(network.getOrbTier()));
        	
			sender.sendMessage(new TextComponentTranslation("commands.bloodmagic.network.fill.success", player.getDisplayName().getFormattedText()));
        }

		@Override
		public List<String> addTabCompletionOptions(MinecraftServer server, ICommandSender sender, String[] args) {
			
			return Collections.<String>emptyList();
		}
		
	}

}
