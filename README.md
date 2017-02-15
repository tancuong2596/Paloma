# la_paloma
Chat client written in JAVA

#USER Table

####__description:__  store the details of all users

+ UID
+ FullName
+ DateOfBirth
+ Gender
+ Location
+ Email
+ Phone number

#CONVERSATIONS_CONTENT Table
####__description:__ store the details of all conversations

+ UID: the user who sent message to this 
+ ConID
+ SentTime
+ ConMsg: content of the conversation

#CONVERSATIONS_MEMBER Table
### __description:__ store the members for each conversation
+ UID
+ ConID
