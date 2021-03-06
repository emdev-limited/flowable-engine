/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.variable.service.event.impl;

import org.flowable.engine.common.api.FlowableIllegalArgumentException;
import org.flowable.engine.common.api.delegate.event.FlowableEvent;
import org.flowable.engine.common.api.delegate.event.FlowableExceptionEvent;
import org.flowable.variable.service.event.FlowableVariableServiceEntityEvent;
import org.flowable.variable.service.event.FlowableVariableServiceEventType;

/**
 * Base class for all {@link FlowableEvent} implementations, represents an exception occurred, related to an entity.
 * 
 * @author Frederik Heremans
 */
public class FlowableEntityExceptionEventImpl extends FlowableEventImpl implements FlowableVariableServiceEntityEvent, FlowableExceptionEvent {

    protected Object entity;
    protected Throwable cause;

    public FlowableEntityExceptionEventImpl(Object entity, FlowableVariableServiceEventType type, Throwable cause) {
        super(type);
        if (entity == null) {
            throw new FlowableIllegalArgumentException("Entity cannot be null.");
        }
        this.entity = entity;
        this.cause = cause;
    }

    @Override
    public Object getEntity() {
        return entity;
    }

    @Override
    public Throwable getCause() {
        return cause;
    }
}
