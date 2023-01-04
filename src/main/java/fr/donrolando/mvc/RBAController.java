package fr.donrolando.mvc;

import java.security.InvalidParameterException;

import ch.atexxi.hw.atx.ui.i2cdebug.I2CDebuggerWebSocket;
import com.pi4j.mvc.templateapp.model.SomeModel;
import com.pi4j.mvc.util.mvcbase.ControllerBase;

public class RBAController extends ControllerBase<RBAModel> {

	private I2CDebuggerWebSocket debugger;
	public RBAController(RBAModel model) {
		super(model);
		debugger = new I2CDebuggerWebSocket();
	}

	// the logic we need in our application
	// these methods can be called from GUI and PUI (and from nowhere else)
	public void connect(){
		if (model.ip.getValue().equals(null))
			throw new InvalidParameterException("ip must be set");
		debugger.open(model.ip.getValue());
		if (debugger.isConnected())
			setValue(model.connected,true);
	}

	public void disconnect(){
		debugger.close();
		if (!debugger.isConnected())
			setValue(model.connected,false);
	}
	public void increaseCounter() {
		increase(model.counter);
	}

	public void decreaseCounter() {
		decrease(model.counter);
	}

	public void setLedGlows(boolean glows){
		setValue(model.ledGlows, glows);
	}

	public void setIp(String text) {
		setValue(model.ip,text);
	}

	public void toggleConnect() {
		if (model.connected.getValue())
			disconnect();
		else {
			connect();
		}
	}
}
