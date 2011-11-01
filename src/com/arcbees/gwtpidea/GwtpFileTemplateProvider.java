package com.arcbees.gwtpidea;

import com.intellij.ide.fileTemplates.FileTemplateDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptor;
import com.intellij.ide.fileTemplates.FileTemplateGroupDescriptorFactory;
import com.intellij.openapi.fileTypes.StdFileTypes;

public class GwtpFileTemplateProvider implements FileTemplateGroupDescriptorFactory {

  @Override
  public FileTemplateGroupDescriptor getFileTemplatesDescriptor() {
    final FileTemplateGroupDescriptor group = new FileTemplateGroupDescriptor("GWT-Platform", null);

    group.addTemplate(new FileTemplateDescriptor("GWT-Platform Action.java", StdFileTypes.JAVA.getIcon()));
    group.addTemplate(new FileTemplateDescriptor("GWT-Platform Event.java", StdFileTypes.JAVA.getIcon()));
    group.addTemplate(new FileTemplateDescriptor("GWT-Platform Presenter.java", StdFileTypes.JAVA.getIcon()));
    group.addTemplate(new FileTemplateDescriptor("GWT-Platform Result.java", StdFileTypes.JAVA.getIcon()));
    group.addTemplate(new FileTemplateDescriptor("GWT-Platform View.java", StdFileTypes.JAVA.getIcon()));

    return group;
  }

}
