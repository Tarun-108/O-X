package com.example.o_x;

public class Blog {

    String title,body,writer;

    public Blog(){

    }
    public Blog(String title, String body, String writer) {
        this.title = title;
        this.body = body;
        this.writer = writer;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getWriter() {
        return writer;
    }

    public void setWriter(String writer) {
        this.writer = writer;
    }

}
