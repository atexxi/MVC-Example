package fr.donrolando.mvc;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.pi4j.mvc.util.mvcbase.ViewMixin;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class RBAGUI extends BorderPane
		implements ViewMixin<RBAModel, RBAController> { //all GUI-elements have to implement ViewMixin

	private static final String LIGHT_BULB = "\uf0eb";  // the unicode of the lightbulb-icon in fontawesome font
	private static final String CALENDAR = "\uF073";  // the unicode of the lightbulb-icon in fontawesome font

	// declare all the UI elements you need
	private Button ledButton;
	private Button increaseButton;
	private Label counterLabel;
	private Label infoLabel;

	private TextField ipTextfield;
	private Button connectButton;
	private Button beepButton;

	private ComboBox<String> debuggerTypeCombo;

	private ListView<Text> messages;

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

		ipTextfield.addEventFilter(KeyEvent.KEY_TYPED, event -> {
//			if (event.getCode().equals(KeyCode.KP_LEFT))
//				System.out.println("KP_LEFT");
//			if (event.getCode().equals(KeyCode.KP_RIGHT))
//				System.out.println("KP_RIGHT");
//
//				if (event.getCode().equals(KeyCode.LEFT))
//				System.out.println("LEFT");
//			if (event.getCode().equals(KeyCode.RIGHT))
//				System.out.println("RIGHT");

			if (!"0123456789.".contains(event.getCharacter())) {
				event.consume();
			}
		});

		debuggerTypeCombo = new ComboBox<>();
		debuggerTypeCombo.getItems().addAll("RBA","MCP2221","WebSocket");

//		ipTextfield.setTextFormatter(new TextFormatter<String>(change -> {
//			final int oldLength = change.getControlText().length();
//			int newLength = change.getControlNewText().length();
////			if (newLength<oldLength) return change;
////			change.setCaretPosition(newLength);
////			change.setAnchor(newLength);
//			return change;
//		}));

//		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
//		try {
//			ipTextfield.setTextFormatter(new TextFormatter<>(new DateTimeStringConverter(format),format.parse("00:00:00")));
//		} catch (ParseException e) {
//			throw new RuntimeException(e);
//		}

		connectButton = new Button("Connect");

		beepButton = new Button("Beep");
		//        connectButton.getStyleClass().add("icon-button");

		messages = new ListView<>();

	}

	@Override
	public void layoutParts() {
		HBox topBox = new HBox(ledButton);
		topBox.setAlignment(Pos.CENTER);

		VBox centerBox = new VBox(counterLabel, increaseButton, debuggerTypeCombo, ipTextfield, connectButton, beepButton, messages);
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

		increaseButton.setOnAction(event -> controller.increaseCounter());
		ledButton.setOnMousePressed(event -> {
			controller.setLedGlows(true);
		});
		ledButton.setOnMouseReleased(event -> controller.setLedGlows(false));

		ipTextfield.textProperty().addListener((observable, oldValue, newValue) -> {
			//TODO Validate IP address
//			if (validateIP(newValue)) {
//				System.out.println("Valid IP Address");
				controller.setIp(newValue);
//			}
//			else
//				System.err.println("Invalid IP Address");
		});

		connectButton.setOnAction(event -> {
			controller.toggleConnect();
		});

		beepButton.setOnAction(event -> controller.beep());

//		debuggerTypeCombo.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
//			@Override
//			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//				controller.setDebuggerType((Integer) newValue);
//			}
//		});
		debuggerTypeCombo.setOnAction(event -> controller.setDebuggerType(debuggerTypeCombo.getSelectionModel().getSelectedIndex()));

	}

	@Override
	public void setupModelToUiBindings(RBAModel model) {
		onChangeOf(
				model.systemInfo)                       // the value we need to observe, in this case that's an ObservableValue<String>, no need to convert it
				.update(infoLabel.textProperty());         // keeps textProperty and systemInfo in sync
		onChangeOf(
				model.counter)                          // the value we need to observe, in this case that's an ObservableValue<Integer>
				.convertedBy(String::valueOf)              // we have to convert the Integer to a String
				.update(counterLabel.textProperty());      // keeps textProperty and counter in sync
//		onChangeOf(model.ip).update(ipTextfield.textProperty());

		Converter<Integer> converterDebuggerType = onChangeOf(model.debuggerType);
		Updater<Integer,Boolean> updaterType = converterDebuggerType.convertedBy(integer -> integer.equals(2));
		updaterType.update(ipTextfield.visibleProperty());
		updaterType.update(beepButton.visibleProperty());

		Converter<Boolean> converterConnected = onChangeOf(model.connected);
		converterConnected.update(ipTextfield.disableProperty());
		converterConnected.update(debuggerTypeCombo.disableProperty());
		Updater<Boolean, Boolean> updater = converterConnected.convertedBy(aBoolean -> !aBoolean);
		updater.update(beepButton.disableProperty());

		converterConnected.convertedBy(aBoolean -> {
			if (aBoolean)
				return "Disconnect";
			else
				return "Connect";
		}).update(connectButton.textProperty());

		Converter<String> converterValidIP = onChangeOf(model.ip);
		Updater<String,Boolean> updaterValidIP = converterValidIP.convertedBy(s -> !validateIP(s));
		updaterValidIP.update(connectButton.disableProperty());

//		Converter<ObservableList<String>> converter =
		ListConverter<String> stringListConverter = onChangeOf(model.messagesList);
//		stringListConverter.update(messages.itemsProperty());

		//		onChangeOf(model.messagesList).convertedBy(new Function<List<String>, List<? extends Object>>() {
//			@Override
//			public List<? extends Object> apply(List<String> strings) {
//				return strings;
//			}
//		})

	}

	public boolean validateIP(final String ip) {
		Pattern pattern;
		Matcher matcher;
		String IPADDRESS_PATTERN
				= "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
				+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
		pattern = Pattern.compile(IPADDRESS_PATTERN);
		matcher = pattern.matcher(ip);
		return matcher.matches();
	}

}
