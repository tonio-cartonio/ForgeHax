package com.matt.forgehax.mods;

import com.matt.forgehax.Helper;
import com.matt.forgehax.events.Render2DEvent;
import com.matt.forgehax.mods.services.RainbowService;
import com.matt.forgehax.util.color.Color;
import com.matt.forgehax.util.color.Colors;
import com.matt.forgehax.util.command.Setting;
import com.matt.forgehax.util.draw.SurfaceHelper;
import com.matt.forgehax.util.mod.Category;
import com.matt.forgehax.util.mod.ToggleMod;
import com.matt.forgehax.util.mod.loader.RegisterMod;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * Created by Babbaj on 10/28/2017.
 */
//I shouldn't say it here, but this Mod is OP -Fleyr
@RegisterMod
public class CompassMod extends ToggleMod {
  
  public final Setting<Double> scale =
      getCommandStub()
          .builders()
          .<Double>newSettingBuilder()
          .name("scale")
          .description("size of the compass")
          .min(0D)
          .max(10D)
          .defaultTo(3.D)
          .build();

  public final Setting<Boolean> axis =
      getCommandStub()
          .builders()
          .<Boolean>newSettingBuilder()
          .name("axis")
          .description("Shows axis instead of cardinal directions")
          .defaultTo(false)
          .build();

  private final Setting<Color> color_north =
    getCommandStub()
      .builders()
      .newSettingColorBuilder()
      .name("color-north")
      .description("Color for North direction")
      .defaultTo(Color.of(191, 97, 106, 255))
      .build();
  private final Setting<Color> color_normal =
    getCommandStub()
      .builders()
      .newSettingColorBuilder()
      .name("color-compass")
      .description("Color for other directions")
      .defaultTo(Color.of(255, 255, 255, 255))
      .build();

    private final Setting<Boolean> rainbow =
            getCommandStub()
                    .builders()
                    .<Boolean>newSettingBuilder()
                    .name("rainbow")
                    .description("Change color")
                    .defaultTo(false)
                    .build();
  
  private static final double HALF_PI = Math.PI / 2;
  
  private enum Direction {
    N, // -Z
    W, // -X
    S, // +Z
    E  // +X
  }
  
  public CompassMod() {
    super(Category.GUI, "Compass", false, "cool compass overlay");
  }
  
  @SubscribeEvent
  public void onRender(Render2DEvent event) {
      int clr;
      if (rainbow.get()) clr = RainbowService.getRainbowColor();
      else clr = color_north.get().toBuffer();
    final double centerX = event.getScreenWidth() / 2;
    final double centerY = event.getScreenHeight() * 0.8;
    
	String dir_name = "";
    for (Direction dir : Direction.values()) {
      double rad = getPosOnCompass(dir);
	  if (axis.get()) {
		switch(dir) {
		  case N: dir_name = "-Z"; break;
		  case W: dir_name = "-X"; break;
		  case S: dir_name = "+Z"; break;
		  case E: dir_name = "+X"; break;
		}
	  } else {
		dir_name = dir.name();
	  }
      SurfaceHelper.drawTextShadowCentered(
          dir_name,
          (float) (centerX + getX(rad)),
          (float) (centerY + getY(rad)),
          dir == Direction.N ? clr : color_normal.get().toBuffer());
      
    }
    
  }
  
  private double getX(double rad) {
    return Math.sin(rad) * (scale.getAsDouble() * 10);
  }
  
  private double getY(double rad) {
    final double epicPitch = MathHelper
        .clamp(Helper.getRenderEntity().rotationPitch + 30f, -90f, 90f);
    final double pitchRadians = Math.toRadians(epicPitch); // player pitch
    return Math.cos(rad) * Math.sin(pitchRadians) * (scale.getAsDouble() * 10);
  }
  
  // return the position on the circle in radians
  private static double getPosOnCompass(Direction dir) {
    double yaw =
        Math.toRadians(
            MathHelper.wrapDegrees(Helper.getRenderEntity().rotationYaw)); // player yaw
    int index = dir.ordinal();
    return yaw + (index * HALF_PI);
  }
}
