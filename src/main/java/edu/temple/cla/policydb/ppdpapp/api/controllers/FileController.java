/* 
 * Copyright (c) 2018, Temple University
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
package edu.temple.cla.policydb.ppdpapp.api.controllers;


import edu.temple.cla.policydb.ppdpapp.api.daos.FileDAO;
import edu.temple.cla.policydb.ppdpapp.api.models.File;
import edu.temple.cla.policydb.ppdpapp.api.models.User;
import edu.temple.cla.policydb.ppdpapp.api.tables.Table;
import edu.temple.cla.policydb.ppdpapp.api.tables.TableLoader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileDAO fileDAO;
    @Autowired
    private TableLoader tableLoader;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getFiles() {
        return new ResponseEntity<>(fileDAO.list(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id:\\d+}")
    public ResponseEntity<?> getFile(@PathVariable int id, 
            @RequestParam(value = "user") User user) {
        return new ResponseEntity<>(fileDAO.find(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id:\\d+}/batches")
    public ResponseEntity<?> getBatchByFileID(@PathVariable int id, 
            @RequestParam(value = "user") User user) {
        return new ResponseEntity<>(fileDAO.findBatchByFileID(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    public ResponseEntity<?> postFile(@RequestBody File fileObj, 
            @RequestParam(value = "user") User user) {
        return new ResponseEntity<>(fileDAO.save(fileObj), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload")
    public ResponseEntity<?> uploadFile(@RequestParam(value = "user") User user,
            @RequestParam(value="tableId") int tableId,
            @RequestBody MultipartFile file) throws Exception {
        
        Table table = tableLoader.getTableById(tableId);
        return table.uploadFile(fileDAO, file);
    }
        
    @RequestMapping(method = RequestMethod.GET, value = "/download/{id:\\d+}")
    public void doDownload(HttpServletRequest request, 
            HttpServletResponse response) throws Exception {
        String requestURI = request.getRequestURI();
        int posLastSlash = requestURI.lastIndexOf("/");
        String idString;
        if (posLastSlash != -1) {
            idString = requestURI.substring(posLastSlash+1);
        } else {
            idString = requestURI;
        }
        int id = Integer.parseInt(idString);
        File fileObj = fileDAO.find(id);
        URI fileURI = new URI(fileObj.getFileURL());
        java.io.File javaFile = new java.io.File(fileURI);
        response.setContentType(fileObj.getContentType());
        response.setContentLength((int) javaFile.length());
        String headerKey = "Content-Disposition";
        String headerValue = String.format("attachment; filename=\"%s\"",
                javaFile.getName());
        response.setHeader(headerKey, headerValue);
        try (FileInputStream in = new FileInputStream(javaFile);
                OutputStream out = response.getOutputStream();) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (IOException ex) {
            throw(ex);
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }

}
