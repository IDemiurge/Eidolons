package main.system.net.user;

import java.net.Socket;

public class UserListRefresher implements Runnable {

    private UserList ul;

    public UserListRefresher(Socket socket, UserList ul) {
        this.ul = ul;
        new Thread(this, "UL_Refresher").start();
    }

    @Override
    public void run() {
        //ul.requestData();
        while (true) {
            try {
                Thread.sleep(1250);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //ul.requestData();

        }
    }

}
