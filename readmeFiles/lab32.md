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
