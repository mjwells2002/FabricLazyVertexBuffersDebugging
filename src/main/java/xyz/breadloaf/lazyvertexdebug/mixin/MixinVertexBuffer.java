package xyz.breadloaf.lazyvertexdebug.mixin;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gl.VertexBuffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.breadloaf.lazyvertexdebug.VertexBufferDebug;


@Mixin(value = VertexBuffer.class)
public class MixinVertexBuffer {
    @Shadow
    private int vertexBufferId;

    @Shadow private int indexBufferId;

    @Shadow private int vertexArrayId;

    private boolean isCreated = false;

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glGenBuffers()I",remap = false))
    private int preventGenBuffers(){
        return -2;
    }
    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/platform/GlStateManager;_glGenVertexArrays()I",remap = false))
    private int preventGenVertexArrays(){
        return -2;
    }

    @Inject(method = "<init>", at = @At(value = "RETURN"))
    private void trackRequestBuffers(CallbackInfo ci) {
        VertexBufferDebug.requested_buffers.put(this.hashCode(),null);
    }

    @Inject(method = "bind", at = @At(value = "HEAD"))
    private void createBeforeBind(CallbackInfo ci) {
        RenderSystem.assertOnRenderThread();
        if (!isCreated) {
            VertexBufferDebug.allocated_buffers.put(this.hashCode(),null);
            this.vertexBufferId = GlStateManager._glGenBuffers();
            this.indexBufferId = GlStateManager._glGenBuffers();
            this.vertexArrayId = GlStateManager._glGenVertexArrays();
            isCreated = true;
        }
    }

    @Inject(method = "close", at = @At(value = "HEAD"), cancellable = true)
    private void resetOnClose(CallbackInfo ci) {
        VertexBufferDebug.requested_buffers.remove(this.hashCode());
        VertexBufferDebug.allocated_buffers.remove(this.hashCode());
        if (isCreated) {
            isCreated = false;
        } else {
            ci.cancel();
        }
    }
}