package xyz.breadloaf.lazyvertexdebug;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class VertexBufferDebug implements ModInitializer {
    public static Logger logger = LogManager.getLogger("vertexdebug");
    public static String nagText = "Fabric Lazy Vertex Buffer Debugging Mod is in use, this may cause issues with other mods, this is only for use in debugging issues, this should not be used in production";

    //2,000,000 should be enough to track this right?
    public static HashMap<Integer,Object> requested_buffers = new HashMap<>(2000000);
    public static HashMap<Integer,Object> allocated_buffers = new HashMap<>(2000000);

    @Override
    public void onInitialize() {
        Thread warnThread = new Thread(() -> {
            while (MinecraftClient.getInstance().isRunning()) {
                MinecraftClient.getInstance().execute(() -> {
                    if (MinecraftClient.getInstance().world != null && MinecraftClient.getInstance().inGameHud != null) {
                        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.of(nagText));
                        logger.warn(nagText);
                    }
                });
                try {
                    for (int i = 0; i < 60; i ++) {
                        Thread.sleep(1000);
                        if (MinecraftClient.getInstance().isRunning()) {
                            return;
                        }
                    }
                } catch (InterruptedException e) {
                    //ignore
                }
            }
        });
        warnThread.setDaemon(true);
        warnThread.start();
    }
}
