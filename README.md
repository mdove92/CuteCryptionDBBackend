# CuteCryptionDBBackend
This project serves as the backend of CuteCryption project using Java and Spring Boot with MongoDB.

## Dependencies
The CuteCryption backend is hosted in Heroku and uses MongoDB as its database. The MongoDB is hosted in Mongo Atlas in the cloud.
It utilizes the Heroku environment variables to stores the connection string to the database as well as the credentials used to send email so that credentials never need to be checked into git.
CuteCryption also depends on the java-ews-api in order to send emails from the CuteCryption@outlook.com email address through the Exchange Web Service.

## Functionality
The CuteCryption backend has three major functions:
- Getting templates
- Uploading new templates
- Sending email as the CuteCryption email address

CuteCryption backend is RESTful and is accessible through http request methods.

To get all templates, simply send a GET request to /?templateName=&email=
To get a template with a given name, send a GET request to /?templateName=NAMEOFTEMPLATE&email=

To upload a new template, send a POST request to "/" with a json body in this form:
	{
  		"TemplateName" : <Name of the template>,
  		"TemplateCreator" : <Name of the template creator>,
  		"TemplateContents" : <Html string containing contents of the template>
	}
  
  For sending mail, POST to "/sendMail" with a body:
	{
  		"messageBody" : <Message body to send>,
		"recipient": <Recipient email address>
	}
  
  TODO: Auth

## Setup
To setup this project, you will need to install the [Java SDK](https://www.oracle.com/technetwork/java/javase/downloads/index.html) as well as [Apache Maven](https://maven.apache.org/install.html).
Additionally, you will need to have a Mongo DB endpoint set up. CuteCryption uses [MongoDB Atlas](https://www.mongodb.com/cloud/atlas)

