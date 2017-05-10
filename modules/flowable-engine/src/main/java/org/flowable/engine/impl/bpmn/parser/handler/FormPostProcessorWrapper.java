package org.flowable.engine.impl.bpmn.parser.handler;

import java.util.ArrayList;
import java.util.List;

import org.flowable.bpmn.model.FlowElement;

public class FormPostProcessorWrapper {
	
	public FormPostProcessorWrapper(FlowElement sourceActivity, FlowElement destinationActivity) {
		this.sourceActivity = sourceActivity;
		this.destinationActivity = destinationActivity;
	}

	FlowElement sourceActivity;
	FlowElement destinationActivity;
	List<String> outputTransitionNames = new ArrayList<String>();
	
	public FlowElement getSourceActivity() {
		return sourceActivity;
	}
	public void setSourceActivity(FlowElement sourceActivity) {
		this.sourceActivity = sourceActivity;
	}
	public FlowElement getDestinationActivity() {
		return destinationActivity;
	}
	public void setDestinationActivity(FlowElement destinationActivity) {
		this.destinationActivity = destinationActivity;
	}
	public List<String> getOutputTransitionNames() {
		return outputTransitionNames;
	}
	public void setOutputTransitionNames(List<String> outputTransitionNames) {
		this.outputTransitionNames = outputTransitionNames;
	}
	
}
