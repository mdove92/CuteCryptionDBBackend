package com.cutecryption.cutecryption;

import org.bson.Document;
import org.springframework.web.bind.annotation.RestController;

import microsoft.exchange.webservices.data.core.ExchangeService;
import microsoft.exchange.webservices.data.core.enumeration.misc.ExchangeVersion;
import microsoft.exchange.webservices.data.core.service.item.EmailMessage;
import microsoft.exchange.webservices.data.credential.ExchangeCredentials;
import microsoft.exchange.webservices.data.credential.WebCredentials;
import microsoft.exchange.webservices.data.property.complex.MessageBody;

import java.util.Date;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import com.mongodb.*;
import com.mongodb.client.*;
import com.mongodb.client.MongoClient;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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

    // Method for sending messages from browser
    @RequestMapping(value="/", method = RequestMethod.OPTIONS)
     ResponseEntity<?> rootOptions() 
     {
        final HttpHeaders headers = new HttpHeaders();
       
        return ResponseEntity.ok()
        .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS)
        .headers(headers).build();
		
     }

    // Method for sending messages from browser
    @RequestMapping(value="/sendMail", method = RequestMethod.OPTIONS)
     ResponseEntity<?> collectionOptions() 
     {
        final HttpHeaders headers = new HttpHeaders();
        return ResponseEntity.ok()
        .allow(HttpMethod.GET, HttpMethod.POST, HttpMethod.OPTIONS)
        .headers(headers).build();
		
     }

    // Method for handling the get request to "/" route
    @RequestMapping("/")
    public ResponseEntity index(@RequestParam String templateName, @RequestParam String email) {
        // This method gets all entries from "Template Collection"
        final FindIterable<Document> myDoc;
        if (templateName == null || templateName == "") {
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
        return new ResponseEntity(jsonData, headers, HttpStatus.OK);
    }

    // Method for handling post calls to add templates to mongo db
    @PostMapping("/")
    public ResponseEntity postController(@RequestBody com.cutecryption.cutecryption.TemplateRequest templateRequest) {
        HttpHeaders headers = new HttpHeaders();
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
    @RequestMapping(value = "/sendMail", method = RequestMethod.POST, consumes= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity mailFunction(@RequestBody MailRequest messageRequest) {
        HttpHeaders headers = new HttpHeaders();
        String userName ="cutecryption@outlook.com";
        String password = "V3r9eCrm9UKN";

        if (System.getenv().containsKey("EMAIL_USERNAME")) {
            userName = System.getenv().get("EMAIL_USERNAME");
        }

        if (System.getenv().containsKey("EMAIL_PASSWORD")) {
            password = System.getenv().get("EMAIL_PASSWORD");
        }
        final ExchangeService service = new ExchangeService(ExchangeVersion.Exchange2010_SP2);
        final ExchangeCredentials credentials = new WebCredentials(userName, password);
        service.setCredentials(credentials);
        try {
            service.autodiscoverUrl(userName);
       
            EmailMessage message = new EmailMessage(service);
            message.setSubject("You've received an encrypted message.");
            message.setBody(MessageBody.getMessageBodyFromText(messageRequest.messageBody));
            message.getToRecipients().add(messageRequest.recipient);
            message.send();
        }  catch(Exception ex) {
            return new ResponseEntity(ex.toString(), headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity("HAPPY DAY", headers, HttpStatus.OK);
    }
}

// Mailrequest class to represent object passed into the controller to send a message to
final class MailRequest{

    // Message body of the request
    public String messageBody;

    // Recipeint email address
    public String recipient;
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
