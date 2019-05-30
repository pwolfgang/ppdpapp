/*
 * Copyright (c) 2019, Temple University
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * * All advertising materials features or use of this software must display 
 *   the following  acknowledgement
 *   This product includes software developed by Temple University
 * * Neither the name of the copyright holder nor the names of its 
 *   contributors may be used to endorse or promote products derived 
 *   from this software without specific prior written permission. 
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package edu.temple.cla.policydb.ppdpapp.util;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * Utility programs to work with zip files.
 * @author Paul
 */
public class ZipUtil {
    
    /**
     * Method to unzip a zip file. This method creates a directory in the parent
     * directory of the zip file. It then unzips the component files and stores
     * them in the directory.
     * @param zipFileURLString URL pointing to the zip file.
     * @return File object of the created directory.
     */
    public static File unzipFiles(String zipFileURLString) {
        File zipFile;
        try {
            URL zipFileURL = new URL(zipFileURLString);
            URI zipFileURI = zipFileURL.toURI();
            zipFile = new File(zipFileURI);
        } catch (MalformedURLException | URISyntaxException ex) {
            // Cannot happen, but we will rethrow it if it does
            throw new RuntimeException(ex);
        }
        File parentDirectory = zipFile.getParentFile();
        String unzippedDirName = zipFile.getName();
        int posDot = unzippedDirName.indexOf(".zip");
        unzippedDirName = unzippedDirName.substring(0, posDot);
        File unzippedDir = new File(parentDirectory, unzippedDirName);
        unzippedDir.mkdir();
        try (ZipFile input = new ZipFile(zipFile)) {
            Enumeration<? extends ZipEntry> zipEntries = input.entries();
            while (zipEntries.hasMoreElements()) {
                ZipEntry zipEntry = zipEntries.nextElement();
                String entryName = zipEntry.getName();
                String[] entryNameParts = entryName.split("[/\\\\]");
                entryName = entryNameParts[entryNameParts.length-1];
                File destinationFile = new File(unzippedDir, entryName);
                Path destination = destinationFile.toPath();
                Files.copy(input.getInputStream(zipEntry), destination, REPLACE_EXISTING);
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    return unzippedDir;    
    }
    
}
