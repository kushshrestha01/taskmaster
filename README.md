# Lab 26: Build & Deploy TaskMaster to EC2 Using DyanamoDB

## TaskMaster application.
TaskMaster is a task-tracking application with the same basic goal
 as Trello: allow users to keep track of tasks to be done and their 
 status.

## Link to your deployed application.
http://taskmaster.us-east-2.elasticbeanstalk.com/


## Description of any issues you encountered during deployment.
After deploying to elastic beanstalk I am having Error Code: AccessDeniedException; 

# Lab 27: Structuring Data with NoSQL

## Overview
Lab 26 code was used and new routes were added to it.

## ChangeLog
Following routes were added/edited per lab requirements
1. /task route was edited
2. /users/{name}/tasks was added
3. /tasks/{id}/assign/{assignee} was added

# Lab 31: S3 Programmatic Uploads

## Overview
Previous lab code was used and new routes were added to it.

## ChangeLog
Following routes were added/edited per lab requirements
1. /task/{id}/images route was edited
2. /tasks/{id} route was added

## Deployed front end
http://taskmaster-react-frontend.s3-website.us-east-2.amazonaws.com

## Deployed back end
taskmaster.us-east-2.elasticbeanstalk.com 

# Lab 32: Lambda

## Overview
Previous lab code was used.

## Feature Tasks
1. A user uploads an image at any size, and have both the original size and a thumbnail size associated with the task.
2. When an image is uploaded to your S3 bucket, it should trigger a Lambda function which should create a 50x50 pixel thumbnail version of that image, and save it to another S3 bucket.
3. Both original size and thumbnail rendered in frontend.

## Deployed front end
http://taskmaster-react-frontend.s3-website.us-east-2.amazonaws.com

## Deployed back end
taskmaster.us-east-2.elasticbeanstalk.com 

### Reference:
https://docs.aws.amazon.com/lambda/latest/dg/with-s3-example.html
