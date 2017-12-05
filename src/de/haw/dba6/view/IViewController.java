package de.haw.dba6.view;
import javafx.scene.Scene;

import java.io.IOException;

public interface IViewController {
    Scene initScene() throws IOException;

    void initializeActions();

    void initialize();
}
