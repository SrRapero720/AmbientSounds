package team.creative.ambientsounds.block;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.world.level.block.state.BlockState;

public class AmbientBlockGroup {
    
    private List<String> data = new ArrayList<>();
    private List<Predicate<BlockState>> filters;
    
    public void onClientLoad() {
        filters = new ArrayList<>();
        for (String condition : data)
            filters.add(AmbientBlockFilters.get(condition));
    }
    
    public void add(String[] data) {
        this.data.addAll(Arrays.asList(data));
    }
    
    public boolean isEmpty() {
        return filters.isEmpty();
    }
    
    public boolean is(BlockState state) {
        for (Predicate<BlockState> predicate : filters)
            if (predicate.test(state))
                return true;
        return false;
    }
    
}
