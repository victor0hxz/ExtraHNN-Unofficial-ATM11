package net.lmor.extrahnn.client;
import dev.shadowsoffire.hostilenetworks.client.item.DataModelGhostRenderer;
import dev.shadowsoffire.hostilenetworks.client.item.DataModelItemModel;
import net.lmor.extrahnn.common.item.ExtraDataModelItem;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.shadowsoffire.hostilenetworks.data.DataModel;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.placebo.dynreg.DynamicHolder;
import java.util.function.Supplier;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.item.ItemModel;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.item.ModelRenderProperties;
import net.minecraft.client.renderer.item.ItemModel.BakingContext;
import net.minecraft.client.renderer.item.ItemStackRenderState.LayerRenderState;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ResolvedModel;
import net.minecraft.client.resources.model.ResolvableModel.Resolver;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector3f;
import org.joml.Vector3fc;
import org.jspecify.annotations.Nullable;

public class ExtraDataModelItemStackRenderer implements ItemModel {
   private static final Vector3fc[] EXTENT_ARRAY = new Vector3fc[]{new Vector3f(0.0F, 0.0F, 0.0F), new Vector3f(1.0F, 1.75F, 1.0F)};
   private static final Supplier<Vector3fc[]> EXTENTS = () -> EXTENT_ARRAY;
   private final ModelRenderProperties properties;
   private final Matrix4fc transformation;

   public ExtraDataModelItemStackRenderer(ModelRenderProperties properties, Matrix4fc transformation) {
      this.properties = properties;
      this.transformation = new Matrix4f(transformation);
   }

   public void update(
      ItemStackRenderState output,
      ItemStack item,
      ItemModelResolver resolver,
      ItemDisplayContext displayContext,
      @Nullable ClientLevel level,
      @Nullable ItemOwner owner,
      int seed
   ) {
      output.appendModelIdentityElement(this);
      int index = 0;
      for (DynamicHolder<DataModel> model : ExtraDataModelItem.getStoredModels(item)) {
          LayerRenderState layer = output.newLayer();
          this.properties.applyToLayer(layer, displayContext);
          layer.setExtents(EXTENTS);
          layer.setLocalTransform(new Matrix4f(this.transformation).translate(index % 2 == 0 ? -0.22F : 0.22F, 0.10F, index / 2 == 0 ? -0.22F : 0.22F).scale(0.65F));
          layer.setupSpecialModel(DataModelGhostRenderer.INSTANCE, new DataModelItemModel.Argument(model, displayContext));
          output.appendModelIdentityElement(model.getId());
          index++;
      }
      output.appendModelIdentityElement(displayContext);
      output.setAnimated();
   }

   public record Argument(DynamicHolder<DataModel> model, ItemDisplayContext context) {
   }

   public record Unbaked(Identifier base) implements net.minecraft.client.renderer.item.ItemModel.Unbaked {
      public static final MapCodec<ExtraDataModelItemStackRenderer.Unbaked> MAP_CODEC = RecordCodecBuilder.mapCodec(
         inst -> inst.group(Identifier.CODEC.fieldOf("base").forGetter(ExtraDataModelItemStackRenderer.Unbaked::base)).apply(inst, ExtraDataModelItemStackRenderer.Unbaked::new)
      );

      public MapCodec<ExtraDataModelItemStackRenderer.Unbaked> type() {
         return MAP_CODEC;
      }

      public void resolveDependencies(Resolver resolver) {
         resolver.markDependency(this.base);
      }

      public ItemModel bake(BakingContext context, Matrix4fc transformation) {
         ModelBaker baker = context.blockModelBaker();
         ResolvedModel model = baker.getModel(this.base);
         ModelRenderProperties properties = ModelRenderProperties.fromResolvedModel(baker, model, model.getTopTextureSlots());
         return new ExtraDataModelItemStackRenderer(properties, transformation);
      }
   }
}
