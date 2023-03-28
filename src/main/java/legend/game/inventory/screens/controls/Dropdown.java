package legend.game.inventory.screens.controls;

import legend.core.MathHelper;
import legend.game.input.InputAction;
import legend.game.inventory.screens.Control;
import legend.game.inventory.screens.InputPropagation;
import legend.game.inventory.screens.TextColour;
import legend.game.types.LodString;

import java.util.ArrayList;
import java.util.List;

import static legend.game.SItem.renderText;
import static legend.game.Scus94491BpeSegment_800b.textZ_800bdf00;

public class Dropdown extends Control {
  private final Panel panel;
  private final Glyph downArrow;
  private final Highlight highlight;

  private final List<LodString> options = new ArrayList<>();
  private int hoverIndex;
  private int selectedIndex;

  public Dropdown() {
    this.panel = this.addControl(new Panel());
    this.panel.setPos(-9, 16);
    this.panel.setZ(10);
    this.panel.hide();

    this.highlight = this.panel.addControl(new Highlight());
    this.highlight.setPos(14, 8);
    this.highlight.setZ(this.panel.getZ() - 2);
    this.highlight.setHeight(16);

    this.panel.onMouseMove((x, y) -> {
      for(int i = 0; i < this.options.size(); i++) {
        if(MathHelper.inBox(x, y, 0, 9 + i * 16, this.getWidth(), 16)) {
          this.hover(i);
          return InputPropagation.HANDLED;
        }
      }

      return InputPropagation.PROPAGATE;
    });

    this.panel.onMouseClick((x, y, button, mods) -> {
      for(int i = 0; i < this.options.size(); i++) {
        if(MathHelper.inBox(x, y, 0, 9 + i * 16, this.getWidth(), 16)) {
          this.select(i);
          return InputPropagation.HANDLED;
        }
      }

      return InputPropagation.PROPAGATE;
    });

    this.downArrow = this.addControl(Glyph.uiElement(53, 60));
    this.downArrow.ignoreInput();

    this.setSize(100, 16);
  }

  public void addOption(final String option) {
    this.options.add(new LodString(option));
    this.panel.setHeight(18 + this.options.size() * 16);
  }

  public void setSelectedIndex(final int index) {
    this.selectedIndex = index;
  }

  public int getSelectedIndex() {
    return this.selectedIndex;
  }

  @Override
  protected void onResize() {
    super.onResize();
    this.panel.setWidth(this.getWidth() + 18);
    this.highlight.setWidth(this.getWidth() + 7);
    this.downArrow.setPos(this.getWidth(), -2);
  }

  @Override
  protected InputPropagation mouseClick(final int x, final int y, final int button, final int mods) {
    if(super.mouseClick(x, y, button, mods) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    this.toggleDrop();
    return InputPropagation.HANDLED;
  }

  private void toggleDrop() {
    this.panel.toggleVisibility();

    if(this.panel.isVisible()) {
      this.setHeight(this.getHeight() + this.panel.getHeight());
      this.hover(this.selectedIndex);
    } else {
      this.setHeight(this.getHeight() - this.panel.getHeight());
    }
  }

  private void hover(final int index) {
    this.hoverIndex = index;
    this.highlight.setY(8 + index * 16);
  }

  private void select(final int index) {
    this.selectedIndex = index;

    if(this.selectionHandler != null) {
      this.selectionHandler.selection(index);
    }
  }

  @Override
  protected InputPropagation pressedThisFrame(final InputAction inputAction) {
    if(super.pressedThisFrame(inputAction) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(inputAction == InputAction.BUTTON_SOUTH) {
      if(this.panel.isVisible()) {
        this.select(this.hoverIndex);
      }

      this.toggleDrop();
      return InputPropagation.HANDLED;
    } else if(inputAction == InputAction.BUTTON_EAST) {
      if(this.panel.isVisible()) {
        this.toggleDrop();
        return InputPropagation.HANDLED;
      }
    }

    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation pressedWithRepeatPulse(final InputAction inputAction) {
    if(super.pressedWithRepeatPulse(inputAction) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(inputAction == InputAction.DPAD_DOWN || inputAction == InputAction.JOYSTICK_LEFT_BUTTON_DOWN) {
      if(this.panel.isVisible()) {
        this.hover((this.hoverIndex + 1) % this.options.size());
        return InputPropagation.HANDLED;
      }
    } else if(inputAction == InputAction.DPAD_UP || inputAction == InputAction.JOYSTICK_LEFT_BUTTON_UP) {
      if(this.panel.isVisible()) {
        this.hover(Math.floorMod(this.hoverIndex - 1, this.options.size()));
        return InputPropagation.HANDLED;
      }
    }

    return InputPropagation.PROPAGATE;
  }

  @Override
  protected void lostFocus() {
    super.lostFocus();

    if(this.panel.isVisible()) {
      this.toggleDrop();
    }
  }

  @Override
  protected void render(final int x, final int y) {
    if(!this.options.isEmpty()) {
      renderText(this.options.get(this.selectedIndex), x + 1, y + 1, TextColour.BROWN);
    }

    if(this.panel.isVisible()) {
      final int oldTextZ = textZ_800bdf00.get();
      textZ_800bdf00.set(this.panel.getZ() - 1);
      for(int i = 0; i < this.options.size(); i++) {
        renderText(this.options.get(i), x + 1, y + 26 + i * 16, TextColour.BROWN);
      }
      textZ_800bdf00.set(oldTextZ);
    }
  }

  public void onSelection(final Selection handler) {
    this.selectionHandler = handler;
  }

  private Selection selectionHandler;

  @FunctionalInterface public interface Selection { void selection(final int index); }
}
