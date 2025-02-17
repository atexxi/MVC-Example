package fr.donrolando.mvc;

import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AppStarter extends Application {

    private RBAController controller;
    private RBAPUI        pui;

    @Override
    public void start(Stage primaryStage) {
        // that's your 'information hub'.
        RBAModel model = new RBAModel();

        controller = new RBAController(model);

        //both gui and pui are working on the same controller
//        pui = new RBAPUI(controller);//, Pi4JContext.createContext());

        Parent gui = new RBAGUI(controller);

        Scene scene = new Scene(gui);

        primaryStage.setTitle("GUI of a Pi4J App");
        primaryStage.setScene(scene);

        primaryStage.show();

        // on desktop it's convenient to have a very basic emulator for the PUI to test the interaction between GUI and PUI
//        startPUIEmulator(new RBAPuiEmulator(controller));
    }

    @Override
    public void stop() {
        controller.shutdown();
//        pui.shutdown();
    }

    private void startPUIEmulator(Parent puiEmulator) {
        Scene emulatorScene  = new Scene(puiEmulator);
        Stage secondaryStage = new Stage();
        secondaryStage.setTitle("PUI Emulator");
        secondaryStage.setScene(emulatorScene);
        secondaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);  //start the whole application
    }
}
