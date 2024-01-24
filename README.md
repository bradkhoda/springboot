# springboothelloworld #
This repository features code from a personal fun project that includes a Springboot API builder base, that is hosted on GCP.

### API Functions ###
* __Hello World__   : /hello?name={Insert Name}                --> Returns "Hello {Name}!"
* __Now__           : /now                                     --> Returns the Date+Time currently, on GMT
* __Binary Message__: /tmpsckt1?binMsg={Insert Binary Message} --> Returns decoded binary message based on decoding principles
* __Hex Message__   : /tmpsckt2?hexMsg={Insert Hex Message}    --> Returns decoded hexadecimal message based on binary decoding principles
* __Try SQL__       : /trsyql?pswd={Insert password you know}  --> Returns everything inside of the test MYSQL database table
* __Insert SQL__    : /insertsql?id={Insert ID}&type={Insert message type}&value={Insert integer value}&pswd={Insert Password you know} --> Insert Values statement

### Message Decoding Principles ###
The messages coming through are currently capped at the following limits:
* Has to be 16 bits in binary length
* Has to translate to 16 bits in binary length if Hex message is passed

The message is broken down into 3 subsections based on binary message. Below you will find an example of what passing "1100110011001010" would be interpreted:

Device ID  | Message Type | Message Value
---------- | ------------ | -------------
1100       | 1100         | 11001010

* __Device ID__    :Decode into an integer
* __Message Type__ :Decode into an integer; Can take one of 3 forms:
** MessageType.Temperature == integer 14 
** MessageType.Pressure == integer 12
** MessageType.Humidity == integer 10

### SQL insertions ###
The messages that are to be ingested are ideally sent to an internal VCN, which handles committing to a MYSQL database.
Currently, the system accepts a password-based query, in order to preserve MYSQL security. In later times, the idea is to host an intermediate request handler along with the MYSQL server onto internal VCNs, which will be operated on by the springboot API as a gateway to the publuic internet, connecting via Google Cloud SQL Proxy to the internal clusters. The reason for intermediate request handler is to enable accessibility through writing to MYSQL, and also allow extensibility through allowing hanlding frontend requests. I want to be able to show off the details of this particular table later on.


### Purpose ###
The purpose for developing the project is primarily to help the author with learning Docker, Kubernetes, GCP, Deployments, Java, SQL, Server management, IP forwarding, and HTTP protocols. It also, will eventually feature integration from a Raspberry Pi device as an "IoT Publisher" and will have the capacity to work with multiple at a time and commit to the same table with different device IDs. 

The idea is:
* Create ability to deploy tables on demand through "Console" interface in WordPress
* Login to said tables 
* Accept Hex messages from Raspberry Pis
* Translate Hex messages to SQL insert objects
* Publish messages to particular tables through using the correct table ID
* Save the messages inside of MYSQL
* Allow the ability for the WordPress frontend to read-only from the particular Table ID

Next level would be to introduce account management systems on Firestore, which allows multiple customers; Similar to Azure AD admins. These "Admins" would then log into the WordPress console system, pull up their version of IoT Console, and look at their particular tables. When you setup the Raspberry Pis, all you have to give them is the Admin ID. They publish to Springboot, and Springboot handles where to send each message.
Optimization becomes important. I might have to introduce Kafka as an intermediary for message queue handling in order to overcome the resource problem.

