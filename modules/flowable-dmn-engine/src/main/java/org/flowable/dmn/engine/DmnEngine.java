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
package org.flowable.dmn.engine;

import org.flowable.dmn.api.DmnManagementService;
import org.flowable.dmn.api.DmnRepositoryService;
import org.flowable.dmn.api.DmnRuleService;

public interface DmnEngine {

    /**
     * the version of the flowable dmn library
     */
    public static String VERSION = "6.0.1.0"; // Note the extra .x at the end. To cater for snapshot releases with different database changes

    /**
     * The name as specified in 'dmn-engine-name' in the flowable.dmn.cfg.xml configuration file. The default name for a process engine is 'default
     */
    String getName();

    void close();

    DmnManagementService getDmnManagementService();

    DmnRepositoryService getDmnRepositoryService();

    DmnRuleService getDmnRuleService();

    DmnEngineConfiguration getDmnEngineConfiguration();
}
