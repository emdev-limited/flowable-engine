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
package org.activiti.engine.test.bpmn.servicetask;

import org.flowable.engine.test.Deployment;

/**
 * @author Esteban Robles Luna
 */
public class WebServiceTaskTest extends AbstractWebServiceTaskTest {

    @Deployment
    public void testWebServiceInvocationWithoutDataFlow() throws Exception {
        assertEquals(-1, webServiceMock.getCount());

        processEngine.getRuntimeService().startProcessInstanceByKey("webServiceInvocationWithoutDataFlow");
        waitForJobExecutorToProcessAllJobs(10000L, 250L);

        assertEquals(0, webServiceMock.getCount());
    }
}
