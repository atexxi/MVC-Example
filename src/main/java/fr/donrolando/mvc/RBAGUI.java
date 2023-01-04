package fr.donrolando.mvc;

import java.util.function.Function;

import com.pi4j.mvc.templateapp.controller.SomeController;
import com.pi4j.mvc.templateapp.model.SomeModel;
import com.pi4j.mvc.util.mvcbase.ViewMixin;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class RBAGUI extends BorderPane implements ViewMixin<RBAModel, RBAController> { //all GUI-elements have to implement ViewMixin

    private static final String LIGHT_BULB = "\uf0eb";  // the unicode of the lightbulb-icon in fontawesome font
    private static final String CALENDAR = "\uF073";  // the unicode of the lightbulb-icon in fontawesome font

    // declare all the UI elements you need
    private Button ledButton;
    private Button increaseButton;
    private Label  counterLabel;
    private Label  infoLabel;

    private TextField ipTextfield;
    private Button connectButton;

    public RBAGUI(RBAController controller) {
        init(controller); //don't forget to call 'init'
    }

    @Override
    public void initializeSelf() {
        //load all fonts you need
        loadFonts("/fonts/Lato/Lato-Lig.ttf", "/fonts/fontawesome-webfont.ttf");

        //apply your style
        addStylesheetFiles("/mvc/rba/style.css");

        getStyleClass().add("root-pane");
    }

    @Override
    public void initializeParts() {
        ledButton = new Button(CALENDAR);
        ledButton.getStyleClass().add("icon-button");

        increaseButton = new Button("+");

        counterLabel = new Label();
        counterLabel.getStyleClass().add("counter-label");

        infoLabel = new Label();
        infoLabel.getStyleClass().add("info-label");

        ipTextfield = new TextField();
        ipTextfield.getStyleClass().add("info-label");

        connectButton = new Button("Connect");
//        connectButton.getStyleClass().add("icon-button");

    }

    @Override
    public void layoutParts() {
        HBox topBox = new HBox(ledButton);
        topBox.setAlignment(Pos.CENTER);

        VBox centerBox = new VBox(counterLabel, increaseButton,ipTextfield,connectButton);
        centerBox.setAlignment(Pos.CENTER);
        centerBox.setFillWidth(true);
        centerBox.setPadding(new Insets(30));

        setTop(topBox);
        setCenter(centerBox);
        setBottom(infoLabel);
    }

    @Override
    public void setupUiToActionBindings(RBAController controller) {
        // look at that: all EventHandlers just trigger an action on 'controller'
        // by calling a single method

        increaseButton.setOnAction  (event -> controller.increaseCounter());
        ledButton.setOnMousePressed (event -> {
            controller.setLedGlows(true);
            controller.setIp("test");
        });
        ledButton.setOnMouseReleased(event -> controller.setLedGlows(false));
        ipTextfield.setOnAction(event -> controller.setIp(ipTextfield.getText()));
        connectButton.setOnAction(event -> controller.toggleConnect());
    }

    @Override
    public void setupModelToUiBindings(RBAModel model) {
        onChangeOf(model.systemInfo)                       // the value we need to observe, in this case that's an ObservableValue<String>, no need to convert it
                .update(infoLabel.textProperty());         // keeps textProperty and systemInfo in sync
        onChangeOf(model.counter)                          // the value we need to observe, in this case that's an ObservableValue<Integer>
                .convertedBy(String::valueOf)              // we have to convert the Integer to a String
                .update(counterLabel.textProperty());      // keeps textProperty and counter in sync
        onChangeOf(model.ip)
                .update(ipTextfield.textProperty());
        onChangeOf(model.connected)
                .convertedBy(aBoolean -> {
                    if (aBoolean)
                        return "Disconnect";
                    else
                        return "Connect";
                })
                .update(connectButton.textProperty());

    }
}
