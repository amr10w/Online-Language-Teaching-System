package Main;

import fileManager.FileManager;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginManager {
    private FileManager fm;

    private ArrayList<String> users;
    private ArrayList<String> passwords;
    private static User selectedUser;



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

        if(user instanceof Student) {
            ApplicationManager.addStudent((Student) user);
            userData.put("role", "student");
            userData.put("language", ((Student) user).getLanguage());

        }
        else {
            ApplicationManager.addTeachers((Teacher) user);
            userData.put("role", "Teacher");
            userData.put("language", ((Teacher) user).getLanguage());
        }
        setSelectedUser(user);
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
        ArrayList<User> users=ApplicationManager.getUsers();
        for(User user:users)
        {
            if(user.getUsername().equals(username)) {
                if (user instanceof Student) {
                    setSelectedUser(user);
                    return 1;
                } else if (user instanceof Teacher) {
                    setSelectedUser(user);
                    return 2;
                }
            }
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

    public static void setSelectedUser(User user)
    {
        if(user instanceof Student)
        {
            selectedUser=(Student)user;
        }
        else{
            selectedUser=(Teacher)user;
        }
    }

    public static User getSelectedUser()
    {
        return selectedUser;
    }
}
