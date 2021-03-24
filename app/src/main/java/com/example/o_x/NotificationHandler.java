package com.example.o_x;

public class NotificationHandler {

    private String senderUser, receiverUser, requestType;

    public NotificationHandler(){
        
    }
    
    public NotificationHandler(String senderUser, String receiverUser, String requestType) {
        this.senderUser = senderUser;
        this.receiverUser = receiverUser;
        this.requestType = requestType;
    }

    public String getSenderUser() {
        return senderUser;
    }

    public void setSenderUser(String SenderUser) {
        this.senderUser = SenderUser;
    }

    public String getReceiverUser() {
        return receiverUser;
    }

    public void setReceiverUser(String ReceiverUser) {
        this.receiverUser = ReceiverUser;
    }

    public void setRequestType(String RequestType) {
        this.requestType = RequestType;
    }

    public String getRequestType() {
        return requestType;
    }
}
