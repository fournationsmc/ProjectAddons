package me.simplicitee.project.addons.ability.air;

import com.projectkorra.projectkorra.Element;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.FlightAbility;
import com.projectkorra.projectkorra.ability.PassiveAbility;
import me.simplicitee.project.addons.ProjectAddons;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class FlightPassive extends FlightAbility implements AddonAbility, PassiveAbility {

	private boolean toggled = false;
	private boolean active = false;
	private float original = 0.8f;

	private float speed;
	private final float flySpeed;
	private final float startSpeed;
	private final float maxSpeed;
	private final float acceleration;
	
	public FlightPassive(Player player) {
		super(player);
		
		flySpeed = (float) ProjectAddons.instance.getConfig().getDouble("Passives.Air.Flying.FlySpeed");
		speed = startSpeed = (float) ProjectAddons.instance.getConfig().getDouble("Passives.Air.Flying.Glide.StartSpeed");
		maxSpeed = (float) ProjectAddons.instance.getConfig().getDouble("Passives.Air.Flying.Glide.MaxSpeed");
		acceleration = (float) ProjectAddons.instance.getConfig().getDouble("Passives.Air.Flying.Acceleration");
		
		flightHandler.createInstance(player, "FlightPassive");
	}

	@Override
	public void progress() {
		if (!bPlayer.isElementToggled(Element.AIR) || !bPlayer.isToggled() || player.getLocation().getBlock().isLiquid()) {
			clear();
			return;
		}

		if ((!player.isFlying() && !player.getCanPickupItems()) || player.getGameMode() == GameMode.CREATIVE) {
			player.setCanPickupItems(true);
		}

		player.setAllowFlight(true);

		if (!active) return;

		if (player.getCanPickupItems()) player.setCanPickupItems(false);

		if (!toggled) return;

		player.setGliding(true);

		if (player.isSneaking() && player.getFlySpeed() < maxSpeed) {
			speed = speed + acceleration;
			if (speed > maxSpeed) {
				speed = maxSpeed;
			}
		}

		player.setVelocity(player.getEyeLocation().getDirection().multiply(speed));
	}
	
	private void clear() {
		active = false;
		toggled = false;
		player.setFlying(false);
		player.setGliding(false);
		player.setAllowFlight(false);
		player.setCanPickupItems(true);
	}
	
	@Override
	public void remove() {
		super.remove();
		clear();
		flightHandler.removeInstance(player, "FlightPassive");
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public boolean isHarmlessAbility() {
		return true;
	}

	@Override
	public long getCooldown() {
		return 0;
	}

	@Override
	public String getName() {
		return "Flying";
	}

	@Override
	public Location getLocation() {
		return player.getLocation();
	}

	@Override
	public boolean isInstantiable() {
		return isEnabled();
	}

	@Override
	public boolean isProgressable() {
		return true;
	}

	@Override
	public void load() {}

	@Override
	public void stop() {}

	@Override
	public String getAuthor() {
		return "Simplicitee";
	}

	@Override
	public String getVersion() {
		return ProjectAddons.instance.version();
	}

	public void toggleGlide() {
		this.toggled = !toggled;
		if (!toggled) {
			player.setGliding(false);
			player.setFlying(true);
			speed = startSpeed;
		} else {
			player.setGliding(true);
			player.setFlying(false);
		}
	}
	
	public void fly(boolean flying) {
		if (flying) {
			active = true;
			original = player.getFlySpeed();
			player.setFlySpeed(flySpeed);
			player.setCanPickupItems(false);
		} else {
			player.setFlySpeed(original);
			player.setCanPickupItems(true);
			active = false;
		}
		toggled = false;
	}
	
	public boolean isActive() {
		return active;
	}
	
	public boolean isGliding() {
		return toggled;
	}
	
	@Override
	public boolean isEnabled() {
		return ProjectAddons.instance.getConfig().getBoolean("Passives.Air.Flying.Enabled");
	}
	
	@Override
	public String getDescription() {
		return "A very rare ability for airbenders is being able to fly freely, without the need of any glider. The only airbenders known to have this ability were Guru Laghima and Zaheer.";
	}
	
	@Override
	public String getInstructions() {
		return "Use double jump to toggle flight, offhand swap to toggle between gliding and creative flight, and sneak while gliding to accelerate!";
	}
}
