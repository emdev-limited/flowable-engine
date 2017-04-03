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

package org.flowable.engine.impl.variable;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.flowable.engine.common.api.FlowableException;
import org.flowable.engine.common.impl.interceptor.Session;

/**
 * @author Frederik Heremans
 */
public interface EntityManagerSession extends Session {
    /**
     * Get an {@link EntityManager} instance associated with this session.
     * 
     * @throws FlowableException
     *             when no {@link EntityManagerFactory} instance is configured for the process engine.
     */
    EntityManager getEntityManager();
}
