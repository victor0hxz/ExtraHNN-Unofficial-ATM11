package net.lmor.extrahnn.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import dev.shadowsoffire.hostilenetworks.HostileNetworks;
import dev.shadowsoffire.hostilenetworks.data.DataModelInstance;
import dev.shadowsoffire.hostilenetworks.item.DataModelItem;
import dev.shadowsoffire.hostilenetworks.util.RedstoneState;
import dev.shadowsoffire.placebo.screen.PlaceboContainerScreen;
import dev.shadowsoffire.placebo.screen.TickableTextList;
import net.lmor.extrahnn.EHNNUtils;
import net.lmor.extrahnn.ExtraHostileNetworks;
import net.lmor.extrahnn.common.container.UltimateSimChamberContainer;
import net.lmor.extrahnn.common.tile.UltimateSimChamberTileEntity.FailureState;
import net.lmor.extrahnn.data.ExtraDataModelInstance;
import net.lmor.extrahnn.data.ExtraModelTier;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UltimateSimChamberScreen extends PlaceboContainerScreen<UltimateSimChamberContainer> {

    public static final int WIDTH = 232;
    public static final int HEIGHT = 256;
    public static final int MAX_TEXT_WIDTH = 178;
    public static final float RUNTIME_TEXT_SPEED = 0.65F;

    DecimalFormat DECIMAL_FORMAT = new DecimalFormat("##.##%");

    private static final Identifier BASE = ExtraHostileNetworks.local("textures/gui/ultimate_sim_chamber.png");
    private static final Identifier PLAYER = ExtraHostileNetworks.local("textures/gui/inventory.png");

    private static final Identifier EXTRACT_MODEL = HostileNetworks.loc("textures/item/blank_data_model.png");

    private TickableTextList body;
    private FailureState lastFailState = FailureState.NONE;
    private boolean runtimeTextLoaded = false;

    public UltimateSimChamberScreen(UltimateSimChamberContainer menu, Inventory inv, Component title) {
        super(menu, inv, title, WIDTH, HEIGHT);
    }

    @Override
    public void init() {
        super.init();
        addRenderableWidget(new RedstoneButton(this.getGuiLeft() + 228, this.getGuiTop()));
        addRenderableWidget(new ExtractModelButton(this.getGuiLeft() + 228, this.getGuiTop() + 24));
        this.body = new TickableTextList(Objects.requireNonNull(this.minecraft).font, MAX_TEXT_WIDTH);
        this.lastFailState = FailureState.NONE;
        this.runtimeTextLoaded = false;
        this.containerTick();
    }

    @Override
    protected void extractTooltip(@NotNull GuiGraphicsExtractor graphics, int x, int y) {
        List<Component> txt = new ArrayList<>();
        if (this.isHovering(211, 73, 7, 87, x, y)) {
            txt.add(Component.translatable("hostilenetworks.gui.energy", this.menu.getEnergyStored(), this.menu.getMaxEnergyStored()));
            ExtraDataModelInstance cModel = new ExtraDataModelInstance(this.menu.getSlot(0).getItem());
            if (cModel.isValid()) {
                txt.add(Component.translatable("hostilenetworks.gui.cost", cModel.simCost()));
            }
        } else if (this.isHovering(14, 73, 7, 87, x, y)) {
            ExtraDataModelInstance cModel = new ExtraDataModelInstance(this.menu.getSlot(0).getItem());
            if (cModel.isValid()) {
                if (cModel.getTier() != cModel.getTier().next()) {
                    txt.add(Component.translatable("hostilenetworks.gui.data", cModel.getData() - cModel.getTierData(), cModel.getNextTierData() - cModel.getTierData()));
                } else {
                    txt.add(Component.translatable("hostilenetworks.gui.max_data").withStyle(ChatFormatting.RED));
                }
            }
        } else if (this.menu.getSlot(0).getItem().isEmpty() && this.isHovering(-13, 1, 16,16, x, y)){
            txt.add(Component.translatable("extrahnn.info.data_model_slot"));
        } else if (this.isHovering(228, 1, 16, 16, x, y)){
            txt.add(Component.translatable(this.menu.getRedstoneState().getKey()));
        } else if (this.isHovering(228, 25, 16, 16, x, y)){
            txt.add(Component.translatable("extrahnn.info.extract_model." + (this.menu.isExtractDataModel() ? "on" : "off")));
        }

        graphics.setComponentTooltipForNextFrame(this.font, txt, x, y);
        super.extractTooltip(graphics, x, y);
    }

    @Override
    protected void extractLabels(@NotNull GuiGraphicsExtractor graphics, int x, int y) {
        int runtime = this.menu.getRuntime();
        if (runtime > 0) {
            int rTime = Math.min(99, Mth.ceil(100.0F * (float)(this.menu.getDuration() - runtime) / this.menu.getDuration()));
            graphics.text(this.font, rTime + "%", 186, 150, 0xff62d8ff, true);
        }

        ExtraDataModelInstance cModel = new ExtraDataModelInstance(this.menu.getSlot(0).getItem());
        renderModelNames(cModel, graphics, x, y);

        this.body.render(graphics, 28, 75);
    }

    protected void renderModelNames(@NotNull ExtraDataModelInstance cModel, @NotNull GuiGraphicsExtractor graphics, int x, int y){
        if (!cModel.isValid()) return;

        graphics.pose().pushMatrix();
        graphics.pose().scale(0.7f, 0.7f);

        int left = 25;
        int top = 9;
        int spacing = 4;

        graphics.text(font, I18n.get("extrahnn.gui.targets"), left, top, 0xFFFFFFFF);
        top += 9 + spacing;

        for (int i = 0; i < cModel.getModels().size(); i++) {
            var modelHolder = cModel.getModels().get(i);
            var modelName = modelHolder.get().name();

            float width = font.width(modelName);
            float scale = width <= 139 ? 1f : 139f / width;
            int yOffset = top + (9 + spacing) * i;

            graphics.pose().pushMatrix();
            graphics.pose().scale(scale, scale);

            graphics.text(font, " - ", left - 2, yOffset, 0xFF00FF60);
            graphics.text(font, modelName, left + 13, yOffset, 0xFF00FF60);

            graphics.pose().popMatrix();
        }

        top += (9 + spacing) * 4;

        // Tier
        int tierColor = cModel.getTier().color();
        String tierName = Component.translatable("hostilenetworks.gui.tier",
                Component.translatable("extrahnn.tier." + cModel.getTier().name).withColor(tierColor)
        ).getString();

        graphics.text(font, tierName, left, top, 0xFFFFFFFF);
        top += 9 + spacing;

        // Accuracy
        String accLabel = Component.translatable("hostilenetworks.gui.accuracy",
                Component.literal(DECIMAL_FORMAT.format(cModel.getAccuracy())).withColor(tierColor)
        ).getString();
        graphics.text(font, accLabel, left, top, 0xFFFFFFFF);

        graphics.pose().popMatrix();
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int pX, int pY, float pPartialTicks) {
        int left = this.getGuiLeft();
        int top = this.getGuiTop();

        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BASE, left + 8, top, 0.0F, 0.0F, 216, 166, 256, 256);
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BASE, left - 14, top, 216, 0, 18, 18, 256, 256);

        // Redstone
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BASE, left + 228, top, 216, 18, 18, 18, 256, 256);
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BASE, left + 228, top + 24, 216, 18, 18, 18, 256, 256);

        int energyHeight = 87 - Mth.ceil(87.0F * (float)this.menu.getEnergyStored() / (float)this.menu.getMaxEnergyStored());
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BASE, left + 211, top + 73, 234, 0, 7, energyHeight, 256, 256);

        int dataHeight = 87;
        ExtraDataModelInstance cModel = new ExtraDataModelInstance(this.menu.getSlot(0).getItem());
        if (cModel.isValid()) {
            int data = cModel.getData();
            ExtraModelTier tier = cModel.getTier();
            dataHeight = tier.isMax() ? 0: 87 - Mth.ceil(87.0F * (float)(data - cModel.getTierData()) / (float)(cModel.getNextTierData() - cModel.getTierData()));
        }

        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, BASE, left + 14, top + 73, 234, 0, 7, dataHeight, 256, 256);
        graphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, PLAYER, left + 28, top + 167, 0.0F, 0.0F, 176, 90, 256, 256);
    }

    @Override
    public void containerTick() {
        if (this.menu.getFailState() != FailureState.NONE) {
            FailureState oState = this.lastFailState;
            this.lastFailState = this.menu.getFailState();
            if (oState != this.lastFailState) {
                this.body.clear();
                MutableComponent msg = Component.translatable(this.lastFailState.getKey());
                if (this.lastFailState == FailureState.INPUT) {
                    DataModelInstance cModel = new DataModelInstance(this.menu.getSlot(0).getItem(), 0);
                    Component name = Component.translatable("item.hostilenetworks.prediction_matrix");
                    if (cModel.isValid()) {
                        name = cModel.getModel().input().items().findFirst().map(net.minecraft.world.item.ItemStack::new).orElse(net.minecraft.world.item.ItemStack.EMPTY).getHoverName();
                    }
                    msg = Component.translatable(this.lastFailState.getKey(), name);
                }
                this.body.addLine(msg, 1);
            }
            this.runtimeTextLoaded = false;
        }
        else if (!this.runtimeTextLoaded) {
            int ticks = this.menu.getDuration() - this.menu.getRuntime();
            this.body.clear();
            int iters = DataModelItem.getIters(this.menu.getSlot(0).getItem());

            List<Component> textAll = new ArrayList<>();
            for(int i = 0; i < 7; ++i) {
                Component txt = Component.translatable("hostilenetworks.run." + i, iters);

                if (i == 0) {
                    txt = Component.empty().append(txt).append(Component.literal("v" + ExtraHostileNetworks.VERSION).withStyle(ChatFormatting.GOLD));
                } else if (i == 5) {
                    String key = "hostilenetworks.color_text." + (this.menu.isPredictionSucceed() ? "success" : "failed");
                    Component status = Component.translatable(key).withStyle(this.menu.isPredictionSucceed() ? ChatFormatting.GOLD : ChatFormatting.RED);
                    textAll.add(status);
                }

                if (i != 6) textAll.add(txt);
            }
            int allChar = textAll.stream().mapToInt(x -> x.getString().length()).sum();

            float speed = EHNNUtils.textTickRate(allChar, this.menu.getDuration());
            for (Component text: textAll){
                this.body.continueLine(Component.empty().append(text).append("\n"), speed);
            }

            this.body.setTicks(ticks);
            this.runtimeTextLoaded = true;
            this.lastFailState = FailureState.NONE;
        }

        this.body.tick();
        if (this.menu.getRuntime() == 0) {
            this.runtimeTextLoaded = false;
        }
    }

    private class RedstoneButton extends AbstractWidget {

        public RedstoneButton(int x, int y) {
            super(x, y, 18, 18, Component.empty());
        }

        @SuppressWarnings("deprecation")
        @Override
        public void onClick(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        double mouseX=event.x(), mouseY=event.y(); int button=event.button();
            UltimateSimChamberScreen scn = UltimateSimChamberScreen.this;
            int idx = scn.menu.getRedstoneState().next().ordinal();
            Objects.requireNonNull(Objects.requireNonNull(scn.minecraft).gameMode).handleInventoryButtonClick(scn.menu.containerId, idx);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {
            this.defaultButtonNarrationText(output);
        }

        @Override
        protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {

            int y = UltimateSimChamberScreen.this.menu.getRedstoneState() != RedstoneState.IGNORED ? this.getY() : this.getY() + 1;
            guiGraphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, UltimateSimChamberScreen.this.menu.getRedstoneState().getResourceLocation(), this.getX() + 1, y, 0, 0, 16, 16, 16, 16);
        }
    }

    private class ExtractModelButton extends AbstractWidget {
        public ExtractModelButton(int x, int y){
            super(x, y, 18, 18, Component.empty());
        }

        @Override
        public void onClick(net.minecraft.client.input.MouseButtonEvent event, boolean doubleClick) {
        double mouseX=event.x(), mouseY=event.y(); int button=event.button();
            UltimateSimChamberScreen scn = UltimateSimChamberScreen.this;
            Objects.requireNonNull(scn.getMinecraft().gameMode).handleInventoryButtonClick(scn.menu.containerId, 100);
        }

        @Override
        protected void updateWidgetNarration(@NotNull NarrationElementOutput output) {this.defaultButtonNarrationText(output);}

        @Override
        protected void extractWidgetRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {


            boolean extract = UltimateSimChamberScreen.this.menu.isExtractDataModel();

            float color = extract ? 1 : 0.5f;

            guiGraphics.blit(net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED, EXTRACT_MODEL, this.getX() + 1, this.getY() + 1, 0, 0, 16, 16, 16, 16);

        }
    }

}