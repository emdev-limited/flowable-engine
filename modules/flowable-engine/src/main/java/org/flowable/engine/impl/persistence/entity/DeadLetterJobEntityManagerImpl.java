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

package org.flowable.engine.impl.persistence.entity;

import java.util.List;

import org.flowable.engine.common.impl.Page;
import org.flowable.engine.delegate.event.FlowableEngineEventType;
import org.flowable.engine.delegate.event.impl.FlowableEventBuilder;
import org.flowable.engine.impl.DeadLetterJobQueryImpl;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.persistence.CountingExecutionEntity;
import org.flowable.engine.impl.persistence.entity.data.DeadLetterJobDataManager;
import org.flowable.engine.runtime.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tijs Rademakers
 */
public class DeadLetterJobEntityManagerImpl extends AbstractEntityManager<DeadLetterJobEntity> implements DeadLetterJobEntityManager {

    private static final Logger logger = LoggerFactory.getLogger(DeadLetterJobEntityManagerImpl.class);

    protected DeadLetterJobDataManager jobDataManager;

    public DeadLetterJobEntityManagerImpl(ProcessEngineConfigurationImpl processEngineConfiguration, DeadLetterJobDataManager jobDataManager) {
        super(processEngineConfiguration);
        this.jobDataManager = jobDataManager;
    }

    @Override
    public List<DeadLetterJobEntity> findJobsByExecutionId(String id) {
        return jobDataManager.findJobsByExecutionId(id);
    }

    @Override
    public List<Job> findJobsByQueryCriteria(DeadLetterJobQueryImpl jobQuery, Page page) {
        return jobDataManager.findJobsByQueryCriteria(jobQuery, page);
    }

    @Override
    public long findJobCountByQueryCriteria(DeadLetterJobQueryImpl jobQuery) {
        return jobDataManager.findJobCountByQueryCriteria(jobQuery);
    }

    @Override
    public void updateJobTenantIdForDeployment(String deploymentId, String newTenantId) {
        jobDataManager.updateJobTenantIdForDeployment(deploymentId, newTenantId);
    }

    @Override
    public void insert(DeadLetterJobEntity jobEntity, boolean fireCreateEvent) {

        // add link to execution
        if (jobEntity.getExecutionId() != null) {
            ExecutionEntity execution = getExecutionEntityManager().findById(jobEntity.getExecutionId());

            // Inherit tenant if (if applicable)
            if (execution.getTenantId() != null) {
                jobEntity.setTenantId(execution.getTenantId());
            }

            if (isExecutionRelatedEntityCountEnabledGlobally()) {
                CountingExecutionEntity countingExecutionEntity = (CountingExecutionEntity) execution;
                if (isExecutionRelatedEntityCountEnabled(countingExecutionEntity)) {
                    countingExecutionEntity.setDeadLetterJobCount(countingExecutionEntity.getDeadLetterJobCount() + 1);
                }
            }
        }

        super.insert(jobEntity, fireCreateEvent);
    }

    @Override
    public void insert(DeadLetterJobEntity jobEntity) {
        insert(jobEntity, true);
    }

    @Override
    public void delete(DeadLetterJobEntity jobEntity) {
        super.delete(jobEntity);

        deleteExceptionByteArrayRef(jobEntity);
        deleteAdvancedJobHandlerConfigurationByteArrayRef(jobEntity);

        if (jobEntity.getExecutionId() != null && isExecutionRelatedEntityCountEnabledGlobally()) {
            CountingExecutionEntity executionEntity = (CountingExecutionEntity) getExecutionEntityManager().findById(jobEntity.getExecutionId());
            if (isExecutionRelatedEntityCountEnabled(executionEntity)) {
                executionEntity.setDeadLetterJobCount(executionEntity.getDeadLetterJobCount() - 1);
            }
        }

        // Send event
        if (getEventDispatcher().isEnabled()) {
            getEventDispatcher().dispatchEvent(FlowableEventBuilder.createEntityEvent(FlowableEngineEventType.ENTITY_DELETED, this));
        }
    }

    /**
     * Deletes a the byte array used to store the exception information. Subclasses may override to provide custom implementations.
     */
    protected void deleteExceptionByteArrayRef(DeadLetterJobEntity jobEntity) {
        ByteArrayRef exceptionByteArrayRef = jobEntity.getExceptionByteArrayRef();
        if (exceptionByteArrayRef != null) {
            exceptionByteArrayRef.delete();
        }
    }
    
    protected void deleteAdvancedJobHandlerConfigurationByteArrayRef(DeadLetterJobEntity jobEntity) {
        ByteArrayRef configurationByteArrayRef = jobEntity.getAdvancedJobHandlerConfigurationByteArrayRef();
        if (configurationByteArrayRef != null) {
            configurationByteArrayRef.delete();
        }
    }

    protected DeadLetterJobEntity createDeadLetterJob(AbstractJobEntity job) {
        DeadLetterJobEntity newJobEntity = create();
        newJobEntity.setJobHandlerConfiguration(job.getJobHandlerConfiguration());
        newJobEntity.setJobHandlerType(job.getJobHandlerType());
        newJobEntity.setExclusive(job.isExclusive());
        newJobEntity.setRepeat(job.getRepeat());
        newJobEntity.setRetries(job.getRetries());
        newJobEntity.setEndDate(job.getEndDate());
        newJobEntity.setExecutionId(job.getExecutionId());
        newJobEntity.setProcessInstanceId(job.getProcessInstanceId());
        newJobEntity.setProcessDefinitionId(job.getProcessDefinitionId());

        // Inherit tenant
        newJobEntity.setTenantId(job.getTenantId());
        newJobEntity.setJobType(job.getJobType());
        return newJobEntity;
    }

    protected DeadLetterJobDataManager getDataManager() {
        return jobDataManager;
    }

    public void setJobDataManager(DeadLetterJobDataManager jobDataManager) {
        this.jobDataManager = jobDataManager;
    }
}
