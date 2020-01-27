package JADevelopmentTeam.database;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.math.BigInteger;

public class MySQLConnector {
    private SessionFactory sessionFactory;
    private static MySQLConnector mySQLConnector;

    private MySQLConnector(){
        sessionFactory = new Configuration()
                .configure() // configures settings from hibernate.cfg.xml
                .buildSessionFactory();
    }

    public static MySQLConnector getInstance(){
        if (mySQLConnector == null) {
            synchronized (MySQLConnector.class) {
                if (mySQLConnector == null) {
                    mySQLConnector = new MySQLConnector();
                }
            }
        }
        return mySQLConnector;
    }
    public void sendObject(Object object){
        try {
            Session session = sessionFactory.openSession();
            session.beginTransaction();
            session.save(object);
            session.getTransaction().commit();
            session.close();
            if (object instanceof Move){
                Move move =(Move) object;
                move.makeOneStep();
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    public int getLastGameID(){
        try {
            Session session = sessionFactory.openSession();
            return ((BigInteger) session.createSQLQuery("SELECT last_insert_id() from game limit 1;").uniqueResult()).intValue();
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return -1;
    }
}
