// package com.cutecryption.cutecryption;

// import org.bson.Document;

// import java.util.Date;

// // Template request class to represent the object passed into the template controller
// public class TemplateRequest {

//     // Template name property
//     public String TemplateName;

//     // Template contents property
//     public String TemplateContents;

//     // Template creator property
//     public String TemplateCreator;

//     // Template created time property
//     private final Date CreatedTime;

//     // Constructor for the template request object
//     public TemplateRequest() {
//         this.CreatedTime = new Date();
//     }

//     // Method to return a document object that contains all the values from the request object properties
//     public Document ToDocument() {
//         // If all the required fields have contents, generate a document object containing all those fields
//         if (TemplateContents != null && TemplateContents != null && TemplateCreator != null) {
//             return new Document("Contents", this.TemplateContents).append("Name", this.TemplateName).append("Creator", this.TemplateCreator)
//                     .append("Created", this.CreatedTime.toString());
//         }
//         else{
//             return null;
//         }
//     }
// }