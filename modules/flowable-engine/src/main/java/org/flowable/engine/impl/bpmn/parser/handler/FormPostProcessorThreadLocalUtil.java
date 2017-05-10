package org.flowable.engine.impl.bpmn.parser.handler;

import java.util.ArrayList;
import java.util.List;

import org.flowable.bpmn.model.FlowElement;
import org.apache.commons.lang3.StringUtils;

public class FormPostProcessorThreadLocalUtil {

	private static ThreadLocal<List<FormPostProcessorWrapper>> LOCALS = new ThreadLocal<List<FormPostProcessorWrapper>>();
	
	public static List<FormPostProcessorWrapper> getFromThreadLocal() {
		return LOCALS.get();
	}
	
	public static FormPostProcessorWrapper getFromThreadLocalBySourceAndDest(FlowElement sourceActivity, FlowElement destinationActivity) {
		if (sourceActivity == null || destinationActivity == null) return null;
		List<FormPostProcessorWrapper> objectList = LOCALS.get();
		if (objectList != null) {
			for (FormPostProcessorWrapper obj : objectList) {
				if (obj == null || obj.getSourceActivity() == null || obj.getDestinationActivity() == null) {
					continue;
				}
				if (obj.getSourceActivity().equals(sourceActivity) && obj.getDestinationActivity().equals(destinationActivity)) {
					return obj;
				}
			}
		}
		return null;
	}
	
	public static FormPostProcessorWrapper getFromThreadLocalBySource(FlowElement sourceActivity) {
		if (sourceActivity == null) return null;
		List<FormPostProcessorWrapper> objectList = LOCALS.get();
		if (objectList != null) {
			for (FormPostProcessorWrapper obj : objectList) {
				if (obj.getSourceActivity().equals(sourceActivity)) {
					return obj;
				}
			}
		}
		return null;
	}
	
	public static FormPostProcessorWrapper getFromThreadLocalByDestination(FlowElement destinationActivity) {
		if (destinationActivity == null) return null;
		List<FormPostProcessorWrapper> objectList = LOCALS.get();
		if (objectList != null) {
			for (FormPostProcessorWrapper obj : objectList) {
				if (obj == null || obj.getDestinationActivity() == null) {
					continue;
				}
				if (obj.getDestinationActivity().equals(destinationActivity)) {
					return obj;
				}
			}
		}
		return null;
	}
	
	public static void putToThreadLocal(FormPostProcessorWrapper formWrapper) {
		if (LOCALS.get() == null) {
			LOCALS.set(new ArrayList<FormPostProcessorWrapper>());
		}
		LOCALS.get().add(formWrapper);
	}
	
	public static void putToThreadLocal(FlowElement sourceActivity, FlowElement destinationActivity, String name) {
		if (destinationActivity == null) return;
		//check if it already exists
		FormPostProcessorWrapper existing = getFromThreadLocalByDestination(destinationActivity);
		if (existing != null) {
			if (!StringUtils.isEmpty(name)) {
				//put sequence flow info
				existing.getOutputTransitionNames().add(name);
			}
			if (sourceActivity != null) {
				existing.setSourceActivity(sourceActivity);
			}
		} else {
			FormPostProcessorWrapper obj = new FormPostProcessorWrapper(sourceActivity, destinationActivity);
			if (!StringUtils.isEmpty(name)) {
				//put sequence flow info
				obj.getOutputTransitionNames().add(name);
			}
			if (LOCALS.get() == null) {
				LOCALS.set(new ArrayList<FormPostProcessorWrapper>());
			}
			LOCALS.get().add(obj);
		}
	}
	
	public static void cleanUp() {
		LOCALS.set(new ArrayList<FormPostProcessorWrapper>());
	}
}
