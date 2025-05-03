package Main;

import fileManager.FileManager;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginManager {
    private FileManager fm;

    private ArrayList<String> users;
    private ArrayList<String> passwords;


    public LoginManager()
    {
        fm=new FileManager("src/main/resources/Database");
        users=new ArrayList<>();
        passwords=new ArrayList<>();


    }

    public void signup(User user)
    {

        HashMap<String,String> userData=new HashMap<>();
        userData.put("username",user.getUsername());
        userData.put("password",user.getPassword());
        userData.put("email",user.getEmail());
        if(user instanceof Student)
            userData.put("role","student");
        else
            userData.put("role","Teacher");

        fm.saveData(userData);
    }
    public int login(String username,String password)
    {

        fm.loadUsersData(users,passwords);

        int index=users.indexOf(username);
        if(index==-1)
        {

            return -1;
        }
        if(!passwords.get(index).equals(password))
        {

            return -2;
        }

        return 1;

    }
    public boolean checkUsername(String username)
    {
        fm.loadUsersData(users,passwords);
        if(users.contains(username))
        {
            return false;
        }
        return true;
    }
}
