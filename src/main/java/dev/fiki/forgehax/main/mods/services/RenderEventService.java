package dev.fiki.forgehax.main.mods.services;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import dev.fiki.forgehax.common.events.render.ProjectionViewMatrixSetupEvent;
import dev.fiki.forgehax.main.events.Render2DEvent;
import dev.fiki.forgehax.main.events.RenderEvent;
import dev.fiki.forgehax.main.Common;
import dev.fiki.forgehax.main.util.entity.EntityUtils;
import dev.fiki.forgehax.main.util.math.VectorUtils;
import dev.fiki.forgehax.main.util.mod.ServiceMod;
import dev.fiki.forgehax.main.util.mod.loader.RegisterMod;
import dev.fiki.forgehax.main.util.tesselation.GeometryTessellator;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import static dev.fiki.forgehax.main.Common.getGameRenderer;

/**
 * Created on 6/14/2017 by fr1kin
 */
@RegisterMod
public class RenderEventService extends ServiceMod {

  private static final GeometryTessellator TESSELLATOR = new GeometryTessellator();

  public RenderEventService() {
    super("RenderEventService");
  }

  @SubscribeEvent
  public void onProjectionViewMatrixSetup(ProjectionViewMatrixSetupEvent event) {
    VectorUtils.setProjectionViewMatrix(event.getProjectionMatrix(), event.getMatrixStack().getLast().getPositionMatrix());
  }

  @SubscribeEvent
  public void onRenderWorld(RenderWorldLastEvent event) {
    RenderSystem.pushMatrix();
    RenderSystem.disableTexture();
    RenderSystem.enableBlend();
    RenderSystem.disableAlphaTest();
    RenderSystem.blendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0);
    RenderSystem.shadeModel(GL11.GL_SMOOTH);
    RenderSystem.disableDepthTest();

    RenderSystem.lineWidth(1.f);

    Vec3d renderPos = EntityUtils.getInterpolatedPos(Common.getLocalPlayer(), event.getPartialTicks());

    RenderEvent e = new RenderEvent(TESSELLATOR, renderPos, event.getPartialTicks());
    e.resetTranslation();
    MinecraftForge.EVENT_BUS.post(e);

    RenderSystem.lineWidth(1.f);

    RenderSystem.shadeModel(GL11.GL_FLAT);
    RenderSystem.disableBlend();
    RenderSystem.enableAlphaTest();
    RenderSystem.enableTexture();
    RenderSystem.enableDepthTest();
    RenderSystem.enableCull();
    RenderSystem.popMatrix();
  }

  @SubscribeEvent(priority = EventPriority.LOW)
  public void onRenderGameOverlayEvent(final RenderGameOverlayEvent.Text event) {
    if (event.getType().equals(RenderGameOverlayEvent.ElementType.TEXT)) {
      MinecraftForge.EVENT_BUS.post(new Render2DEvent(event.getPartialTicks()));
      RenderSystem.color4f(1.f, 1.f, 1.f, 1.f); // reset color
    }
  }
}
