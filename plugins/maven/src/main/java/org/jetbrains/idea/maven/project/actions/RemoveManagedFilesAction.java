/*
 * Copyright 2000-2010 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jetbrains.idea.maven.project.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.idea.maven.project.MavenProjectsManager;
import org.jetbrains.idea.maven.utils.MavenUtil;
import org.jetbrains.idea.maven.utils.actions.MavenAction;
import org.jetbrains.idea.maven.utils.actions.MavenActionUtil;

public class RemoveManagedFilesAction extends MavenAction {
  @Override
  protected boolean isAvailable(AnActionEvent e) {
    if (!super.isAvailable(e)) return false;

    return !MavenActionUtil.getMavenProjectsFiles(e.getDataContext()).isEmpty();
  }

  @Override
  public void actionPerformed(AnActionEvent e) {
    final DataContext context = e.getDataContext();

    boolean hasManagedFiles = false;

    MavenProjectsManager projectsManager = MavenActionUtil.getProjectsManager(context);

    for (VirtualFile each : MavenActionUtil.getMavenProjectsFiles(context)) {
      if (projectsManager.isManagedFile(each)) {
        hasManagedFiles = true;
        break;
      }
    }

    if (!hasManagedFiles) {
      Notification notification = new Notification(MavenUtil.MAVEN_NOTIFICATION_GROUP, "Failed to remove project",
                                                   "You can not remove selected project because it's " +
                                                   "imported as module of other project. You can ignore it. Only root project can be removed.",
                                                   NotificationType.ERROR);

      notification.setImportant(true);
      notification.notify(MavenActionUtil.getProject(context));
      return;
    }

    projectsManager.removeManagedFiles(MavenActionUtil.getMavenProjectsFiles(context));
  }
}
