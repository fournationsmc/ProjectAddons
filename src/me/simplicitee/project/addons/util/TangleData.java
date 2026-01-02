package me.simplicitee.project.addons.util;

import com.projectkorra.projectkorra.util.MovementHandler;
import com.projectkorra.projectkorra.util.TempBlock;
import org.bukkit.entity.Player;

public class TangleData {

    private final Player caster;
    private double threshold;
    private final MovementHandler movementHandler;
    private final TempBlock tempBlock;

    public TangleData(Player caster, double threshold, MovementHandler mh, TempBlock tb) {
        this.caster = caster;
        this.threshold = threshold;
        this.movementHandler = mh;
        this.tempBlock = tb;
    }

    public Player getCaster() { return caster; }
    public double getThreshold() { return threshold; }
    public void setThreshold(double threshold) { this.threshold = threshold; }
    public MovementHandler getMovementHandler() { return movementHandler; }
    public TempBlock getTempBlock() { return tempBlock; }
}
