package com.github.pocketkid2.deathswap;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import com.github.pocketkid2.deathswap.DeathSwapTimer.Job;

public class DeathSwapGame {

	private DeathSwapPlugin plugin;

	public enum Status {
		WAITING, STARTING, IN_GAME;
	}

	private List<Player> players;
	private Status status;
	private List<Player> votes;
	private BukkitTask task;

	public DeathSwapGame(DeathSwapPlugin p) {
		plugin = p;
		players = new ArrayList<Player>();
		status = Status.WAITING;
		task = null;
		votes = new ArrayList<Player>();
	}

	public void cancelTask() {
		if (task != null) {
			task.cancel();
		}
	}

	public void setTask(BukkitTask t) {
		if ((task != null) && !task.isCancelled()) {
			task.cancel();
		}
		task = t;
	}

	public void broadcast(String message) {
		for (Player p : players) {
			p.sendMessage(message);
		}
	}

	public void broadcastExcept(Player player, String message) {
		for (Player p : players) {
			if (p != player) {
				p.sendMessage(message);
			}
		}
	}

	public List<Player> getPlayers() {
		return players;
	}

	public boolean addPlayer(Player p) {
		if (players.contains(p))
			return false;
		players.add(p);
		return true;
	}

	public boolean removePlayer(Player p) {
		if (!players.contains(p))
			return false;
		players.remove(p);
		return true;
	}

	public boolean isPlayer(Player p) {
		return players.contains(p);
	}

	public void clearPlayers() {
		players.clear();
	}

	public Status getStatus() {
		return status;
	}

	public int getVotes() {
		return votes.size();
	}

	public boolean playerVote(Player player) {
		if (votes.contains(player))
			return false;
		votes.add(player);
		return true;
	}

	public void clearVotes() {
		votes.clear();
	}

	public void processVote() {
		if (players.size() >= 2) {
			if (votes.size() == players.size()) {
				broadcast(ChatColor.AQUA + "All players are ready, game now starting!");
				status = Status.STARTING;
				plugin.broadcast(ChatColor.AQUA + "The Death Swap game is starting!");
				new DeathSwapTimer(plugin, 10, "The game will start in %d %s", Job.START_GAME).runTaskTimer(plugin, 20, 20);
				clearVotes();
			}
		} else {
			players.get(0).sendMessage(ChatColor.RED + "The game cannot start until there are at least two players!");
		}
	}

	public void processPlayerRemoved() {
		if ((getStatus() == Status.IN_GAME) && (players.size() < 2)) {
			Player winner = players.get(0);
			winner.sendMessage(ChatColor.GREEN + "You won Death Swap!");
			plugin.broadcast(ChatColor.GREEN + winner.getDisplayName() + " won Death Swap!");
			winner.teleport(plugin.getLobby());
			clearPlayers();
			cancelTask();
		}
	}
}
