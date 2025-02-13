package me.simplicitee.project.addons.ability.fire;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.LightningAbility;
import com.projectkorra.projectkorra.attribute.Attribute;
import com.projectkorra.projectkorra.util.DamageHandler;
import me.simplicitee.project.addons.ProjectAddons;
import me.simplicitee.project.addons.Util;
import me.simplicitee.project.addons.util.SoundEffect;
import me.simplicitee.project.addons.util.versionadapter.PotionEffectAdapter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Electrify extends LightningAbility implements AddonAbility {
	
	private static final BlockFace[] faces = {BlockFace.UP, BlockFace.DOWN, BlockFace.NORTH, BlockFace.SOUTH, BlockFace.WEST, BlockFace.EAST};
	
	private static Set<Block> electrified = new HashSet<>();
	
	@Attribute(Attribute.COOLDOWN)
	private long cooldown;
	@Attribute(Attribute.DURATION)
	private long duration;
	@Attribute("DamageInWater")
	private double waterdmg;
	@Attribute("Slowness")
	private int slowness;
	@Attribute("Weakness")
	private int weakness;
	
	private int spread;
	private Block block;
	private Location center;
	private List<PotionEffect> effects = new ArrayList<>();
	private SoundEffect sound;
	
	public Electrify(Player player, Block block, boolean direct) {
		this(player, block, direct, 2);
	}

	public Electrify(Player player, Block block, boolean direct, int spread) {
		super(player);
		
		if (!ProjectAddons.instance.getConfig().getStringList("Properties.MetallicBlocks").contains(block.getType().toString()) && block.getType() != Material.WATER) {
			return;
		} else if (electrified.contains(block)) {
			return;
		}
		
		electrified.add(block);
		this.block = block;
		this.center = block.getLocation().add(0.5, 0.5, 0.5);
		this.cooldown = ProjectAddons.instance.getConfig().getLong("Abilities.Fire.Electrify.Cooldown");
		this.duration = ProjectAddons.instance.getConfig().getLong("Abilities.Fire.Electrify.Duration");
		this.waterdmg = ProjectAddons.instance.getConfig().getDouble("Abilities.Fire.Electrify.DamageInWater");
		this.slowness = ProjectAddons.instance.getConfig().getInt("Abilities.Fire.Electrify.Slowness") + 1;
		this.weakness = ProjectAddons.instance.getConfig().getInt("Abilities.Fire.Electrify.Weakness") + 1;
		this.spread = spread;

		PotionEffectAdapter effectAdapter = ProjectAddons.instance.getPotionEffectAdapter();
		
		effects.add(new PotionEffect(effectAdapter.getSlownessPotionEffectType(), 10, slowness, true, false));
		effects.add(new PotionEffect(PotionEffectType.WEAKNESS, 10, weakness, true, false));
		effects.add(new PotionEffect(effectAdapter.getJumpBoostPotionEffectType(), 10, 128, true, false));
		
		sound = new SoundEffect(Sound.ENTITY_CREEPER_PRIMED, 0.3f, 0.6f, 100);
		
		if (direct) {
			bPlayer.addCooldown(this);
		}
		
		start();
	}

	@Override
	public void progress() {
		if (getStartTime() + duration <= System.currentTimeMillis()) {
			remove();
			return;
		}
		
		if (!ProjectAddons.instance.getConfig().getStringList("Properties.MetallicBlocks").contains(block.getType().toString()) && block.getType() != Material.WATER) {
			remove();
			return;
		}

		if (spread > 0) {
			for (BlockFace face : faces) {
				Block b = block.getRelative(face);
				new Electrify(player, b, false, spread - 1);
			}
			spread = 0;
		}
		
		for (Entity e : GeneralMethods.getEntitiesAroundPoint(center, 1)) {
			if (e instanceof LivingEntity) {
				if (block.getType() == Material.WATER && e.getLocation().getBlock().equals(block)) {
					DamageHandler.damageEntity(e, waterdmg, this);
				} else if (!e.getLocation().getBlock().equals(block.getRelative(BlockFace.UP))) {
					continue;
				}
				
				LivingEntity living = (LivingEntity) e;
				
				if (e instanceof Player) {
					BendingPlayer bp = BendingPlayer.getBendingPlayer((Player) e);
					if (bp != null && bp.canLightningbend()) {
						continue;
					}
				}
				
				living.addPotionEffects(effects);
			}
		}
		
		Util.playLightningParticles(center, 1, 0.5, 0.5, 0.5);
		Util.emitFireLight(center);
		sound.play(center);
	}
	
	@Override
	public void remove() {
		super.remove();
		
		new BukkitRunnable() {

			@Override
			public void run() {
				electrified.remove(block);
			}
		}.runTaskLater(ProjectAddons.instance, 80);
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public String getName() {
		return "Electrify";
	}

	@Override
	public Location getLocation() {
		return center;
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

	@Override
	public boolean isEnabled() {
		return ProjectAddons.instance.getConfig().getBoolean("Abilities.Fire.Electrify.Enabled");
	}
	
	@Override
	public String getDescription() {
		return "Electrify water and metallic blocks to slow and weaken entities! Entities in electrified water will also take damage! Lightningbenders are immune to the slowness and weakness, but still take damage in water.";
	}
	
	@Override
	public String getInstructions() {
		return "Right click a block!";
	}
}
