# Routing Information Protocol (RIP) v2

Simulation of RIP v2 protocol using java. Infinite loop problem is solved using split horizon and poison reversing.

@author Ashutosh katkar

Use following four servers to simulate RIP v2 protocol.

1. queeg: 129.21.30.37
2. comet: 129.21.34.80
3. rhea: 129.21.37.49
4. glados: 129.21.22.196

subnet mask is 255.255.255.0 for all. 

limitations : The number of neighbors (or interfaces) for each router is limited up to two.

How to run:

1. login to each server.
2. copy all java files to one directory
3. compile using command 
   javac *java
4. type following command to run:
   java Router <Router_Name> <Router_IP> <Interger_random_ seed>
   e.g. java Router glados 129.21.22.196 10 
5. run this command on each server
6. enter number of neighbors ( 1 or 2)
7. enter details of neighbors
   Enter Router_name link_cost send_port recieve_port
   e.g. queeg 4 5200 5210
8. IMPORTANT : enter router name in small case letters.
               while entering ports use free ports. Remember ports and cost assigned to
               neighbors. You must enter SAME cost while entering details from neighboring server
               console. But, reverse the ports.
               
   e.g. if glados is neighbor of queeg with cost 5.
        you must enter details in glados console as 
        
        Enter Router_name link_cost send_port recieve_port
        queeg 5 5200 5210
        
        and following details in queeg console as
        Enter Router_name link_cost send_port recieve_port
        glados 5 5210 5200 
                   
9. All ports for a pair must be unique.

10. After entering data Router will start sending and reciveing packet on dedicated port. randomly
    after some time link cost will be changed. detail message will get dislplayed.

11. use ctrl + c to terminate program ( router will down). So it become unreachable.
    neighboring router trigger update this and send trigger update to other neighbors.
    ( demo for handling count to infinity problem using split horizon with poison reversing.)
                       
                        
 Tested on following topology:
               3                        4                             7
 glaods <------------>   qeeug  <---------------->   comet   <-----------------> rhea 
       sport  rport               sport rport                sport rport               sport rport
 queeg 5210   5200        glados   5200  5210        queeg   5310  5300         comet   5410 5400
                          comet    5300  5310        rhea    5400 5410
                          
                          
 termiante rhea its unreachable information get updated to all via trigger updates.
