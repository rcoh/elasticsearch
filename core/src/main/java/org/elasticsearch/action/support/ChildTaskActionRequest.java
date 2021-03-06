/*
 * Licensed to Elasticsearch under one or more contributor
 * license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright
 * ownership. Elasticsearch licenses this file to you under
 * the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.elasticsearch.action.support;

import org.elasticsearch.action.ActionRequest;
import org.elasticsearch.common.io.stream.StreamInput;
import org.elasticsearch.common.io.stream.StreamOutput;
import org.elasticsearch.tasks.Task;

import java.io.IOException;

/**
 * Base class for action requests that can have associated child tasks
 */
public abstract class ChildTaskActionRequest<Request extends ActionRequest<Request>> extends ActionRequest<Request> {

    private String parentTaskNode;

    private long parentTaskId;

    protected ChildTaskActionRequest() {

    }

    public void setParentTask(String parentTaskNode, long parentTaskId) {
        this.parentTaskNode = parentTaskNode;
        this.parentTaskId = parentTaskId;
    }

    /**
     * The node that owns the parent task.
     */
    public String getParentTaskNode() {
        return parentTaskNode;
    }

    /**
     * The task id of the parent task on the parent node.
     */
    public long getParentTaskId() {
        return parentTaskId;
    }

    @Override
    public void readFrom(StreamInput in) throws IOException {
        super.readFrom(in);
        parentTaskNode = in.readOptionalString();
        parentTaskId = in.readLong();
    }

    @Override
    public void writeTo(StreamOutput out) throws IOException {
        super.writeTo(out);
        out.writeOptionalString(parentTaskNode);
        out.writeLong(parentTaskId);
    }

    @Override
    public Task createTask(long id, String type, String action) {
        return new Task(id, type, action, this::getDescription, parentTaskNode, parentTaskId);
    }

}
