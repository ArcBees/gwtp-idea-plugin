/**
 * Copyright 2013 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.arcbees.plugin.idea.moduletypes;

import com.arcbees.plugin.idea.icons.PluginIcons;
import com.intellij.openapi.module.ModuleType;

import javax.swing.*;

public class CreateProjectModuleType extends ModuleType<CreateProjectBuilder> {
    public static final String MODULE_TYPE_ID = "GWTP_CREATE_PROJECT_MODULE";

    public CreateProjectModuleType() {
        super(MODULE_TYPE_ID);
    }

    @Override
    public CreateProjectBuilder createModuleBuilder() {
        return new CreateProjectBuilder();
    }

    @Override
    public String getName() {
        return "Create GWTP Project";
    }

    @Override
    public String getDescription() {
        return "Create a new GWT-Platform (GWTP) project.";
    }

    @Override
    public Icon getBigIcon() {
        // TODO 24x24
        return PluginIcons.GWTP_ICON_16x16;
    }

    @Override
    public Icon getNodeIcon(@Deprecated boolean b) {
        return PluginIcons.GWTP_ICON_16x16;
    }
}
