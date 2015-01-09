package com.chainreactionai.game;


public interface IGoogleServices
{
public void signIn();
public void signOut();
public void rateGame();
public void submitScore(long score);
public void showScores();
public boolean isSignedIn();
public void showAchievement();
public void getAchievement(String achievementCode);
public void getIncAchievement(String achievementCode, int inc);
}