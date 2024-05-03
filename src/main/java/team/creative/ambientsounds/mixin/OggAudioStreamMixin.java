package team.creative.ambientsounds.mixin;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import javax.sound.sampled.AudioFormat;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import com.jcraft.jogg.Packet;
import com.jcraft.jogg.Page;

import net.minecraft.client.sounds.JOrbisAudioStream;
import net.minecraft.resources.ResourceLocation;
import team.creative.ambientsounds.sound.OggAudioStreamExtended;

@Mixin(JOrbisAudioStream.class)
public abstract class OggAudioStreamMixin implements OggAudioStreamExtended {
    
    private static final Random RANDOM = new Random();
    
    @Shadow
    @Final
    private AudioFormat audioFormat;
    
    @Shadow
    @Final
    private InputStream input;
    
    @Override
    public void setPositionRandomly(long length, ResourceLocation id) throws IOException {
        if (length == 0)
            return;
        int skipped = RANDOM.nextInt((int) (length - length / 4));
        input.skipNBytes(skipped);
        while (true) {
            try {
                for (int i = 0; i < 4; i++)
                    ((JOrbisAudioStream) (Object) this).readChunk(x -> {});
            } catch (IOException | IllegalStateException e) {
                try {
                    readToBuffer();
                } catch (IOException e2) {
                    break;
                }
                continue;
            }
            break;
        }
    }
    
    @Shadow
    private Packet readPacket() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Shadow
    private Page readPage() throws IOException {
        throw new UnsupportedOperationException();
    }
    
    @Shadow
    private boolean readToBuffer() throws IOException {
        throw new UnsupportedOperationException();
    }
    
}
