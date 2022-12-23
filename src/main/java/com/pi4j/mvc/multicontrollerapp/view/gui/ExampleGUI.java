package com.pi4j.mvc.multicontrollerapp.view.gui;

import com.pi4j.mvc.multicontrollerapp.controller.ApplicationController;
import com.pi4j.mvc.multicontrollerapp.model.ExampleModel;
import com.pi4j.mvc.util.mvcbase.ViewMixin;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.*;

public class ExampleGUI extends BorderPane implements ViewMixin<ExampleModel, ApplicationController> {
	private static final String LIGHT_BULB = "\uf0eb";  // the unicode of the lightbulb-icon in fontawesome font
	private static final String HEARTBEAT = "\uf21e";  // the unicode of the heartbeat-icon in fontawesome font

	// declare all the UI elements you need
	private Button ledButton;
	private Button blinkButton;
	private Button increaseButton;
	private ListView<String> positionsList;
	private Label counterLabel;
	private Label infoLabel;

	public ExampleGUI(ApplicationController controller) {
		init(controller); //don't forget to call init
	}

	@Override
	public void initializeSelf() {
		//load all fonts you need
		loadFonts("/fonts/Lato/Lato-Lig.ttf", "/fonts/fontawesome-webfont.ttf");

		//apply your style
		addStylesheetFiles("/mvc/multicontrollerapp/style.css");

		getStyleClass().add("root-pane");
	}

	@Override
	public void initializeParts() {
		ledButton = new Button(LIGHT_BULB);
		ledButton.getStyleClass().add("icon-button");

		blinkButton = new Button(HEARTBEAT);
		blinkButton.getStyleClass().add("icon-button");

		increaseButton = new Button("+");

		counterLabel = new Label();
		counterLabel.getStyleClass().add("counter-label");

		infoLabel = new Label();
		infoLabel.getStyleClass().add("info-label");

		positionsList = new ListView<>();

	}

	@Override
	public void layoutParts() {
		// consider to use GridPane instead
		Region spacer = new Region();
		HBox.setHgrow(spacer, Priority.ALWAYS);

		HBox topBox = new HBox(this.ledButton, spacer, this.blinkButton);
		topBox.setAlignment(Pos.CENTER);

		VBox centerBox = new VBox(this.counterLabel, this.increaseButton, this.positionsList);
		centerBox.setAlignment(Pos.CENTER);
		centerBox.setFillWidth(true);
		centerBox.setPadding(new Insets(30));

		setTop(topBox);
		setCenter(centerBox);
		setBottom(this.infoLabel);
	}

	@Override
	public void setupUiToActionBindings(ApplicationController controller) {
		// look at that: all EventHandlers just trigger an action on Controller
		// by calling a single method

		increaseButton.setOnAction(event -> controller.increaseCounter());
		ledButton.setOnMousePressed(event -> controller.setLedGlows(true));
		ledButton.setOnMouseReleased(event -> controller.setLedGlows(false));
		blinkButton.setOnAction(event -> controller.blink());
	}

	@Override
	public void setupModelToUiBindings(ExampleModel model) {
		onChangeOf(
				model.systemInfo)                       // the value we need to observe, in this case that's an ObservableValue<String>, no need to convert it
				.update(infoLabel.textProperty());         // keeps textProperty and systemInfo in sync

		onChangeOf(
				model.counter)                          // the value we need to observe, in this case that's an ObservableValue<Integer>
				.convertedBy(String::valueOf)              // we have to convert the Integer to a String
				.update(counterLabel.textProperty());      // keeps textProperty and counter in sync

		onChangeOf(model.connectedPositions) //
				.convertedBy(positions -> positions.stream().map(String::valueOf).toList())
				.update(positionsList.itemsProperty().get());
	}
}
