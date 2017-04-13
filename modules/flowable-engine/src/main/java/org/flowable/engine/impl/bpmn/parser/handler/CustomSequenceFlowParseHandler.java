package org.flowable.engine.impl.bpmn.parser.handler;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.impl.bpmn.parser.BpmnParse;
import org.apache.commons.lang3.StringUtils;

public class CustomSequenceFlowParseHandler extends SequenceFlowParseHandler {

	@Override
	protected void executeParse(BpmnParse bpmnParse, SequenceFlow sequenceFlow) {
		super.executeParse(bpmnParse, sequenceFlow);
		
		BpmnModel scope = bpmnParse.getBpmnModel();
		FlowElement sourceActivity = scope.getFlowElement(sequenceFlow.getSourceRef());
		FlowElement destinationActivity = scope.getFlowElement(sequenceFlow.getTargetRef());
	    
	    if (sourceActivity != null && sourceActivity instanceof ExclusiveGateway) {
	    	if (StringUtils.isEmpty(sequenceFlow.getConditionExpression())) {
	    		//Turn flow name into expression
	    		if (!StringUtils.isEmpty(sequenceFlow.getName())) {
	    			String expression = "${outputTransition == \"" + sequenceFlow.getName() + "\"}";
		    		sequenceFlow.setConditionExpression(expression);
		            //do we need it??
		            for (FlowableListener fl : sequenceFlow.getExecutionListeners()) {
		            	createExecutionListener(bpmnParse, fl);
		            }
		            FormPostProcessorThreadLocalUtil.putToThreadLocal(null, sourceActivity, sequenceFlow.getName());
	    		}
	    	}	
	    }
	    
	    if (destinationActivity != null && destinationActivity instanceof ExclusiveGateway
	    		&& sourceActivity != null && sourceActivity instanceof UserTask) {
	    	if (StringUtils.isEmpty(sequenceFlow.getConditionExpression())) {
	    		//put it into thread local for post-processing. Use {@link FormPostProcessorWrapper}
		    	FormPostProcessorThreadLocalUtil.putToThreadLocal(sourceActivity, destinationActivity, null);
	    	}
	    }
	    //Remove standard Done action name in the Portal
	    if (destinationActivity != null && !(destinationActivity instanceof ExclusiveGateway)
	    		&& sourceActivity != null && sourceActivity instanceof UserTask) {
	    	if (!StringUtils.isEmpty(sequenceFlow.getName())) {
	    		FormPostProcessorWrapper obj = new FormPostProcessorWrapper(sourceActivity, null);
	    		obj.getOutputTransitionNames().add(sequenceFlow.getName());
	    		FormPostProcessorThreadLocalUtil.putToThreadLocal(obj);
	    	}
	    }
	    
	}
}
