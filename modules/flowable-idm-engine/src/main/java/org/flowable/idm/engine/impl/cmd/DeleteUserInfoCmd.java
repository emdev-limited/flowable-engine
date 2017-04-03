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

package org.flowable.idm.engine.impl.cmd;

import java.io.Serializable;

import org.flowable.idm.engine.impl.interceptor.Command;
import org.flowable.idm.engine.impl.interceptor.CommandContext;

/**
 * @author Tom Baeyens
 */
public class DeleteUserInfoCmd implements Command<Object>, Serializable {

    private static final long serialVersionUID = 1L;
    protected String userId;
    protected String key;

    public DeleteUserInfoCmd(String userId, String key) {
        this.userId = userId;
        this.key = key;
    }

    public String execute(CommandContext commandContext) {
        commandContext.getIdentityInfoEntityManager().deleteUserInfoByUserIdAndKey(userId, key);
        return null;
    }
}
