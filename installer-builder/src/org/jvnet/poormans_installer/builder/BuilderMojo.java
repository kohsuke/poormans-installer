package org.jvnet.poormans_installer.builder;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.factory.ArtifactFactory;
import org.apache.tools.ant.BuildException;

import java.io.File;

/**
 * Packages the main artifact of the project into a self-extractable installer jar file.
 *
 * @author Kohsuke Kawaguchi
 * @goal installer
 * @requiresProject
 */
public class BuilderMojo extends AbstractMojo {
    /**
     * The maven project.
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    protected MavenProject project;

    /**
     * Sets the name of the output file when compiling a set of
     * sources to a single file.
     *
     * @parameter expression="${outputFileName}" default-value="${project.build.directory}/${project.build.finalName}-installer.jar"
     */
    private File outputFileName;

    /**
     * License file to be displayed.
     *
     * @parameter expression="${license}
     * @required
     */
    private File licenseFile;

    /**
     * @component
     */
    protected ArtifactFactory artifactFactory;

    public void execute() throws MojoExecutionException, MojoFailureException {
        Artifact a = project.getArtifact();
        if(a==null || a.getFile()==null)
            throw new MojoExecutionException("Project has no main artifact");
        if(licenseFile==null)
            throw new MojoExecutionException("No license file specified");
        
        try {
            Main.build(licenseFile,a.getFile(),outputFileName);
        } catch (Exception e) {
            throw new BuildException(e);
        }

        // attach the artifact
        Artifact installer = artifactFactory.createArtifactWithClassifier(
            project.getGroupId(),project.getArtifactId(),project.getVersion(),
            "jar","installer");
        installer.setFile(outputFileName);
        project.getAttachedArtifacts().add(installer);

    }
}
