package com.example.abhishek.weehive;

/**
 * Created by Abhishek on 22-Dec-16.
 */

public class Post {

    String Title,Image,Name,Like_count,View_count;

    public Post() {
    }

    public Post(String title, String image,String name,String like_count,String view_count) {
        Title = title;
        Image = image;
        Name = name;
        Like_count = like_count;
        View_count = view_count;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getName() {
        return Name;
    }

    public String getLike_count() {
        return Like_count;
    }

    public void setLike_count(String like_count) {
        Like_count = like_count;
    }

    public void setView_count(String view_count) {
        View_count = view_count;
    }

    public String getView_count() {
        return View_count;
    }
}
