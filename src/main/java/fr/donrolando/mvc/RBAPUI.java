package fr.donrolando.mvc;

import com.pi4j.context.Context;
import com.pi4j.mvc.util.mvcbase.PuiBase;

public class RBAPUI extends PuiBase<RBAModel,RBAController> {

	public RBAPUI(RBAController controller) {
		super(controller,null);
	}
	public RBAPUI(RBAController controller, Context pi4J) {
		super(controller, pi4J);
	}

	@Override
	public void init(RBAController controller) {
		super.init(controller);
	}

	@Override
	public void initializeSelf() {
		super.initializeSelf();
	}

	@Override
	public void initializeParts() {

	}

	@Override
	public void setupUiToActionBindings(RBAController controller) {
		super.setupUiToActionBindings(controller);
	}

	@Override
	public void setupModelToUiBindings(RBAModel model) {
		super.setupModelToUiBindings(model);
	}
}
