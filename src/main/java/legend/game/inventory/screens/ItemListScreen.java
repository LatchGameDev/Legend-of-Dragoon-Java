package legend.game.inventory.screens;

import legend.core.Config;
import legend.core.MathHelper;
import legend.core.memory.types.ArrayRef;
import legend.core.memory.types.UnsignedByteRef;
import legend.game.input.InputAction;
import legend.game.types.LodString;
import legend.game.types.MenuItemStruct04;
import legend.game.types.MessageBoxResult;
import legend.game.types.Renderable58;

import java.util.ArrayList;
import java.util.List;

import static legend.game.SItem.FUN_800fc814;
import static legend.game.SItem.FUN_800fc824;
import static legend.game.SItem.FUN_801038d4;
import static legend.game.SItem.FUN_80104738;
import static legend.game.SItem.FUN_80104b60;
import static legend.game.SItem.Press_to_sort_8011d024;
import static legend.game.SItem._8011c314;
import static legend.game.SItem._8011c32c;
import static legend.game.SItem.allocateUiElement;
import static legend.game.SItem.goodsGlyphs_801141c4;
import static legend.game.SItem.menuStack;
import static legend.game.SItem.renderGlyphs;
import static legend.game.SItem.renderMenuItems;
import static legend.game.SItem.renderString;
import static legend.game.SItem.renderText;
import static legend.game.SItem.renderThreeDigitNumber;
import static legend.game.SItem.renderTwoDigitNumber;
import static legend.game.Scus94491BpeSegment.scriptStartEffect;
import static legend.game.Scus94491BpeSegment_8002.deallocateRenderables;
import static legend.game.Scus94491BpeSegment_8002.playSound;
import static legend.game.Scus94491BpeSegment_8002.recalcInventory;
import static legend.game.Scus94491BpeSegment_8002.setInventoryFromDisplay;
import static legend.game.Scus94491BpeSegment_8002.sortItems;
import static legend.game.Scus94491BpeSegment_8002.uploadRenderables;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.Scus94491BpeSegment_800b.saveListDownArrow_800bdb98;
import static legend.game.Scus94491BpeSegment_800b.saveListUpArrow_800bdb94;
import static org.lwjgl.glfw.GLFW.*;

public class ItemListScreen extends MenuScreen {
  private int loadingStage;
  private double scrollAccumulator;
  private final Runnable unload;

  private int selectedSlot;
  private int slotScrollEquipment;
  private int slotScrollItem;
  private int equippedItemsCount;
  private Renderable58 highlight;
  private Renderable58 _800bdb9c;
  private Renderable58 _800bdba0;
  private int mouseX;
  private int mouseY;

  private ArrayRef<UnsignedByteRef> currentList;
  private List<MenuItemStruct04> currentDisplayList;
  private int currentIndex;
  private int currentItemId;

  private boolean leftSide = true;

  private final List<MenuItemStruct04> equipment = new ArrayList<>();
  private final List<MenuItemStruct04> items = new ArrayList<>();

  public ItemListScreen(final Runnable unload) {
    this.unload = unload;
  }

  @Override
  protected void render() {
    switch(this.loadingStage) {
      case 0 -> {
        scriptStartEffect(2, 10);
        deallocateRenderables(0xff);
        renderGlyphs(goodsGlyphs_801141c4, 0, 0);
        recalcInventory();
        this.selectedSlot = 0;
        this.slotScrollEquipment = 0;
        this.slotScrollItem = 0;
        this.setCurrent(gameState_800babc8.equipment_1e8, this.equipment, 0);

        this.highlight = allocateUiElement(0x76, 0x76, FUN_800fc824(0), FUN_800fc814(this.selectedSlot) + 32);
        FUN_80104b60(this.highlight);
        this.equippedItemsCount = FUN_80104738(this.equipment, this.items, 0x1L);
        this.renderItemList(this.slotScrollEquipment, this.slotScrollItem, 0xff, 0xff);
        this.loadingStage++;
      }

      case 1 -> {
        this.renderItemList(this.slotScrollEquipment, this.slotScrollItem, this.currentItemId, 0);

        if(this.scrollAccumulator >= 1.0d) {
          this.scrollAccumulator -= 1.0d;

          if(MathHelper.inBox(this.mouseX, this.mouseY, 8, 40, 174, 122)) {
            if(this.slotScrollEquipment > 0) {
              playSound(1);
              this.slotScrollEquipment--;
              this.setCurrent(gameState_800babc8.equipment_1e8, this.equipment, this.slotScrollEquipment + this.selectedSlot);
            }
          }

          if(MathHelper.inBox(this.mouseX, this.mouseY, 186, 40, 174, 122)) {
            if(this.slotScrollItem > 0) {
              playSound(1);
              this.slotScrollItem--;
              this.setCurrent(gameState_800babc8.items_2e9, this.items, this.slotScrollItem + this.selectedSlot);
            }
          }
        }

        if(this.scrollAccumulator <= -1.0d) {
          this.scrollAccumulator += 1.0d;

          if(MathHelper.inBox(this.mouseX, this.mouseY, 8, 40, 174, 122)) {
            if(this.slotScrollEquipment < gameState_800babc8.equipmentCount_1e4.get() + (int)this.equippedItemsCount - 7) {
              playSound(1);
              this.slotScrollEquipment++;
              this.setCurrent(gameState_800babc8.equipment_1e8, this.equipment, this.slotScrollEquipment + this.selectedSlot);
            }
          }

          if(MathHelper.inBox(this.mouseX, this.mouseY, 186, 40, 174, 122)) {
            if(this.slotScrollItem < gameState_800babc8.itemCount_1e6.get() - 7) {
              playSound(1);
              this.slotScrollItem++;
              this.setCurrent(gameState_800babc8.items_2e9, this.items, this.slotScrollItem + this.selectedSlot);
            }
          }
        }
      }

      // Fade out
      case 100 -> {
        this.renderItemList(this.slotScrollEquipment, this.slotScrollItem, this.currentItemId, 0);
        this._800bdba0 = null;

        this._800bdb9c = null;
        saveListDownArrow_800bdb98 = null;
        saveListUpArrow_800bdb94 = null;

        this.unload.run();
      }
    }
  }

  private void handleMenuFocusState(final boolean leftSide) {
    this.leftSide = leftSide;
  }

  private void handleVerticalInput(final boolean isScrollingUp) {
    if(this.leftSide) {
      if(isScrollingUp ? this.selectedSlot > 0 : this.selectedSlot < 6) {
        playSound(1);
        this.selectedSlot += isScrollingUp ? -1 : 1;
      } else if(isScrollingUp ? this.slotScrollEquipment > 0 : this.slotScrollEquipment < (gameState_800babc8.equipmentCount_1e4.get() + (int)this.equippedItemsCount - 7)) {
        playSound(1);
        this.slotScrollEquipment += isScrollingUp ? -1 : 1;
      }
      this.setCurrent(gameState_800babc8.equipment_1e8, this.equipment, this.slotScrollEquipment + this.selectedSlot);
    } else {
      if(isScrollingUp ? this.selectedSlot > 0 : this.selectedSlot < 6) {
        playSound(1);
        this.selectedSlot += isScrollingUp ? -1 : 1;
      } else if(isScrollingUp ? this.slotScrollItem > 0 : this.slotScrollItem < (gameState_800babc8.itemCount_1e6.get() - 7)) {
        playSound(1);
        this.slotScrollItem += isScrollingUp ? -1 : 1;
      }
      this.setCurrent(gameState_800babc8.items_2e9, this.items, this.slotScrollItem + this.selectedSlot);
    }
    this.highlight.y_44 = FUN_800fc814(this.selectedSlot) + 32;
  }

  private void setCurrent(final ArrayRef<UnsignedByteRef> list, final List<MenuItemStruct04> display, final int index) {
    this.currentList = list;
    this.currentDisplayList = display;
    this.currentIndex = index;
    this.currentItemId = list.get(index).get();
  }

  private void renderItemList(final int slotScroll1, final int slotScroll2, final int itemId, final long a3) {
    renderMenuItems(16, 33, this.equipment, slotScroll1, 7, saveListUpArrow_800bdb94, saveListDownArrow_800bdb98);
    renderMenuItems(194, 33, this.items, slotScroll2, 7, this._800bdb9c, this._800bdba0);
    renderThreeDigitNumber(136, 24, gameState_800babc8.equipmentCount_1e4.get(), 0x2L);
    renderTwoDigitNumber(326, 24, gameState_800babc8.itemCount_1e6.get(), 0x2L);

    final boolean allocate = a3 == 0xff;
    if(allocate) {
      allocateUiElement(0xb, 0xb, 154, 24);
      renderThreeDigitNumber(160, 24, 0xff);
      allocateUiElement(0xb, 0xb, 338, 24);
      renderTwoDigitNumber(344, 24, Config.inventorySize());
      allocateUiElement(0x55, 0x55, 16, 16);
      saveListUpArrow_800bdb94 = allocateUiElement(0x3d, 0x44, 180, FUN_800fc814(2));
      saveListDownArrow_800bdb98 = allocateUiElement(0x35, 0x3c, 180, FUN_800fc814(8));
      allocateUiElement(0x55, 0x55, 194, 16);
      this._800bdb9c = allocateUiElement(0x3d, 0x44, 358, FUN_800fc814(2));
      this._800bdba0 = allocateUiElement(0x35, 0x3c, 358, FUN_800fc814(8));
    }

    renderText(_8011c314, 32, 22, 4);
    renderText(_8011c32c, 210, 22, 4);

    if(a3 != 1) {
      FUN_801038d4(0x89, 84, 178).clut_30 = 0x7ceb;
      renderText(Press_to_sort_8011d024, 37, 178, 4);
    }

    renderString(0, 194, 178, itemId, allocate);
    uploadRenderables();
  }

  @Override
  protected void mouseMove(final int x, final int y) {
    this.mouseX = x;
    this.mouseY = y;

    if(this.loadingStage == 1) {
      for(int i = 0; i < Math.min(7, gameState_800babc8.equipmentCount_1e4.get() - this.slotScrollEquipment); i++) {
        if(MathHelper.inBox(x, y, 8, 31 + FUN_800fc814(i), 174, 17)) {
          playSound(1);
          this.handleMenuFocusState(true);
          this.selectedSlot = i;
          this.highlight.y_44 = FUN_800fc814(i) + 32;
          this.highlight.x_40 = FUN_800fc824(0);
          this.setCurrent(gameState_800babc8.equipment_1e8, this.equipment, this.slotScrollEquipment + this.selectedSlot);
        }
      }

      for(int i = 0; i < Math.min(7, gameState_800babc8.itemCount_1e6.get() - this.slotScrollItem); i++) {
        if(MathHelper.inBox(x, y, 186, 31 + FUN_800fc814(i), 174, 17)) {
          playSound(1);
          this.handleMenuFocusState(false);
          this.selectedSlot = i;
          this.highlight.y_44 = FUN_800fc814(i) + 32;
          this.highlight.x_40 = FUN_800fc824(1);
          this.setCurrent(gameState_800babc8.items_2e9, this.items, this.slotScrollItem + this.selectedSlot);
        }
      }
    }
  }

  @Override
  protected void mouseClick(final int x, final int y, final int button, final int mods) {
    if(mods != 0) {
      return;
    }

    if(this.loadingStage == 1 && button == GLFW_MOUSE_BUTTON_LEFT) {
      for(int i = 0; i < Math.min(7, gameState_800babc8.equipmentCount_1e4.get() - this.slotScrollEquipment); i++) {
        if(MathHelper.inBox(x, y, 8, 31 + FUN_800fc814(i), 174, 17)) {
          playSound(1);
          this.handleMenuFocusState(true);
          this.selectedSlot = i;
          this.highlight.y_44 = FUN_800fc814(i) + 32;
          this.highlight.x_40 = FUN_800fc824(0);
          this.setCurrent(gameState_800babc8.equipment_1e8, this.equipment, this.slotScrollEquipment + this.selectedSlot);

          if((this.currentDisplayList.get(this.currentIndex).flags_02 & 0x2000) != 0) {
            playSound(40);
          } else {
            playSound(2);
            menuStack.pushScreen(new MessageBoxScreen(new LodString("Discard?"), 2, this::discard));
          }
        }
      }

      for(int i = 0; i < Math.min(7, gameState_800babc8.itemCount_1e6.get() - this.slotScrollItem); i++) {
        if(MathHelper.inBox(x, y, 186, 31 + FUN_800fc814(i), 174, 17)) {
          playSound(1);
          this.handleMenuFocusState(false);
          this.selectedSlot = i;
          this.highlight.y_44 = FUN_800fc814(i) + 32;
          this.highlight.x_40 = FUN_800fc824(1);
          this.setCurrent(gameState_800babc8.items_2e9, this.items, this.slotScrollItem + this.selectedSlot);

          if((this.currentDisplayList.get(this.currentIndex).flags_02 & 0x2000) != 0) {
            playSound(40);
          } else {
            playSound(2);
            menuStack.pushScreen(new MessageBoxScreen(new LodString("Discard?"), 2, this::discard));
          }
        }
      }
    }
  }

  private void discard(final MessageBoxResult result) {
    if(result == MessageBoxResult.YES) {
      this.currentDisplayList.remove(this.currentIndex);
      setInventoryFromDisplay(this.currentDisplayList, this.currentList, this.currentDisplayList.size());
      recalcInventory();
    }
  }

  @Override
  protected void mouseScroll(final double deltaX, final double deltaY) {
    if(this.loadingStage != 1) {
      return;
    }

    if(this.scrollAccumulator < 0 && deltaY > 0 || this.scrollAccumulator > 0 && deltaY < 0) {
      this.scrollAccumulator = 0;
    }

    this.scrollAccumulator += deltaY;
  }

  private void menuEscape() {
    this.loadingStage = 100;
  }
  private void menuSelect() {
    if(this.currentIndex >= this.currentDisplayList.size()) {
      return;
    }

    if(((this.currentDisplayList.get(this.currentIndex).flags_02 & 0x2000) != 0)) {
      playSound(40);
    } else {
      playSound(2);
      menuStack.pushScreen(new MessageBoxScreen(new LodString("Discard?"), 2, this::discard));
    }
  }
  private void menuSort() {
    playSound(2);
    sortItems(this.equipment, gameState_800babc8.equipment_1e8, gameState_800babc8.equipmentCount_1e4.get() + this.equippedItemsCount);
    sortItems(this.items, gameState_800babc8.items_2e9, gameState_800babc8.itemCount_1e6.get());
  }
  private void menuNavigateUp() {
    this.handleVerticalInput(true);
  }
  private void menuNavigateDown() {
    this.handleVerticalInput(false);
  }
  private void menuNavigateLeft() {
    playSound(1);
    this.handleMenuFocusState(true);
    this.setCurrent(gameState_800babc8.equipment_1e8, this.equipment, this.slotScrollEquipment + this.selectedSlot);
    this.highlight.x_40 = FUN_800fc824(0);
  }
  private void menuNavigateRight() {
    playSound(1);
    this.handleMenuFocusState(false);
    this.setCurrent(gameState_800babc8.items_2e9, this.items, this.slotScrollItem + this.selectedSlot);
    this.highlight.x_40 = FUN_800fc824(1);
  }


  @Override
  public void pressedThisFrame(final InputAction inputAction) {
    if(this.loadingStage != 1) {
      return;
    }

    if(inputAction == InputAction.DPAD_LEFT || inputAction == InputAction.JOYSTICK_LEFT_BUTTON_LEFT) {
      this.menuNavigateLeft();
    }
    if(inputAction == InputAction.DPAD_RIGHT || inputAction == InputAction.JOYSTICK_LEFT_BUTTON_RIGHT) {
      this.menuNavigateRight();
    }
    if(inputAction == InputAction.BUTTON_EAST) {
      this.menuEscape();
    }
    if(inputAction == InputAction.BUTTON_SOUTH) {
      this.menuSelect();
    }
    if(inputAction == InputAction.BUTTON_NORTH) {
      this.menuSort();
    }
  }

  @Override
  public void pressedWithRepeatPulse(final InputAction inputAction) {
    if(inputAction == InputAction.DPAD_UP || inputAction == InputAction.JOYSTICK_LEFT_BUTTON_UP) {
      this.menuNavigateUp();
    }
    if(inputAction == InputAction.DPAD_DOWN || inputAction == InputAction.JOYSTICK_LEFT_BUTTON_DOWN) {
      this.menuNavigateDown();
    }
  }
}
