package me.simplicitee.project.addons.ability.earth;

import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.MetalAbility;
import me.simplicitee.project.addons.ProjectAddons;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class ShrapnelBlast extends MetalAbility implements AddonAbility {

	private final Set<Entity> hitEntities = new HashSet<>(); // Track hit entities

	public ShrapnelBlast(Player player) {
		super(player);
		
		if (bPlayer.isOnCooldown("Shrapnel")) {
			return;
		}
		
		int shots = ProjectAddons.instance.getConfig().getInt("Abilities.Earth.Shrapnel.Blast.Shots");
		int spread = ProjectAddons.instance.getConfig().getInt("Abilities.Earth.Shrapnel.Blast.Spread");
		long cooldown = ProjectAddons.instance.getConfig().getLong("Abilities.Earth.Shrapnel.Blast.Cooldown");
		double speed = ProjectAddons.instance.getConfig().getDouble("Abilities.Earth.Shrapnel.Blast.Speed");
		
		for (int i = 0; i < shots; i++) {
			Location loc = player.getLocation().clone();
			
			int yaw = new Random().nextInt(spread/2) - spread/4;
			loc.setYaw(loc.getYaw() + yaw);
			
			int pitch = new Random().nextInt(spread/2) - spread/4;
			loc.setPitch(loc.getPitch() + pitch);
			
			new ShrapnelShot(player, loc.getDirection(), speed, hitEntities);
		}
		
		bPlayer.addCooldown("Shrapnel", cooldown);
	}

	@Override
	public long getCooldown() {
		return 0;
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public String getName() {
		return "ShrapnelBlast";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public void progress() {}
	
	@Override
	public boolean isHiddenAbility() {
		return true;
	}

	@Override
	public void load() {}

	@Override
	public void stop() {}

	@Override
	public String getAuthor() {
		return null;
	}

	@Override
	public String getVersion() {
		return null;
	}

	@Override
	public boolean isEnabled() {
		return ProjectAddons.instance.getConfig().getBoolean("Abilities.Earth.Shrapnel.Enabled");
	}
}
