package Main;



import java.util.ArrayList;
import java.util.HashMap;

public class LoginManager {


    private static ArrayList<String> users;
    private static ArrayList<String> passwords;
    private static User selectedUser;



    public LoginManager()
    {

        users=new ArrayList<>();
        passwords=new ArrayList<>();


    }

    public static void signup(User user)
    {
        addUsername(user.getUsername());
        addPassword(user.getPassword());

        System.out.println(users);

        if(user instanceof Student) {
            ApplicationManager.addStudent((Student) user);


        }
        else {
            ApplicationManager.addTeachers((Teacher) user);

        }
        setSelectedUser(user);

    }
    public static int login(String username,String password)
    {

        System.out.println(users);
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
    public static boolean checkUsername(String username)
    {

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

    private static void addUsername(String username){users.add(username);}
    private static void addPassword(String password){passwords.add(password);}
}
