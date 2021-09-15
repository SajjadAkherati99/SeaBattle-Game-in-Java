package Controllers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.User;
import model.UserPublicInformation;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedList;

public class DatabaseController {

    public static Object wait = new Object();


    public static boolean checkUserExistence(User user){
        //System.out.println(user.getUsername());
        LinkedList<String> Users = new LinkedList<>();
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            File file = new File("");
            String path = file.getAbsolutePath();
            String filePath = String.format("%s/src/Database/usersList.json", path);

            Reader reader = Files.newBufferedReader(Paths.get(filePath));
            Users = gson.fromJson(reader, LinkedList.class);
            return Users.contains(user.getUsername());
        } catch (IOException e) {
            return false;
        }
    }

    public static void addToUsers(User user)  {
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            File file = new File("");
            String path = file.getAbsolutePath();
            String filePath = String.format("%s/src/Database/Users/%s.json", path, user.getUsername());

            FileWriter fileWriter = new FileWriter(filePath);
            gson.toJson(user, fileWriter);
            fileWriter.close();

            LinkedList<String> Users = new LinkedList<>();
            filePath = String.format("%s/src/Database/usersList.json", path);
            try {
                Reader reader = Files.newBufferedReader(Paths.get(filePath));
                Users = gson.fromJson(reader, LinkedList.class);
                Users.add(user.getUsername());
                fileWriter = new FileWriter(filePath);
                gson.toJson(Users, fileWriter);
                fileWriter.close();
            }catch (IOException e){
                Users.add(user.getUsername());
                fileWriter = new FileWriter(filePath);
                gson.toJson(Users, fileWriter);
                fileWriter.close();
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean checkIncorrectPassword(User user){
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            File file = new File("");
            String path = file.getAbsolutePath();
            String filePath = String.format("%s/src/Database/Users/%s.json", path, user.getUsername());

            Reader reader = Files.newBufferedReader(Paths.get(filePath));
            User userInfo = gson.fromJson(reader, User.class);
            return !userInfo.getPassword().equals(user.getPassword());
        } catch (IOException e) {
            return true;
        }
    }



    public static void updateUser(User user) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File file = new File("");
        String path = file.getAbsolutePath();
        String filePath = String.format("%s/src/Database/Users/%s.json", path, user.getUsername());

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath);
            gson.toJson(user, fileWriter);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Type collectionType = new TypeToken<LinkedList<UserPublicInformation>>(){}.getType();
        LinkedList<UserPublicInformation> usersInformation = new LinkedList<>();
        filePath = String.format("%s/src/Database/usersInformation.json", path);
        synchronized (wait) {
            try {
                Reader reader = Files.newBufferedReader(Paths.get(filePath));
                usersInformation = gson.fromJson(reader, collectionType);
                usersInformation = updateUserInformationList(usersInformation,
                        new UserPublicInformation(user.getUsername(), user.getLevel(), "online"));
                fileWriter = new FileWriter(filePath);
                gson.toJson(usersInformation, fileWriter);
                fileWriter.close();
            } catch (IOException e) {
                usersInformation.add(new UserPublicInformation(user.getUsername(), user.getLevel(), "online"));
                try {
                    fileWriter = new FileWriter(filePath);
                    gson.toJson(usersInformation, fileWriter);
                    fileWriter.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
        }
    }

    private static LinkedList<UserPublicInformation> updateUserInformationList(
            LinkedList<UserPublicInformation> usersInformation, UserPublicInformation user) {

        LinkedList<UserPublicInformation> newUsersInformation = new LinkedList<>();

        boolean entered = false;
        for (UserPublicInformation u : usersInformation){

            if (u.getLevel() > user.getLevel()){
                if(!u.getUsername().equals(user.getUsername()))
                    newUsersInformation.add(u);
            }else if(!entered){
                entered = true;
                newUsersInformation.add(user);
                if(!u.getUsername().equals(user.getUsername()))
                    newUsersInformation.add(u);
            }
            else {
                if(!u.getUsername().equals(user.getUsername()))
                    newUsersInformation.add(u);
            }
        }
        if (!entered){
            usersInformation.addFirst(user);
        }
        return newUsersInformation;
    }

    public static User readUserInformation(User user) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        File file = new File("");
        String path = file.getAbsolutePath();
        String filePath = String.format("%s/src/Database/Users/%s.json", path, user.getUsername());

        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        User userInfo = gson.fromJson(reader, User.class);
        return userInfo;
    }

    public static LinkedList<UserPublicInformation> getUsersInformation() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        Type collectionType = new TypeToken<LinkedList<UserPublicInformation>>(){}.getType();
        LinkedList<UserPublicInformation> usersInformation = new LinkedList<>();
        File file = new File("");
        String path = file.getAbsolutePath();
        String filePath = String.format("%s/src/Database/usersInformation.json", path);
        Reader reader = null;
        try {
            reader = Files.newBufferedReader(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        usersInformation = gson.fromJson(reader, collectionType);
        return usersInformation;
    }
}
