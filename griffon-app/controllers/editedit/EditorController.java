package editedit;

import griffon.core.artifact.GriffonController;
import griffon.inject.MVCMember;
import griffon.metadata.ArtifactProviderFor;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Cursor;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.codehaus.griffon.runtime.core.artifact.AbstractGriffonController;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.Map;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.io.FileUtils.writeStringToFile;

@ArtifactProviderFor(GriffonController.class)
public class EditorController extends AbstractGriffonController {
    public TextArea editor;
    @MVCMember
    @Nonnull
    private EditorModel model;
    @MVCMember
    @Nonnull
    private EditorView view;

    @Override
    public void mvcGroupInit(@Nonnull Map<String, Object> args) {
        model.setDocument((Document) args.get("document"));
        runOutsideUI(() -> {
            try {
                final String content = readFileToString(model.getDocument().getFile());
                runInsideUIAsync(() -> model.getDocument().setContents(content));
            } catch (IOException e) {
                getLog().warn("Can't open file", e);
            }
        });
    }

    public void saveFile() {
        try {
            writeStringToFile(model.getDocument().getFile(), view.getEditor().getText());
            runInsideUIAsync(() -> model.getDocument().setContents(view.getEditor().getText()));
        } catch (IOException e) {
            getLog().warn("Can't save file", e);
        }
    }

    public void closeFile() {
        destroyMVCGroup(getMvcGroup().getMvcId());
    }

    public String getFileText() {
        try {
            return view.getEditor().getText();
        } catch (Exception e) {
            getLog().warn("Can't get file text", e);
        }
        return "";
    }

    public int getCursorPosition() {
        try {
            return view.getEditor().getCaretPosition();
        } catch (Exception e) {
            getLog().warn("Error getting cursor position", e);
        }
        return 0;
    }

    public void selectText(int from, int to) {
        try {
            view.getEditor().selectRange(from, to);
        } catch (Exception e) {
            getLog().warn("Error selecting text", e);
        }
    }

    public void replaceSelectedText(String to) {
        try {
            if (view.getEditor().getSelection().getLength() != 0)
                view.getEditor().replaceText(view.getEditor().getSelection(), to);
        } catch (Exception e) {
            getLog().warn("Replace problem", e);
        }
    }
}