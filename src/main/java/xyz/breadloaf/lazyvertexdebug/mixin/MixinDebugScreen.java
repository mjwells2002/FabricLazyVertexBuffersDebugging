package xyz.breadloaf.lazyvertexdebug.mixin;

import net.minecraft.client.gui.hud.DebugHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

import static xyz.breadloaf.lazyvertexdebug.VertexBufferDebug.allocated_buffers;
import static xyz.breadloaf.lazyvertexdebug.VertexBufferDebug.requested_buffers;

@Mixin(DebugHud.class)
public class MixinDebugScreen {
    @Inject(method = "getLeftText",at = @At("RETURN"))
    private void addDebugText(CallbackInfoReturnable<List<String>> cir) {
        cir.getReturnValue().add(String.format("VertexBuffer Statistics %d/%d == %d/%d glGenBuffers calls (requested/provided)",requested_buffers.size(),allocated_buffers.size(),requested_buffers.size()*2,allocated_buffers.size()*2));
    }

}
