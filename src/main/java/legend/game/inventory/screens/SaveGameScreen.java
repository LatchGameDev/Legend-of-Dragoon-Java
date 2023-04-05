package legend.game.inventory.screens;

import legend.game.SItem;
import legend.game.inventory.screens.controls.Background;
import legend.game.inventory.screens.controls.BigList;
import legend.game.inventory.screens.controls.Glyph;
import legend.game.inventory.screens.controls.SaveCard;
import legend.game.saves.SavedGame;
import legend.game.types.LodString;
import legend.game.types.MessageBoxResult;

import javax.annotation.Nullable;

import static legend.core.GameEngine.SAVES;
import static legend.game.SItem.Overwrite_save_8011c9e8;
import static legend.game.SItem.menuStack;
import static legend.game.SMap._800cb450;
import static legend.game.Scus94491BpeSegment.scriptStartEffect;
import static legend.game.Scus94491BpeSegment_8002.deallocateRenderables;
import static legend.game.Scus94491BpeSegment_8002.playSound;
import static legend.game.Scus94491BpeSegment_8005.index_80052c38;
import static legend.game.Scus94491BpeSegment_800b.gameState_800babc8;

public class SaveGameScreen extends MenuScreen {
  private final Runnable unload;

  public SaveGameScreen(final Runnable unload) {
    this.unload = unload;

    deallocateRenderables(0xff);
    scriptStartEffect(2, 10);

    this.addControl(new Background());

    // Bottom line
    this.addControl(Glyph.glyph(78)).setPos(26, 155);
    this.addControl(Glyph.glyph(79)).setPos(192, 155);

    final SaveCard saveCard = this.addControl(new SaveCard());
    saveCard.setPos(16, 160);

    final BigList<SavedGame> saveList = this.addControl(new BigList<>(savedGame -> savedGame != null ? savedGame.filename() : "<new save>"));
    saveList.setPos(16, 16);
    saveList.setSize(360, 144);
    saveList.onHighlight(saveCard::setSaveData);
    saveList.onSelection(this::onSelection);
    this.setFocus(saveList);

    saveList.addEntry(null);

    for(final SavedGame save : SAVES.loadAllSaves(gameState_800babc8.campaignName)) {
      saveList.addEntry(save);
    }
  }

  @Override
  protected void render() {
    SItem.renderCentredText(new LodString("Save Game"), 188, 10, TextColour.BROWN);
  }

  private void onSelection(@Nullable final SavedGame save) {
    playSound(2);

    if(save == null) {
      menuStack.pushScreen(new InputBoxScreen("Save name:", SAVES.generateSaveName(gameState_800babc8.campaignName), 2, this::onNewSaveResult));
    } else {
      menuStack.pushScreen(new MessageBoxScreen(Overwrite_save_8011c9e8, 2, result -> this.onOverwriteResult(result, save)));
    }
  }

  private void onNewSaveResult(final MessageBoxResult result, final String name) {
    if(result == MessageBoxResult.YES) {
      if(SAVES.saveExists(gameState_800babc8.campaignName, name)) {
        menuStack.pushScreen(new MessageBoxScreen(new LodString("Save name already\nin use"), 0, result1 -> { }));
        return;
      }

      gameState_800babc8.submapScene_a4 = index_80052c38.get();
      gameState_800babc8.submapCut_a8 = (int)_800cb450.get();

      SAVES.newSave(name, gameState_800babc8);

      this.unload.run();
    }
  }

  private void onOverwriteResult(final MessageBoxResult result, final SavedGame save) {
    if(result == MessageBoxResult.YES) {
      gameState_800babc8.submapScene_a4 = index_80052c38.get();
      gameState_800babc8.submapCut_a8 = (int)_800cb450.get();

      SAVES.overwriteSave(save.filename(), gameState_800babc8);

      this.unload.run();
    }
  }
}
