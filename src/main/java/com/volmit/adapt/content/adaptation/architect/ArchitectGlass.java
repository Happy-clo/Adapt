/*------------------------------------------------------------------------------
 -   Adapt is a Skill/Integration plugin  for Minecraft Bukkit Servers
 -   Copyright (c) 2022 Arcane Arts (Volmit Software)
 -
 -   This program is free software: you can redistribute it and/or modify
 -   it under the terms of the GNU General Public License as published by
 -   the Free Software Foundation, either version 3 of the License, or
 -   (at your option) any later version.
 -
 -   This program is distributed in the hope that it will be useful,
 -   but WITHOUT ANY WARRANTY; without even the implied warranty of
 -   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 -   GNU General Public License for more details.
 -
 -   You should have received a copy of the GNU General Public License
 -   along with this program.  If not, see <https://www.gnu.org/licenses/>.
 -----------------------------------------------------------------------------*/

package com.volmit.adapt.content.adaptation.architect;

import com.volmit.adapt.Adapt;
import com.volmit.adapt.api.adaptation.SimpleAdaptation;
import com.volmit.adapt.util.C;
import com.volmit.adapt.util.Element;
import com.volmit.adapt.util.J;
import lombok.NoArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCanBuildEvent;
import org.bukkit.inventory.ItemStack;


public class ArchitectGlass extends SimpleAdaptation<ArchitectGlass.Config> {
    public ArchitectGlass() {
        super("architect-glass");
        registerConfiguration(ArchitectGlass.Config.class);
        setDescription(Adapt.dLocalize("architect", "glass", "description"));
        setDisplayName(Adapt.dLocalize("architect", "glass", "name"));
        setIcon(Material.GLASS);
        setInterval(25000);
        setBaseCost(getConfig().baseCost);
        setMaxLevel(getConfig().maxLevel);
        setInitialCost(getConfig().initialCost);
        setCostFactor(getConfig().costFactor);
    }

    @Override
    public void addStats(int level, Element v) {
        v.addLore(C.GREEN + Adapt.dLocalize("architect", "glass", "lore1"));
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void on(BlockBreakEvent e) {
        if (e.isCancelled()) {
            return;
        }
        Player p = e.getPlayer();
        if (hasAdaptation(p) && (p.getInventory().getItemInMainHand().getType() == Material.AIR || !isTool(p.getInventory().getItemInMainHand())) && !e.isCancelled()) {
            BlockCanBuildEvent can = new BlockCanBuildEvent(e.getBlock(), p, e.getBlock().getBlockData(), true);
            Bukkit.getServer().getPluginManager().callEvent(can);

            if (!can.isBuildable()) {
                return;
            }

            if (e.getBlock().getType().toString().contains("GLASS") && !e.getBlock().getType().toString().contains("TINTED_GLASS")) {
                e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), new ItemStack(e.getBlock().getType(), 1));
                e.getBlock().getWorld().playSound(e.getBlock().getLocation(), Sound.BLOCK_LARGE_AMETHYST_BUD_BREAK, 1.0f, 1.0f);
                if (getConfig().showParticles) {

                    e.getBlock().getWorld().spawnParticle(Particle.SCRAPE, e.getBlock().getLocation(), 1);
                    J.a(() -> vfxSingleCubeOutline(e.getBlock(), Particle.REVERSE_PORTAL));
                }
                e.getBlock().breakNaturally();
                xp(p, 3);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return getConfig().enabled;
    }


    @Override
    public void onTick() {
    }

    @Override
    public boolean isPermanent() {
        return getConfig().permanent;
    }

    @NoArgsConstructor
    protected static class Config {
        boolean permanent = true;
        boolean enabled = true;
        boolean showParticles = true;
        int baseCost = 3;
        int maxLevel = 1;
        int initialCost = 0;
        double costFactor = 5;
    }
}
