package com.makemusiccount.android.model;

/**
 * Created by Welcome on 24-01-2018.
 */

public class SongList {
    String ID;
    String name;
    String image;
    String status;
    String song_file;
    String play_songs;
    String song_hint_image;
    String artist;
    String song_category;

    public String getSong_category() {
        return song_category;
    }

    public void setSong_category(String song_category) {
        this.song_category = song_category;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getPlay_autoplay() {
        return play_autoplay;
    }

    public void setPlay_autoplay(String play_autoplay) {
        this.play_autoplay = play_autoplay;
    }

    public String getSong_level() {
        return song_level;
    }

    public void setSong_level(String song_level) {
        this.song_level = song_level;
    }

    public String getSong_quiz() {
        return song_quiz;
    }

    public void setSong_quiz(String song_quiz) {
        this.song_quiz = song_quiz;
    }

    public String getSong_intro_name() {
        return song_intro_name;
    }

    public void setSong_intro_name(String song_intro_name) {
        this.song_intro_name = song_intro_name;
    }

    public String getSong_intro() {
        return song_intro;
    }

    public void setSong_intro(String song_intro) {
        this.song_intro = song_intro;
    }

    String play_autoplay;
    String song_level;
    String song_quiz;
    String song_intro_name;
    String song_intro;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    String url;





    public SongList(String ID, String name, String image, String status, String play_songs) {
        this.ID = ID;
        this.name = name;
        this.image = image;
        this.status = status;
        this.play_songs = play_songs;
    }

    public SongList() {
    }

    public String getSong_hint_image() {
        return song_hint_image;
    }

    public void setSong_hint_image(String song_hint_image) {
        this.song_hint_image = song_hint_image;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImage() {
        return image;
    }

    public String getSong_file() {
        return song_file;
    }

    public void setSong_file(String song_file) {
        this.song_file = song_file;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPlay_songs() {
        return play_songs;
    }

    public void setPlay_songs(String play_songs) {
        this.play_songs = play_songs;
    }
}
