package legend.game.input;

import java.util.ArrayList;
import java.util.List;

import static legend.game.Scus94491BpeSegment.keyRepeat;
import static legend.game.Scus94491BpeSegment_800b._800bee90;
import static legend.game.Scus94491BpeSegment_800b._800bee94;
import static legend.game.Scus94491BpeSegment_800b._800bee98;

public class InputMapping {
  public List<InputBinding> bindings = new ArrayList<>();
  private InputControllerData controllerData;
  private boolean anyActivityThisFrame;
  private boolean anyActivity;

  private boolean anyActivityThisFrameExcludeAxis;

  public void update() {
    this.controllerData.updateState();

    this.anyActivityThisFrame = false;
    this.anyActivity = false;
    this.anyActivityThisFrameExcludeAxis = false;

    for(final InputBinding binding : this.bindings) {
      binding.update();

      if(this.controllerData.getPlayerSlot() == 1) {
        this.updateLegacyInput(binding);
      }

      if(binding.getState() == InputBindingState.PRESSED_THIS_FRAME) {
        this.anyActivityThisFrame = true;
        this.anyActivity = true;
        if(binding.getInputType() == InputType.GAMEPAD_BUTTON || binding.getInputType() == InputType.GAMEPAD_HAT || binding.getInputType() == InputType.KEYBOARD) {
          this.anyActivityThisFrameExcludeAxis = true;
        }
      } else if(binding.getState() == InputBindingState.PRESSED) {
        this.anyActivity = true;
      }
    }
  }

  private void updateLegacyInput(final InputBinding binding) {
    final int hexCode = binding.getHexCode();
    if(hexCode == -1) {
      return;
    }

    if(binding.getState() == InputBindingState.PRESSED_THIS_FRAME) {
      _800bee90.or(hexCode);
      _800bee94.or(hexCode);
      _800bee98.or(hexCode);

      keyRepeat.put(hexCode, 0);
    } else if(binding.getState() == InputBindingState.RELEASED_THIS_FRAME) {
      _800bee90.and(~hexCode);
      _800bee94.and(~hexCode);
      _800bee98.and(~hexCode);

      keyRepeat.remove(hexCode);
    }
  }

  public boolean hasActivityThisFrameExcludeAxis() {
    return this.anyActivityThisFrameExcludeAxis;
  }

  public boolean hasActivityThisFrame() {
    return this.anyActivityThisFrame;
  }

  public boolean hasActivity() {
    return this.anyActivity;
  }

  public InputControllerData getControllerData() {
    return this.controllerData;
  }

  public void setControllerData(final InputControllerData controllerData) {
    this.controllerData = controllerData;
    if(controllerData.getGlfwControllerId() == -1) {
      this.bindings = new ArrayList<>();
      return;
    }
    this.bindings = ControllerDatabase.getBindings(controllerData.getGlfwJoystickGUID());
    for(final InputBinding binding : this.bindings) {
      binding.setTargetController(controllerData);
    }
  }

  public void addBinding(final InputBinding binding) {
    this.bindings.add(binding);
  }

  public void removeBinding(final InputBinding binding) {
    this.bindings.remove(binding);
  }

  public void removeBinding(final InputAction inputAction, final InputType inputType) {
    for(final InputBinding binding : this.bindings) {
      if(binding.getInputAction() == inputAction && binding.getInputType() == inputType) {
        this.bindings.remove(binding);
        break;
      }
    }
  }
}
