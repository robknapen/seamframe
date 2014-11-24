/*
 * seamframe: Svn.java
 * ==============================================================================
 * This work has been carried out as part of the SEAMLESS Integrated Framework
 * project, EU 6th Framework Programme, contract no. 010036-2 and/or as part
 * of the SEAMLESS association.
 *
 * Copyright (c) 2009 The SEAMLESS Association.
 *
 * For more information: http://www.seamlessassociation.org;
 * email: info@seamless-if.org
 *
 * The contents of this file is subject to the SEAMLESS Association License for
 * software infrastructure and model components Version 1.1 (the "License");
 * you may not use this file except in compliance with the License. You may
 * obtain a copy of the License at http://www.seamlessassociation.org/License.htm
 *
 * Software distributed under the License is distributed on an "AS IS"  basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License for
 * the specific governing rights and limitations.
 *
 * The Initial Developers of the Original Code are:
 *  - Ioannis Athanasiadis; IDSIA Dalle Molle Institute for Artificial Intelligence
 *  - Sander Janssen; Alterra, Wageningen UR
 *  - Benny Johnsson; Lund University
 *  - Rob Knapen; Alterra, Wageningen UR
 *  - Hongtao Li; IDSIA Dalle Molle Institute for Artificial Intelligence
 *  - Michiel Rop; Alterra, Wageningen UR / ilionX
 *  - Lorenzo Ruinelli; IDSIA Dalle Molle Institute for Artificial Intelligence
 *
 * ================================================================================
 * Contributor(s): N/A
 * ================================================================================
 */
package org.seamless_ip.core.utilities.filefunctions;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;
import org.seamless_if.processing.sofa.SeamException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.io.ISVNEditor;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.io.diff.SVNDeltaGenerator;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.Ostermiller.util.CircularByteBuffer;

/**
 * Commit a (zip) file to a repository.
 *
 * @author Benny Jonsson
 */
public class Svn {
    private static Logger logger = Logger.getLogger(Svn.class);

    private SVNURL url;
    private String userName;
    private String password;


    /**
     * @param url to repository
     */
    public Svn(String url) {
        this(url, null, null);
    }

    /**
     * @param url      to repository
     * @param userName repository user name
     * @param password repository password
     * @throws SeamException
     */
    public Svn(String url,
               String userName, String password) throws SeamException {
        try {
            this.url = SVNURL.parseURIEncoded(url);
            this.userName = userName;
            this.password = password;

        } catch (SVNException e) {
            logger.error(e.getMessage(), e);
            throw new SeamException(e);
        }

        setupLibrary();
    }

    /**
     * Add or update a file in the repository
     * Creates the dir if it doesn't exist
     *
     * @param experimentDir
     * @param zipFile
     */
    public void addUpdateFile(String experimentDir, String zipFile) {


        SVNRepository repository;


        try {
            repository = getAutRep();

            SVNNodeKind nodeKind;

            nodeKind = repository.checkPath(experimentDir, SVNRevision.BASE
                    .getNumber());
            if (nodeKind == SVNNodeKind.NONE) {
                createDir(experimentDir, repository);
            }
            // Get the name of the file
            String fileName = zipFile
                    .substring(zipFile.lastIndexOf(File.separator) + 1);

            // Checking if file exists in repository
            nodeKind = repository.checkPath(experimentDir + "/" + fileName,
                    SVNRevision.BASE.getNumber());

            FileInputStream fis = new FileInputStream(zipFile);

            InputStream inputStream = new BufferedInputStream(fis);

            if (nodeKind == SVNNodeKind.FILE) {
                updateFile(fileName, experimentDir, inputStream);
            } else if (nodeKind == SVNNodeKind.NONE) {
                addFile(fileName, experimentDir, inputStream);
            } else {
                throw new SeamException("What");
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
            throw new SeamException(e);
        } catch (SVNException e) {
            logger.error(e.getMessage(), e);
            throw new SeamException(e);
        }

    }

    private SVNRepository getAutRep() throws SVNException {
        SVNRepository repository;
        ISVNAuthenticationManager authManager = SVNWCUtil
                .createDefaultAuthenticationManager(userName, password);

        repository = SVNRepositoryFactory.create(this.url, null);
        repository.setAuthenticationManager(authManager);

        return repository;

    }

    private void updateFile(String fileName, String directory,
                            InputStream inputStream) throws SVNException {
        SVNProperties fileProperties = new SVNProperties();
        SVNRepository repository = SVNRepositoryFactory.create(this.url);
        SVNRepository repository2 = SVNRepositoryFactory.create(this.url);
        CircularByteBuffer cbb = new CircularByteBuffer(
                CircularByteBuffer.INFINITE_SIZE);

        try {
            ISVNEditor editor = repository.getCommitEditor(String.format(
                    "File %s updated.", fileName), null);
            editor.openRoot(SVNRevision.BASE.getNumber());
            editor.openDir(directory, SVNRevision.BASE.getNumber());
            editor.openFile(fileName, SVNRevision.BASE.getNumber());
            editor.applyTextDelta(fileName, null);

            repository2.getFile(directory + "/" + fileName, SVNRevision.BASE.getNumber(),
                    fileProperties, cbb.getOutputStream());

            SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
            String checksum = deltaGenerator.sendDelta(fileName, cbb
                    .getInputStream(), 0,
                    inputStream, editor, true);
            editor.closeFile(fileName, checksum);
            editor.closeEdit();
        } catch (SVNException e) {
            logger.error(e.getMessage(), e);
            throw new SeamException(e);
        }

    }

    /**
     * @param fileName
     * @param directory
     * @param inputStream
     * @throws SVNException
     */
    private void addFile(String fileName,
                         String directory, InputStream inputStream) throws SVNException {
        SVNRepository repository = getAutRep();
        ISVNEditor editor = repository.getCommitEditor(String.format(
                "File %s added.", fileName), null);
        editor.openRoot(-1);
        editor.openDir(directory, SVNRevision.BASE.getNumber());
        editor.addFile(fileName, null, SVNRevision.BASE.getNumber());
        editor.applyTextDelta(fileName, null);
        SVNDeltaGenerator deltaGenerator = new SVNDeltaGenerator();
        String checksum = deltaGenerator.sendDelta(fileName,
                inputStream, editor, true);
        editor.closeFile(fileName, checksum);
        editor.closeEdit();
    }

    /**
     * @param experimentDir
     * @param repository
     * @throws SVNException
     */
    private void createDir(String experimentDir, SVNRepository repository)
            throws SVNException {
        ISVNEditor editor = repository.getCommitEditor(String.format(
                "directory %s created", experimentDir), null);
        editor.openRoot(SVNRevision.BASE.getNumber());
        editor.addDir(experimentDir, null, SVNRevision.BASE.getNumber());
        editor.closeDir();
        editor.closeEdit();
    }

    /*
      * Initializes the library to work with a repository via
      * different protocols.
      */
    private static void setupLibrary() {
        /*
           * For using over http:// and https://
           */
        DAVRepositoryFactory.setup();
        /*
           * For using over svn:// and svn+xxx://
           */
        SVNRepositoryFactoryImpl.setup();

        /*
           * For using over file:///
           */
        FSRepositoryFactory.setup();
	}
}
