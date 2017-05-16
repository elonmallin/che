/*******************************************************************************
 * Copyright (c) 2012-2017 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.plugin.maven.server.projecttype;

import org.eclipse.che.api.core.ForbiddenException;
import org.eclipse.che.api.core.ServerException;
import org.eclipse.che.api.project.server.FileEntry;
import org.eclipse.che.api.project.server.FolderEntry;
import org.eclipse.che.api.project.server.type.ReadonlyValueProvider;
import org.eclipse.che.api.project.server.type.ValueStorageException;
import org.eclipse.che.commons.xml.XMLTreeException;
import org.eclipse.che.ide.maven.tools.Model;
import org.eclipse.che.ide.maven.tools.Resource;
import org.eclipse.che.maven.data.MavenResource;
import org.eclipse.che.plugin.maven.server.core.MavenProjectManager;
import org.eclipse.che.plugin.maven.server.core.project.MavenProject;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.singletonList;
import static org.eclipse.che.ide.ext.java.shared.Constants.SOURCE_FOLDER;
import static org.eclipse.che.plugin.maven.shared.MavenAttributes.ARTIFACT_ID;
import static org.eclipse.che.plugin.maven.shared.MavenAttributes.DEFAULT_PACKAGING;
import static org.eclipse.che.plugin.maven.shared.MavenAttributes.DEFAULT_RESOURCES_FOLDER;
import static org.eclipse.che.plugin.maven.shared.MavenAttributes.DEFAULT_SOURCE_FOLDER;
import static org.eclipse.che.plugin.maven.shared.MavenAttributes.DEFAULT_TEST_RESOURCES_FOLDER;
import static org.eclipse.che.plugin.maven.shared.MavenAttributes.DEFAULT_TEST_SOURCE_FOLDER;
import static org.eclipse.che.plugin.maven.shared.MavenAttributes.GROUP_ID;
import static org.eclipse.che.plugin.maven.shared.MavenAttributes.PACKAGING;
import static org.eclipse.che.plugin.maven.shared.MavenAttributes.PARENT_ARTIFACT_ID;
import static org.eclipse.che.plugin.maven.shared.MavenAttributes.PARENT_GROUP_ID;
import static org.eclipse.che.plugin.maven.shared.MavenAttributes.PARENT_VERSION;
import static org.eclipse.che.plugin.maven.shared.MavenAttributes.RESOURCE_FOLDER;
import static org.eclipse.che.plugin.maven.shared.MavenAttributes.TEST_SOURCE_FOLDER;
import static org.eclipse.che.plugin.maven.shared.MavenAttributes.VERSION;

/**
 * @author Vitalii Parfonov
 */
public class MavenValueProvider extends ReadonlyValueProvider {


    private MavenProjectManager mavenProjectManager;
    private FolderEntry         projectFolder;

    protected MavenValueProvider(MavenProjectManager mavenProjectManager, FolderEntry projectFolder) {
        this.mavenProjectManager = mavenProjectManager;
        this.projectFolder = projectFolder;
    }

    @Override
    public List<String> getValues(String attributeName) throws ValueStorageException {
        try {
            if (mavenProjectManager == null) {
                return readFromPom(attributeName);
            }

            final MavenProject mavenProject = mavenProjectManager.getMavenProject(projectFolder.getPath().toString());
            if (mavenProject != null) {
                return getFromMavenProject(mavenProject, attributeName);
            } else {
                return readFromPom(attributeName);
            }
        } catch (ServerException | ForbiddenException | IOException e) {
            throwReadException(e);
        } catch (XMLTreeException e) {
            throw new ValueStorageException("Error parsing pom.xml : " + e.getMessage());
        }
        return null;
    }

    private List<String> getFromMavenProject(MavenProject mavenProject, String attributeName) throws ValueStorageException {
        switch (attributeName) {
            case ARTIFACT_ID:
                return singletonList(mavenProject.getMavenKey().getArtifactId());
            case GROUP_ID:
                return singletonList(mavenProject.getMavenKey().getGroupId());
            case PACKAGING:
                String packaging = mavenProject.getPackaging();
                return singletonList(packaging != null ? packaging : DEFAULT_PACKAGING);
            case VERSION:
                return singletonList(mavenProject.getMavenKey().getVersion());
            case PARENT_ARTIFACT_ID:
                return singletonList(mavenProject.getParentKey() == null ? "" : mavenProject.getParentKey().getArtifactId());
            case PARENT_GROUP_ID:
                return singletonList(mavenProject.getParentKey() == null ? "" : mavenProject.getParentKey().getGroupId());
            case PARENT_VERSION:
                return singletonList(mavenProject.getParentKey() == null ? "" : mavenProject.getParentKey().getVersion());
            case SOURCE_FOLDER:
                return (mavenProject.getSources() != null && !mavenProject.getSources().isEmpty()) ? mavenProject.getSources()
                                                                                                   : singletonList(DEFAULT_SOURCE_FOLDER);
            case TEST_SOURCE_FOLDER:
                return (mavenProject.getTestSources() != null && !mavenProject.getTestSources().isEmpty()) ? mavenProject.getTestSources()
                                                                                                           : singletonList(
                                                                                                                   DEFAULT_TEST_SOURCE_FOLDER);
            case RESOURCE_FOLDER:
                if (mavenProject.getResources() != null && !mavenProject.getResources().isEmpty()) {
                    return mavenProject.getResources().stream().map(MavenResource::getDirectory).collect(Collectors.toList());
                } else {
                    return Arrays.asList(DEFAULT_RESOURCES_FOLDER, DEFAULT_TEST_RESOURCES_FOLDER);
                }
            default:
                throw new ValueStorageException(String.format("Unknown attribute %s", attributeName));
        }
    }


    private List<String> readFromPom(String attributeName)
            throws ServerException, ForbiddenException, IOException, XMLTreeException, ValueStorageException {
        final Model model = readModel(projectFolder);
        switch (attributeName) {
            case ARTIFACT_ID:
                return singletonList(model.getArtifactId());
            case GROUP_ID:
                return singletonList(model.getGroupId());
            case PACKAGING:
                String packaging = model.getPackaging();
                return singletonList(packaging != null ? packaging : DEFAULT_PACKAGING);
            case VERSION:
                return singletonList(model.getVersion());
            case PARENT_ARTIFACT_ID:
                return singletonList(model.getParent() == null ? "" : model.getParent().getArtifactId());
            case PARENT_GROUP_ID:
                return singletonList(model.getParent() == null ? "" : model.getParent().getGroupId());
            case PARENT_VERSION:
                return singletonList(model.getParent() == null ? "" : model.getParent().getVersion());
            case SOURCE_FOLDER:
                if (model.getBuild() != null && model.getBuild().getSourceDirectory() != null) {
                    return singletonList(model.getBuild().getSourceDirectory());
                } else {
                    return singletonList(DEFAULT_SOURCE_FOLDER);
                }
            case TEST_SOURCE_FOLDER:
                if (model.getBuild() != null && model.getBuild().getTestSourceDirectory() != null) {
                    return singletonList(model.getBuild().getTestSourceDirectory());
                } else {
                    return singletonList(DEFAULT_TEST_SOURCE_FOLDER);
                }
            case RESOURCE_FOLDER:
                if (model.getBuild() != null && model.getBuild().getResources() != null) {
                    return model.getBuild().getResources().stream().map(Resource::getDirectory).collect(Collectors.toList());
                } else {
                    return Arrays.asList(DEFAULT_RESOURCES_FOLDER, DEFAULT_TEST_RESOURCES_FOLDER);
                }
            default:
                throw new ValueStorageException(String.format("Unknown attribute %s", attributeName));
        }
    }

    protected Model readModel(FolderEntry projectFolder) throws ValueStorageException, ServerException, ForbiddenException, IOException {
        FileEntry pomFile = (FileEntry)projectFolder.getChild("pom.xml");
        if (pomFile == null) {
            throw new ValueStorageException("pom.xml does not exist.");
        }
        return Model.readFrom(pomFile.getInputStream());
    }

    protected void throwReadException(Exception e) throws ValueStorageException {
        throw new ValueStorageException("Can't read pom.xml : " + e.getMessage());
    }

}
