package legend.game.inventory.screens;

import legend.game.types.LodString;
import legend.game.types.MessageBoxResult;

import static legend.game.SItem.Overwrite_save_8011c9e8;
import static legend.game.SItem.Save_new_game_8011c9c8;
import static legend.game.SItem.fadeOutArrow;
import static legend.game.SItem.getSlotY;
import static legend.game.SItem.menuStack;
import static legend.game.SItem.renderCentredText;
import static legend.game.SItem.renderSaveGameSlot;
import static legend.game.SItem.saveGame;
import static legend.game.SItem.saves;
import static legend.game.SMap._800cb450;
import static legend.game.Scus94491BpeSegment_8002.playSound;
import static legend.game.Scus94491BpeSegment_8005.index_80052c38;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;
import static legend.game.Scus94491BpeSegment_800b.saveListDownArrow_800bdb98;
import static legend.game.Scus94491BpeSegment_800b.saveListUpArrow_800bdb94;

public class SaveGameScreen extends SaveListScreen {
  private int slot;

  public SaveGameScreen(final Runnable unload) {
    super(unload);
  }

  @Override
  protected int menuCount() {
    return saves.size() + 1;
  }

  @Override
  protected void onSelect(final int slot) {
    playSound(2);
    this.slot = slot;

    if(this.slot == 0) {
      menuStack.pushScreen(new MessageBoxScreen(Save_new_game_8011c9c8, 2, this::onMessageboxResult));
    } else if(slot < this.menuCount()) {
      menuStack.pushScreen(new MessageBoxScreen(Overwrite_save_8011c9e8, 2, this::onMessageboxResult));
    } else {
      return;
    }

    if(saveListUpArrow_800bdb94 != null) {
      fadeOutArrow(saveListUpArrow_800bdb94);
      saveListUpArrow_800bdb94 = null;
    }

    //LAB_800ff3a4
    if(saveListDownArrow_800bdb98 != null) {
      fadeOutArrow(saveListDownArrow_800bdb98);
      saveListDownArrow_800bdb98 = null;
    }
  }

  @Override
  protected void onMessageboxResult(final MessageBoxResult result) {
    if(result == MessageBoxResult.YES) {
      gameState_800babc8.submapScene_a4 = index_80052c38.get();
      gameState_800babc8.submapCut_a8 = (int)_800cb450.get();

      saveGame(this.slot - 1);

      this.loadingStage = 2;
    }
  }

  @Override
  protected void renderSaveSlot(final int slot, final int fileIndex, final boolean allocate) {
    if(fileIndex == 0) {
      renderCentredText(new LodString("New save"), 188, getSlotY(slot) + 25, TextColour.BROWN);
    } else if(fileIndex < this.menuCount()) {
      renderSaveGameSlot(fileIndex - 1, getSlotY(slot), allocate);
    }
  }
}
