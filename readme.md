INTRO

CLI Mailer is a simple tool for distribution of emails.

_DO_NOT_USE_IT_FOR_A_SPAMMING_


GET AND BUILD

Install SBT from: http://www.scala-sbt.org
Go to folder where you like to install program, open command prompt and run next commands:
```
    git clone https://github.com/AlexCAB/CLIMailer
    cd CLIMailer
    sbt assembly
    mailer -h```
    

COMMAND LINE

Program take four mandatory arguments:
    -o (-output) -- If “s” the tool sends emails. If “d” the tool saves for every recipient a *.eml file.
    -d (-destination) -- Path to the file containing the list of recipients.
    -c (-configuration) -- Path to the file containing the configuration info.
    -m (-message) -- Path to the file containing the message information.
And one not mandatory arguments:
    -I (-index) -- The first recipient to start from (Default = 1).
    

DESTINATION FILE

Is a CSV file with a comma as delimiter, format:
```
    <email>,<arg1>,<arg2>,...,<argN>
    ...```
Where:
    email  -- recipient email,
    arg1-N – templating arguments.
    

CONFIGURATION FILE 

Is a YAML file, which contain mail server parameters, example for the Gmail: 
```
  server:
    hostCONFI:  smtp.gmail.com
    port:  465
    user:  <user>
    passw: <password>```
    

MESSAGE FILE

Also is a YAML file, which contain message parameters, example:
```
  sender:
    email:    mail1@gmail.com
    subject:  Test message.
    replayTo: mail2@gmail.com
    cc:
      - mail3@gmail.com
      - mail4@gmail.com
    bcc:
      - mail5@gmail.com
      - mail6@gmail.com
  content:
    file: template.html
  attachments:
    - java.gif
    - some.pdf```


TEMPLATE FILE

It’s any text file, which will use as the message body.
In template you can use marks like “@<integer>@” (without quotes) which will replace to appropriate arguments from the destination file, like example template:
```Hello @1@, send me @2@ from @0@```
And the destination file row:
```emeil@gmail.com,Alex,file```
Will produce: 
```Hello Alex, send me file from emeil@gmail.com```
