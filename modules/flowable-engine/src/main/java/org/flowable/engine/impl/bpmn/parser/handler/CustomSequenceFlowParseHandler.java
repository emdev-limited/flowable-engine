package org.flowable.engine.impl.bpmn.parser.handler;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.impl.bpmn.parser.BpmnParse;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class CustomSequenceFlowParseHandler extends SequenceFlowParseHandler {

	@Override
	protected void executeParse(BpmnParse bpmnParse, SequenceFlow sequenceFlow) {
		super.executeParse(bpmnParse, sequenceFlow);
		
		BpmnModel scope = bpmnParse.getBpmnModel();
		FlowElement sourceActivity = scope.getFlowElement(sequenceFlow.getSourceRef());
		FlowElement destinationActivity = scope.getFlowElement(sequenceFlow.getTargetRef());
	    
	    if (sourceActivity != null && sourceActivity instanceof ExclusiveGateway) {
	    	if (StringUtils.isEmpty(sequenceFlow.getConditionExpression())) {
	    		// check - is it default Condition
    			ExclusiveGateway sourceGateway = (ExclusiveGateway)sourceActivity;
    			boolean isDefault = sequenceFlow.getId().equals(sourceGateway.getDefaultFlow());
    			
	    		//Turn flow name into expression
	    		if (!isDefault && !StringUtils.isEmpty(sequenceFlow.getName())) {
	    			String expression = "${outputTransition == \"" + sequenceFlow.getName().trim() + "\"}";
		    		sequenceFlow.setConditionExpression(expression);
		            //do we need it??
		            for (FlowableListener fl : sequenceFlow.getExecutionListeners()) {
		            	createExecutionListener(bpmnParse, fl);
		            }
					boolean hasNoUserTaskSourceActivity = putOutputTransitionsForUserTasksToLocalThread(
							sequenceFlow, sourceActivity);
					if (hasNoUserTaskSourceActivity) {
						FormPostProcessorThreadLocalUtil.putToThreadLocal(null, sourceActivity, sequenceFlow.getName());
					}
	    		}
	    	}	
	    }
	    
	    if (destinationActivity != null && destinationActivity instanceof ExclusiveGateway
	    		&& sourceActivity != null && sourceActivity instanceof UserTask) {
	    	if (StringUtils.isEmpty(sequenceFlow.getConditionExpression())) {
	    		//put it into thread local for post-processing. Use {@link FormPostProcessorWrapper}
		    	FormPostProcessorThreadLocalUtil.putUserTaskToExclusiveGatewayFlowToThreadLocal(sourceActivity, destinationActivity, null);
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

	/**
	 * If incoming flows to Exclusive gateway comes from UserTasks - put to thread local output transitions to each
	 * user task
	 */
	private boolean putOutputTransitionsForUserTasksToLocalThread(SequenceFlow sequenceFlow, FlowElement sourceActivity) {
		boolean hasNoUserTaskSourceActivity = true;
		List<SequenceFlow> incomingFlows = ((ExclusiveGateway) sourceActivity).getIncomingFlows();
		for (SequenceFlow incomingFlow : incomingFlows) {
			FlowElement sourceFlowElement = incomingFlow.getSourceFlowElement();
			if (sourceFlowElement != null && sourceFlowElement instanceof UserTask) {
				FormPostProcessorThreadLocalUtil.putToThreadLocal(sourceFlowElement, sourceActivity,
						sequenceFlow.getName());
				hasNoUserTaskSourceActivity = false;
			}
		}
		return hasNoUserTaskSourceActivity;
	}
}
