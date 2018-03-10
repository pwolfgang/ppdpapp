package edu.temple.cla.policydb.ppdpapp.api.controllers;


import edu.temple.cla.policydb.ppdpapp.api.daos.BatchDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.FileDAO;
import edu.temple.cla.policydb.ppdpapp.api.models.File;
import edu.temple.cla.policydb.ppdpapp.api.models.User;
import edu.temple.cla.policydb.ppdpapp.api.services.Account;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.List;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
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
    private BatchDAO batchDAO;
    @Autowired
    private Account accountSvc;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> getFiles() {
        return new ResponseEntity<List<File>>(fileDAO.list(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id:\\d+}")
    public ResponseEntity<?> getFile(@PathVariable int id, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<File>(fileDAO.find(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{id:\\d+}/batches")
    public ResponseEntity<?> getBatchByFileID(@PathVariable int id, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<Object>(fileDAO.findBatchByFileID(id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/update")
    public ResponseEntity<?> postFile(@RequestBody File fileObj, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<File>(fileDAO.save(fileObj), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/upload")
    public ResponseEntity<?> uploadFile(@RequestParam(value = "token") String token,
            @RequestBody MultipartFile file) throws Exception {

        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<String>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        String fileName = file.getOriginalFilename();
        java.io.File baseDir = new java.io.File("/var/ppdp/files");
        java.io.File javaFile = new java.io.File(baseDir, fileName);
        File fileObj = new File();
        try {
            URL fileURL = javaFile.toURI().toURL();
            fileObj.setFileURL(fileURL.toString());
        } catch (MalformedURLException ex) {
            // cannot happen
        }
        fileObj.setContentType(file.getContentType());

        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                BufferedOutputStream stream
                        = new BufferedOutputStream(new FileOutputStream(javaFile));
                stream.write(bytes);
                stream.close();
                fileObj = fileDAO.save(fileObj);
                return new ResponseEntity<File>(fileObj, HttpStatus.OK);
            } catch (Exception e) {
                return new ResponseEntity<String>(e.toString(), HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<String>("file NOT upload No DATA", HttpStatus.NOT_FOUND);
        }

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
