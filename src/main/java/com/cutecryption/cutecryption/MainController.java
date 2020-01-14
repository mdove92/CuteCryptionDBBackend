package com.cutecryption.cutecryption;

import org.bson.Document;
import org.springframework.web.bind.annotation.RestController;
import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

// Controller class for our template controller
@RestController
public class MainController {
    // Private collection variable for storing contents of our template collection
    private final MongoCollection<Document> templateCollection;

    // Constructor for the template controller class
    public MainController() {
        String connectionString = "mongodb+srv://CuteCryption:<pw>@cluster0-v5biy.mongodb.net/test?retryWrites=true&w=majority";
        if (System.getenv().containsKey("CONNECTION_STRING")) {
            connectionString = System.getenv().get("CONNECTION_STRING");
        }
        // Here we pass in our connection string to our mongo db cluster
        final MongoClient mongoClient = MongoClients.create(connectionString);
        final MongoDatabase database = mongoClient.getDatabase("morereal");
        this.templateCollection = database.getCollection("templateCollection");
    }

    // Method for handling the get request to "/" route
    @RequestMapping("/")
    public ResponseEntity index(@RequestParam String templateName, @RequestParam String email) {
        // This method gets all entries from "Template Collection"
        final FindIterable<Document> myDoc;
        if (templateName == null || templateName.isBlank()) {
            myDoc = this.templateCollection.find();
        } else {
            BasicDBObject eqQuery = new BasicDBObject();
            eqQuery.put("Name", new BasicDBObject("$eq", templateName));
            myDoc = this.templateCollection.find(eqQuery);
        }
        String jsonData = StreamSupport.stream(myDoc.spliterator(), false).map(Document::toJson)
                .collect(Collectors.joining(", ", "[", "]"));
        if (email != null && email != "") {
            jsonData = jsonData.replace("##USEREMAIL##", email);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        return new ResponseEntity(jsonData, headers, HttpStatus.OK);
    }

    // Method for handling post calls to add templates to mongo db
    @PostMapping("/")
    public ResponseEntity postController(@RequestBody com.cutecryption.cutecryption.TemplateRequest templateRequest) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Access-Control-Allow-Origin", "*");
        // Call the ToDocument() method on the template request object to turn it into a
        // Document object
        final Document requestDoc = templateRequest.ToDocument();
        if (requestDoc != null) {
            // If the result is non-null, insert into collection
            this.templateCollection.insertOne(requestDoc);
            return new ResponseEntity("", headers, HttpStatus.OK);
        } else {
            return null;
        }
    }
}

// Template request class to represent the object passed into the template
// controller
final class TemplateRequest {
    // Template name property
    public String TemplateName;
    // Template contents property
    public String TemplateContents;
    // Template creator property
    public String TemplateCreator;
    // Template created time property
    private final Date CreatedTime;

    // Constructor for the template request object
    public TemplateRequest() {
        this.CreatedTime = new Date();
    }

    // Method to return a document object that contains all the values from the
    // request object properties
    public Document ToDocument() {
        // If all the required fields have contents, generate a document object
        // containing all those fields
        if (TemplateContents != null && TemplateContents != null && TemplateCreator != null) {
            return new Document("Contents", this.TemplateContents).append("Name", this.TemplateName)
                    .append("Creator", this.TemplateCreator).append("Created", this.CreatedTime.toString());
        } else {
            return null;
        }
    }
}
