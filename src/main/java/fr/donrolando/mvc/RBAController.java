package fr.donrolando.mvc;

import ch.atexxi.hw.atx.ui.i2cdebug.I2CDebuggerWebSocket;
import ch.atexxi.hw.atx.usbtoi2c.I2CDebuggerInterface;
import ch.atexxi.hw.atx.usbtoi2c.mcp2221.I2CDebuggerMCP2221;
import ch.atexxi.ipsc.gateway.ws.telegram.BuzzTelegram;
import com.pi4j.mvc.util.mvcbase.ControllerBase;
import li.strolch.utils.communication.PacketObserver;

public class RBAController extends ControllerBase<RBAModel> {

	private PacketObserver observer;
	private I2CDebuggerInterface debugger;
	public RBAController(RBAModel model) {
		super(model);
//		observer = new I2CDebugConnectionObserver()
	}

	// the logic we need in our application
	// these methods can be called from GUI and PUI (and from nowhere else)
	public void connect(){
		if (debugger instanceof I2CDebuggerWebSocket)
			((I2CDebuggerWebSocket) debugger).open(model.ip.getValue());
		else
			debugger.open();
//		if (debugger.isOpen())
		setValue(model.connected,true);
	}

	public void disconnect(){
		debugger.close();
//		if (!debugger.isConnected())
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

//		model.ip.setValue(text);  //Do it now !!!!!
		setValue(model.ip,text);
	}

	public void toggleConnect() {
		if (model.connected.getValue())
			disconnect();
		else {
			connect();
		}
	}
	public void beep() {
		if (model.connected.getValue()){
			if (debugger instanceof I2CDebuggerWebSocket){
				((I2CDebuggerWebSocket) debugger).telegramSend(new BuzzTelegram());
				model.messagesList.getValue().add("Buzz sent");
			}
		} else
			model.messagesList.getValue().add("Unable to send Buzz telegram, because debugger is not connected!");
	}

	public void setDebuggerType(int selectedIndex) {
		setValue(model.debuggerType,selectedIndex);
		switch (model.debuggerType.getValue()){
		case 0->{
			//			I2CDebugSerialPort serialPort = new I2CDebugSerialPort(this.model.getSerialPort(), this.observer);
			//			serialPort.setMinSleepTime(this.model.getMinWaitTime());
			//			serialPort.setMaxTries(1);
			//			serialPort.setThrowOnFail(false);
			//			serialPort.setPacketObserver(this.observer);
			//			debugger = new I2CDebuggerRBA(serialPort);
		}
		case 1 -> debugger = new I2CDebuggerMCP2221();
		case 2 -> {
			debugger = new I2CDebuggerWebSocket();
//			model.ip.setValue("");
			setValue(model.ip,"");
		}
		default -> throw new IllegalStateException("Unexpected value: " + model.debuggerType.getValue());
		}
	}
}
