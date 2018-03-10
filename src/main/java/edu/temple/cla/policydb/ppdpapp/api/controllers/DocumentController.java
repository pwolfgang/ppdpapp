package edu.temple.cla.policydb.ppdpapp.api.controllers;

import edu.temple.cla.policydb.ppdpapp.api.daos.DocumentDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.NewsClipTypeDAO;
import edu.temple.cla.policydb.ppdpapp.api.daos.UserDAO;
import edu.temple.cla.policydb.ppdpapp.api.models.User;
import edu.temple.cla.policydb.ppdpapp.api.services.Account;
import edu.temple.cla.policydb.ppdpapp.api.tables.Table;
import edu.temple.cla.policydb.ppdpapp.api.tables.TableLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/documents")
public class DocumentController {

    @Autowired
    DocumentDAO documentDAO;
    @Autowired
    private UserDAO userDAO;
    @Autowired
    private Account accountSvc;
    @Autowired
    private NewsClipTypeDAO newsClipTypeDAO;
    @Autowired
    private TableLoader tableLoader;

    @RequestMapping(method = RequestMethod.GET, value = "/{tableName}")
    public ResponseEntity getDocuments(@PathVariable String tableName, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(documentDAO.findDocuments(tableName), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{tableName}/page/{page}")

    public ResponseEntity<?> getDocumentsPaged(@PathVariable String tableName,
            @PathVariable int page, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(documentDAO.findDocumentsPage(tableName, page), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{tableName}/{id}")
    public ResponseEntity<?> getDocument(@PathVariable String tableName,
            @PathVariable String id, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
            // The id parameter may be a command
            switch (id) {
                case "count":
                    return new ResponseEntity<>(documentDAO.getDocumentCount(tableName), HttpStatus.OK);
                default:
                    return new ResponseEntity<>(documentDAO.findDocument(tableName, id), HttpStatus.OK);
            }
        }

    @RequestMapping(method = RequestMethod.GET, value = "/{tableName}/nobatch/{assignmentType}/{batch_id}")
    public ResponseEntity<?> getDocument(@PathVariable String tableName,
            @PathVariable int assignmentType, @PathVariable int batch_id, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
            return new ResponseEntity<>(documentDAO.findDocumentsNoBatch(tableName, assignmentType, batch_id), HttpStatus.OK);
        }

    @RequestMapping(method = RequestMethod.GET, value = "/{tableName}/{id}/codes")
    public ResponseEntity<?> getDocumentCodes(@PathVariable String tableName, @PathVariable String id, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(documentDAO.findDocumentCodes(tableName, id), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{tableName}/{id}/code")
    public ResponseEntity<?> getDocumentCode(@PathVariable String tableName, @PathVariable String id, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(documentDAO.findDocumentCode(tableName, id, user.getEmail()), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{tableName}/{docid}/batch/{batchid}/add/code/{codeid}")
    public ResponseEntity<?> addDocumentCodes(@PathVariable String tableName, @PathVariable String docid, @PathVariable String batchid, @PathVariable String codeid, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        int code;
        try {
            code = Integer.parseInt(codeid);
            documentDAO.addDocumentCode(user.getEmail(), tableName, docid, batchid, code);
        } catch (NumberFormatException nfex) {
            if (!"null".equals(codeid)) {
                return new ResponseEntity<>("Bac code value " + codeid, HttpStatus.BAD_REQUEST);
            }
        }
        // Either way, this is OK.
        return new ResponseEntity<>("document code added, bud", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/{tableName}")
    public ResponseEntity<?> updateDocument(@RequestBody Object docObj, @PathVariable String tableName, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        documentDAO.updateDocument(tableName, docObj);
        return new ResponseEntity<>("document updated, lad", HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{tableName}")
    public ResponseEntity<?> insertDocument(@RequestBody Object docObj, @PathVariable String tableName, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        int docID = documentDAO.insertDocument(tableName, docObj);
        return new ResponseEntity<>(docID, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{tableName}/batch/{batchid}/nocodes")
    public ResponseEntity<?> getDocumentNoCodes(@PathVariable String tableName, @PathVariable int batchid, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(documentDAO.findDocumentsNoCodes(tableName, batchid, user.getEmail()), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/{tableName}/batch/{batchid}/tiebreak")
    public ResponseEntity<?> getDocumentTieBreak(@PathVariable String tableName, @PathVariable int batchid, @RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(documentDAO.findDocumentsTieBreak(tableName, batchid, user.getEmail()), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, value = "/NewsClips/type")
    public ResponseEntity<?> getNewsClipType(@RequestParam(value = "token") String token) {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }
        return new ResponseEntity<>(newsClipTypeDAO.list(), HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.POST, value = "/{tableName}/upload")
    public ResponseEntity<?> uploadFile(
            @PathVariable String tableName,
            @RequestParam(value = "token") String token,
            @RequestParam(value = "docObj") String docObjJson,
            @RequestBody MultipartFile file) throws Exception {
        User user = null;
        try {
            user = accountSvc.doAuthentication(token);
        } catch (Exception e) {
            return new ResponseEntity<>("Unauthorized", HttpStatus.UNAUTHORIZED);
        }

        Table table = tableLoader.getTableByTableName(tableName);
        return table.uploadFile(docObjJson, file);
    }
}
