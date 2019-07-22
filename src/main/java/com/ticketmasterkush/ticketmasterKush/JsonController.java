package com.ticketmasterkush.ticketmasterKush;

import com.amazonaws.regions.Regions;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailService;
import com.amazonaws.services.simpleemail.AmazonSimpleEmailServiceClientBuilder;
import com.amazonaws.services.simpleemail.model.*;
import com.amazonaws.services.sns.AmazonSNSClient;
import com.amazonaws.services.sns.model.MessageAttributeValue;
import com.ticketmasterkush.ticketmasterKush.database.Task;
import com.ticketmasterkush.ticketmasterKush.database.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;

import java.util.*;


import com.amazonaws.services.sns.model.PublishRequest;
import com.amazonaws.services.sns.model.PublishResult;


@RestController
@CrossOrigin
@RequestMapping("")
public class JsonController {

    private S3Client s3Client;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    JsonController(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @GetMapping("")
    public String gethome() {
        return "home";
    }

    @GetMapping("/task")
    public List<Task> getTask(){
        List<Task> all = (List)taskRepository.findAll();
        return all;
    }

    @PostMapping("/task")
    public List<Task> postTask(@RequestBody Task task) {
        if(task.getAssignee().trim().isEmpty()){
            task.setStatus("Available");
        } else {
            task.setStatus("Assigned");
        }
        taskRepository.save(task);
        List<Task> all = (List)taskRepository.findAll();
        return all;
    }

    @PutMapping("/tasks/{id}/state")
    public Task putTask(@PathVariable UUID id) {
        Task t = taskRepository.findById(id).get();
        if(t.getStatus().equals("Available")){
          t.setStatus("Assigned");
            helperSendSMS();
        } else if(t.getStatus().equals("Assigned")){
            t.setStatus("Accepted");
        } else if(t.getStatus().equals("Accepted")){
            t.setStatus("Finished");
            helperSendEmail();
        } else if(t.getStatus()== null) {
            t.setStatus("Available");
        }
        taskRepository.save(t);
        return t;
    }

    @GetMapping("/users/{name}/tasks")
    public List<Task> taskWithName(@PathVariable String name){
        List<Task> t = taskRepository.findByAssignee(name);
        return t;
    }

    @PutMapping("/tasks/{id}/assign/{assignee}")
    public Task putTask(@PathVariable UUID id, @PathVariable String assignee) {
        Task t = taskRepository.findById(id).get();
        t.setAssignee(assignee);
        t.setStatus("Assigned");
        taskRepository.save(t);
        return t;
    }

    @PostMapping("/task/{id}/images")
    public RedirectView uploadFile(@PathVariable UUID id, @RequestPart(value = "file") MultipartFile file){
        Task t = taskRepository.findById(id).get();
        ArrayList<String> list = this.s3Client.uploadFile(file);
        t.setPic(list.get(0));
        t.setResized(list.get(1));
        taskRepository.save(t);
        return new RedirectView("http://taskmaster-react-frontend.s3-website.us-east-2.amazonaws.com");
//        return new RedirectView("http://localhost:3000/");

    }

    @GetMapping("/tasks/{id}")
    public Task uploadFile(@PathVariable UUID id){
        Task t = taskRepository.findById(id).get();
        return t;
    }

    //    https://docs.aws.amazon.com/sns/latest/dg/sms_publish-to-phone.html
    public void helperSendSMS(){
        AmazonSNSClient snsClient = new AmazonSNSClient();
        String message = "Kush has assigned task";
        String phoneNumber = "+13344324282";
        Map<String, MessageAttributeValue> smsAttributes =
                new HashMap<String, MessageAttributeValue>();
        //<set SMS attributes>
        PublishResult result = snsClient.publish(new PublishRequest()
                .withMessage(message)
                .withPhoneNumber(phoneNumber)
                .withMessageAttributes(smsAttributes));
    }

    public void helperSendEmail(){
        // Replace sender@example.com with your "From" address.
        // This address must be verified with Amazon SES.
        String FROM = "kushshrestha01@gmail.com";

        // Replace recipient@example.com with a "To" address. If your account
        // is still in the sandbox, this address must be verified.
        String TO = "kush_shrestha01@yahoo.com";

        // The configuration set to use for this email. If you do not want to use a
        // configuration set, comment the following variable and the
        // .withConfigurationSetName(CONFIGSET); argument below.
//        String CONFIGSET = "ConfigSet";

        // The subject line for the email.
        String SUBJECT = "Amazon SES test (AWS SDK for Java)";

        // The HTML body for the email.
        String HTMLBODY = "<h1>Amazon SES test (AWS SDK for Java)</h1>"
                + "<p>This email was sent with <a href='https://aws.amazon.com/ses/'>"
                + "Amazon SES</a> using the <a href='https://aws.amazon.com/sdk-for-java/'>"
                + "AWS SDK for Java</a>";

        // The email body for recipients with non-HTML email clients.
        String TEXTBODY = "This email was sent through Amazon SES "
                + "using the AWS SDK for Java.";


            try {
                AmazonSimpleEmailService client =
                        AmazonSimpleEmailServiceClientBuilder.standard()
                                // Replace US_WEST_2 with the AWS Region you're using for
                                // Amazon SES.
                                .withRegion(Regions.US_EAST_1).build();
                SendEmailRequest request = new SendEmailRequest()
                        .withDestination(
                                new Destination().withToAddresses(TO))
                        .withMessage(new Message()
                                .withBody(new Body()
                                        .withHtml(new Content()
                                                .withCharset("UTF-8").withData(HTMLBODY))
                                        .withText(new Content()
                                                .withCharset("UTF-8").withData(TEXTBODY)))
                                .withSubject(new Content()
                                        .withCharset("UTF-8").withData(SUBJECT)))
                        .withSource(FROM);
                        // Comment or remove the next line if you are not using a
                        // configuration set
//                        .withConfigurationSetName(CONFIGSET);
                client.sendEmail(request);
                System.out.println("Email sent!");
            } catch (Exception ex) {
                System.out.println("The email was not sent. Error message: "
                        + ex.getMessage());
            }
        }
}
