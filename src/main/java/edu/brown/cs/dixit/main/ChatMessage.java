package edu.brown.cs.dixit.main;

import java.util.ArrayList;
import java.util.List;

public class ChatMessage{
    public List<String> game;
    public List<String> username;
    public List<String> body;
    public List<Integer> time;

    public ChatMessage() {
    	game = new ArrayList<>();
    	username = new ArrayList<>();
    	body = new ArrayList<>();
    	time = new ArrayList<>();
    }
}