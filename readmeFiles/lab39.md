# Lab 39: SQS with Lambda

## Overview
Wiring up our Queues with live running cloud based code, creating a distributed, event driven architecture. Taskmaster app was used.

## Feature Tasks
1. Send an email to an administrator when a task is completed
2. Send a text to the person to whom a task is assigned (when it gets assigned)
3. When a task is deleted from Dynamo, trigger a message that will fire a lambda to remove any images associated to it from S3
4. Instead of having S3 run the resizer automatically on upload, evaluate the size of the image in your Java code and then send a message to a Q, that will in turn trigger the lambda resizer -- only when the image > 350k

## Deployed front end
http://taskmaster-react-frontend.s3-website.us-east-2.amazonaws.com

## Deployed back end
taskmaster.us-east-2.elasticbeanstalk.com 

