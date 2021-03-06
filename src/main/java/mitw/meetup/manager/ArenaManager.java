package mitw.meetup.manager;

import java.io.File;

import mitw.meetup.UHCMeetup;
import mitw.meetup.border.BorderBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import mitw.meetup.border.worldborder.Config;
import mitw.meetup.border.worldborder.CoordXZ;
import mitw.meetup.border.worldborder.WorldFillTask;
import mitw.meetup.enums.GameStatus;
import mitw.meetup.player.UHCTeam;
import mitw.meetup.util.SitUtil;
import net.md_5.bungee.api.ChatColor;

public class ArenaManager {

	public static Location center = new Location(Bukkit.getWorld("world"), 0, 120, 0);

	public void sendPlayers(final Player player) {
		UHCMeetup.getInstance().getGameManager().setCleanPlayerLobby(player, GameMode.SURVIVAL);
		Location loc = null;
		if (UHCMeetup.TeamMode) {
			final UHCTeam team = UHCMeetup.getInstance().getGameManager().getProfile(player.getUniqueId()).getTeam();
			if (team != null) {
				loc = team.scatter != null ? team.scatter : UHCMeetup.getInstance().getGameManager().generateLocations();
			} else {
				Bukkit.broadcastMessage("§cERROR - Player " + player.getName() + " is not in any team! please screenshot it and send to admin!");
				loc = UHCMeetup.getInstance().getGameManager().generateLocations();
			}
		} else {
			loc = UHCMeetup.getInstance().getGameManager().generateLocations();
		}
		player.teleport(loc);
		SitUtil.spawn(player);
		return;
	}

	public void preparegameManager() {

		canWorld(true, true);

	}

	public void canWorld(final Boolean b, final Boolean b1) {
		if ((b1.booleanValue()) && (b.booleanValue())) {
			new BukkitRunnable() {
				@Override
				public void run() {
					createWorld("UHCArena");
				}

			}.runTaskLater(UHCMeetup.getInstance(), 20L);
			return;
		}
	}

	private void createWorld(final String worldName) {
		final World uhc = Bukkit.getWorld("world");
		uhc.setPVP(false);
		uhc.setTime(0L);
		uhc.setDifficulty(Difficulty.EASY);
		uhc.setGameRuleValue("doDaylightCycle", "false");
		uhc.setGameRuleValue("naturalRegeneration", "false");
		uhc.setGameRuleValue("doMobSpawning", "false");
		uhc.setSpawnLocation(0, 100, 0);
		uhc.setMonsterSpawnLimit(0);
		uhc.setAnimalSpawnLimit(0);
		Bukkit.getScheduler().runTaskLater(UHCMeetup.getInstance(), () -> {
			UHCMeetup.getInstance().getGameManager().setBorder(125);
			new BorderBuilder(uhc, 125, 4).force().start(true);
			final int fillFrequency = 1000;
			final int fillPadding = CoordXZ.chunkToBlock(13);
			final int ticks = 1;
			int repeats = 1;
			repeats = fillFrequency / 20;
			Config.fillTask = new WorldFillTask(Bukkit.getServer(), null, "world", fillPadding, repeats, ticks);
			if (Config.fillTask.valid()) {
				final int task = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(UHCMeetup.getInstance(),
						Config.fillTask, ticks, ticks);
				Config.fillTask.setTaskID(task);
			} else {
				Bukkit.getConsoleSender().sendMessage("The world map generation task failed to start.");
			}
		}, 20L);
	}

	public void unloadWorld(final World world) {
		if (world != null) {
			for (final Player p : world.getPlayers()) {
				p.teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
			}
			Bukkit.getServer().unloadWorld(world, false);
		}
	}

	public void deleteWorld(final String world) {
		final File filePath = new File(Bukkit.getWorldContainer(), world);
		deleteFiles(filePath);
	}

	public boolean deleteFiles(final File path) {
		if (path.exists()) {
			final File[] files = path.listFiles();
			File[] arrayOfFile1;
			final int j = (arrayOfFile1 = files).length;
			for (int i = 0; i < j; i++) {
				final File file = arrayOfFile1[i];
				if (file.isDirectory()) {
					deleteFiles(file);
				} else {
					file.delete();
				}
			}
		}
		return path.delete();
	}

	public void deleteFolder(final File file, final boolean bl) {
		final File[] arrfile = file.listFiles();
		boolean bl2 = true;
		if (arrfile != null) {
			final File[] arrfile2 = arrfile;
			final int n = arrfile2.length;
			int n2 = 0;
			while (n2 < n) {
				final File file2 = arrfile2[n2];
				if (file2.isDirectory()) {
					deleteFolder(file2, bl);
				} else if ((!file2.getName().contains("session.lock")) || (bl)) {
					file2.delete();
				} else {
					bl2 = false;
				}
				n2++;
			}
		}
		if (bl2) {
			file.delete();
		}
	}

	public static void finishLoad(final World uhc) {
		GameStatus.set(GameStatus.WAITING);
		UHCMeetup.getInstance().getGameManager().spawn = new Location(uhc, 0, uhc.getHighestBlockYAt(0, 0) + 1, 0);
		Bukkit.broadcastMessage(" ");
		Bukkit.broadcastMessage(ChatColor.GREEN + "MitwMeetup load world finished");
		Bukkit.broadcastMessage(" ");
		for (final Chunk c : uhc.getLoadedChunks()) {
			for (final org.bukkit.entity.Entity entity : c.getEntities()) {
				entity.remove();
			}
		}
	}

}
