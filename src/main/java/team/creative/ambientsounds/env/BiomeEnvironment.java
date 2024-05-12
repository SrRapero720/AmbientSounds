package team.creative.ambientsounds.env;

import java.util.Iterator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.Position;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import team.creative.ambientsounds.AmbientEngine;
import team.creative.ambientsounds.AmbientTickHandler;
import team.creative.ambientsounds.env.BiomeEnvironment.BiomeArea;
import team.creative.ambientsounds.env.BiomeEnvironment.BiomeStats;
import team.creative.creativecore.client.CreativeCoreClient;
import team.creative.creativecore.common.util.type.list.Pair;
import team.creative.creativecore.common.util.type.list.PairList;

public class BiomeEnvironment implements Iterable<Pair<BiomeArea, BiomeStats>> {
    
    private final PairList<BiomeArea, BiomeStats> biomes = new PairList<>();
    
    public BiomeEnvironment() {}
    
    public BiomeEnvironment(AmbientEngine engine, Player player, Level level, double volume) {
        if (volume > 0.0) {
            Position eyePosition = player.getEyePosition(CreativeCoreClient.getFrameTime());
            BlockPos center = new BlockPos(eyePosition.x(), eyePosition.y(), eyePosition.z());
            MutableBlockPos pos = new MutableBlockPos();
            for (int x = -engine.biomeScanCount; x <= engine.biomeScanCount; x++) {
                for (int z = -engine.biomeScanCount; z <= engine.biomeScanCount; z++) {
                    pos.set(center.getX() + x * engine.biomeScanDistance, center.getY(), center.getZ() + z * engine.biomeScanDistance);
                    Holder<Biome> holder = level.getBiome(pos);
                    
                    float biomeVolume = (float) ((1 - Math.sqrt(center.distSqr(pos)) / (engine.biomeScanCount * engine.biomeScanDistance * 2)) * volume);
                    BiomeArea area = new BiomeArea(level, holder, pos);
                    Pair<BiomeArea, BiomeStats> before = biomes.getPair(area);
                    if (before == null)
                        biomes.add(area, new BiomeStats(biomeVolume));
                    else
                        before.value.volume = Math.max(before.value.volume, biomeVolume);
                }
            }
            
            biomes.sort((x, y) -> y.value.compareTo(x.value));
        }
    }
    
    @Override
    public Iterator<Pair<BiomeArea, BiomeStats>> iterator() {
        return biomes.iterator();
    }
    
    public static class BiomeArea {
        
        public final Holder<Biome> biome;
        public final ResourceLocation location;
        public final BlockPos pos;
        
        public BiomeArea(Level level, Holder<Biome> biome, BlockPos pos) {
            this.biome = biome;
            this.location = level.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(biome.value());
            this.pos = pos;
        }
        
        public boolean checkBiome(String[] names) {
            for (String name : names)
                if (location.getPath().matches(".*" + name.replace("*", ".*") + ".*"))
                    return true;
            return false;
        }
        
        @Override
        public boolean equals(Object object) {
            if (object instanceof BiomeArea)
                return ((BiomeArea) object).biome.equals(biome);
            return false;
        }
        
        @Override
        public int hashCode() {
            return biome.hashCode();
        }
        
    }
    
    public static class BiomeStats implements Comparable<BiomeStats> {
        
        private double volume;
        
        public BiomeStats(double volume) {
            this.volume = volume;
        }
        
        public double volume(AmbientEnvironment env, String type) {
            return volume * env.biomeTypeVolumes.getOrDefault(type, 1D);
        }
        
        @Override
        public int compareTo(BiomeStats o) {
            return Double.compare(volume, o.volume);
        }
        
        @Override
        public String toString() {
            return AmbientTickHandler.df.format(volume);
        }
    }
    
}
