package com.ticketmasterkush.ticketmasterKush;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.ticketmasterkush.ticketmasterKush.database.Task;
import com.ticketmasterkush.ticketmasterKush.database.TaskRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TicketmasterKushApplication.class)
@WebAppConfiguration
@ActiveProfiles("local")

public class TaskTest {

    private DynamoDBMapper dynamoDBMapper;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    private AmazonDynamoDB amazonDynamoDB;

    private static final String testTitle = "testTitle";
    private static final String testDescription = "testDescription";
    private static final String testAssignee = "testAssignee";

    @Before
    public void setup() throws Exception {
        dynamoDBMapper = new DynamoDBMapper(amazonDynamoDB);

        CreateTableRequest tableRequest = dynamoDBMapper.generateCreateTableRequest(Task.class);

        tableRequest.setProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        dynamoDBMapper.batchDelete((List<Task>)taskRepository.findAll());
    }

    @Test
    public void readWriteTestCase() {
        Task test = new Task(testTitle, testDescription, testAssignee);
        taskRepository.save(test);

        List<Task> result = (List<Task>)taskRepository.findAll();

        assertTrue("Not empty", result.size() > 0);
        assertTrue("Contains item with expected title", result.get(0).getTitle().equals(testTitle));
    }

}