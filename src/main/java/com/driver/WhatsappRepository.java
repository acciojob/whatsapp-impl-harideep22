package com.driver;

import java.util.*;

import org.springframework.stereotype.Repository;

@Repository
public class WhatsappRepository {


    //Assume that each user belongs to at most one group
    //You can use the below mentioned hashmaps or delete these and create your own.
    private HashMap<Group, List<User>> groupUserMap;
    private HashMap<Group, List<Message>> groupMessageMap;
    private HashMap<Message, User> senderMap;
    private HashMap<Group, User> adminMap;
    private HashSet<String> userMobile;
    private int customGroupCount;
    private int messageId;

    public WhatsappRepository(){
        this.groupMessageMap = new HashMap<Group, List<Message>>();
        this.groupUserMap = new HashMap<Group, List<User>>();
        this.senderMap = new HashMap<Message, User>();
        this.adminMap = new HashMap<Group, User>();
        this.userMobile = new HashSet<>();
        this.customGroupCount = 0;
        this.messageId = 0;
    }

    public String createUser(String name,String mobile) throws Exception {
        if(userMobile.contains(mobile)){
            throw new Exception("User already exists");
        }
        userMobile.add(mobile);
        User user=new User(name,mobile);
        return "SUCCESS";

    }
    public Group createGroup(List<User> users){
        if (users.size()==2){
            Group group=new Group(users.get(1).getName(),2);
            adminMap.put(group,users.get(0));
            groupUserMap.put(group,users);
            groupMessageMap.put(group,new ArrayList<>());
            return group;
        }
        this.customGroupCount++;
        Group group=new Group("Group "+this.customGroupCount,users.size());
        adminMap.put(group,users.get(0));
        groupUserMap.put(group,users);
        groupMessageMap.put(group,new ArrayList<>());
        return group;
    }
    public int createMessage(String content){
        // The 'i^th' created message has message id 'i'.
        // Return the message id.
        this.messageId+=1;
        Message message=new Message(this.messageId,content);
        return message.getId();
    }
    public int sendMessage(Message message, User sender, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "You are not allowed to send message" if the sender is not a member of the group
        //If the message is sent successfully, return the final number of messages in that group.

        if (adminMap.containsKey(group)){
            List<User> users=groupUserMap.get(group);
            boolean visited=false;
            for (User user:users){
                if (user==sender){
                    visited=true;
                    break;
                }
            }
            if (visited){
                senderMap.put(message,sender);
                List<Message> messageList=groupMessageMap.get(group);
                messageList.add(message);
                groupMessageMap.put(group,messageList);
                return messageList.size();
            }
            throw new Exception("You are not allowed to send message");
        }
        throw new Exception("Group does not exist");
    }
    public String changeAdmin(User approver, User user, Group group) throws Exception{
        //Throw "Group does not exist" if the mentioned group does not exist
        //Throw "Approver does not have rights" if the approver is not the current admin of the group
        //Throw "User is not a participant" if the user is not a part of the group
        //Change the admin of the group to "user" and return "SUCCESS". Note that at one time there is only one admin and the admin rights are transferred from approver to user.
        if(adminMap.containsKey(group)){
            if (adminMap.get(group).equals(approver)){
                List<User> participants=groupUserMap.get(group);
                boolean userFound=false;
                for (User participant:participants){
                    if (participant==user){
                        userFound=true;
                        break;
                    }
                }
                if (userFound){
                    adminMap.put(group,user);
                    return "SUCCESS";
                }
                throw new Exception("User is not a participant");
            }
            throw new Exception("Approver does not have rights");
        }
        throw new Exception("Group does not exist");

    }


}
