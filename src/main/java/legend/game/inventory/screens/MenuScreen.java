package legend.game.inventory.screens;

import legend.game.SItem;
import legend.game.input.InputAction;

import javax.annotation.Nullable;
import java.util.LinkedList;
import java.util.Queue;

public abstract class MenuScreen extends ControlHost {
  private final Queue<Runnable> deferredActions = new LinkedList<>();

  private MenuStack stack;

  private Control hover;
  private Control focus;

  void setStack(@Nullable final MenuStack stack) {
    this.stack = stack;
  }

  public MenuStack getStack() {
    return this.stack;
  }

  @Override
  protected MenuScreen getScreen() {
    return this;
  }

  protected abstract void render();

  protected void renderNumber(final int x, final int y, final int value, final int digitCount) {
    SItem.renderNumber(x, y, value, 0x2, digitCount);
  }

  protected void renderNumber(final int x, final int y, final int value, final int digitCount, final int flags) {
    SItem.renderNumber(x, y, value, flags | 0x2, digitCount);
  }

  final void renderScreen() {
    this.runDeferredActions();
    this.render();
    this.renderControls(0, 0);
  }

  @Override
  protected InputPropagation mouseMove(final int x, final int y) {
    if(super.mouseMove(x, y) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    this.updateHover(x, y);
    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation mouseClick(final int x, final int y, final int button, final int mods) {
    this.updateHover(x, y);
    this.updateFocusFromHover();

    if(super.mouseClick(x, y, button, mods) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation keyPress(final int key, final int scancode, final int mods) {
    if(super.keyPress(key, scancode, mods) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(this.focus != null) {
      return this.focus.keyPress(key, scancode, mods);
    }

    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation charPress(final int codepoint) {
    if(super.charPress(codepoint) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(this.focus != null) {
      return this.focus.charPress(codepoint);
    }

    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation pressedThisFrame(final InputAction inputAction) {
    if(super.pressedThisFrame(inputAction) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(this.focus != null) {
      return this.focus.pressedThisFrame(inputAction);
    }

    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation pressedWithRepeatPulse(final InputAction inputAction) {
    if(super.pressedWithRepeatPulse(inputAction) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(this.focus != null) {
      return this.focus.pressedWithRepeatPulse(inputAction);
    }

    return InputPropagation.PROPAGATE;
  }

  @Override
  protected InputPropagation releasedThisFrame(final InputAction inputAction) {
    if(super.releasedThisFrame(inputAction) == InputPropagation.HANDLED) {
      return InputPropagation.HANDLED;
    }

    if(this.focus != null) {
      return this.focus.releasedThisFrame(inputAction);
    }

    return InputPropagation.PROPAGATE;
  }

  private void updateHover(final int x, final int y) {
    final Control hover = this.findControlAt(x, y);

    if(hover != this.hover) {
      if(this.hover != null) {
        this.hover.hoverOut();
      }

      this.hover = hover;

      if(this.hover != null) {
        this.hover.hoverIn();
      }
    }
  }

  private void updateFocusFromHover() {
    if(this.focus != this.hover) {
      this.setFocus(this.hover);
    }
  }

  public void setFocus(@Nullable final Control control) {
    if(this.focus == control) {
      return;
    }

    if(this.focus != null) {
      this.focus.lostFocus();
    }

    this.focus = control;

    if(this.focus != null) {
      this.focus.gotFocus();
    }
  }

  @Nullable
  public Control getFocus() {
    return this.focus;
  }

  protected boolean propagateRender() {
    return false;
  }

  protected boolean propagateInput() {
    return false;
  }

  protected void deferAction(final Runnable action) {
    synchronized(this.deferredActions) {
      this.deferredActions.add(action);
    }
  }

  protected void runDeferredActions() {
    synchronized(this.deferredActions) {
      Runnable action;
      while((action = this.deferredActions.poll()) != null) {
        action.run();
      }
    }
  }
}
